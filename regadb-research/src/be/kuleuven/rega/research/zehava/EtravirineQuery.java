package be.kuleuven.rega.research.zehava;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import net.sf.hivgensim.queries.framework.IQuery;
import net.sf.hivgensim.queries.framework.Query;
import net.sf.hivgensim.queries.framework.QueryInput;
import net.sf.hivgensim.queries.framework.snapshot.FromSnapshot;
import net.sf.hivgensim.queries.framework.utils.PatientUtils;
import net.sf.hivgensim.queries.framework.utils.TherapyUtils;
import net.sf.hivgensim.queries.framework.utils.ViralIsolateUtils;
import net.sf.hivgensim.queries.input.FromDatabase;
import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Therapy;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.io.importXML.ResistanceInterpretationParser;
import net.sf.regadb.util.settings.RegaDBSettings;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class EtravirineQuery extends Query<Patient,EtravirineRecord> {

	private String rega8description = "REGA v8.0.2";
	private String hivdbdescription = "HIVDB 6.0.5";
	private HashMap<String,String> currentResistanceRecord = new HashMap<String, String>(); 
	private ResistanceInterpretationParser rip;	
	
	public EtravirineQuery(IQuery<EtravirineRecord> nextQuery) {
		super(nextQuery);
		rip = new ResistanceInterpretationParser() {
			
			@Override
			public void completeScore(String drug, int level, double gss, String description, char sir, ArrayList<String> mutations, String remarks) {
				currentResistanceRecord.clear();
				currentResistanceRecord.put("gss", ""+gss);
				currentResistanceRecord.put("mutations", mutations.toString().replace("[","").replace("]","").replace(",", ""));
				currentResistanceRecord.put("remarks", remarks);
			}
		};
	}
	
	public void process(Patient input) {
		for(ViralIsolate vi : input.getViralIsolates()){
			boolean naive = true;
			boolean NNRTI = false;
			boolean NVP = false;
			boolean EFV = false;
			Date sampleDate = vi.getSampleDate();
			if(sampleDate == null){
				continue;
			}
			for(Therapy t : TherapyUtils.sortTherapiesByStartDate(input.getTherapies())){
				if(t.getStartDate().before(sampleDate)){
					naive = false;
					for(DrugGeneric dg : TherapyUtils.getGenericDrugs(t)){
						if(dg.getDrugClass().getClassId().equals("NNRTI")){
							NNRTI = true;
						}
						if(dg.getGenericId().equals("NVP")){
							NVP = true;
						}
						if(dg.getGenericId().equals("EFV")){
							EFV = true;
						}
					}
				} else {
					break;
				}
			}
			if(NNRTI && !EFV && !NVP){
				continue;
			}
			EtravirineRecord etvr = new EtravirineRecord();
			etvr.setPatientId(input.getPatientId());
			etvr.setNaive(naive);
			etvr.setNnrti(NNRTI);
			etvr.setNvp(NVP);
			etvr.setEfv(EFV);
			etvr.setSampleDate(vi.getSampleDate());
			try {
				rip.parse(new InputSource(new ByteArrayInputStream(ViralIsolateUtils.fullResistanceRecord(vi, rega8description, "ETV"))));
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			etvr.setRegaGss(currentResistanceRecord.get("gss"));
			etvr.setRegaMutations(currentResistanceRecord.get("mutations"));
			etvr.setRegaRemarks(currentResistanceRecord.get("remarks"));
			try {
				rip.parse(new InputSource(new ByteArrayInputStream(ViralIsolateUtils.fullResistanceRecord(vi, hivdbdescription, "ETV"))));
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			etvr.setHivdbGss(currentResistanceRecord.get("gss"));
			etvr.setHivdbMutations(currentResistanceRecord.get("mutations"));
			etvr.setHivdbRemarks(currentResistanceRecord.get("remarks"));
			String country = PatientUtils.getPatientAttributeValue(input, "Infection place");
			if(country == null)
				country = PatientUtils.getPatientAttributeValue(input, "Country of infection");
			if(country == null)
				country = PatientUtils.getPatientAttributeValue(input, "Country of origin");
			if(country == null)
				country = "";
			etvr.setCountry(country);
			etvr.setDataset(input.getDatasets().iterator().next().getDescription());
			etvr.setSubtype(ViralIsolateUtils.extractSubtype(vi));
			etvr.setGender(PatientUtils.getPatientAttributeValue(input, "Gender"));
			etvr.setTransmissionGroup(PatientUtils.getPatientAttributeValue(input, "Transmission group"));
			etvr.setConcatenatedSequence(ViralIsolateUtils.getConcatenatedNucleotideSequence(vi));
			getNextQuery().process(etvr);
		}
	}
	
	public static void main(String[] args) {
		RegaDBSettings.createInstance();
		QueryInput qi = new FromDatabase("admin", "admin", new EtravirineQuery(new EtravirineSelection(new EtravirineOutput(false))));		
		qi.run();
		qi = new FromSnapshot(new File("/home/gbehey0/temp/20100315-snapshot"), new DatasetFilter(new EtravirineQuery(new EtravirineSelection(new EtravirineOutput(true)))));		
		qi.run();
	}

}
