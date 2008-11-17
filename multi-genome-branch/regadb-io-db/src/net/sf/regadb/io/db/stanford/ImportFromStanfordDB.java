package net.sf.regadb.io.db.stanford;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Therapy;
import net.sf.regadb.db.TherapyGeneric;
import net.sf.regadb.db.TherapyGenericId;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.io.db.util.ConsoleLogger;
import net.sf.regadb.io.db.util.Mappings;
import net.sf.regadb.io.db.util.Utils;
import net.sf.regadb.io.util.IOUtils;

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
		String dataPath = "/home/gbehey0/stanford";
		String mappingsPath = "/home/gbehey0/workspace/regadb-io-db/src/net/sf/regadb/io/db/stanford/mappings";
		ImportFromStanfordDB imp = new ImportFromStanfordDB(dataPath,mappingsPath);
		imp.run();
	}

	private HashMap<String,Patient> patients = new HashMap<String,Patient>();
	private List<DrugGeneric> regaDrugGenerics = Utils.prepareRegaDrugGenerics();

	private Mappings mappings;
	String dataPath;

	public ImportFromStanfordDB(String dataPath,String mappingsPath){
		mappings = Mappings.getInstance(mappingsPath);
		this.dataPath = dataPath;		
	}

	public void run(){
		importTherapyFile(new File(dataPath + File.separator + "B" + File.separator + "data" + File.separator + "PatientsRx.txt"));
		importTherapyFile(new File(dataPath + File.separator + "NONB" + File.separator + "data" + File.separator + "PatientsRx.txt"));

		//important to FIRST import PR and then RT because the sequences are concatenated
		importSequenceFile(new File(dataPath + File.separator + "B" + File.separator + "data" + File.separator + "BelgimumPR.txt"));
		importSequenceFile(new File(dataPath + File.separator + "B" + File.separator + "data" + File.separator + "BelgimumRT.txt"));

		importSequenceFile(new File(dataPath + File.separator + "NONB" + File.separator + "data" + File.separator + "BelgimumPR.txt"));
		importSequenceFile(new File(dataPath + File.separator + "NONB" + File.separator + "data" + File.separator + "BelgimumRT.txt"));

		IOUtils.exportPatientsXML(patients, dataPath + File.separatorChar + "patients_stanford.xml", ConsoleLogger.getInstance());
		IOUtils.exportNTXMLFromPatients(patients, dataPath + File.separatorChar + "viral_isolates_stanford.xml", ConsoleLogger.getInstance());

	}

	private void importTherapyFile(File file){
		try {
			Scanner s = new Scanner(file);

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
				drugs = s.nextLine();

				if(!therapyAlreadyAdded(patientId, startDate)){
					p = getPatient(patientId);
					t = new Therapy();
					t.setStartDate(startDate);
					t.setStopDate(stopDate);
					for(DrugGeneric dg : parseDrugGenerics(drugs)){
						t.getTherapyGenerics().add(new TherapyGeneric(new TherapyGenericId(t, dg), false, false));
					}
					if(t.getTherapyGenerics().size() == 0 && !drugs.trim().equalsIgnoreCase("none")){
						System.err.println("therapy generic(s) not added to therapy of patient: "+patientId+" "+drugs);
					}
					if(t.getTherapyGenerics().size() > 0){ //do not add therapy if drugs is equal to "none"
						p.addTherapy(t);
						patients.put(p.getPatientId(), p);
					}
				}
			}
			s.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void importSequenceFile(File file){
		try {
			Scanner s = new Scanner(file);

			String patientId;
			Date sampleDate;
			String sampleId;
			String nucleotides;
			ViralIsolate v = null;
			NtSequence n = null;

			while(s.hasNextLine()){
				patientId = s.next();
				sampleDate = Utils.parseEnglishAccessDate(s.next());
				sampleId = s.next();
				s.next(); //read subtype and discard it				
				nucleotides = s.nextLine().trim();

				v = getViralIsolate(patientId,sampleId, sampleDate);
				if(v.getNtSequences().size() == 0){
					//new sequence
					n = new NtSequence(v);
					n.setNucleotides(Utils.clearNucleotides(nucleotides));
					n.setSequenceDate(sampleDate);	
					n.setLabel("Sequence 1");
					v.getNtSequences().add(n);
				}else{
					//append RT sequence to PR sequence 
					n = v.getNtSequences().iterator().next();
					n.setNucleotides(n.getNucleotides()+Utils.clearNucleotides(nucleotides));
				}
			}
			s.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private Patient getPatient(String patientId){
		Patient p = patients.get(patientId);
		if(p == null){
			p = new Patient();
			p.setPatientId(patientId);
		}
		return p;
	}

	private ViralIsolate getViralIsolate(String patientId, String sampleId, Date sampleDate){
		Set<ViralIsolate> viralIsolates = getPatient(patientId).getViralIsolates();
		Comparator<Date> c = Utils.getDayComparator(); 
		for(ViralIsolate v : viralIsolates){
			if(c.compare(v.getSampleDate(), sampleDate) == 0){
				return v;
			}			
		}
		//no viral isolate found, so we create one
		ViralIsolate v = getPatient(patientId).createViralIsolate();
		v.setSampleId(sampleId);
		v.setSampleDate(sampleDate);
		return v;
	}

	private DrugGeneric getDrugGeneric(String genericId){
		DrugGeneric result = null;
		for(DrugGeneric dg : regaDrugGenerics){
			if(dg.getGenericId().equals(genericId))
				result = dg;
		}
		return result;
	}

	private boolean therapyAlreadyAdded(String patientId, Date therapyStartDate){
		Comparator<Date> c = Utils.getDayComparator();
		for(Therapy t : getPatient(patientId).getTherapies()){
			if(c.compare(t.getStartDate(), therapyStartDate) == 0){
				return true;
			}
		}
		return false;
	}

	public Set<DrugGeneric> parseDrugGenerics(String therapyGenerics){
		Set<DrugGeneric> result = new HashSet<DrugGeneric>();
		String[] drugs = therapyGenerics.split("\\+");
		DrugGeneric dg = null;
		for(String drug : drugs){
			drug = drug.trim();
			String mappedDrug = mappings.getMapping("drugs.mapping", drug, true);
			dg = (mappedDrug == null) ? getDrugGeneric(drug) : getDrugGeneric(mappedDrug);
			if(dg != null){
				result.add(dg);				
			}
		}
		return result;
	}
}
