package be.kuleuven.rega.research.zehava;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import net.sf.hivgensim.queries.framework.IQuery;
import net.sf.hivgensim.queries.framework.QueryInput;
import net.sf.hivgensim.queries.framework.utils.TherapyUtils;
import net.sf.hivgensim.queries.framework.utils.ViralIsolateUtils;
import net.sf.hivgensim.queries.input.FromXml;
import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Therapy;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.io.importXML.ResistanceInterpretationParser;
import net.sf.regadb.util.settings.RegaDBSettings;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class EtravirineQuery implements IQuery<Patient> {

	private String rega8description = "REGA v8.0.2";
	private HashMap<String,String> currentResistanceRecord = new HashMap<String, String>(); 
	private ResistanceInterpretationParser rip;
	
	public EtravirineQuery() {
		rip = new ResistanceInterpretationParser() {
			
			@Override
			public void completeScore(String drug, int level, double gss, String description, char sir, ArrayList<String> mutations, String remarks) {
				currentResistanceRecord.clear();
				currentResistanceRecord.put("level", ""+level);
				currentResistanceRecord.put("mutations", mutations.toString());
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
			System.out.print(input.getPatientId()+",");
			System.out.print(naive+",");
			System.out.print(NVP+",");
			System.out.print(EFV+",");
			try {
				rip.parse(new InputSource(new ByteArrayInputStream(ViralIsolateUtils.fullResistanceRecord(vi, rega8description, "ETV"))));
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.print(currentResistanceRecord.get("level")+",");
			System.out.print(currentResistanceRecord.get("mutations")+",");
			System.out.print(currentResistanceRecord.get("remarks"));
			System.out.println();
		}
	}
	
	public void close() {
			
	}
	
	public static void main(String[] args) {
		RegaDBSettings.createInstance();
		QueryInput qi = new FromXml(new File("/home/gbehey0/telaviv/csv/patients.xml"), "admin", "admin", new EtravirineQuery());
		qi.run();
	}

}
