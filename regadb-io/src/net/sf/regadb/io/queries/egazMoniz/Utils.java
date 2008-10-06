package net.sf.regadb.io.queries.egazMoniz;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import net.sf.regadb.db.AaMutation;
import net.sf.regadb.db.AaSequence;
import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Test;
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
	private static Transaction t;
	
	static {
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
	    
	    t = login.createTransaction();
	}
	
	public static List<Patient> getPatients() {
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
	
	public static Map<String, Integer> getSIRHeaders(String drugClass) {
		Map<String, Integer> header = new HashMap<String, Integer>();
		
		List<Test> gssTests = t.getTests(t.getTestType("Genotypic Susceptibility Score (GSS)"));
		
		List<DrugGeneric> genericDrugs = t.getDrugGenericSortedOnResistanceRanking(t.getDrugClass(drugClass));
		
		int counter = 0;
		for(Test gssTest : gssTests) {
			for(DrugGeneric dg : genericDrugs) {
				header.put(dg.getGenericId() + " (" + gssTest.getDescription() + ")", counter);
				counter++;
			}
		}
		
		return header;
	}
	
    public static String getFixedGenericId(TestResult tr) {
        String genericId = tr.getDrugGeneric().getGenericId();
        if(genericId.startsWith("APV"))
            return genericId.replace("APV", "FPV");
        else
            return genericId;
    }
    
    public static String getSIR(String gssS) {
    	double gss = Double.parseDouble(gssS);
        if(gss == 0.0) {
            return "R";
        }
        else if(gss == 0.5 || gss == 0.75) {
            return "I";
        }
        else if(gss == 1.0 || gss == 1.5) {
            return "S";
        } else {
        	return "CANNOT INTERPRETE";
        }
    }
}
