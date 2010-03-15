package be.kuleuven.rega.research.gss;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import net.sf.hivgensim.queries.framework.utils.TherapyUtils;
import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.Therapy;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.io.importXML.ResistanceInterpretationParser;

public class FailedTherapy {
	
	private int numberOfPreviousTherapies;
	private Therapy therapy;
	private Patient patient;
	private ViralIsolate begin;
	private ViralIsolate end;

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public FailedTherapy(Patient patient, Therapy therapy, int n) {
		setNumberOfPreviousTherapies(n);
		setTherapy(therapy);
		setPatient(patient);
	}

	public int getNumberOfPreviousTherapies() {
		return numberOfPreviousTherapies;
	}

	public void setNumberOfPreviousTherapies(int numberOfPreviousTherapies) {
		this.numberOfPreviousTherapies = numberOfPreviousTherapies;
	}

	public Therapy getTherapy() {
		return therapy;
	}

	public void setTherapy(Therapy therapy) {
		this.therapy = therapy;
	}

	public List<DrugGeneric> getDrugs() {
		return TherapyUtils.getGenericDrugs(getTherapy());
	}
	
	public boolean boosted() {
		for(DrugGeneric dg : getDrugs()) {
			if(dg.getGenericId().endsWith("/r") || dg.getGenericId().equals("RTV")){
				return true;
			}
		}
		return false;
	}

	public Date getStartDate() {
		return getTherapy().getStartDate();
	}

	public Date getStopDate() {
		return getTherapy().getStopDate();
	}

	public Patient getPatient() {
		return patient;
	}

	public ViralIsolate getBeginViralIsolate() {
		return begin;
	}

	public void setBeginViralIsolate(ViralIsolate begin) {
		this.begin = begin;
	}

	public ViralIsolate getEndViralIsolate() {
		return end;
	}

	public void setEndViralIsolate(ViralIsolate end) {
		this.end = end;
	}

	private double getGSS(ViralIsolate vi, String algorithmDescription) {
		boolean boosted = boosted();
		final Map<String, Double> resistanceScores = new HashMap<String, Double>();
		ResistanceInterpretationParser rip = new ResistanceInterpretationParser() {

			public void completeScore(String drug, int level, double gss, String description, char sir, ArrayList<String> mutations, String remarks) {
				resistanceScores.put(drug, gss);
			}

		};
		
		for (TestResult tr : vi.getTestResults()) {
			if (tr.getTest().getDescription().equals(algorithmDescription)) {
				try {
					rip.parse(new InputSource(new ByteArrayInputStream(tr.getData())));
				} catch (SAXException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		double gss = 0;
		for (DrugGeneric dg : getDrugs()) {
			try{
				gss += resistanceScores.get(dg.getGenericId());
			}catch(NullPointerException e){
				if(dg.getGenericId().startsWith("APV")){
					gss += resistanceScores.get(dg.getGenericId().replace("APV","FPV"));
				} else if(boosted && !dg.getGenericId().endsWith("/r")) {
					try{
						gss += resistanceScores.get(dg.getGenericId()+"/r");
					}catch (NullPointerException exc) {
						System.err.println(dg.getGenericId()+" score not added");
					}
				} else {					
					System.err.println(dg.getGenericId()+" unweighted");
					double t = resistanceScores.get(dg.getGenericId()+"/r");
					if(t == 0)
						gss += 0;
					else if (t == 0.75)
						gss += 0.5;
					else if (t == 1.5)
						gss += 1;
					else
						throw new RuntimeException();
				}
			}
		}
		return gss;
	}
	
	public double getPreGSS(String algorithmDescription) {
		return getGSS(getBeginViralIsolate(),algorithmDescription);
	}
	
	public double getPostGSS(String algorithmDescription) {
		return getGSS(getEndViralIsolate(),algorithmDescription);
	}

}
