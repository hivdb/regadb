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
import net.sf.regadb.db.PatientImplHelper;
import java.util.Set;
import net.sf.regadb.util.xml.XMLTools;
import net.sf.regadb.io.datasetAccess.DatasetAccessSolver;

public class ExportToCsv {
public String getCsvContentLine(TestObject TestObjectvar) {
String TestObjectLine = "";
if(TestObjectvar.getDescription()!=null) {
TestObjectLine += TestObjectvar.getDescription().toString();
}
TestObjectLine += ",";
if(TestObjectvar.getTestObjectId()!=null) {
TestObjectLine += TestObjectvar.getTestObjectId().toString();
}
TestObjectLine += ",";
return TestObjectLine;
}

public String getCsvContentLine(Therapy Therapyvar) {
String TherapyLine = "";
TherapyLine += Therapyvar.getTherapyMotivation().getValue()+",";
if(Therapyvar.getStartDate()!=null) {
TherapyLine += XMLTools.dateToRelaxNgString(Therapyvar.getStartDate());
}
TherapyLine += ",";
if(Therapyvar.getStopDate()!=null) {
TherapyLine += XMLTools.dateToRelaxNgString(Therapyvar.getStopDate());
}
TherapyLine += ",";
if(Therapyvar.getComment()!=null) {
TherapyLine += Therapyvar.getComment().toString();
}
TherapyLine += ",";
return TherapyLine;
}

public String getCsvContentLine(TherapyGeneric TherapyGenericvar) {
String TherapyGenericLine = "";
TherapyGenericLine += TherapyGenericvar.getId().getDrugGeneric().getGenericId()+",";
if(TherapyGenericvar.getDayDosageMg()!=null) {
TherapyGenericLine += TherapyGenericvar.getDayDosageMg().toString();
}
TherapyGenericLine += ",";
return TherapyGenericLine;
}

public String getCsvContentLine(PatientAttributeValue PatientAttributeValuevar) {
String PatientAttributeValueLine = "";
if(PatientAttributeValuevar.getValue()!=null) {
PatientAttributeValueLine += PatientAttributeValuevar.getValue().toString();
}
PatientAttributeValueLine += ",";
return PatientAttributeValueLine;
}

public String getCsvContentLine(TestResult TestResultvar) {
String TestResultLine = "";
TestResultLine += TestResultvar.getDrugGeneric().getGenericId()+",";
if(TestResultvar.getValue()!=null) {
TestResultLine += TestResultvar.getValue().toString();
}
TestResultLine += ",";
if(TestResultvar.getTestDate()!=null) {
TestResultLine += XMLTools.dateToRelaxNgString(TestResultvar.getTestDate());
}
TestResultLine += ",";
if(TestResultvar.getSampleId()!=null) {
TestResultLine += TestResultvar.getSampleId().toString();
}
TestResultLine += ",";
if(TestResultvar.getData()!=null) {
TestResultLine += XMLTools.base64Encoding(TestResultvar.getData());
}
TestResultLine += ",";
return TestResultLine;
}

public String getCsvContentLine(Analysis Analysisvar) {
String AnalysisLine = "";
AnalysisLine += Analysisvar.getAnalysisType().getType()+",";
if(Analysisvar.getUrl()!=null) {
AnalysisLine += Analysisvar.getUrl().toString();
}
AnalysisLine += ",";
if(Analysisvar.getAccount()!=null) {
AnalysisLine += Analysisvar.getAccount().toString();
}
AnalysisLine += ",";
if(Analysisvar.getPassword()!=null) {
AnalysisLine += Analysisvar.getPassword().toString();
}
AnalysisLine += ",";
if(Analysisvar.getBaseinputfile()!=null) {
AnalysisLine += Analysisvar.getBaseinputfile().toString();
}
AnalysisLine += ",";
if(Analysisvar.getBaseoutputfile()!=null) {
AnalysisLine += Analysisvar.getBaseoutputfile().toString();
}
AnalysisLine += ",";
if(Analysisvar.getServiceName()!=null) {
AnalysisLine += Analysisvar.getServiceName().toString();
}
AnalysisLine += ",";
if(Analysisvar.getDataoutputfile()!=null) {
AnalysisLine += Analysisvar.getDataoutputfile().toString();
}
AnalysisLine += ",";
return AnalysisLine;
}

public String getCsvContentLine(AaSequence AaSequencevar) {
String AaSequenceLine = "";
AaSequenceLine += AaSequencevar.getProtein().getAbbreviation()+",";
AaSequenceLine += String.valueOf(AaSequencevar.getFirstAaPos());
AaSequenceLine += ",";
AaSequenceLine += String.valueOf(AaSequencevar.getLastAaPos());
AaSequenceLine += ",";
return AaSequenceLine;
}

public String getCsvContentLine(Patient PatientImplvar) {
String PatientImplLine = "";
if(PatientImplvar.getPatientId()!=null) {
PatientImplLine += PatientImplvar.getPatientId().toString();
}
PatientImplLine += ",";
if(PatientImplvar.getLastName()!=null) {
PatientImplLine += PatientImplvar.getLastName().toString();
}
PatientImplLine += ",";
if(PatientImplvar.getFirstName()!=null) {
PatientImplLine += PatientImplvar.getFirstName().toString();
}
PatientImplLine += ",";
if(PatientImplvar.getBirthDate()!=null) {
PatientImplLine += XMLTools.dateToRelaxNgString(PatientImplvar.getBirthDate());
}
PatientImplLine += ",";
if(PatientImplvar.getDeathDate()!=null) {
PatientImplLine += XMLTools.dateToRelaxNgString(PatientImplvar.getDeathDate());
}
PatientImplLine += ",";
return PatientImplLine;
}

public String getCsvContentLine(TestType TestTypevar) {
String TestTypeLine = "";
if(TestTypevar.getDescription()!=null) {
TestTypeLine += TestTypevar.getDescription().toString();
}
TestTypeLine += ",";
return TestTypeLine;
}

public String getCsvContentLine(AttributeGroup AttributeGroupvar) {
String AttributeGroupLine = "";
if(AttributeGroupvar.getGroupName()!=null) {
AttributeGroupLine += AttributeGroupvar.getGroupName().toString();
}
AttributeGroupLine += ",";
return AttributeGroupLine;
}

public String getCsvContentLine(TestNominalValue TestNominalValuevar) {
String TestNominalValueLine = "";
if(TestNominalValuevar.getValue()!=null) {
TestNominalValueLine += TestNominalValuevar.getValue().toString();
}
TestNominalValueLine += ",";
return TestNominalValueLine;
}

public String getCsvContentLine(ViralIsolate ViralIsolatevar) {
String ViralIsolateLine = "";
if(ViralIsolatevar.getSampleId()!=null) {
ViralIsolateLine += ViralIsolatevar.getSampleId().toString();
}
ViralIsolateLine += ",";
if(ViralIsolatevar.getSampleDate()!=null) {
ViralIsolateLine += XMLTools.dateToRelaxNgString(ViralIsolatevar.getSampleDate());
}
ViralIsolateLine += ",";
return ViralIsolateLine;
}

public String getCsvContentLine(ValueType ValueTypevar) {
String ValueTypeLine = "";
if(ValueTypevar.getDescription()!=null) {
ValueTypeLine += ValueTypevar.getDescription().toString();
}
ValueTypeLine += ",";
if(ValueTypevar.getMinimum()!=null) {
ValueTypeLine += ValueTypevar.getMinimum().toString();
}
ValueTypeLine += ",";
if(ValueTypevar.getMaximum()!=null) {
ValueTypeLine += ValueTypevar.getMaximum().toString();
}
ValueTypeLine += ",";
if(ValueTypevar.getMultiple()!=null) {
ValueTypeLine += ValueTypevar.getMultiple().toString();
}
ValueTypeLine += ",";
return ValueTypeLine;
}

public String getCsvContentLine(Test Testvar) {
String TestLine = "";
if(Testvar.getDescription()!=null) {
TestLine += Testvar.getDescription().toString();
}
TestLine += ",";
return TestLine;
}

public String getCsvContentLine(Attribute Attributevar) {
String AttributeLine = "";
if(Attributevar.getName()!=null) {
AttributeLine += Attributevar.getName().toString();
}
AttributeLine += ",";
return AttributeLine;
}

public String getCsvContentLine(AaInsertion AaInsertionvar) {
String AaInsertionLine = "";
AaInsertionLine += String.valueOf(AaInsertionvar.getId().getInsertionPosition());
AaInsertionLine += ",";
AaInsertionLine += String.valueOf(AaInsertionvar.getId().getInsertionOrder());
AaInsertionLine += ",";
if(AaInsertionvar.getAaInsertion()!=null) {
AaInsertionLine += AaInsertionvar.getAaInsertion().toString();
}
AaInsertionLine += ",";
if(AaInsertionvar.getNtInsertionCodon()!=null) {
AaInsertionLine += AaInsertionvar.getNtInsertionCodon().toString();
}
AaInsertionLine += ",";
return AaInsertionLine;
}

public String getCsvContentLine(TherapyCommercial TherapyCommercialvar) {
String TherapyCommercialLine = "";
TherapyCommercialLine += TherapyCommercialvar.getId().getDrugCommercial().getName()+",";
if(TherapyCommercialvar.getDayDosageUnits()!=null) {
TherapyCommercialLine += TherapyCommercialvar.getDayDosageUnits().toString();
}
TherapyCommercialLine += ",";
return TherapyCommercialLine;
}

public String getCsvContentLine(Dataset Datasetvar) {
String DatasetLine = "";
if(Datasetvar.getDescription()!=null) {
DatasetLine += Datasetvar.getDescription().toString();
}
DatasetLine += ",";
if(Datasetvar.getCreationDate()!=null) {
DatasetLine += XMLTools.dateToRelaxNgString(Datasetvar.getCreationDate());
}
DatasetLine += ",";
if(Datasetvar.getClosedDate()!=null) {
DatasetLine += XMLTools.dateToRelaxNgString(Datasetvar.getClosedDate());
}
DatasetLine += ",";
if(Datasetvar.getRevision()!=null) {
DatasetLine += Datasetvar.getRevision().toString();
}
DatasetLine += ",";
return DatasetLine;
}

public String getCsvContentLine(AttributeNominalValue AttributeNominalValuevar) {
String AttributeNominalValueLine = "";
if(AttributeNominalValuevar.getValue()!=null) {
AttributeNominalValueLine += AttributeNominalValuevar.getValue().toString();
}
AttributeNominalValueLine += ",";
return AttributeNominalValueLine;
}

public String getCsvContentLine(AaMutation AaMutationvar) {
String AaMutationLine = "";
AaMutationLine += String.valueOf(AaMutationvar.getId().getMutationPosition());
AaMutationLine += ",";
if(AaMutationvar.getAaReference()!=null) {
AaMutationLine += AaMutationvar.getAaReference().toString();
}
AaMutationLine += ",";
if(AaMutationvar.getAaMutation()!=null) {
AaMutationLine += AaMutationvar.getAaMutation().toString();
}
AaMutationLine += ",";
if(AaMutationvar.getNtReferenceCodon()!=null) {
AaMutationLine += AaMutationvar.getNtReferenceCodon().toString();
}
AaMutationLine += ",";
if(AaMutationvar.getNtMutationCodon()!=null) {
AaMutationLine += AaMutationvar.getNtMutationCodon().toString();
}
AaMutationLine += ",";
return AaMutationLine;
}

public String getCsvContentLine(AnalysisData AnalysisDatavar) {
String AnalysisDataLine = "";
if(AnalysisDatavar.getName()!=null) {
AnalysisDataLine += AnalysisDatavar.getName().toString();
}
AnalysisDataLine += ",";
if(AnalysisDatavar.getData()!=null) {
AnalysisDataLine += XMLTools.base64Encoding(AnalysisDatavar.getData());
}
AnalysisDataLine += ",";
if(AnalysisDatavar.getMimetype()!=null) {
AnalysisDataLine += AnalysisDatavar.getMimetype().toString();
}
AnalysisDataLine += ",";
return AnalysisDataLine;
}

public String getCsvContentLine(NtSequence NtSequencevar) {
String NtSequenceLine = "";
if(NtSequencevar.getLabel()!=null) {
NtSequenceLine += NtSequencevar.getLabel().toString();
}
NtSequenceLine += ",";
if(NtSequencevar.getSequenceDate()!=null) {
NtSequenceLine += XMLTools.dateToRelaxNgString(NtSequencevar.getSequenceDate());
}
NtSequenceLine += ",";
if(NtSequencevar.getNucleotides()!=null) {
NtSequenceLine += NtSequencevar.getNucleotides().toString();
}
NtSequenceLine += ",";
return NtSequenceLine;
}

public String getCsvHeaderLineTestObject() {
String TestObjectLine = "";
TestObjectLine += "TestObject.description,";
TestObjectLine += "TestObject.testObjectId,";
return TestObjectLine;
}

public String getCsvHeaderLineTherapy() {
String TherapyLine = "";
TherapyLine += "Therapy.therapyMotivation,";
TherapyLine += "Therapy.startDate,";
TherapyLine += "Therapy.stopDate,";
TherapyLine += "Therapy.comment,";
return TherapyLine;
}

public String getCsvHeaderLineTherapyGeneric() {
String TherapyGenericLine = "";
TherapyGenericLine += "TherapyGeneric.id.drugGeneric,";
TherapyGenericLine += "TherapyGeneric.dayDosageMg,";
return TherapyGenericLine;
}

public String getCsvHeaderLinePatientAttributeValue() {
String PatientAttributeValueLine = "";
PatientAttributeValueLine += "PatientAttributeValue.value,";
return PatientAttributeValueLine;
}

public String getCsvHeaderLineTestResult() {
String TestResultLine = "";
TestResultLine += "TestResult.drugGeneric,";
TestResultLine += "TestResult.value,";
TestResultLine += "TestResult.testDate,";
TestResultLine += "TestResult.sampleId,";
TestResultLine += "TestResult.data,";
return TestResultLine;
}

public String getCsvHeaderLineAnalysis() {
String AnalysisLine = "";
AnalysisLine += "Analysis.analysisType,";
AnalysisLine += "Analysis.url,";
AnalysisLine += "Analysis.account,";
AnalysisLine += "Analysis.password,";
AnalysisLine += "Analysis.baseinputfile,";
AnalysisLine += "Analysis.baseoutputfile,";
AnalysisLine += "Analysis.serviceName,";
AnalysisLine += "Analysis.dataoutputfile,";
return AnalysisLine;
}

public String getCsvHeaderLineAaSequence() {
String AaSequenceLine = "";
AaSequenceLine += "AaSequence.protein,";
AaSequenceLine += "AaSequence.firstAaPos,";
AaSequenceLine += "AaSequence.lastAaPos,";
return AaSequenceLine;
}

public String getCsvHeaderLinePatient() {
String PatientImplLine = "";
PatientImplLine += "PatientImpl.patientId,";
PatientImplLine += "PatientImpl.lastName,";
PatientImplLine += "PatientImpl.firstName,";
PatientImplLine += "PatientImpl.birthDate,";
PatientImplLine += "PatientImpl.deathDate,";
return PatientImplLine;
}

public String getCsvHeaderLineTestType() {
String TestTypeLine = "";
TestTypeLine += "TestType.description,";
return TestTypeLine;
}

public String getCsvHeaderLineAttributeGroup() {
String AttributeGroupLine = "";
AttributeGroupLine += "AttributeGroup.groupName,";
return AttributeGroupLine;
}

public String getCsvHeaderLineTestNominalValue() {
String TestNominalValueLine = "";
TestNominalValueLine += "TestNominalValue.value,";
return TestNominalValueLine;
}

public String getCsvHeaderLineViralIsolate() {
String ViralIsolateLine = "";
ViralIsolateLine += "ViralIsolate.sampleId,";
ViralIsolateLine += "ViralIsolate.sampleDate,";
return ViralIsolateLine;
}

public String getCsvHeaderLineValueType() {
String ValueTypeLine = "";
ValueTypeLine += "ValueType.description,";
ValueTypeLine += "ValueType.minimum,";
ValueTypeLine += "ValueType.maximum,";
ValueTypeLine += "ValueType.multiple,";
return ValueTypeLine;
}

public String getCsvHeaderLineTest() {
String TestLine = "";
TestLine += "Test.description,";
return TestLine;
}

public String getCsvHeaderLineAttribute() {
String AttributeLine = "";
AttributeLine += "Attribute.name,";
return AttributeLine;
}

public String getCsvHeaderLineAaInsertion() {
String AaInsertionLine = "";
AaInsertionLine += "AaInsertion.id.insertionPosition,";
AaInsertionLine += "AaInsertion.id.insertionOrder,";
AaInsertionLine += "AaInsertion.aaInsertion,";
AaInsertionLine += "AaInsertion.ntInsertionCodon,";
return AaInsertionLine;
}

public String getCsvHeaderLineTherapyCommercial() {
String TherapyCommercialLine = "";
TherapyCommercialLine += "TherapyCommercial.id.drugCommercial,";
TherapyCommercialLine += "TherapyCommercial.dayDosageUnits,";
return TherapyCommercialLine;
}

public String getCsvHeaderLineDataset() {
String DatasetLine = "";
DatasetLine += "Dataset.description,";
DatasetLine += "Dataset.creationDate,";
DatasetLine += "Dataset.closedDate,";
DatasetLine += "Dataset.revision,";
return DatasetLine;
}

public String getCsvHeaderLineAttributeNominalValue() {
String AttributeNominalValueLine = "";
AttributeNominalValueLine += "AttributeNominalValue.value,";
return AttributeNominalValueLine;
}

public String getCsvHeaderLineAaMutation() {
String AaMutationLine = "";
AaMutationLine += "AaMutation.id.mutationPosition,";
AaMutationLine += "AaMutation.aaReference,";
AaMutationLine += "AaMutation.aaMutation,";
AaMutationLine += "AaMutation.ntReferenceCodon,";
AaMutationLine += "AaMutation.ntMutationCodon,";
return AaMutationLine;
}

public String getCsvHeaderLineAnalysisData() {
String AnalysisDataLine = "";
AnalysisDataLine += "AnalysisData.name,";
AnalysisDataLine += "AnalysisData.data,";
AnalysisDataLine += "AnalysisData.mimetype,";
return AnalysisDataLine;
}

public String getCsvHeaderLineNtSequence() {
String NtSequenceLine = "";
NtSequenceLine += "NtSequence.label,";
NtSequenceLine += "NtSequence.sequenceDate,";
NtSequenceLine += "NtSequence.nucleotides,";
return NtSequenceLine;
}

public String getCsvLineSwitch(Object object, Set<Dataset> datasets) {
if(PatientImplHelper.isInstanceOfPatientImpl(object)) {if(DatasetAccessSolver.getInstance().canAccessPatient(PatientImplHelper.castPatientImplToPatient(object, datasets), datasets)){return getCsvContentLine(PatientImplHelper.castPatientImplToPatient(object, datasets));}{return null;}}else if(object instanceof Dataset) {
if(DatasetAccessSolver.getInstance().canAccessDataset((Dataset)object, datasets)){
return getCsvContentLine((Dataset)object);
}
else {
 return null;
}
}
else if(object instanceof TestResult) {
if(DatasetAccessSolver.getInstance().canAccessTestResult((TestResult)object, datasets)){
return getCsvContentLine((TestResult)object);
}
else {
 return null;
}
}
else if(object instanceof Test) {
if(DatasetAccessSolver.getInstance().canAccessTest((Test)object, datasets)){
return getCsvContentLine((Test)object);
}
else {
 return null;
}
}
else if(object instanceof Analysis) {
if(DatasetAccessSolver.getInstance().canAccessAnalysis((Analysis)object, datasets)){
return getCsvContentLine((Analysis)object);
}
else {
 return null;
}
}
else if(object instanceof AnalysisData) {
if(DatasetAccessSolver.getInstance().canAccessAnalysisData((AnalysisData)object, datasets)){
return getCsvContentLine((AnalysisData)object);
}
else {
 return null;
}
}
else if(object instanceof TestType) {
if(DatasetAccessSolver.getInstance().canAccessTestType((TestType)object, datasets)){
return getCsvContentLine((TestType)object);
}
else {
 return null;
}
}
else if(object instanceof ValueType) {
if(DatasetAccessSolver.getInstance().canAccessValueType((ValueType)object, datasets)){
return getCsvContentLine((ValueType)object);
}
else {
 return null;
}
}
else if(object instanceof TestObject) {
if(DatasetAccessSolver.getInstance().canAccessTestObject((TestObject)object, datasets)){
return getCsvContentLine((TestObject)object);
}
else {
 return null;
}
}
else if(object instanceof TestNominalValue) {
if(DatasetAccessSolver.getInstance().canAccessTestNominalValue((TestNominalValue)object, datasets)){
return getCsvContentLine((TestNominalValue)object);
}
else {
 return null;
}
}
else if(object instanceof PatientAttributeValue) {
if(DatasetAccessSolver.getInstance().canAccessPatientAttributeValue((PatientAttributeValue)object, datasets)){
return getCsvContentLine((PatientAttributeValue)object);
}
else {
 return null;
}
}
else if(object instanceof Attribute) {
if(DatasetAccessSolver.getInstance().canAccessAttribute((Attribute)object, datasets)){
return getCsvContentLine((Attribute)object);
}
else {
 return null;
}
}
else if(object instanceof AttributeGroup) {
if(DatasetAccessSolver.getInstance().canAccessAttributeGroup((AttributeGroup)object, datasets)){
return getCsvContentLine((AttributeGroup)object);
}
else {
 return null;
}
}
else if(object instanceof AttributeNominalValue) {
if(DatasetAccessSolver.getInstance().canAccessAttributeNominalValue((AttributeNominalValue)object, datasets)){
return getCsvContentLine((AttributeNominalValue)object);
}
else {
 return null;
}
}
else if(object instanceof ViralIsolate) {
if(DatasetAccessSolver.getInstance().canAccessViralIsolate((ViralIsolate)object, datasets)){
return getCsvContentLine((ViralIsolate)object);
}
else {
 return null;
}
}
else if(object instanceof NtSequence) {
if(DatasetAccessSolver.getInstance().canAccessNtSequence((NtSequence)object, datasets)){
return getCsvContentLine((NtSequence)object);
}
else {
 return null;
}
}
else if(object instanceof AaSequence) {
if(DatasetAccessSolver.getInstance().canAccessAaSequence((AaSequence)object, datasets)){
return getCsvContentLine((AaSequence)object);
}
else {
 return null;
}
}
else if(object instanceof AaMutation) {
if(DatasetAccessSolver.getInstance().canAccessAaMutation((AaMutation)object, datasets)){
return getCsvContentLine((AaMutation)object);
}
else {
 return null;
}
}
else if(object instanceof AaInsertion) {
if(DatasetAccessSolver.getInstance().canAccessAaInsertion((AaInsertion)object, datasets)){
return getCsvContentLine((AaInsertion)object);
}
else {
 return null;
}
}
else if(object instanceof Therapy) {
if(DatasetAccessSolver.getInstance().canAccessTherapy((Therapy)object, datasets)){
return getCsvContentLine((Therapy)object);
}
else {
 return null;
}
}
else if(object instanceof TherapyCommercial) {
if(DatasetAccessSolver.getInstance().canAccessTherapyCommercial((TherapyCommercial)object, datasets)){
return getCsvContentLine((TherapyCommercial)object);
}
else {
 return null;
}
}
else if(object instanceof TherapyGeneric) {
if(DatasetAccessSolver.getInstance().canAccessTherapyGeneric((TherapyGeneric)object, datasets)){
return getCsvContentLine((TherapyGeneric)object);
}
else {
 return null;
}
}

 return null;
}
public String getCsvHeaderSwitch(Object object) {
if(PatientImplHelper.isInstanceOfPatientImpl(object)) {return getCsvHeaderLinePatient();
}
if(object instanceof Dataset) {
return getCsvHeaderLineDataset();
}
else if(object instanceof TestResult) {
return getCsvHeaderLineTestResult();
}
else if(object instanceof Test) {
return getCsvHeaderLineTest();
}
else if(object instanceof Analysis) {
return getCsvHeaderLineAnalysis();
}
else if(object instanceof AnalysisData) {
return getCsvHeaderLineAnalysisData();
}
else if(object instanceof TestType) {
return getCsvHeaderLineTestType();
}
else if(object instanceof ValueType) {
return getCsvHeaderLineValueType();
}
else if(object instanceof TestObject) {
return getCsvHeaderLineTestObject();
}
else if(object instanceof TestNominalValue) {
return getCsvHeaderLineTestNominalValue();
}
else if(object instanceof PatientAttributeValue) {
return getCsvHeaderLinePatientAttributeValue();
}
else if(object instanceof Attribute) {
return getCsvHeaderLineAttribute();
}
else if(object instanceof AttributeGroup) {
return getCsvHeaderLineAttributeGroup();
}
else if(object instanceof AttributeNominalValue) {
return getCsvHeaderLineAttributeNominalValue();
}
else if(object instanceof ViralIsolate) {
return getCsvHeaderLineViralIsolate();
}
else if(object instanceof NtSequence) {
return getCsvHeaderLineNtSequence();
}
else if(object instanceof AaSequence) {
return getCsvHeaderLineAaSequence();
}
else if(object instanceof AaMutation) {
return getCsvHeaderLineAaMutation();
}
else if(object instanceof AaInsertion) {
return getCsvHeaderLineAaInsertion();
}
else if(object instanceof Therapy) {
return getCsvHeaderLineTherapy();
}
else if(object instanceof TherapyCommercial) {
return getCsvHeaderLineTherapyCommercial();
}
else if(object instanceof TherapyGeneric) {
return getCsvHeaderLineTherapyGeneric();
}

 return null;
}

}