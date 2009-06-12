package net.sf.regadb.io.db.cuba;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
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
import net.sf.regadb.io.db.util.ConsoleLogger;
import net.sf.regadb.io.db.util.DelimitedReader;
import net.sf.regadb.io.db.util.FastaFile;
import net.sf.regadb.io.db.util.Mappings;
import net.sf.regadb.io.db.util.Parser;
import net.sf.regadb.io.db.util.Utils;
import net.sf.regadb.io.db.util.mapping.OfflineObjectStore;
import net.sf.regadb.io.util.IOUtils;
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
		PositionalArgument xmlDir = as.addPositionalArgument("xml-output-directory", true);
		
		if(!as.handle(args))
			return;
		
		ParseAll pa = new ParseAll();
		pa.run(new File(csvDir.getValue()),new File(mapDir.getValue()),new File(xmlDir.getValue()));
	}
	
	public ParseAll(){
		setDateFormat(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"));
		setLogger(new ConsoleLogger());
	}
	
	public void run(File csvDir, File mapDir, File xmlDir){
		if(!xmlDir.canWrite()){
			System.err.println("unable to write to "+ xmlDir.getAbsolutePath());
			return;
		}
		
		File cd4File = new File(csvDir.getAbsolutePath() + File.separatorChar + "CD4_COUNTS.csv");
		File drugsFile = new File(csvDir.getAbsolutePath() + File.separatorChar + "COMB_MED.csv");
		File pvihsFile = new File(csvDir.getAbsolutePath() + File.separatorChar + "PVIHS.csv");
		File therapyFile = new File(csvDir.getAbsolutePath() + File.separatorChar + "TREATMENTS.csv");
		File vlFile = new File(csvDir.getAbsolutePath() + File.separatorChar + "VIRAL_LOAD.csv");
		File seqsDir = new File(csvDir.getAbsolutePath() + File.separatorChar + "seqs");
		
		if(!(	   check(cd4File) 
				&& check(drugsFile) 
				&& check(pvihsFile) 
				&& check(therapyFile) 
				&& check(vlFile)
				&& check(seqsDir)))
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
		
		System.out.println("parse sequences");
		parseSequences(seqsDir);
		
		IOUtils.exportPatientsXML(getObjectStore().getPatients(), xmlDir.getAbsolutePath() + File.separatorChar +"patients.xml", ConsoleLogger.getInstance());
		IOUtils.exportNTXMLFromPatients(getObjectStore().getPatients(), xmlDir.getAbsolutePath() + File.separatorChar +"viral-isolates.xml", ConsoleLogger.getInstance());
		System.out.println("done");
	}
	
	public void parsePatients(File patFile){
		try {
			DelimitedReader dr = new DelimitedReader(patFile,separator,delimiter);

			Attribute municipart = getObjectStore().createAttribute(StandardObjects.getDemographicsAttributeGroup(),
																	StandardObjects.getNominalValueType(),
																	"municipart");
			Attribute provpart = getObjectStore().createAttribute(	StandardObjects.getDemographicsAttributeGroup(),
																	StandardObjects.getNominalValueType(),
																	"provpart");

			while(dr.readLine() != null){
				Patient p = getObjectStore().createPatient(null, dr.get("casoind"));
				AttributeNominalValue anv;
				String s;
				
				s = dr.get("sexo");
				p.addPatientAttributeValue(Utils.createPatientAttributeValue(StandardObjects.getGenderAttribute(), s.equals("F") ? "female":"male"));
				
				s = dr.get("municipart");
				if(s != null && s.length() > 0){
					anv = getObjectStore().getAttributeNominalValue(municipart, s);
					if(anv == null)
						anv = getObjectStore().createAttributeNominalValue(municipart, s);
					p.addPatientAttributeValue(Utils.createPatientAttributeValue(anv));
				}
				
				s = dr.get("provpart");
				if(s != null && s.length() > 0){
					anv = getObjectStore().getAttributeNominalValue(provpart, s);
					if(anv == null)
						anv = getObjectStore().createAttributeNominalValue(provpart, dr.get("provpart"));
					p.addPatientAttributeValue(Utils.createPatientAttributeValue(anv));
				}
				
				s = dr.get("fechadiag");
				if(s != null && s.length() > 0){
					Date d = getDate(s);
					if(d != null)
						Utils.setBirthDate(p, d);
					d = getDate(dr.get("fechafall"));
					if(d != null)
						Utils.setDeathDate(p, d);
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
				addTestResult(p, cd4p, d, dr.get("cd4proc"));
			}
			
			dr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void addTestResult(Patient p, Test t, Date d, String value){
		if(value != null && value.length() > 0){
			TestResult tr = p.createTestResult(t);
			tr.setValue(value);
			tr.setTestDate(d);
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
			
			Test vl = getObjectStore().getTest(	StandardObjects.getGenericHiv1ViralLoadTest().getDescription(),
												StandardObjects.getHiv1ViralLoadTestType().getDescription(),
												StandardObjects.getHiv1Genome().getOrganismName());

			Test vll = getObjectStore().getTest(StandardObjects.getGenericHiv1ViralLoadLog10Test().getDescription(),
												StandardObjects.getHiv1ViralLoadLog10TestType().getDescription(),
												StandardObjects.getHiv1Genome().getOrganismName());

			while(dr.readLine() != null){
				Patient p = getObjectStore().getPatient(null, dr.get("casoind"));
				
				Date d = getDate(dr.get("fecha"));
				
				addTestResult(p, vl, d, dr.get("copias"));
				addTestResult(p, vll, d, dr.get("logar"));
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
	
	private void parseSequences(File seqsDir){
		File info = new File(seqsDir.getAbsolutePath() + File.separatorChar +"Fecha de Muestra.xls");
		Map<String, ViralIsolate> vis = new HashMap<String, ViralIsolate>();
		Map<String, Patient> visps = new HashMap<String, Patient>();

		//create empty viral isolates
		Workbook wb;
		try {
			wb = Workbook.getWorkbook(info);
			Sheet sh = wb.getSheet(0);
			
			int iSampleId = find(sh,0,"NUMERO");
			int iPatientId = find(sh,0,"CIND");
			int iSampleDate = find(sh,0,"FechaMuestra");
			
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy");
			for(int i=1; i<sh.getRows(); ++i){
				Patient p = getObjectStore().getPatient(null, sh.getCell(iPatientId,i).getContents().trim());
				if(p == null)
					continue;
				
				Date d = null;
				try {
					d = sdf.parse(sh.getCell(iSampleDate,i).getContents().trim());
				} catch (ParseException e) {
					e.printStackTrace();
				}
				if(d == null)
					continue;
				
				String sSampleId = sh.getCell(iSampleId,i).getContents().trim();
				
				ViralIsolate vi = new ViralIsolate();
				vi.setSampleId(sSampleId);
				vi.setSampleDate(d);
				
				vis.put(sSampleId,vi);
				visps.put(sSampleId,p);
			}

		} catch (BiffException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//parse the sequence files
		for(File fasta : seqsDir.listFiles()){
			if(!fasta.getName().endsWith(".fas"))
				continue;
			
			try {
				FastaFile ff = new FastaFile(fasta);
				
				//try to connect the sequences with a viral isolate
				for(NtSequence nt : ff.values()){
					for(String sampleid : vis.keySet()){
						if(nt.getLabel().toLowerCase().contains(sampleid.toLowerCase())){
							ViralIsolate vi = vis.get(sampleid);
							
							if(!containsSequence(vi,nt)){
								vi.getNtSequences().add(nt);
								nt.setViralIsolate(vi);
							}
							break;
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		//add the not-empty viral isolates to the patients
		for(Map.Entry<String, ViralIsolate> me : vis.entrySet()){
			if(me.getValue().getNtSequences().size() > 0){
				visps.get(me.getKey()).addViralIsolate(me.getValue());
			}
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
	
	public OfflineObjectStore getObjectStore(){
		return objstore;
	}
}
