package net.sf.regadb.db;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.sf.regadb.db.meta.Equals;
import net.sf.regadb.util.date.DateUtils;

public class PatientImplHelper {
    public static boolean canAccessViralIsolate(ViralIsolate vi, Set<Dataset> datasets, Set<Integer> accessiblePatients) {
        return canAccesPI(vi.getPatient(), datasets, accessiblePatients);
    }
    
    public static Patient getPatient(ViralIsolate vi, Set<Dataset> datasets) {
    	return castPatientImplToPatient(vi.getPatient(), datasets);
    }
    
    public static boolean canAccessPatient(Patient p, Set<Dataset> datasets, Set<Integer> accessiblePatients) {
        return canAccesPI(p.getPatient(), datasets, accessiblePatients);
    }
    
    public static boolean canAccessPatientAttributeValue(PatientAttributeValue patientAttributeValuevar, Set<Dataset> datasets, Set<Integer> accessiblePatients) {
        return canAccesPI(patientAttributeValuevar.getPatient(), datasets, accessiblePatients);
    }
    
    public static boolean canAccessPatientEventValue(PatientEventValue patientEventValuevar, Set<Dataset> datasets, Set<Integer> accessiblePatients) {
        return canAccesPI(patientEventValuevar.getPatient(), datasets, accessiblePatients);
    }
    
    public static boolean canAccessTestResult(TestResult testResultvar, Set<Dataset> datasets, Set<Integer> accessiblePatients) {
    	if (testResultvar.getPatient() != null) {
            return canAccesPI(testResultvar.getPatient(), datasets, accessiblePatients);
    	}
    	else if (testResultvar.getViralIsolate() != null) {
    		return canAccessViralIsolate(testResultvar.getViralIsolate(), datasets, accessiblePatients);
    	}
    	else if (testResultvar.getNtSequence() != null) {
    		return canAccessViralIsolate(testResultvar.getNtSequence().getViralIsolate(), datasets, accessiblePatients);
    	}
    	else {
    		return false;
    	}
    }

    public static boolean canAccessTherapy(Therapy therapyvar, Set<Dataset> datasets, Set<Integer> accessiblePatients) {
        return canAccesPI(therapyvar.getPatient(), datasets, accessiblePatients);
    }
    
    private static boolean canAccesPI(PatientImpl p, Set<Dataset> datasets, Set<Integer> accessiblePatients) {
    	if (accessiblePatients != null) {
    		return accessiblePatients.contains(p.getPatientIi());
    	}
        Set<Dataset> pDatasets = new HashSet<Dataset>();
        for(PatientDataset pd : p.getPatientDatasets()) {
            pDatasets.add(pd.getId().getDataset());
        }
        pDatasets.retainAll(datasets);
        return pDatasets.size()>0;
    }
    
    public static boolean isInstanceOfPatientImpl(Object o) {
        return (o instanceof PatientImpl);
    }
    
    public static Patient castPatientImplToPatient(Object o, Set<Dataset> datasets) {
        int privillege = -1;
        for(Dataset ds : datasets) {
            for(DatasetAccess dsa : ds.getDatasetAccesses()) {
                if(dsa.getProvider().equals(ds.getSettingsUser().getUid())) {
                    if(dsa.getPermissions()>privillege) {
                        privillege = dsa.getPermissions();
                    }
                }
            }
        }
    	return new Patient((PatientImpl)o, privillege);
    }
    
    /**
     * Moves non-conflicting objects from Patient b to Patient a
     * @param a receiving Patient
     * @param b donating Patient
     * @param log PrintStream for logging purpose
     * @param dryrun if true, no actual changes will be made
     * @throws IOException 
     */
    public static void mergePatients(Patient a, Patient b, Appendable log, boolean dryrun) throws IOException{
    	mergePatientAttributeValues(a, b, log, dryrun);
    	mergePatientEventValues(a, b, log, dryrun);
    	mergeTestResults(a, b, log, dryrun);
    	mergeTherapies(a, b, log, dryrun);
    	mergeViralIsolates(a, b, log, dryrun);
    }

    /**
     * Moves non-conflicting PatientAttributeValues from Patient b to Patient a
     * @param a receiving Patient
     * @param b donating Patient
     * @param log PrintStream for logging purpose
     * @param dryrun if true, no actual changes will be made
     * @throws IOException 
     */
    public static void mergePatientAttributeValues(Patient a, Patient b, Appendable log, boolean dryrun) throws IOException{
    	log.append("PatientAttributeValue:").append("\n");
    	for(Iterator<PatientAttributeValue> i = b.getPatientAttributeValues().iterator(); i.hasNext();){
    		PatientAttributeValue bObj = i.next();
    		
    		PatientAttributeValue aObj = a.getAttributeValue(bObj.getAttribute());
    		if(aObj == null){
    			log.append(" \t- moving: ");
    			if(!dryrun){
    				i.remove();
    				bObj.setPatient(a.getPatient());
    				a.getPatientAttributeValues().add(bObj);
    			}
    		}else{
    			if(Equals.isSamePatientAttributeValue(aObj, bObj))
    				log.append(" \t- identical: ");
    			else
    				log.append(" \t- conflict: ");
    		}
			log.append(bObj.getAttribute().getName())
				.append("\n");
    	}
    }
    
    /**
     * Moves non-conflicting PatientEventValues from Patient b to Patient a
     * @param a receiving Patient
     * @param b donating Patient
     * @param log PrintStream for logging purpose
     * @param dryrun if true, no actual changes will be made
     * @throws IOException 
     */
    public static void mergePatientEventValues(Patient a, Patient b, Appendable log, boolean dryrun) throws IOException{
    	log.append("\nPatientEventValue:").append("\n");
    	for(Iterator<PatientEventValue> i = b.getPatientEventValues().iterator(); i.hasNext();){
    		PatientEventValue bObj = i.next();
    		
    		PatientEventValue aObj = null;
    		for(PatientEventValue obj : a.getPatientEventValues()){
    			if(obj.getStartDate().equals(bObj.getStartDate())
    					&& Equals.isSameEvent(obj.getEvent(), bObj.getEvent())){
    				aObj = obj;
    				break;
    			}
    		}
    		if(aObj == null){
    			log.append(" \t- moving: ");
    			if(!dryrun){
    				i.remove();
    				bObj.setPatient(a.getPatient());
    				a.getPatientEventValues().add(bObj);
    			}
    		}else{
    			if(Equals.isSamePatientEventValue(aObj, bObj))
    				log.append(" \t- identical: ");
    			else
    				log.append(" \t- conflict: ");
    		}
			log.append(bObj.getEvent().getName())
				.append(": ")
				.append(DateUtils.format(bObj.getStartDate()))
				.append("\n");
    	}
    }
    
    /**
     * Moves non-conflicting TestResults from Patient b to Patient a
     * @param a receiving Patient
     * @param b donating Patient
     * @param log PrintStream for logging purpose
     * @param dryrun if true, no actual changes will be made
     * @throws IOException 
     */
    public static void mergeTestResults(Patient a, Patient b, Appendable log, boolean dryrun) throws IOException{
    	log.append("\nTestResult:").append("\n");
    	for(Iterator<TestResult> i = b.getTestResults().iterator(); i.hasNext();){
    		TestResult bObj = i.next();
    		
    		//skip ViralIsolate and NtSequence TestResults, Patient is a secondary parent
    		if(bObj.getNtSequence() != null || bObj.getViralIsolate() != null)
    			continue;
    		
    		TestResult aObj = null;
    		for(TestResult obj : a.getTestResults()){
    			if(obj.getTestDate().equals(bObj.getTestDate())
    					&& Equals.isSameTest(obj.getTest(), bObj.getTest())){
    				aObj = obj;
    				break;
    			}
    		}
    		if(aObj == null){
    			log.append(" \t- moving: ");
    			if(!dryrun){
    				i.remove();
    				bObj.setPatient(a.getPatient());
    				a.getTestResults().add(bObj);
    			}
    		}else{
    			if(Equals.isSameTestResult(aObj, bObj))
    				log.append(" \t- identical: ");
    			else
    				log.append(" \t- conflict: ");
    		}
    		log.append(bObj.getTest().getDescription())
				.append(": ")
				.append(DateUtils.format(bObj.getTestDate()))
				.append("\n");
    	}
    }
    
    /**
     * Moves non-conflicting Therapies from Patient b to Patient a
     * @param a receiving Patient
     * @param b donating Patient
     * @param log PrintStream for logging purpose
     * @param dryrun if true, no actual changes will be made
     * @throws IOException 
     */
    public static void mergeTherapies(Patient a, Patient b, Appendable log, boolean dryrun) throws IOException{
    	log.append("\nTherapy:").append("\n");
    	for(Iterator<Therapy> i = b.getTherapies().iterator(); i.hasNext();){
    		Therapy bObj = i.next();
    		
    		Therapy aObj = null;
    		for(Therapy obj : a.getTherapies()){
    			if(Equals.isSameTherapy(obj, bObj)){
    				aObj = obj;
    				break;
    			}
    		}
    		if(aObj == null){
    			log.append(" \t- moving: ");
    			if(!dryrun){
    				i.remove();
    				bObj.setPatient(a.getPatient());
    				a.getTherapies().add(bObj);
    			}
    		}else{
    			if(Equals.isSameTherapyEx(aObj, bObj))
    				log.append(" \t- identical: ");
    			else
    				log.append(" \t- conflict: ");
    		}
			log.append(DateUtils.format(bObj.getStartDate()))
				.append("\n");
    	}
    }
    
    /**
     * Moves non-conflicting ViralIsolates from Patient b to Patient a
     * @param a receiving Patient
     * @param b donating Patient
     * @param log PrintStream for logging purpose
     * @param dryrun if true, no actual changes will be made
     * @throws IOException 
     */
    public static void mergeViralIsolates(Patient a, Patient b, Appendable log, boolean dryrun) throws IOException{
    	log.append("\nViralIsolate:").append("\n");
    	for(Iterator<ViralIsolate> i = b.getViralIsolates().iterator(); i.hasNext();){
    		ViralIsolate bObj = i.next();
    		
    		boolean found = false;
    		for(ViralIsolate aObj : a.getViralIsolates()){
    			if(aObj.getSampleId().equals(bObj.getSampleId())){
    				found = true;
    				break;
    			}
    		}
    		if(!found){
    			log.append(" \t- moving: ");
    			if(!dryrun){
    				i.remove();
    				bObj.setPatient(a.getPatient());
    				a.getViralIsolates().add(bObj);
    				
    				//change Patient from ViralIsolate and NtSequence TestResults
    				for(TestResult tr : bObj.getTestResults()){
    					if(tr.getPatient() != null){
    						tr.setPatient(a.getPatient());
    						b.getTestResults().remove(tr);
    					}
    				}
    				
    				for(NtSequence nt : bObj.getNtSequences()){
    					for(TestResult tr : nt.getTestResults()){
    						if(tr.getPatient() != null){
    							tr.setPatient(a.getPatient());
    							b.getTestResults().remove(tr);
    						}
    					}
    				}
    			}
    		}else{
    			log.append(" \t- conflict: ");
    		}
    		log.append(bObj.getSampleId())
    			.append("\n");
    	}
    }
}
