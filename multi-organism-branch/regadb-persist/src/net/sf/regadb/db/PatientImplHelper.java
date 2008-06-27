package net.sf.regadb.db;

import java.util.HashSet;
import java.util.Set;

public class PatientImplHelper {
    public static boolean canAccessViralIsolate(ViralIsolate vi, Set<Dataset> datasets) {
        return canAccesPI(vi.getPatient(), datasets);
    }
    
    public static boolean canAccessPatient(Patient p, Set<Dataset> datasets) {
        return canAccesPI(p.getPatient(), datasets);
    }
    
    public static boolean canAccessPatientAttributeValue(PatientAttributeValue patientAttributeValuevar, Set<Dataset> datasets) {
        return canAccesPI(patientAttributeValuevar.getPatient(), datasets);
    }
    
    public static boolean canAccessPatientEventValue(PatientEventValue patientEventValuevar, Set<Dataset> datasets) {
        return canAccesPI(patientEventValuevar.getPatient(), datasets);
    }
    
    public static boolean canAccessTestResult(TestResult testResultvar, Set<Dataset> datasets) {
    	if (testResultvar.getPatient() != null) {
            return canAccesPI(testResultvar.getPatient(), datasets);
    	}
    	else if (testResultvar.getViralIsolate() != null) {
    		return canAccessViralIsolate(testResultvar.getViralIsolate(), datasets);
    	}
    	else if (testResultvar.getNtSequence() != null) {
    		return canAccessViralIsolate(testResultvar.getNtSequence().getViralIsolate(), datasets);
    	}
    	else {
    		return false;
    	}
    }

    public static boolean canAccessTherapy(Therapy therapyvar, Set<Dataset> datasets) {
        return canAccesPI(therapyvar.getPatient(), datasets);
    }
    
    private static boolean canAccesPI(PatientImpl p, Set<Dataset> datasets) {
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
