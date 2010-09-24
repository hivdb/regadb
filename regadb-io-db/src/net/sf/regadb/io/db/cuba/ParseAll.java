package net.sf.regadb.io.db.cuba;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import jxl.Sheet;
import net.sf.regadb.db.Attribute;
import net.sf.regadb.db.AttributeNominalValue;
import net.sf.regadb.db.DrugCommercial;
import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.Therapy;
import net.sf.regadb.db.TherapyCommercial;
import net.sf.regadb.db.TherapyCommercialId;
import net.sf.regadb.db.TherapyGeneric;
import net.sf.regadb.db.TherapyGenericId;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.db.meta.Equals;
import net.sf.regadb.io.db.util.ConsoleLogger;
import net.sf.regadb.io.db.util.DelimitedReader;
import net.sf.regadb.io.db.util.DrugMap;
import net.sf.regadb.io.db.util.Mappings;
import net.sf.regadb.io.db.util.Parser;
import net.sf.regadb.io.db.util.Utils;
import net.sf.regadb.io.db.util.mapping.ObjectStore;
import net.sf.regadb.io.db.util.mapping.OfflineObjectStore;
import net.sf.regadb.io.util.IOUtils;
import net.sf.regadb.io.util.StandardObjects;
import net.sf.regadb.tools.FastaFile;
import net.sf.regadb.util.args.Arguments;
import net.sf.regadb.util.args.PositionalArgument;
import net.sf.regadb.util.args.ValueArgument;
import net.sf.regadb.util.date.DateUtils;
import net.sf.regadb.util.frequency.Frequency;
import net.sf.regadb.util.settings.RegaDBSettings;

public class ParseAll extends Parser{
	private OfflineObjectStore objstore = new OfflineObjectStore();
	private Map<String,Drugs> therapyMap;
	
	private String delimiter = "\"";
	private String separator = ",";
	
	public static void main(String args[]){
		Arguments as = new Arguments();
		PositionalArgument csvDir = as.addPositionalArgument("csv-directory", true);
		PositionalArgument mapDir = as.addPositionalArgument("mapping-directory", true);
		PositionalArgument xmlDir = as.addPositionalArgument("xml-output-directory", true);
		ValueArgument confDir = as.addValueArgument("c", "configuration-directory", false);
		ValueArgument seqCsv = as.addValueArgument("seq-csv", "sequence-csv", false);
		ValueArgument seqFasta = as.addValueArgument("seq-fasta", "sequence-fasta", false);
		
		if(!as.handle(args))
			return;
		
		if(confDir.isSet())
			RegaDBSettings.createInstance(confDir.getValue());
		else
			RegaDBSettings.createInstance();
		
		ParseAll pa = new ParseAll();
		pa.run(new File(csvDir.getValue()),new File(mapDir.getValue()),new File(xmlDir.getValue()),
				seqCsv.isSet() ? new File(seqCsv.getValue()) : null,
				seqFasta.isSet() ? new File(seqFasta.getValue()) : null);
	}
	
	public ParseAll(){
		setDateFormat(new SimpleDateFormat("dd/MM/yyyy"));
		setLogger(new ConsoleLogger());
	}
	
	public void run(File csvDir, File mapDir, File xmlDir, File seqCsv, File seqFasta){
		if(!xmlDir.canWrite()){
			System.err.println("unable to write to "+ xmlDir.getAbsolutePath());
			return;
		}
		
		File cd4File = new File(csvDir.getAbsolutePath() + File.separatorChar + "cd4text.txt");
		File drugsFile = new File(csvDir.getAbsolutePath() + File.separatorChar + "combtext.txt");
		File pvihsFile = new File(csvDir.getAbsolutePath() + File.separatorChar + "pvihtext.txt");
		File therapyFile = new File(csvDir.getAbsolutePath() + File.separatorChar + "ttotext.txt");
		File vlFile = new File(csvDir.getAbsolutePath() + File.separatorChar + "cvtext.txt");
		
		if(!(	   check(cd4File) 
				&& check(drugsFile) 
				&& check(pvihsFile) 
				&& check(therapyFile) 
				&& check(vlFile)
				&& (seqCsv == null || check(seqCsv))
				&& (seqFasta == null || check(seqFasta))
			))
			return;
		
		System.out.println("parse patients");
		parsePatients(pvihsFile);
		
		System.out.println("parse drugs");
		parseDrugs(drugsFile, mapDir);
		
		System.out.println("parse therapies");
		parseTherapy(therapyFile);
		
		System.out.println("parse cd4");
		parseCd4(cd4File);
		
		System.out.println("parse viral load");
		parseViralLoad(vlFile);
		
		if(seqCsv != null && seqFasta != null){
			System.out.println("parse sequences");
			parseSequences(seqCsv, seqFasta);
			IOUtils.exportNTXMLFromPatients(getObjectStore().getPatients(), xmlDir.getAbsolutePath() + File.separatorChar +"viral-isolates.xml", ConsoleLogger.getInstance());
		}
		
		IOUtils.exportPatientsXML(getObjectStore().getPatients(), xmlDir.getAbsolutePath() + File.separatorChar +"patients.xml", ConsoleLogger.getInstance());
		System.out.println("done");
	}
	
	public void parsePatients(File patFile){
		try {
			
			Attribute aids = getObjectStore().createAttribute(
					getObjectStore().getAttributeGroup(StandardObjects.getClinicalAttributeGroup().getGroupName()),
					getObjectStore().getValueType(StandardObjects.getDateValueType().getDescription()),
					"Diagnosis");
							
			DelimitedReader dr = new DelimitedReader(patFile,separator,delimiter);

			Attribute gender = getObjectStore().getAttribute(
					StandardObjects.getGenderAttribute().getName(),
					StandardObjects.getPersonalAttributeGroup().getGroupName());
			AttributeNominalValue female = getObjectStore().getAttributeNominalValue(gender, "female");
			AttributeNominalValue male   = getObjectStore().getAttributeNominalValue(gender, "male");
			

			while(dr.readLine() != null){
				Patient p = getObjectStore().createPatient(null, dr.get("casoind"));
				String s;
				
				s = dr.get("sexo");
				if(s != null && s.length() > 0)
					p.createPatientAttributeValue(gender).setAttributeNominalValue(s.equals("F") ? female:male);
				
				s = dr.get("fnac");
				if(s != null && s.length() > 0){
					Date d = getDate(s);
					
					if(d != null)
						Utils.setBirthDate(p, d);
				}
				
				s = dr.get("fechadiag");
				if(s != null && s.length() > 0){
					Date d = getDate(dr.get("fechadiag"));
					if(d != null)
						p.createPatientAttributeValue(aids).setValue(d.getTime()+"");
				}

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
				
				addTestResult(p, cd4, d, dr.get("cd4abs"));
				addTestResult(p, cd4p, d, dr.get("cd4porc"));
			}
			
			dr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void addTestResult(Patient p, Test t, Date d, String value){
		if(value != null && value.length() > 0){
			
			//check duplicate results, keep worst case value
			for(TestResult tr : p.getTestResults()){
				if(tr.getTestDate().equals(d) && Equals.isSameTest(tr.getTest(), t)){
					if(t.getTestType().getDescription().contains("CD4")){
						double oldValue = Double.parseDouble(tr.getValue());
						double newValue = Double.parseDouble(value);
						
						if(oldValue > newValue)
							tr.setValue(value);
					}
					else if(t.getTestType().getDescription().contains("Viral")){
						double oldValue = Double.parseDouble(tr.getValue().substring(1));
						double newValue = Double.parseDouble(value.substring(1));
						
						if(oldValue < newValue)
							tr.setValue(value);
					}
					else{
						System.err.println("Duplicate test result: "+ p.getPatientId() +","+ t.getDescription() +","+ DateUtils.format(d));
					}
					return;
				}
			}
			
			//no duplicate
			p.createTestResult(t, null, d, value);
		}
	}
	
	private static class Drugs{
		Set<DrugGeneric> dgs = new HashSet<DrugGeneric>();
		Set<DrugCommercial> dcs = new HashSet<DrugCommercial>();
	}
	
	public void parseDrugs(File drugsFile, File mapDir){
		Mappings map = Mappings.getInstance(mapDir.getAbsolutePath());
		therapyMap = new HashMap<String,Drugs>();
		
		try{
			DelimitedReader dr = new DelimitedReader(drugsFile,separator,delimiter);
			Set<String> noMap = new HashSet<String>();

			DrugMap gmap = new DrugMap();
			
			Map<String,DrugCommercial> cmap = new HashMap<String, DrugCommercial>();
			for(DrugCommercial dc : Utils.prepareRegaDrugCommercials())
				cmap.put(dc.getName(), dc);
			
			
			while(dr.readLine() != null){
				String combina = dr.get("combina");
				String med = dr.get("medicam");
				
				Drugs ds = therapyMap.get(combina);
				if(ds == null){
					ds = new Drugs();
					therapyMap.put(combina,ds);
				}

				
				String m = map.getMapping("drugs.mapping", med);
				if(m == null){
					if(noMap.add(med))
						System.err.println("no mapping for drug(s): '"+ dr.get("medicam") +'\'');
					continue;
				}
				for(String d : m.split(";")){
					DrugGeneric dg = gmap.get(d);
					if(dg != null)
						ds.dgs.add(dg);
					else{
						DrugCommercial dc = cmap.get(d);
						if(dc != null)
							ds.dcs.add(dc);
						else
							System.err.println("wrong mapping for drug: '"+ d +'\'');
					}
				}
			}
			dr.close();
			
			for(Drugs ds : therapyMap.values())
				gmap.toBoosted(ds.dgs);
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
			
			Test vl = getObjectStore().getTest(	StandardObjects.getGenericHiv1ViralLoadTest().getDescription(),
												StandardObjects.getHiv1ViralLoadTestType().getDescription(),
												StandardObjects.getHiv1Genome().getOrganismName());

			while(dr.readLine() != null){
				Patient p = getObjectStore().getPatient(null, dr.get("casoind"));
				
				Date d = getDate(dr.get("fecha"));
				
				addTestResult(p, vl, d, '='+ dr.get("copias"));
			}
			
			dr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void addDrugs(Therapy t, String combina){
		Drugs ds = therapyMap.get(combina);
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
	
	private void parseSequences(File info, File fasta){
		Map<String, ViralIsolate> vis = new HashMap<String, ViralIsolate>();
		Map<String, Patient> visps = new HashMap<String, Patient>();
		
		Test manualSubtype = getObjectStore().createTest(
				getObjectStore().getTestType(StandardObjects.getSubtypeTestType()),
				"Manual Subtype");

		//create empty viral isolates

		try {
			DelimitedReader dr = new DelimitedReader(info, ",", "\"");
		
			FastaFile ff = new FastaFile(fasta);
			
			while(dr.readLine() != null){
				String patientId = dr.get("casoind");
				String sampleId = dr.get("Entrada");
				String subtype = dr.get("SUBTIPO");
				
				Patient p = getObjectStore().getPatient(null, patientId);
				if(p == null)
					p = getObjectStore().createPatient(null, patientId);
				
				NtSequence nt = ff.get(sampleId);
				if(nt != null){
					ViralIsolate vi = p.createViralIsolate();
					vi.setSampleId(sampleId);
					vi.setSampleDate(null);
					
					nt.setViralIsolate(vi);
					vi.getNtSequences().add(nt);
					
					if(subtype != null && subtype.length() != 0){
						TestResult tr = p.createTestResult(manualSubtype);
						tr.setNtSequence(nt);
						nt.getTestResults().add(tr);
						tr.setValue(subtype);
					}
				}
				else{
					System.err.println("sample id not in fasta file: "+ sampleId);
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	private boolean containsSequence(ViralIsolate vi, NtSequence nt) throws Exception{
		for(NtSequence n : vi.getNtSequences()){
			if(n.getLabel().equals(nt.getLabel())){
				if(!n.getNucleotides().equals(nt.getNucleotides()))
					System.err.println("conflict: same label, different nucleotides: "+ n.getLabel());
				return true;
			}
		}
		return false;
	}
	
	private int find(Sheet sh, int row, String value){
		for(int col = 0; col < sh.getColumns(); ++col){
			if(sh.getCell(col, row).getContents().trim().equals(value))
				return col;
		}
		return -1;
	}
	
	public ObjectStore getObjectStore(){
		return objstore;
	}
}
