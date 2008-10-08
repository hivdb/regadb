package net.sf.regadb.io.queries.egazMoniz;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.regadb.db.AaSequence;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.ViralIsolate;


public class PIFailureMutPrevalence {
	private static Set<String> mutations = new HashSet<String>();
	
	static {
		mutations.add("74|P");
		mutations.add("47|V");
		mutations.add("58|E");
		mutations.add("82|L"); 
		mutations.add("82|T");
		mutations.add("83|D");
		mutations.add("54|A");
		mutations.add("54|M");
		mutations.add("54|V");

		mutations.add("43|T");
		mutations.add("84|V");
		mutations.add("10|V");
		mutations.add("46|L");
		
		mutations.add("11|L");
		mutations.add("32|I");
		mutations.add("33|F");

		mutations.add("50|V");
		mutations.add("54|L");
		
		mutations.add("76|V");
		
		mutations.add("89|V");
		
		mutations.add("46|I");
		mutations.add("46|L");
		mutations.add("54|V");
		mutations.add("82|A");
		mutations.add("90|M");
		mutations.add("10|V");
		mutations.add("10|F");
		mutations.add("10|I");
	}
	
	public static void main(String [] args) {
		List<Patient> pts = Utils.getPatients();
		
		Map<String, Integer> mutPrevalence = new HashMap<String, Integer>();
		
		
		int seqCounter = 0;
		
		for(Patient p : pts) {
			if(Utils.therapiesContainClass(p.getTherapies(), "PI").size()>0) {
				TestResult mostRecentTherapyFailure = Utils.getMostRecentTherapyFailure(p);
				if(mostRecentTherapyFailure!=null) {
					ViralIsolate vi = Utils.getViralIsolate(p, mostRecentTherapyFailure.getTestDate(), 20);
					if(vi==null) {
						//if(p.getViralIsolates().size()!=0)
						//	System.err.println("No viral isolate for most recent Therapy Failure: " + p.getPatientId() + " vi's -> " + p.getViralIsolates().size());
					} else {
						AaSequence pro = Utils.getAaSequence(p, vi, "PRO");
						if(pro!=null) {
							Utils.calculatePrevalence(mutations, mutPrevalence, pro);
							seqCounter++;
						} else {
							System.err.println("no aaseqs for p " + p.getPatientId());
						}
					}
				}
			}
		}
		
		System.err.println("seqCounter: " + seqCounter);
		
		for(String mut : mutations) {
			System.err.println(mut + ": " + mutPrevalence.get(mut));
		}
	}
}
