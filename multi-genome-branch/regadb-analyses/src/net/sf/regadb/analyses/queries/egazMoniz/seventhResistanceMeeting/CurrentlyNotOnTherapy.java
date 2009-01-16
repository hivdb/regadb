package net.sf.regadb.analyses.queries.egazMoniz.seventhResistanceMeeting;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import net.sf.regadb.analyses.queries.egazMoniz.Utils;
import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.PatientAttributeValue;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.Therapy;
import net.sf.regadb.db.TherapyCommercial;
import net.sf.regadb.db.TherapyGeneric;
import net.sf.regadb.db.ViralIsolate;

public class CurrentlyNotOnTherapy {
	public static void main(String [] args) {
		CurrentlyNotOnTherapy cnot = new CurrentlyNotOnTherapy();
		cnot.run();
	}
	
	public void run() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
		Date date1800 = null;
		try {
			date1800 = sdf.parse("1800.01.01");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		System.out.println("patient id, last regimen, stop date, # SI drugs, sex, birtdate, last vl date, last vl");
		
		List<Patient> patients = Utils.getPatients();
		for(Patient p : patients) {
			if(p.getTherapies().size()>0) {
				Therapy t = getLastTherapy(p);
				if(t.getStartDate().after(date1800) && t.getStopDate()!=null && t.getStopDate().before(new Date())) {
					ViralIsolate vi = getLastViralIsolateAfter(p, t.getStopDate());
					if(vi!=null) {
					System.out.print(p.getPatientId() + ", ");
					System.out.print(getRegimen(t) + ", ");
					System.out.print(t.getStopDate() + ", ");
					
					int counter = 0;
					for(TestResult tr : vi.getTestResults()) {
						if(tr.getTest().getDescription().equals("REGA v7.1")) {
							double gss = Double.parseDouble(tr.getValue());
							if(gss<1.0) {
								counter++;
							}
						}
					}
					
					System.out.print(counter + ", ");
					System.out.print(p.getBirthDate() + ", ");
					System.out.print(getPAV(p, "Gender") + ", ");
					
					TestResult vl = getLastTestAfter(p, t.getStopDate(), "Viral Load");
					if(vl!=null) {
						System.out.print(vl.getTestDate() + ", ");
						System.out.print(vl.getValue() + " ");
					} else {
						System.out.print(", ");
						System.out.print(" ");
					}
					
					System.out.println();
					} else {
						//System.err.println("no vi for patient " + p.getPatientId());
					}
				}
			}
		}
	}
	
	public TestResult getLastTestAfter(Patient p, Date d, String testTypeName) {
		TestResult[] results = new TestResult[p.getTestResults().size()];
		p.getTestResults().toArray(results);
		Arrays.sort(results, new Comparator<TestResult>(){
			public int compare(TestResult tr1, TestResult tr2) {
				return tr1.getTestDate().compareTo(tr2.getTestDate());
			}
		});
		
		TestResult last_tr = null;
		
		for(TestResult tr : results) {
			if(tr.getTest().getTestType().getDescription().startsWith(testTypeName)) {
				last_tr = tr;
			}
		}
		
		if(last_tr!=null && last_tr.getTestDate().after(d))
			return last_tr;
		else 
			return null;
	}
	
	public String getPAV(Patient p, String attributeName) {
		for(PatientAttributeValue pav : p.getPatientAttributeValues()) {
			if(pav.getAttribute().getName().equals(attributeName)) {
				if(pav.getValue()==null) {
					return pav.getAttributeNominalValue().getValue();
				} else {
					return pav.getValue();
				}
			}
		}
		
		return null;
	}
	
	public ViralIsolate getLastViralIsolateAfter(Patient p, Date d) {
		ViralIsolate[] viralIsolates = new ViralIsolate[p.getViralIsolates().size()];
		p.getViralIsolates().toArray(viralIsolates);
		Arrays.sort(viralIsolates, new Comparator<ViralIsolate>(){
			public int compare(ViralIsolate vi1, ViralIsolate vi2) {
				return vi1.getSampleDate().compareTo(vi2.getSampleDate());
			}
		});

		if(viralIsolates.length==0)
			return null;
		
		ViralIsolate vi = viralIsolates[viralIsolates.length-1];
		              
		if(vi.getSampleDate().equals(d) || vi.getSampleDate().after(d))
			return vi;
		else
			return null;
	}
	
	public String getRegimen(Therapy t) {
		String toReturn = "";
		for(TherapyCommercial tc : t.getTherapyCommercials()) {
			for(DrugGeneric dg : tc.getId().getDrugCommercial().getDrugGenerics()) {
				toReturn += dg.getGenericId() + " ";
			}
		}
		
		for(TherapyGeneric tg : t.getTherapyGenerics()) {
			toReturn += tg.getId().getDrugGeneric().getGenericId() + " ";
		}
		
		return toReturn;
	}
	
	public Therapy getLastTherapy(Patient p) {
		List<Therapy> therapies = new ArrayList<Therapy>();
		
		for(Therapy t : p.getTherapies()) {
			therapies.add(t);
		}
		
		Collections.sort(therapies, new Comparator<Therapy>(){
			public int compare(Therapy t1, Therapy t2) {
				return t1.getStartDate().compareTo(t2.getStartDate());
			}
		});
		
		return therapies.get(therapies.size()-1);
	}
}
