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
import net.sf.regadb.util.xml.XMLTools;
public class ExportToCsv {
public String getCsvContentLine(TestObject TestObjectvar) {
String TestObjectLine = "";
if(TestObjectvar.getDescription()!=null) {
TestObjectLine += TestObjectvar.getDescription().toString()+",";
}
if(TestObjectvar.getTestObjectId()!=null) {
TestObjectLine += TestObjectvar.getTestObjectId().toString()+",";
}
return TestObjectLine;
}
public String getCsvContentLine(Therapy Therapyvar) {
String TherapyLine = "";
TherapyLine += Therapyvar.getTherapyMotivation().getValue()+",";
if(Therapyvar.getStartDate()!=null) {
TherapyLine += XMLTools.dateToRelaxNgString(Therapyvar.getStartDate())+",";
}
if(Therapyvar.getStopDate()!=null) {
TherapyLine += XMLTools.dateToRelaxNgString(Therapyvar.getStopDate())+",";
}
if(Therapyvar.getComment()!=null) {
TherapyLine += Therapyvar.getComment().toString()+",";
}
return TherapyLine;
}
public String getCsvContentLine(TherapyGeneric TherapyGenericvar) {
String TherapyGenericLine = "";
TherapyGenericLine += TherapyGenericvar.getId().getDrugGeneric().getGenericId()+",";
if(TherapyGenericvar.getDayDosageMg()!=null) {
TherapyGenericLine += TherapyGenericvar.getDayDosageMg().toString()+",";
}
return TherapyGenericLine;
}
public String getCsvContentLine(PatientAttributeValue PatientAttributeValuevar) {
String PatientAttributeValueLine = "";
if(PatientAttributeValuevar.getValue()!=null) {
PatientAttributeValueLine += PatientAttributeValuevar.getValue().toString()+",";
}
return PatientAttributeValueLine;
}
public String getCsvContentLine(TestResult TestResultvar) {
String TestResultLine = "";
TestResultLine += TestResultvar.getDrugGeneric().getGenericId()+",";
if(TestResultvar.getValue()!=null) {
TestResultLine += TestResultvar.getValue().toString()+",";
}
if(TestResultvar.getTestDate()!=null) {
TestResultLine += XMLTools.dateToRelaxNgString(TestResultvar.getTestDate())+",";
}
if(TestResultvar.getSampleId()!=null) {
TestResultLine += TestResultvar.getSampleId().toString()+",";
}
if(TestResultvar.getData()!=null) {
TestResultLine += XMLTools.base64Encoding(TestResultvar.getData())+",";
}
return TestResultLine;
}
public String getCsvContentLine(Analysis Analysisvar) {
String AnalysisLine = "";
AnalysisLine += Analysisvar.getAnalysisType().getType()+",";
if(Analysisvar.getUrl()!=null) {
AnalysisLine += Analysisvar.getUrl().toString()+",";
}
if(Analysisvar.getAccount()!=null) {
AnalysisLine += Analysisvar.getAccount().toString()+",";
}
if(Analysisvar.getPassword()!=null) {
AnalysisLine += Analysisvar.getPassword().toString()+",";
}
if(Analysisvar.getBaseinputfile()!=null) {
AnalysisLine += Analysisvar.getBaseinputfile().toString()+",";
}
if(Analysisvar.getBaseoutputfile()!=null) {
AnalysisLine += Analysisvar.getBaseoutputfile().toString()+",";
}
if(Analysisvar.getServiceName()!=null) {
AnalysisLine += Analysisvar.getServiceName().toString()+",";
}
if(Analysisvar.getDataoutputfile()!=null) {
AnalysisLine += Analysisvar.getDataoutputfile().toString()+",";
}
return AnalysisLine;
}
public String getCsvContentLine(AaSequence AaSequencevar) {
String AaSequenceLine = "";
AaSequenceLine += AaSequencevar.getProtein().getAbbreviation()+",";
AaSequenceLine += String.valueOf(AaSequencevar.getFirstAaPos())+",";
AaSequenceLine += String.valueOf(AaSequencevar.getLastAaPos())+",";
return AaSequenceLine;
}
public String getCsvContentLine(Patient Patientvar) {
String PatientLine = "";
if(Patientvar.getPatientId()!=null) {
PatientLine += Patientvar.getPatientId().toString()+",";
}
if(Patientvar.getLastName()!=null) {
PatientLine += Patientvar.getLastName().toString()+",";
}
if(Patientvar.getFirstName()!=null) {
PatientLine += Patientvar.getFirstName().toString()+",";
}
if(Patientvar.getBirthDate()!=null) {
PatientLine += XMLTools.dateToRelaxNgString(Patientvar.getBirthDate())+",";
}
if(Patientvar.getDeathDate()!=null) {
PatientLine += XMLTools.dateToRelaxNgString(Patientvar.getDeathDate())+",";
}
return PatientLine;
}
public String getCsvContentLine(TestType TestTypevar) {
String TestTypeLine = "";
if(TestTypevar.getDescription()!=null) {
TestTypeLine += TestTypevar.getDescription().toString()+",";
}
return TestTypeLine;
}
public String getCsvContentLine(AttributeGroup AttributeGroupvar) {
String AttributeGroupLine = "";
if(AttributeGroupvar.getGroupName()!=null) {
AttributeGroupLine += AttributeGroupvar.getGroupName().toString()+",";
}
return AttributeGroupLine;
}
public String getCsvContentLine(TestNominalValue TestNominalValuevar) {
String TestNominalValueLine = "";
if(TestNominalValuevar.getValue()!=null) {
TestNominalValueLine += TestNominalValuevar.getValue().toString()+",";
}
return TestNominalValueLine;
}
public String getCsvContentLine(ViralIsolate ViralIsolatevar) {
String ViralIsolateLine = "";
if(ViralIsolatevar.getSampleId()!=null) {
ViralIsolateLine += ViralIsolatevar.getSampleId().toString()+",";
}
if(ViralIsolatevar.getSampleDate()!=null) {
ViralIsolateLine += XMLTools.dateToRelaxNgString(ViralIsolatevar.getSampleDate())+",";
}
return ViralIsolateLine;
}
public String getCsvContentLine(ValueType ValueTypevar) {
String ValueTypeLine = "";
if(ValueTypevar.getDescription()!=null) {
ValueTypeLine += ValueTypevar.getDescription().toString()+",";
}
if(ValueTypevar.getMinimum()!=null) {
ValueTypeLine += ValueTypevar.getMinimum().toString()+",";
}
if(ValueTypevar.getMaximum()!=null) {
ValueTypeLine += ValueTypevar.getMaximum().toString()+",";
}
if(ValueTypevar.getMultiple()!=null) {
ValueTypeLine += ValueTypevar.getMultiple().toString()+",";
}
return ValueTypeLine;
}
public String getCsvContentLine(Test Testvar) {
String TestLine = "";
if(Testvar.getDescription()!=null) {
TestLine += Testvar.getDescription().toString()+",";
}
return TestLine;
}
public String getCsvContentLine(Attribute Attributevar) {
String AttributeLine = "";
if(Attributevar.getName()!=null) {
AttributeLine += Attributevar.getName().toString()+",";
}
return AttributeLine;
}
public String getCsvContentLine(AaInsertion AaInsertionvar) {
String AaInsertionLine = "";
AaInsertionLine += String.valueOf(AaInsertionvar.getId().getInsertionPosition())+",";
AaInsertionLine += String.valueOf(AaInsertionvar.getId().getInsertionOrder())+",";
if(AaInsertionvar.getAaInsertion()!=null) {
AaInsertionLine += AaInsertionvar.getAaInsertion().toString()+",";
}
if(AaInsertionvar.getNtInsertionCodon()!=null) {
AaInsertionLine += AaInsertionvar.getNtInsertionCodon().toString()+",";
}
return AaInsertionLine;
}
public String getCsvContentLine(TherapyCommercial TherapyCommercialvar) {
String TherapyCommercialLine = "";
TherapyCommercialLine += TherapyCommercialvar.getId().getDrugCommercial().getName()+",";
if(TherapyCommercialvar.getDayDosageUnits()!=null) {
TherapyCommercialLine += TherapyCommercialvar.getDayDosageUnits().toString()+",";
}
return TherapyCommercialLine;
}
public String getCsvContentLine(Dataset Datasetvar) {
String DatasetLine = "";
if(Datasetvar.getDescription()!=null) {
DatasetLine += Datasetvar.getDescription().toString()+",";
}
if(Datasetvar.getCreationDate()!=null) {
DatasetLine += XMLTools.dateToRelaxNgString(Datasetvar.getCreationDate())+",";
}
if(Datasetvar.getClosedDate()!=null) {
DatasetLine += XMLTools.dateToRelaxNgString(Datasetvar.getClosedDate())+",";
}
if(Datasetvar.getRevision()!=null) {
DatasetLine += Datasetvar.getRevision().toString()+",";
}
return DatasetLine;
}
public String getCsvContentLine(AttributeNominalValue AttributeNominalValuevar) {
String AttributeNominalValueLine = "";
if(AttributeNominalValuevar.getValue()!=null) {
AttributeNominalValueLine += AttributeNominalValuevar.getValue().toString()+",";
}
return AttributeNominalValueLine;
}
public String getCsvContentLine(AaMutation AaMutationvar) {
String AaMutationLine = "";
AaMutationLine += String.valueOf(AaMutationvar.getId().getMutationPosition())+",";
if(AaMutationvar.getAaReference()!=null) {
AaMutationLine += AaMutationvar.getAaReference().toString()+",";
}
if(AaMutationvar.getAaMutation()!=null) {
AaMutationLine += AaMutationvar.getAaMutation().toString()+",";
}
if(AaMutationvar.getNtReferenceCodon()!=null) {
AaMutationLine += AaMutationvar.getNtReferenceCodon().toString()+",";
}
if(AaMutationvar.getNtMutationCodon()!=null) {
AaMutationLine += AaMutationvar.getNtMutationCodon().toString()+",";
}
return AaMutationLine;
}
public String getCsvContentLine(AnalysisData AnalysisDatavar) {
String AnalysisDataLine = "";
if(AnalysisDatavar.getName()!=null) {
AnalysisDataLine += AnalysisDatavar.getName().toString()+",";
}
if(AnalysisDatavar.getData()!=null) {
AnalysisDataLine += XMLTools.base64Encoding(AnalysisDatavar.getData())+",";
}
if(AnalysisDatavar.getMimetype()!=null) {
AnalysisDataLine += AnalysisDatavar.getMimetype().toString()+",";
}
return AnalysisDataLine;
}
public String getCsvContentLine(NtSequence NtSequencevar) {
String NtSequenceLine = "";
if(NtSequencevar.getLabel()!=null) {
NtSequenceLine += NtSequencevar.getLabel().toString()+",";
}
if(NtSequencevar.getSequenceDate()!=null) {
NtSequenceLine += XMLTools.dateToRelaxNgString(NtSequencevar.getSequenceDate())+",";
}
if(NtSequencevar.getNucleotides()!=null) {
NtSequenceLine += NtSequencevar.getNucleotides().toString()+",";
}
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
public String getCsvLineSwitch(Object object) {
if(object instanceof Patient) {
return getCsvContentLine((Patient)object);
}
else if(object instanceof Dataset) {
return getCsvContentLine((Dataset)object);
}
else if(object instanceof TestResult) {
return getCsvContentLine((TestResult)object);
}
else if(object instanceof Test) {
return getCsvContentLine((Test)object);
}
else if(object instanceof Analysis) {
return getCsvContentLine((Analysis)object);
}
else if(object instanceof AnalysisData) {
return getCsvContentLine((AnalysisData)object);
}
else if(object instanceof TestType) {
return getCsvContentLine((TestType)object);
}
else if(object instanceof ValueType) {
return getCsvContentLine((ValueType)object);
}
else if(object instanceof TestObject) {
return getCsvContentLine((TestObject)object);
}
else if(object instanceof TestNominalValue) {
return getCsvContentLine((TestNominalValue)object);
}
else if(object instanceof PatientAttributeValue) {
return getCsvContentLine((PatientAttributeValue)object);
}
else if(object instanceof Attribute) {
return getCsvContentLine((Attribute)object);
}
else if(object instanceof AttributeGroup) {
return getCsvContentLine((AttributeGroup)object);
}
else if(object instanceof AttributeNominalValue) {
return getCsvContentLine((AttributeNominalValue)object);
}
else if(object instanceof ViralIsolate) {
return getCsvContentLine((ViralIsolate)object);
}
else if(object instanceof NtSequence) {
return getCsvContentLine((NtSequence)object);
}
else if(object instanceof AaSequence) {
return getCsvContentLine((AaSequence)object);
}
else if(object instanceof AaMutation) {
return getCsvContentLine((AaMutation)object);
}
else if(object instanceof AaInsertion) {
return getCsvContentLine((AaInsertion)object);
}
else if(object instanceof Therapy) {
return getCsvContentLine((Therapy)object);
}
else if(object instanceof TherapyCommercial) {
return getCsvContentLine((TherapyCommercial)object);
}
else if(object instanceof TherapyGeneric) {
return getCsvContentLine((TherapyGeneric)object);
}

 return null;
}

}