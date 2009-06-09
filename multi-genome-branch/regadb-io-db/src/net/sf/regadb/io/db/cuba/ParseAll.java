package net.sf.regadb.io.db.cuba;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import net.sf.regadb.db.Attribute;
import net.sf.regadb.db.AttributeNominalValue;
import net.sf.regadb.db.DrugCommercial;
import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.Therapy;
import net.sf.regadb.db.TherapyCommercial;
import net.sf.regadb.db.TherapyCommercialId;
import net.sf.regadb.db.TherapyGeneric;
import net.sf.regadb.db.TherapyGenericId;
import net.sf.regadb.io.db.util.ConsoleLogger;
import net.sf.regadb.io.db.util.DelimitedReader;
import net.sf.regadb.io.db.util.Mappings;
import net.sf.regadb.io.db.util.Parser;
import net.sf.regadb.io.db.util.Utils;
import net.sf.regadb.io.db.util.mapping.OfflineObjectStore;
import net.sf.regadb.io.util.StandardObjects;
import net.sf.regadb.util.args.Arguments;
import net.sf.regadb.util.args.PositionalArgument;
import net.sf.regadb.util.frequency.Frequency;

public class ParseAll extends Parser{
	private OfflineObjectStore objstore = new OfflineObjectStore();
	private Map<String,Drugs> drugMap;
	
	private String delimiter = "\"";
	private String separator = ",";
	
	public static void main(String args[]){
		Arguments as = new Arguments();
		PositionalArgument csvDir = as.addPositionalArgument("csv-directory", true);
		PositionalArgument mapDir = as.addPositionalArgument("mapping-directory", true);
		
		if(!as.handle(args))
			return;
		
		ParseAll pa = new ParseAll();
		pa.run(new File(csvDir.getValue()),new File(mapDir.getValue()));
	}
	
	public ParseAll(){
		setDateFormat(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"));
		setLogger(new ConsoleLogger());
	}
	
	public void run(File csvDir, File mapDir){
		File cd4File = new File(csvDir.getAbsolutePath() + File.separatorChar + "CD4_COUNTS.csv");
		File drugsFile = new File(csvDir.getAbsolutePath() + File.separatorChar + "COMB_MED.csv");
		File pvihsFile = new File(csvDir.getAbsolutePath() + File.separatorChar + "PVIHS.csv");
		File therapyFile = new File(csvDir.getAbsolutePath() + File.separatorChar + "TREATMENTS.csv");
		File vlFile = new File(csvDir.getAbsolutePath() + File.separatorChar + "VIRAL_LOAD.csv");
		
		if(!(	   check(cd4File) 
				&& check(drugsFile) 
				&& check(pvihsFile) 
				&& check(therapyFile) 
				&& check(vlFile)))
			return;
		
		parsePatients(pvihsFile);
		parseDrugs(drugsFile, mapDir);
		parseTherapy(therapyFile);
		parseCd4(cd4File);
		parseViralLoad(vlFile);
	}
	
	public void parsePatients(File patFile){
		try {
			DelimitedReader dr = new DelimitedReader(patFile,separator,delimiter);

			Attribute municipart = getObjectStore().createAttribute(	StandardObjects.getDemographicsAttributeGroup(),
					StandardObjects.getNominalValueType(),
					"municipart");
			Attribute provpart = getObjectStore().createAttribute(	StandardObjects.getDemographicsAttributeGroup(),
																	StandardObjects.getNominalValueType(),
																	"provpart");


			while(dr.readLine() != null){
				Patient p = getObjectStore().createPatient(null, dr.get("casoind"));
				
				String s = dr.get("sexo");
				p.addPatientAttributeValue(Utils.createPatientAttributeValue(StandardObjects.getGenderAttribute(), s.equals("F") ? "female":"male"));
				
				AttributeNominalValue anv = getObjectStore().getAttributeNominalValue(municipart, dr.get("municipart"));
				if(anv == null)
					anv = getObjectStore().createAttributeNominalValue(municipart, dr.get("municipart"));
				p.addPatientAttributeValue(Utils.createPatientAttributeValue(anv));
				
				anv = getObjectStore().getAttributeNominalValue(provpart, dr.get("provpart"));
				if(anv == null)
					anv = getObjectStore().createAttributeNominalValue(provpart, dr.get("provpart"));
				p.addPatientAttributeValue(Utils.createPatientAttributeValue(anv));
				
				Date d = getDate(dr.get("fechadiag"));
				if(d != null)
					Utils.setBirthDate(p, d);
				d = getDate(dr.get("fechafall"));
				if(d != null)
					Utils.setDeathDate(p, d);
			}
			
			dr.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void parseCd4(File cd4File){
		try {
			DelimitedReader dr = new DelimitedReader(cd4File,separator,delimiter);
			
			Test cd4 = getObjectStore().getTest(StandardObjects.getGenericCD4Test().getDescription(),
												StandardObjects.getCd4TestType().getDescription(),
												null);
			Test cd4p = getObjectStore().getTest(	StandardObjects.getGenericCD4PercentageTest().getDescription(),
													StandardObjects.getCd4PercentageTestType().getDescription(),
													null);
			while(dr.readLine() != null){
				Patient p = getObjectStore().getPatient(null, dr.get("casoind"));
				
				Date d = getDate(dr.get("fecha"));
				
				TestResult tr = p.createTestResult(cd4);
				tr.setValue(dr.get("cd4abs"));
				tr.setTestDate(d);
				
				tr = p.createTestResult(cd4p);
				tr.setValue(dr.get("cd4porc"));
				tr.setTestDate(d);
			}
			
			dr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static class Drugs{
		Set<DrugGeneric> dgs = new HashSet<DrugGeneric>();
		Set<DrugCommercial> dcs = new HashSet<DrugCommercial>();
		
		public int size(){
			return dgs.size()+dcs.size();
		}
	}
	
	public void parseDrugs(File drugsFile, File mapDir){
		Mappings map = Mappings.getInstance(mapDir.getAbsolutePath());
		drugMap = new HashMap<String,Drugs>();
		
		try{
			DelimitedReader dr = new DelimitedReader(drugsFile,separator,delimiter);
			Set<String> noMap = new HashSet<String>();

			List<DrugGeneric> generics = Utils.prepareRegaDrugGenerics();
			List<DrugCommercial> commercials = Utils.prepareRegaDrugCommercials();
			
			while(dr.readLine() != null){
				String combina = dr.get("combina");
				String medicam = dr.get("medicam");
				Drugs ds = new Drugs();
				
				String m = map.getMapping("drugs.mapping", medicam);
				if(m == null){
					if(noMap.add(medicam))
						System.err.println("no mapping for drug(s): '"+ medicam +'\'');
					continue;
				}
				for(String d : m.split(";")){
					int size = ds.size();
					
					for(DrugGeneric dg : generics){
						if(dg.getGenericId().equals(d)){
							ds.dgs.add(dg);
							break;
						}
					}
					
					if(size == ds.size()){
						for(DrugCommercial dc : commercials){
							if(dc.getName().equals(d)){
								ds.dcs.add(dc);
								break;
							}
						}
					
						if(size == ds.size())
							System.err.println("wrong mapping for drug: '"+ d +'\'');
					}
				}
				
				drugMap.put(combina, ds);
			}
			
			dr.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void parseTherapy(File therapyFile){
		try {
			DelimitedReader dr = new DelimitedReader(therapyFile,separator,delimiter);
			Map<Patient,TreeMap<Date,Therapy>> timelines = new HashMap<Patient,TreeMap<Date,Therapy>>(); 
			
			while(dr.readLine() != null){
				Patient p = getObjectStore().getPatient(null, dr.get("casoind"));
				
				TreeMap<Date,Therapy> tl = timelines.get(p);
				if(tl == null){
					tl = new TreeMap<Date,Therapy>();
					timelines.put(p,tl);
				}
				
				Date d = getDate(dr.get("fecha"));
				Therapy t = p.createTherapy(d);
				addDrugs(t, dr.get("combina"));
				
				tl.put(t.getStartDate(),t);
			}
			
			dr.close();
			
			for(Map.Entry<Patient,TreeMap<Date,Therapy>> me : timelines.entrySet()){
				Therapy prev = null;
				for(Therapy t : me.getValue().values()){
					if(prev != null)
						prev.setStopDate(t.getStartDate());
					prev = t;
				}
			}
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void parseViralLoad(File vlFile){
		try {
			DelimitedReader dr = new DelimitedReader(vlFile,separator,delimiter);
			
			Test vl = getObjectStore().getTest(StandardObjects.getGenericHiv1ViralLoadTest().getDescription(),
					StandardObjects.getHiv1ViralLoadTestType().getDescription(),
					null);

			Test vll = getObjectStore().getTest(StandardObjects.getGenericHiv1ViralLoadLog10Test().getDescription(),
					StandardObjects.getHiv1ViralLoadLog10TestType().getDescription(),
					null);

			while(dr.readLine() != null){
				Patient p = getObjectStore().getPatient(null, dr.get("casoind"));
				
				Date d = getDate(dr.get("fecha"));
				
				TestResult tr = p.createTestResult(vl);
				tr.setValue(dr.get("copias"));
				tr.setTestDate(d);
				
				p.createTestResult(vll);
				tr.setValue(dr.get("logar"));
				tr.setTestDate(d);
			}
			
			dr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void addDrugs(Therapy t, String combina){
		Drugs ds = drugMap.get(combina);
		if(ds == null)
			return;
		
		for(DrugGeneric dg : ds.dgs){
			TherapyGeneric tg =	new TherapyGeneric(
									new TherapyGenericId(t,dg),
									false,
									false);
			tg.setFrequency((long)Frequency.getDefaultFrequency());
			t.getTherapyGenerics().add(tg);
		}
		for(DrugCommercial dc : ds.dcs){
			TherapyCommercial tc = new TherapyCommercial(
										new TherapyCommercialId(t,dc),
										false,
										false);
			tc.setFrequency((long)Frequency.getDefaultFrequency());
			tc.setDayDosageUnits(1.0);
			t.getTherapyCommercials().add(tc);
		}
	}
	
	public OfflineObjectStore getObjectStore(){
		return objstore;
	}
}
