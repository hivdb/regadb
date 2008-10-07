package net.sf.regadb.io.queries.egazMoniz;

import java.util.List;
import java.util.Map;

import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.io.util.StandardObjects;

public class FailureSIR {
	public static void main(String [] args) {
		List<Patient> pts = Utils.getPatients();
		
		String drugClass = args[0];
		
		Map<String, Integer> headers = Utils.getSIRHeaders(drugClass);
		String[] row = new String[headers.size()];
		
		System.out.print("PatientID"+","+"SampleID"+","+"SampleDate"+",");
		for(Map.Entry<String, Integer> e : headers.entrySet()) {
			row[e.getValue()] = e.getKey();
		}
		for(int i = 0; i<row.length; i++) {
			System.out.print(row[i]);
			if(i<row.length-1) {
				System.out.print(",");
			}
		}
		System.out.println();
		
		for(Patient p : pts) {
			if(Utils.therapiesContainClass(p.getTherapies(), drugClass)) {
				TestResult mostRecentTherapyFailure = Utils.getMostRecentTherapyFailure(p);
				if(mostRecentTherapyFailure!=null) {
					ViralIsolate vi = Utils.getViralIsolate(p, mostRecentTherapyFailure.getTestDate(), 20);
					if(vi==null) {
						//if(p.getViralIsolates().size()!=0)
							//System.err.println("No viral isolate for most recent Therapy Failure: " + p.getPatientId() + " vi's -> " + p.getViralIsolates().size());
					} else {
						for(int i = 0; i<row.length; i++) {
							row[i] = "NA";
						}
						
						for(TestResult tr : vi.getTestResults()) {
							if(tr.getTest().getTestType().getDescription().equals(StandardObjects.getGssId())) {
								DrugGeneric dg = tr.getDrugGeneric();
								if(dg.getDrugClass().getClassId().equals(drugClass)) {
									Integer pos = headers.get(Utils.getFixedGenericId(tr) + " (" + tr.getTest().getDescription() + ")");
									if(pos!=null)
										row[pos] = Utils.getSIR(tr.getValue());
									pos = headers.get(Utils.getFixedGenericId(tr) +"/r"+ " (" + tr.getTest().getDescription() + ")");
									if(pos!=null)	
										row[pos] = Utils.getSIR(tr.getValue());
								}
							}
						}
						
						System.out.print(p.getPatientId()+","+vi.getSampleId()+","+vi.getSampleDate()+",");
						for(int i = 0; i<row.length; i++) {
							System.out.print(row[i]);
							if(i<row.length-1) {
								System.out.print(",");
							}
						}
						System.out.println();
					}
				}
			}
		}
	}
}
