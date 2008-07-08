package net.sf.regadb.db;

import java.util.HashSet;
import java.util.Set;

public class PatientImplHelper {
    public static boolean canAccessViralIsolate(ViralIsolate vi, Set<Dataset> datasets, Set<Integer> accessiblePatients) {
        return canAccesPI(vi.getPatient(), datasets, accessiblePatients);
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
    

}
