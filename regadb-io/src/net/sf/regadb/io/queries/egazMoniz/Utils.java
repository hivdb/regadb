package net.sf.regadb.io.queries.egazMoniz;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import net.sf.regadb.db.AaMutation;
import net.sf.regadb.db.AaSequence;
import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.Therapy;
import net.sf.regadb.db.TherapyCommercial;
import net.sf.regadb.db.TherapyGeneric;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.db.login.DisabledUserException;
import net.sf.regadb.db.login.WrongPasswordException;
import net.sf.regadb.db.login.WrongUidException;
import net.sf.regadb.db.session.Login;

public class Utils {
	public static List<Patient> getPatients() {
	    Login login = null;
	    try
	    {
	        login = Login.authenticate("admin", "admin");
	    }
	    catch (WrongUidException e)
	    {
	        e.printStackTrace();
	    }
	    catch (WrongPasswordException e)
	    {
	        e.printStackTrace();
	    } 
	    catch (DisabledUserException e) 
	    {
	        e.printStackTrace();
	    }
	    
	    Transaction t = login.createTransaction();
	    List<Patient> l = t.getPatients();
	    
	    return l;
	}
	
	public static boolean therapiesContainClass(Set<Therapy> ts, String drugClass) {
		for(Therapy t : ts) {
			for(TherapyGeneric tg : t.getTherapyGenerics()) {
				if(tg.getId().getDrugGeneric().getDrugClass().getClassId().equals(drugClass)) {
					return true;
				}
			}
			for(TherapyCommercial tc : t.getTherapyCommercials()) {
				for(DrugGeneric dg : tc.getId().getDrugCommercial().getDrugGenerics()) {
					if(dg.getDrugClass().getClassId().equals(drugClass)) {
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	public static ViralIsolate getViralIsolate(Patient p, Date d) {
		for(ViralIsolate vi : p.getViralIsolates()) {
			if(vi.getSampleDate().equals(d)) {
				return vi;
			}
		}
		
		return null;
	}
	
	public static AaSequence getAaSequence(Patient p, ViralIsolate vi, String protein) {
		for(NtSequence ntseq : vi.getNtSequences()) {
			for(AaSequence aaseq : ntseq.getAaSequences()) {
				if(aaseq.getProtein().getAbbreviation().equals(protein)) {
					return aaseq;
				}
			}
		}
		
		return null;
	}
	
	public static TestResult getMostRecentTherapyFailure(Patient p) {
		TestResult mostRecentTherapyFailure = null;
		
		for(TestResult tr : p.getTestResults()) {
			if(tr.getTest().getTestType().getDescription().equals("Therapy Failure")) {
				if(mostRecentTherapyFailure==null) {
					mostRecentTherapyFailure = tr;
				} else if(tr.getTestDate().after(mostRecentTherapyFailure.getTestDate())) {
					mostRecentTherapyFailure = tr;
				}
			}
		}
		
		return mostRecentTherapyFailure;
	}
	
	public static void calculatePrevalence(Set<String> interestingMutations, Map<String, Integer> prevalenceMap, AaSequence aaseq) {
		for(AaMutation aamut : aaseq.getAaMutations()) {
			for(String interestingMut : interestingMutations) {
				StringTokenizer st = new StringTokenizer(interestingMut, "|");
				int iPos = Integer.parseInt(st.nextToken());
				String iAa = st.nextToken();

				if(aamut.getAaMutation()!=null && aamut.getAaMutation().contains(iAa) && aamut.getId().getMutationPosition()==iPos) {
					Integer prevalence = prevalenceMap.get(interestingMut);
					int prevalenceToPut;
					if(prevalence==null) {
						prevalenceToPut = 1;
					} else {
						prevalenceToPut = prevalence + 1;
					}
					prevalenceMap.put(interestingMut, prevalenceToPut);
				}
			}
		}
	}
}
