package net.sf.regadb.io.exportCsv;
import net.sf.regadb.db.Analysis;
import net.sf.regadb.db.QueryDefinitionRunParameter;
import net.sf.regadb.db.QueryDefinitionRun;
import net.sf.regadb.db.ValueType;
import net.sf.regadb.db.AnalysisData;
import net.sf.regadb.db.SettingsUser;
import net.sf.regadb.db.QueryDefinitionParameterType;
import net.sf.regadb.db.DrugClass;
import net.sf.regadb.db.ResistanceInterpretationTemplate;
import net.sf.regadb.db.AttributeGroup;
import net.sf.regadb.db.AaSequence;
import net.sf.regadb.db.QueryDefinition;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.db.UserAttribute;
import net.sf.regadb.db.TestObject;
import net.sf.regadb.db.PatientAttributeValue;
import net.sf.regadb.db.DatasetAccess;
import net.sf.regadb.db.Therapy;
import net.sf.regadb.db.QueryDefinitionParameter;
import net.sf.regadb.db.AttributeNominalValue;
import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.TestNominalValue;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Protein;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Attribute;
import net.sf.regadb.db.AnalysisType;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.TherapyMotivation;
import net.sf.regadb.db.TestType;
import net.sf.regadb.db.AaInsertion;
import net.sf.regadb.db.TherapyCommercial;
import net.sf.regadb.db.AaMutation;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.PatientDataset;
import net.sf.regadb.db.Dataset;
import net.sf.regadb.db.TherapyGeneric;
import net.sf.regadb.db.DrugCommercial;

public class ExportToCsv {
public String getCsvContentLine(TestObject TestObjectvar) {
String TestObjectLine = "";
return TestObjectLine;
}
public String getCsvContentLine(Therapy Therapyvar) {
String TherapyLine = "";
TherapyLine += Therapyvar.getTherapyMotivation().getValue();
return TherapyLine;
}
public String getCsvContentLine(TherapyGeneric TherapyGenericvar) {
String TherapyGenericLine = "";
return TherapyGenericLine;
}
public String getCsvContentLine(PatientAttributeValue PatientAttributeValuevar) {
String PatientAttributeValueLine = "";
return PatientAttributeValueLine;
}
public String getCsvContentLine(TestResult TestResultvar) {
String TestResultLine = "";
TestResultLine += TestResultvar.getDrugGeneric().getGenericId();
return TestResultLine;
}
public String getCsvContentLine(Analysis Analysisvar) {
String AnalysisLine = "";
AnalysisLine += Analysisvar.getAnalysisType().getType();
return AnalysisLine;
}
public String getCsvContentLine(AaSequence AaSequencevar) {
String AaSequenceLine = "";
AaSequenceLine += AaSequencevar.getProtein().getAbbreviation();
return AaSequenceLine;
}
public String getCsvContentLine(Patient Patientvar) {
String PatientLine = "";
return PatientLine;
}
public String getCsvContentLine(TestType TestTypevar) {
String TestTypeLine = "";
return TestTypeLine;
}
public String getCsvContentLine(AttributeGroup AttributeGroupvar) {
String AttributeGroupLine = "";
return AttributeGroupLine;
}
public String getCsvContentLine(TestNominalValue TestNominalValuevar) {
String TestNominalValueLine = "";
return TestNominalValueLine;
}
public String getCsvContentLine(ViralIsolate ViralIsolatevar) {
String ViralIsolateLine = "";
return ViralIsolateLine;
}
public String getCsvContentLine(ValueType ValueTypevar) {
String ValueTypeLine = "";
return ValueTypeLine;
}
public String getCsvContentLine(Test Testvar) {
String TestLine = "";
return TestLine;
}
public String getCsvContentLine(Attribute Attributevar) {
String AttributeLine = "";
return AttributeLine;
}
public String getCsvContentLine(AaInsertion AaInsertionvar) {
String AaInsertionLine = "";
return AaInsertionLine;
}
public String getCsvContentLine(TherapyCommercial TherapyCommercialvar) {
String TherapyCommercialLine = "";
return TherapyCommercialLine;
}
public String getCsvContentLine(Dataset Datasetvar) {
String DatasetLine = "";
return DatasetLine;
}
public String getCsvContentLine(AttributeNominalValue AttributeNominalValuevar) {
String AttributeNominalValueLine = "";
return AttributeNominalValueLine;
}
public String getCsvContentLine(AaMutation AaMutationvar) {
String AaMutationLine = "";
return AaMutationLine;
}
public String getCsvContentLine(AnalysisData AnalysisDatavar) {
String AnalysisDataLine = "";
return AnalysisDataLine;
}
public String getCsvContentLine(NtSequence NtSequencevar) {
String NtSequenceLine = "";
return NtSequenceLine;
}
public String getCsvHeaderLine(TestObject TestObjectvar) {
String TestObjectLine = "";
return TestObjectLine;
}
public String getCsvHeaderLine(Therapy Therapyvar) {
String TherapyLine = "";
return TherapyLine;
}
public String getCsvHeaderLine(TherapyGeneric TherapyGenericvar) {
String TherapyGenericLine = "";
return TherapyGenericLine;
}
public String getCsvHeaderLine(PatientAttributeValue PatientAttributeValuevar) {
String PatientAttributeValueLine = "";
return PatientAttributeValueLine;
}
public String getCsvHeaderLine(TestResult TestResultvar) {
String TestResultLine = "";
return TestResultLine;
}
public String getCsvHeaderLine(Analysis Analysisvar) {
String AnalysisLine = "";
return AnalysisLine;
}
public String getCsvHeaderLine(AaSequence AaSequencevar) {
String AaSequenceLine = "";
return AaSequenceLine;
}
public String getCsvHeaderLine(Patient Patientvar) {
String PatientLine = "";
return PatientLine;
}
public String getCsvHeaderLine(TestType TestTypevar) {
String TestTypeLine = "";
return TestTypeLine;
}
public String getCsvHeaderLine(AttributeGroup AttributeGroupvar) {
String AttributeGroupLine = "";
return AttributeGroupLine;
}
public String getCsvHeaderLine(TestNominalValue TestNominalValuevar) {
String TestNominalValueLine = "";
return TestNominalValueLine;
}
public String getCsvHeaderLine(ViralIsolate ViralIsolatevar) {
String ViralIsolateLine = "";
return ViralIsolateLine;
}
public String getCsvHeaderLine(ValueType ValueTypevar) {
String ValueTypeLine = "";
return ValueTypeLine;
}
public String getCsvHeaderLine(Test Testvar) {
String TestLine = "";
return TestLine;
}
public String getCsvHeaderLine(Attribute Attributevar) {
String AttributeLine = "";
return AttributeLine;
}
public String getCsvHeaderLine(AaInsertion AaInsertionvar) {
String AaInsertionLine = "";
return AaInsertionLine;
}
public String getCsvHeaderLine(TherapyCommercial TherapyCommercialvar) {
String TherapyCommercialLine = "";
return TherapyCommercialLine;
}
public String getCsvHeaderLine(Dataset Datasetvar) {
String DatasetLine = "";
return DatasetLine;
}
public String getCsvHeaderLine(AttributeNominalValue AttributeNominalValuevar) {
String AttributeNominalValueLine = "";
return AttributeNominalValueLine;
}
public String getCsvHeaderLine(AaMutation AaMutationvar) {
String AaMutationLine = "";
return AaMutationLine;
}
public String getCsvHeaderLine(AnalysisData AnalysisDatavar) {
String AnalysisDataLine = "";
return AnalysisDataLine;
}
public String getCsvHeaderLine(NtSequence NtSequencevar) {
String NtSequenceLine = "";
return NtSequenceLine;
}

}