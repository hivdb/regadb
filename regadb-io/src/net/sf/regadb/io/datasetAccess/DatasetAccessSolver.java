package net.sf.regadb.io.datasetAccess;

import java.util.Set;

import net.sf.regadb.db.AaInsertion;
import net.sf.regadb.db.AaMutation;
import net.sf.regadb.db.AaSequence;
import net.sf.regadb.db.Analysis;
import net.sf.regadb.db.AnalysisData;
import net.sf.regadb.db.Attribute;
import net.sf.regadb.db.AttributeGroup;
import net.sf.regadb.db.AttributeNominalValue;
import net.sf.regadb.db.Dataset;
import net.sf.regadb.db.DrugCommercial;
import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.Event;
import net.sf.regadb.db.EventNominalValue;
import net.sf.regadb.db.Genome;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.OpenReadingFrame;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.PatientAttributeValue;
import net.sf.regadb.db.PatientEventValue;
import net.sf.regadb.db.PatientImplHelper;
import net.sf.regadb.db.Protein;
import net.sf.regadb.db.SplicingPosition;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestNominalValue;
import net.sf.regadb.db.TestObject;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.TestType;
import net.sf.regadb.db.Therapy;
import net.sf.regadb.db.TherapyCommercial;
import net.sf.regadb.db.TherapyGeneric;
import net.sf.regadb.db.ValueType;
import net.sf.regadb.db.ViralIsolate;

public class DatasetAccessSolver implements IDatasetAccess {
	@SuppressWarnings("serial")
	public static class IllegalAccessException extends Exception{
		public IllegalAccessException(String s){
			super("Illegal access to '"+ s +"'");
		}
	}
	
    private static DatasetAccessSolver das;
    private DatasetAccessSolver() {
        
    }

    public boolean canAccessAaInsertion(AaInsertion AaInsertionvar, Set<Dataset> datasets, Set<Integer> accessiblePatients) {
        return PatientImplHelper.canAccessViralIsolate(AaInsertionvar.getId().getAaSequence().getNtSequence().getViralIsolate(), datasets, accessiblePatients);
    }

    public boolean canAccessAaMutation(AaMutation AaMutationvar, Set<Dataset> datasets, Set<Integer> accessiblePatients) {
        return PatientImplHelper.canAccessViralIsolate(AaMutationvar.getId().getAaSequence().getNtSequence().getViralIsolate(), datasets, accessiblePatients);
    }

    public boolean canAccessAaSequence(AaSequence AaSequencevar, Set<Dataset> datasets, Set<Integer> accessiblePatients) {
        return PatientImplHelper.canAccessViralIsolate(AaSequencevar.getNtSequence().getViralIsolate(), datasets, accessiblePatients);
    }

    public boolean canAccessAnalysis(Analysis Analysisvar, Set<Dataset> datasets, Set<Integer> accessiblePatients) {
        return true;
    }

    public boolean canAccessAnalysisData(AnalysisData AnalysisDatavar, Set<Dataset> datasets, Set<Integer> accessiblePatients) {
        return true;
    }

    public boolean canAccessAttribute(Attribute Attributevar, Set<Dataset> datasets, Set<Integer> accessiblePatients) {
        return true;
    }

    public boolean canAccessAttributeGroup(AttributeGroup AttributeGroupvar, Set<Dataset> datasets, Set<Integer> accessiblePatients) {
        return true;
    }

    public boolean canAccessAttributeNominalValue(AttributeNominalValue AttributeNominalValuevar, Set<Dataset> datasets, Set<Integer> accessiblePatients) {
        return true;
    }

    public boolean canAccessDataset(Dataset Datasetvar, Set<Dataset> datasets, Set<Integer> accessiblePatients) {
        return true;
    }

    public boolean canAccessNtSequence(NtSequence NtSequencevar, Set<Dataset> datasets, Set<Integer> accessiblePatients) {
        return PatientImplHelper.canAccessViralIsolate(NtSequencevar.getViralIsolate(), datasets, accessiblePatients);
    }

    public boolean canAccessPatient(Patient Patientvar, Set<Dataset> datasets, Set<Integer> accessiblePatients) {
        return PatientImplHelper.canAccessPatient(Patientvar, datasets, accessiblePatients);
    }

    public boolean canAccessPatientAttributeValue(PatientAttributeValue PatientAttributeValuevar, Set<Dataset> datasets, Set<Integer> accessiblePatients) {
        return PatientImplHelper.canAccessPatientAttributeValue(PatientAttributeValuevar, datasets, accessiblePatients);
    }

    public boolean canAccessTest(Test Testvar, Set<Dataset> datasets, Set<Integer> accessiblePatients) {
        return true;
    }

    public boolean canAccessTestNominalValue(TestNominalValue TestNominalValuevar, Set<Dataset> datasets, Set<Integer> accessiblePatients) {
        return true;
    }

    public boolean canAccessTestObject(TestObject TestObjectvar, Set<Dataset> datasets, Set<Integer> accessiblePatients) {
        return true;
    }

    public boolean canAccessTestResult(TestResult TestResultvar, Set<Dataset> datasets, Set<Integer> accessiblePatients) {
        return PatientImplHelper.canAccessTestResult(TestResultvar, datasets, accessiblePatients);
    }

    public boolean canAccessTestType(TestType TestTypevar, Set<Dataset> datasets, Set<Integer> accessiblePatients) {
        return true;
    }

    public boolean canAccessTherapy(Therapy Therapyvar, Set<Dataset> datasets, Set<Integer> accessiblePatients) {
        return PatientImplHelper.canAccessTherapy(Therapyvar, datasets, accessiblePatients);
    }

    public boolean canAccessTherapyCommercial(TherapyCommercial TherapyCommercialvar, Set<Dataset> datasets, Set<Integer> accessiblePatients) {
        return PatientImplHelper.canAccessTherapy(TherapyCommercialvar.getId().getTherapy(), datasets, accessiblePatients);
    }

    public boolean canAccessTherapyGeneric(TherapyGeneric TherapyGenericvar, Set<Dataset> datasets, Set<Integer> accessiblePatients) {
        return PatientImplHelper.canAccessTherapy(TherapyGenericvar.getId().getTherapy(), datasets, accessiblePatients);
    }

    public boolean canAccessValueType(ValueType ValueTypevar, Set<Dataset> datasets, Set<Integer> accessiblePatients) {
        return true;
    }

    public boolean canAccessViralIsolate(ViralIsolate ViralIsolatevar, Set<Dataset> datasets, Set<Integer> accessiblePatients) {
        return PatientImplHelper.canAccessViralIsolate(ViralIsolatevar, datasets, accessiblePatients);
    }

    public boolean canAccessEvent(Event Eventvar, Set<Dataset> datasets, Set<Integer> accessiblePatients) {
        return true;
    }

    public boolean canAccessEventNominalValue(
            EventNominalValue EventNominalValuevar, Set<Dataset> datasets, Set<Integer> accessiblePatients) {
        return true;
    }

    public boolean canAccessPatientEventValue(
            PatientEventValue PatientEventValuevar, Set<Dataset> datasets, Set<Integer> accessiblePatients) {
        return PatientImplHelper.canAccessPatientEventValue(PatientEventValuevar, datasets, accessiblePatients);
    }
    
    public static DatasetAccessSolver getInstance() {
        if(das == null) {
            das = new DatasetAccessSolver();
        }
        return das;
    }

	public boolean canAccessProtein(Protein Proteinvar, Set<Dataset> datasets,
			Set<Integer> accessiblePatients) {
		return true;
	}
	
	public boolean canAccessSplicingPosition(SplicingPosition splicingPosition, Set<Dataset> datasets,
            Set<Integer> accessiblePatients) {
        return true;
    }

	public boolean canAccessDrugCommercial(DrugCommercial DrugCommercialvar,
			Set<Dataset> datasets, Set<Integer> accessiblePatients) {
		return true;
	}

	public boolean canAccessDrugGeneric(DrugGeneric DrugGenericvar,
			Set<Dataset> datasets, Set<Integer> accessiblePatients) {
		return true;
	}

    public boolean canAccessGenome(Genome Genomevar, Set<Dataset> datasets,
            Set<Integer> accessiblePatients) {
        return true;
    }

    public boolean canAccessOpenReadingFrame(
            OpenReadingFrame OpenReadingFramevar, Set<Dataset> datasets,
            Set<Integer> accessiblePatients) {
        return true;
    }
}
