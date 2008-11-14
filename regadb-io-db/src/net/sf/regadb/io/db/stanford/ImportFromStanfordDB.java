package net.sf.regadb.io.db.stanford;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import net.sf.regadb.csv.Table;
import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Therapy;
import net.sf.regadb.db.TherapyGeneric;
import net.sf.regadb.db.TherapyGenericId;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.io.db.util.Mappings;
import net.sf.regadb.io.db.util.Utils;

public class ImportFromStanfordDB {

	//PatientsRx
	//patient_id	start_date	stop_date	drugs
	//12278			1998-11-03	1999-06-01	AZT+ DDI

	//RT
	//patient_id	sample_date	sample_id	subtype	sequence
	//12278			1999-06-01	29013		G		CCAATT...

	//PR
	//patient_id	sample_date	sample_id	subtype	sequence
	//12278			1999-06-01	29014		G		CCTCAAA...



	public static void main(String[] args){
		System.setProperty("http.proxyHost", "www-proxy");
		System.setProperty("http.proxyPort", "3128");
		String dataPath = "/home/gbehey0/stanford/B/data";
		String mappingsPath = "/home/gbehey0/workspace/regadb-io-db/src/net/sf/regadb/io/db/stanford/mappings";
		ImportFromStanfordDB imp = new ImportFromStanfordDB(dataPath,mappingsPath);
		imp.run();
	}
	
	private HashMap<String,Patient> patients = new HashMap<String,Patient>();
	private List<DrugGeneric> regaDrugGenerics = Utils.prepareRegaDrugGenerics();
	
	private Mappings mappings;
	private File therapies;
	private File samplePR;
	private File sampleRT;
	
	public ImportFromStanfordDB(String dataPath,String mappingsPath){
		mappings = Mappings.getInstance(mappingsPath);
		therapies = new File(dataPath + File.separator + "PatientsRx.txt");
		samplePR = new File(dataPath + File.separator + "BelgimumPR.txt");
		sampleRT = new File(dataPath + File.separator + "BelgimumRT.txt");
	}
	
	public void run(){
		try {
			Scanner s = new Scanner(therapies);

			String patientId;
			Date startDate;
			Date stopDate;
			String drugs;
			Patient p = null;
			Therapy t = null;

			while(s.hasNextLine()){
				patientId = s.next();
				startDate = Utils.parseEnglishAccessDate(s.next());
				stopDate = Utils.parseEnglishAccessDate(s.next());
				drugs = s.next();
				
				p = getPatient(patientId);				
				t = new Therapy();
				t.setStartDate(startDate);
				t.setStopDate(stopDate);
				for(DrugGeneric dg : parseDrugGenerics(drugs)){
					System.out.println(patientId + " " + dg.getGenericId());
					t.getTherapyGenerics().add(new TherapyGeneric( new TherapyGenericId(t, dg), false, false) );
				}
				p.addTherapy(t);
				patients.put(p.getPatientId(), p);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public Patient getPatient(String patientId){
		Patient p = patients.get(patientId);
		if(p == null){
			p = new Patient();
			p.setPatientId(patientId);
		}
		return p;
	}
	
	public Set<DrugGeneric> parseDrugGenerics(String therapyGenerics){
		Set<DrugGeneric> result = new HashSet<DrugGeneric>();
		String[] drugs = therapyGenerics.split("\\+");
		DrugGeneric dg = null;
		for(String drug : drugs){
			String mappedDrug = mappings.getMapping("drugs.mapping", drug, false);
			dg = (mappedDrug == null) ? getDrugGeneric(drug) : getDrugGeneric(mappedDrug);
			if(dg != null){
				result.add(dg);				
			}
		}
		return result;
	}
	
	public DrugGeneric getDrugGeneric(String genericId){
		DrugGeneric result = null;
		for(DrugGeneric dg : regaDrugGenerics){
			if(dg.getGenericId().equals(genericId))
				result = dg;
		}
		return result;
	}


	
//	private static String dataPath = "/home/dluypa0/stanford_import/nonB_data";
//	private static int ID = 0, STARTDATE = 1, STOPDATE = 2, DRUGS = 3;
//	private static int SAMPLEDATE = 1, SAMPLEID = 2, SEQUENCE = 4;
//
//	private static Table drugTable_;
//	
//	private static HashMap<String, Patient> patientMap;
//	private static HashMap<String,ViralIsolate> viralisolates = new HashMap<String,ViralIsolate> ();



	//	public static void main( String[]args ) {
	//		if ( args.length >= 1 ) dataPath = args[0];
	//		
	//		System.setProperty("http.proxyHost", "www-proxy");
	//        System.setProperty("http.proxyPort", "3128");
	//        regaDrugGenerics = Utils.prepareRegaDrugGenerics();
	//        drugTable_ = Utils.readTable(mapPath + File.separatorChar + "drugs.mapping");
	//        patientMap = new HashMap<String, Patient>();
	//        
	//		try {
	//			BufferedReader br = new BufferedReader(new FileReader(new File(dataPath + File.separatorChar + "PatientsRx.txt")));
	//			
	//			while ( br.ready() ) {
	//				ArrayList<String> lineElements = token2array(br.readLine(), "\t");
	//				Patient p = findPatient(lineElements.get(ID));
	//				fixTherapy(p, lineElements);
	//				patientMap.put(lineElements.get(ID), p);
	//			}
	//			
	//			fixSequences("BelgimumRT.txt");
	//			fixSequences("BelgimumPR.txt");
	//		} catch ( FileNotFoundException fnfe ) {
	//			System.err.println( fnfe.getLocalizedMessage() );
	//		} catch ( IOException ioe ) {
	//			System.err.println( ioe.getLocalizedMessage() );
	//		}
	//		
	//    	IOUtils.exportPatientsXML(patientMap, dataPath + File.separatorChar + "patients_stanford.xml", ConsoleLogger.getInstance());
	//    	IOUtils.exportNTXML(viralisolates, dataPath + File.separatorChar + "viralisolates.xml", ConsoleLogger.getInstance());
	//    	
	//    	System.out.println("done");
	//	}

//	public static void fixTherapy(Patient p, ArrayList<String> lineElements) {
//		if ( lineElements.size() == 4 ) {
//			Set<String> drugs = new HashSet<String>();
//			Therapy t = null;
//
//			for ( String drug : token2array(lineElements.get(DRUGS), "+") ) {
//				if ( drug.toUpperCase().equals("NONE") ) continue;
//
//				for(int i=0; i<drugTable_.numRows(); i++) {
//					if ( drug.equals(drugTable_.valueAt(0, i)) ) {
//						drug = drugTable_.valueAt(1, i);
//					}
//				}
//
//				DrugGeneric selectedDg = null;
//				for( DrugGeneric dg : regaDrugGenerics ) {
//					if( drug.equals( dg.getGenericId() ) ) {
//						selectedDg = dg;
//						break;
//					}
//				}
//
//				if ( selectedDg != null ) {
//					if ( t == null ) {
//						t = p.createTherapy(getDate(lineElements.get(STARTDATE)));
//						t.setStopDate(getDate(lineElements.get(STOPDATE)));
//					}
//
//					if ( !drugs.contains(drug) ) {
//						drugs.add(drug);
//						t.getTherapyGenerics().add(new TherapyGeneric( new TherapyGenericId(t, selectedDg), false, false) );
//					}
//				} else {
//					ConsoleLogger.getInstance().logWarning("No valid generic drug: " + drug);
//				}
//			}
//		}
//	}
//
//	public static void fixSequences(String fileName) throws IOException, FileNotFoundException {
//		BufferedReader br = new BufferedReader(new FileReader(new File(dataPath + File.separatorChar + fileName)));
//
//		while ( br.ready() ) {
//			String info = br.readLine();
//			ArrayList<String> elements = token2array(info, "\t");
//			if ( elements.size() != 5 ) {
//				System.err.println("Line not properly format: " + info);
//				continue;
//			}
//
//			Patient p = findPatient(elements.get(ID));
//
//			ViralIsolate vi = null;
//			Date d = getDate(elements.get(SAMPLEDATE));
//
//			for(ViralIsolate vii : p.getViralIsolates() ) {
//				if ( vii.getSampleDate().equals(d) ) {
//					vi = vii;
//					break;
//				}
//			}
//
//			if ( vi == null ) {
//				vi = new ViralIsolate();
//				vi.setSampleDate(d);
//				vi.setSampleId(elements.get(SAMPLEID));
//
//				p.addViralIsolate(vi);
//				viralisolates.put(elements.get(SAMPLEID), vi);
//			}
//
//			NtSequence nt = new NtSequence();
//			nt.setNucleotides( Utils.clearNucleotides( elements.get(SEQUENCE) ) );
//			vi.getNtSequences().add(nt);
//		}
//	}
//
//	public static Patient findPatient(String ID) {
//		return findPatient(ID, true);
//	}
//	public static Patient findPatient(String ID, boolean create) {
//		Patient p = null;
//		for(Patient pi : patientMap.values() ) {
//			if ( pi.getPatientId().equals(ID) ) {
//				p = pi;
//				break;
//			}
//		}
//		if ( create && p == null ) {
//			p = new Patient();
//			p.setPatientId(ID);
//		}
//		return p;
//	}
//
//	public static Date getDate(String date) {
//		Date d = null;
//		try {
//			d = stanford.parse(date);
//		} catch ( ParseException pe ) {
//			System.err.println("Couldn't parse date: " + date);
//		}
//		return d;
//	}
//
//	public static ArrayList<String> token2array(String line) {
//		return token2array(line, " ");
//	}
//	public static ArrayList<String> token2array(String line, String separator) {
//		ArrayList<String> elements = new ArrayList<String>();
//		StringTokenizer tokenizer = new StringTokenizer(line, separator);
//
//		while ( tokenizer.hasMoreTokens() ) {
//			String t = tokenizer.nextToken().trim();
//			elements.add(t);
//		}
//
//		return elements;
//	}
}
