package net.sf.regadb.io.exportCsv;
import net.sf.regadb.db.TestObject;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.TestNominalValue;
import net.sf.regadb.db.SplicingPosition;
import net.sf.regadb.db.QueryDefinitionParameterType;
import net.sf.regadb.db.DatasetAccess;
import net.sf.regadb.db.Analysis;
import net.sf.regadb.db.QueryDefinitionRunParameter;
import net.sf.regadb.db.Therapy;
import net.sf.regadb.db.Event;
import net.sf.regadb.db.QueryDefinitionRun;
import net.sf.regadb.db.AnalysisData;
import net.sf.regadb.db.ValueType;
import net.sf.regadb.db.Attribute;
import net.sf.regadb.db.OpenReadingFrame;
import net.sf.regadb.db.Protein;
import net.sf.regadb.db.AaMutation;
import net.sf.regadb.db.AttributeNominalValue;
import net.sf.regadb.db.AaInsertion;
import net.sf.regadb.db.AnalysisType;
import net.sf.regadb.db.SettingsUser;
import net.sf.regadb.db.PatientEventValue;
import net.sf.regadb.db.TherapyCommercial;
import net.sf.regadb.db.TherapyGeneric;
import net.sf.regadb.db.ResistanceInterpretationTemplate;
import net.sf.regadb.db.PatientDataset;
import net.sf.regadb.db.QueryDefinition;
import net.sf.regadb.db.EventNominalValue;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.CombinedQuery;
import net.sf.regadb.db.TherapyMotivation;
import net.sf.regadb.db.Genome;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.db.DrugCommercial;
import net.sf.regadb.db.CombinedQueryDefinition;
import net.sf.regadb.db.UserAttribute;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestType;
import net.sf.regadb.db.QueryDefinitionParameter;
import net.sf.regadb.db.PatientAttributeValue;
import net.sf.regadb.db.AaSequence;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.DrugClass;
import net.sf.regadb.db.AttributeGroup;
import net.sf.regadb.db.Dataset;
import net.sf.regadb.db.PatientImplHelper;
import java.util.Set;
import net.sf.regadb.util.xml.XMLTools;
import net.sf.regadb.io.datasetAccess.DatasetAccessSolver;

public class ExportToCsv {
public String getCsvContentLine(OpenReadingFrame OpenReadingFramevar) {
String OpenReadingFrameLine = "";
if(OpenReadingFramevar.getName()!=null) {
OpenReadingFrameLine += OpenReadingFramevar.getName().toString();
}
OpenReadingFrameLine += ",";
if(OpenReadingFramevar.getDescription()!=null) {
OpenReadingFrameLine += OpenReadingFramevar.getDescription().toString();
}
OpenReadingFrameLine += ",";
if(OpenReadingFramevar.getReferenceSequence()!=null) {
OpenReadingFrameLine += OpenReadingFramevar.getReferenceSequence().toString();
}
OpenReadingFrameLine += ",";
return OpenReadingFrameLine;
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

public String getCsvContentLine(DrugGeneric DrugGenericvar) {
String DrugGenericLine = "";
if(DrugGenericvar.getGenericId()!=null) {
DrugGenericLine += DrugGenericvar.getGenericId().toString();
}
DrugGenericLine += ",";
if(DrugGenericvar.getGenericName()!=null) {
DrugGenericLine += DrugGenericvar.getGenericName().toString();
}
DrugGenericLine += ",";
if(DrugGenericvar.getResistanceTableOrder()!=null) {
DrugGenericLine += DrugGenericvar.getResistanceTableOrder().toString();
}
DrugGenericLine += ",";
if(DrugGenericvar.getAtcCode()!=null) {
DrugGenericLine += DrugGenericvar.getAtcCode().toString();
}
DrugGenericLine += ",";
return DrugGenericLine;
}

public String getCsvContentLine(AttributeNominalValue AttributeNominalValuevar) {
String AttributeNominalValueLine = "";
if(AttributeNominalValuevar.getValue()!=null) {
AttributeNominalValueLine += AttributeNominalValuevar.getValue().toString();
}
AttributeNominalValueLine += ",";
return AttributeNominalValueLine;
}

public String getCsvContentLine(Genome Genomevar) {
String GenomeLine = "";
if(Genomevar.getOrganismName()!=null) {
GenomeLine += Genomevar.getOrganismName().toString();
}
GenomeLine += ",";
if(Genomevar.getOrganismDescription()!=null) {
GenomeLine += Genomevar.getOrganismDescription().toString();
}
GenomeLine += ",";
if(Genomevar.getGenbankNumber()!=null) {
GenomeLine += Genomevar.getGenbankNumber().toString();
}
GenomeLine += ",";
return GenomeLine;
}

public String getCsvContentLine(AaSequence AaSequencevar) {
String AaSequenceLine = "";
AaSequenceLine += String.valueOf(AaSequencevar.getFirstAaPos());
AaSequenceLine += ",";
AaSequenceLine += String.valueOf(AaSequencevar.getLastAaPos());
AaSequenceLine += ",";
return AaSequenceLine;
}

public String getCsvContentLine(TestType TestTypevar) {
String TestTypeLine = "";
if(TestTypevar.getDescription()!=null) {
TestTypeLine += TestTypevar.getDescription().toString();
}
TestTypeLine += ",";
return TestTypeLine;
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
NtSequenceLine += String.valueOf(NtSequencevar.isAligned());
NtSequenceLine += ",";
return NtSequenceLine;
}

public String getCsvContentLine(AttributeGroup AttributeGroupvar) {
String AttributeGroupLine = "";
if(AttributeGroupvar.getGroupName()!=null) {
AttributeGroupLine += AttributeGroupvar.getGroupName().toString();
}
AttributeGroupLine += ",";
return AttributeGroupLine;
}

public String getCsvContentLine(TherapyGeneric TherapyGenericvar) {
String TherapyGenericLine = "";
if(TherapyGenericvar.getDayDosageMg()!=null) {
TherapyGenericLine += TherapyGenericvar.getDayDosageMg().toString();
}
TherapyGenericLine += ",";
TherapyGenericLine += String.valueOf(TherapyGenericvar.isPlacebo());
TherapyGenericLine += ",";
TherapyGenericLine += String.valueOf(TherapyGenericvar.isBlind());
TherapyGenericLine += ",";
if(TherapyGenericvar.getFrequency()!=null) {
TherapyGenericLine += TherapyGenericvar.getFrequency().toString();
}
TherapyGenericLine += ",";
return TherapyGenericLine;
}

public String getCsvContentLine(Attribute Attributevar) {
String AttributeLine = "";
if(Attributevar.getName()!=null) {
AttributeLine += Attributevar.getName().toString();
}
AttributeLine += ",";
return AttributeLine;
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

public String getCsvContentLine(Analysis Analysisvar) {
String AnalysisLine = "";
if(Analysisvar.getAnalysisType()!=null) {
AnalysisLine += Analysisvar.getAnalysisType().getType();
}
AnalysisLine += ",";
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

public String getCsvContentLine(TestNominalValue TestNominalValuevar) {
String TestNominalValueLine = "";
if(TestNominalValuevar.getValue()!=null) {
TestNominalValueLine += TestNominalValuevar.getValue().toString();
}
TestNominalValueLine += ",";
return TestNominalValueLine;
}

public String getCsvContentLine(Therapy Therapyvar) {
String TherapyLine = "";
if(Therapyvar.getTherapyMotivation()!=null) {
TherapyLine += Therapyvar.getTherapyMotivation().getValue();
}
TherapyLine += ",";
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

public String getCsvContentLine(PatientAttributeValue PatientAttributeValuevar) {
String PatientAttributeValueLine = "";
if(PatientAttributeValuevar.getValue()!=null) {
PatientAttributeValueLine += PatientAttributeValuevar.getValue().toString();
}
PatientAttributeValueLine += ",";
return PatientAttributeValueLine;
}

public String getCsvContentLine(PatientEventValue PatientEventValuevar) {
String PatientEventValueLine = "";
if(PatientEventValuevar.getValue()!=null) {
PatientEventValueLine += PatientEventValuevar.getValue().toString();
}
PatientEventValueLine += ",";
if(PatientEventValuevar.getStartDate()!=null) {
PatientEventValueLine += XMLTools.dateToRelaxNgString(PatientEventValuevar.getStartDate());
}
PatientEventValueLine += ",";
if(PatientEventValuevar.getEndDate()!=null) {
PatientEventValueLine += XMLTools.dateToRelaxNgString(PatientEventValuevar.getEndDate());
}
PatientEventValueLine += ",";
return PatientEventValueLine;
}

public String getCsvContentLine(TherapyCommercial TherapyCommercialvar) {
String TherapyCommercialLine = "";
if(TherapyCommercialvar.getDayDosageUnits()!=null) {
TherapyCommercialLine += TherapyCommercialvar.getDayDosageUnits().toString();
}
TherapyCommercialLine += ",";
TherapyCommercialLine += String.valueOf(TherapyCommercialvar.isPlacebo());
TherapyCommercialLine += ",";
TherapyCommercialLine += String.valueOf(TherapyCommercialvar.isBlind());
TherapyCommercialLine += ",";
if(TherapyCommercialvar.getFrequency()!=null) {
TherapyCommercialLine += TherapyCommercialvar.getFrequency().toString();
}
TherapyCommercialLine += ",";
return TherapyCommercialLine;
}

public String getCsvContentLine(SplicingPosition SplicingPositionvar) {
String SplicingPositionLine = "";
SplicingPositionLine += String.valueOf(SplicingPositionvar.getNtPosition());
SplicingPositionLine += ",";
return SplicingPositionLine;
}

public String getCsvContentLine(Patient PatientImplvar) {
String PatientImplLine = "";
if(PatientImplvar.getPatientId()!=null) {
PatientImplLine += PatientImplvar.getPatientId().toString();
}
PatientImplLine += ",";
return PatientImplLine;
}

public String getCsvContentLine(DrugCommercial DrugCommercialvar) {
String DrugCommercialLine = "";
if(DrugCommercialvar.getName()!=null) {
DrugCommercialLine += DrugCommercialvar.getName().toString();
}
DrugCommercialLine += ",";
if(DrugCommercialvar.getAtcCode()!=null) {
DrugCommercialLine += DrugCommercialvar.getAtcCode().toString();
}
DrugCommercialLine += ",";
return DrugCommercialLine;
}

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

public String getCsvContentLine(Event Eventvar) {
String EventLine = "";
if(Eventvar.getName()!=null) {
EventLine += Eventvar.getName().toString();
}
EventLine += ",";
return EventLine;
}

public String getCsvContentLine(TestResult TestResultvar) {
String TestResultLine = "";
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

public String getCsvContentLine(Test Testvar) {
String TestLine = "";
if(Testvar.getDescription()!=null) {
TestLine += Testvar.getDescription().toString();
}
TestLine += ",";
return TestLine;
}

public String getCsvContentLine(EventNominalValue EventNominalValuevar) {
String EventNominalValueLine = "";
if(EventNominalValuevar.getValue()!=null) {
EventNominalValueLine += EventNominalValuevar.getValue().toString();
}
EventNominalValueLine += ",";
return EventNominalValueLine;
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

public String getCsvContentLine(Protein Proteinvar) {
String ProteinLine = "";
if(Proteinvar.getAbbreviation()!=null) {
ProteinLine += Proteinvar.getAbbreviation().toString();
}
ProteinLine += ",";
if(Proteinvar.getFullName()!=null) {
ProteinLine += Proteinvar.getFullName().toString();
}
ProteinLine += ",";
ProteinLine += String.valueOf(Proteinvar.getStartPosition());
ProteinLine += ",";
ProteinLine += String.valueOf(Proteinvar.getStopPosition());
ProteinLine += ",";
return ProteinLine;
}

public String getCsvHeaderLineOpenReadingFrame() {
String OpenReadingFrameLine = "";
OpenReadingFrameLine += "OpenReadingFrame.name,";
OpenReadingFrameLine += "OpenReadingFrame.description,";
OpenReadingFrameLine += "OpenReadingFrame.referenceSequence,";
return OpenReadingFrameLine;
}

public String getCsvHeaderLineViralIsolate() {
String ViralIsolateLine = "";
ViralIsolateLine += "ViralIsolate.sampleId,";
ViralIsolateLine += "ViralIsolate.sampleDate,";
return ViralIsolateLine;
}

public String getCsvHeaderLineDrugGeneric() {
String DrugGenericLine = "";
DrugGenericLine += "DrugGeneric.genericId,";
DrugGenericLine += "DrugGeneric.genericName,";
DrugGenericLine += "DrugGeneric.resistanceTableOrder,";
DrugGenericLine += "DrugGeneric.atcCode,";
return DrugGenericLine;
}

public String getCsvHeaderLineAttributeNominalValue() {
String AttributeNominalValueLine = "";
AttributeNominalValueLine += "AttributeNominalValue.value,";
return AttributeNominalValueLine;
}

public String getCsvHeaderLineGenome() {
String GenomeLine = "";
GenomeLine += "Genome.organismName,";
GenomeLine += "Genome.organismDescription,";
GenomeLine += "Genome.genbankNumber,";
return GenomeLine;
}

public String getCsvHeaderLineAaSequence() {
String AaSequenceLine = "";
AaSequenceLine += "AaSequence.firstAaPos,";
AaSequenceLine += "AaSequence.lastAaPos,";
return AaSequenceLine;
}

public String getCsvHeaderLineTestType() {
String TestTypeLine = "";
TestTypeLine += "TestType.description,";
return TestTypeLine;
}

public String getCsvHeaderLineNtSequence() {
String NtSequenceLine = "";
NtSequenceLine += "NtSequence.label,";
NtSequenceLine += "NtSequence.sequenceDate,";
NtSequenceLine += "NtSequence.nucleotides,";
NtSequenceLine += "NtSequence.aligned,";
return NtSequenceLine;
}

public String getCsvHeaderLineAttributeGroup() {
String AttributeGroupLine = "";
AttributeGroupLine += "AttributeGroup.groupName,";
return AttributeGroupLine;
}

public String getCsvHeaderLineTherapyGeneric() {
String TherapyGenericLine = "";
TherapyGenericLine += "TherapyGeneric.dayDosageMg,";
TherapyGenericLine += "TherapyGeneric.placebo,";
TherapyGenericLine += "TherapyGeneric.blind,";
TherapyGenericLine += "TherapyGeneric.frequency,";
return TherapyGenericLine;
}

public String getCsvHeaderLineAttribute() {
String AttributeLine = "";
AttributeLine += "Attribute.name,";
return AttributeLine;
}

public String getCsvHeaderLineAnalysisData() {
String AnalysisDataLine = "";
AnalysisDataLine += "AnalysisData.name,";
AnalysisDataLine += "AnalysisData.data,";
AnalysisDataLine += "AnalysisData.mimetype,";
return AnalysisDataLine;
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

public String getCsvHeaderLineAaInsertion() {
String AaInsertionLine = "";
AaInsertionLine += "AaInsertion.id.insertionPosition,";
AaInsertionLine += "AaInsertion.id.insertionOrder,";
AaInsertionLine += "AaInsertion.aaInsertion,";
AaInsertionLine += "AaInsertion.ntInsertionCodon,";
return AaInsertionLine;
}

public String getCsvHeaderLineTestNominalValue() {
String TestNominalValueLine = "";
TestNominalValueLine += "TestNominalValue.value,";
return TestNominalValueLine;
}

public String getCsvHeaderLineTherapy() {
String TherapyLine = "";
TherapyLine += "Therapy.therapyMotivation,";
TherapyLine += "Therapy.startDate,";
TherapyLine += "Therapy.stopDate,";
TherapyLine += "Therapy.comment,";
return TherapyLine;
}

public String getCsvHeaderLinePatientAttributeValue() {
String PatientAttributeValueLine = "";
PatientAttributeValueLine += "PatientAttributeValue.value,";
return PatientAttributeValueLine;
}

public String getCsvHeaderLinePatientEventValue() {
String PatientEventValueLine = "";
PatientEventValueLine += "PatientEventValue.value,";
PatientEventValueLine += "PatientEventValue.startDate,";
PatientEventValueLine += "PatientEventValue.endDate,";
return PatientEventValueLine;
}

public String getCsvHeaderLineTherapyCommercial() {
String TherapyCommercialLine = "";
TherapyCommercialLine += "TherapyCommercial.dayDosageUnits,";
TherapyCommercialLine += "TherapyCommercial.placebo,";
TherapyCommercialLine += "TherapyCommercial.blind,";
TherapyCommercialLine += "TherapyCommercial.frequency,";
return TherapyCommercialLine;
}

public String getCsvHeaderLineSplicingPosition() {
String SplicingPositionLine = "";
SplicingPositionLine += "SplicingPosition.ntPosition,";
return SplicingPositionLine;
}

public String getCsvHeaderLinePatient() {
String PatientImplLine = "";
PatientImplLine += "PatientImpl.patientId,";
return PatientImplLine;
}

public String getCsvHeaderLineDrugCommercial() {
String DrugCommercialLine = "";
DrugCommercialLine += "DrugCommercial.name,";
DrugCommercialLine += "DrugCommercial.atcCode,";
return DrugCommercialLine;
}

public String getCsvHeaderLineTestObject() {
String TestObjectLine = "";
TestObjectLine += "TestObject.description,";
TestObjectLine += "TestObject.testObjectId,";
return TestObjectLine;
}

public String getCsvHeaderLineValueType() {
String ValueTypeLine = "";
ValueTypeLine += "ValueType.description,";
ValueTypeLine += "ValueType.minimum,";
ValueTypeLine += "ValueType.maximum,";
ValueTypeLine += "ValueType.multiple,";
return ValueTypeLine;
}

public String getCsvHeaderLineDataset() {
String DatasetLine = "";
DatasetLine += "Dataset.description,";
DatasetLine += "Dataset.creationDate,";
DatasetLine += "Dataset.closedDate,";
DatasetLine += "Dataset.revision,";
return DatasetLine;
}

public String getCsvHeaderLineEvent() {
String EventLine = "";
EventLine += "Event.name,";
return EventLine;
}

public String getCsvHeaderLineTestResult() {
String TestResultLine = "";
TestResultLine += "TestResult.value,";
TestResultLine += "TestResult.testDate,";
TestResultLine += "TestResult.sampleId,";
TestResultLine += "TestResult.data,";
return TestResultLine;
}

public String getCsvHeaderLineTest() {
String TestLine = "";
TestLine += "Test.description,";
return TestLine;
}

public String getCsvHeaderLineEventNominalValue() {
String EventNominalValueLine = "";
EventNominalValueLine += "EventNominalValue.value,";
return EventNominalValueLine;
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

public String getCsvHeaderLineProtein() {
String ProteinLine = "";
ProteinLine += "Protein.abbreviation,";
ProteinLine += "Protein.fullName,";
ProteinLine += "Protein.startPosition,";
ProteinLine += "Protein.stopPosition,";
return ProteinLine;
}

public String getCsvLineSwitch(Object object, Set<Dataset> datasets, Set<Integer> accessiblePatients) {
if(PatientImplHelper.isInstanceOfPatientImpl(object)) {
Patient p_casted = PatientImplHelper.castPatientImplToPatient(object, datasets);
if(DatasetAccessSolver.getInstance().canAccessPatient(p_casted, datasets, accessiblePatients)){
return getCsvContentLine(p_casted);
}
else {
return null;
}
}
else if(object instanceof PatientEventValue) {
if(DatasetAccessSolver.getInstance().canAccessPatientEventValue((PatientEventValue)object, datasets, accessiblePatients)){
return getCsvContentLine((PatientEventValue)object);
}
else {
 return null;
}
}
else if(object instanceof Event) {
if(DatasetAccessSolver.getInstance().canAccessEvent((Event)object, datasets, accessiblePatients)){
return getCsvContentLine((Event)object);
}
else {
 return null;
}
}
else if(object instanceof ValueType) {
if(DatasetAccessSolver.getInstance().canAccessValueType((ValueType)object, datasets, accessiblePatients)){
return getCsvContentLine((ValueType)object);
}
else {
 return null;
}
}
else if(object instanceof EventNominalValue) {
if(DatasetAccessSolver.getInstance().canAccessEventNominalValue((EventNominalValue)object, datasets, accessiblePatients)){
return getCsvContentLine((EventNominalValue)object);
}
else {
 return null;
}
}
else if(object instanceof Dataset) {
if(DatasetAccessSolver.getInstance().canAccessDataset((Dataset)object, datasets, accessiblePatients)){
return getCsvContentLine((Dataset)object);
}
else {
 return null;
}
}
else if(object instanceof TestResult) {
if(DatasetAccessSolver.getInstance().canAccessTestResult((TestResult)object, datasets, accessiblePatients)){
return getCsvContentLine((TestResult)object);
}
else {
 return null;
}
}
else if(object instanceof Test) {
if(DatasetAccessSolver.getInstance().canAccessTest((Test)object, datasets, accessiblePatients)){
return getCsvContentLine((Test)object);
}
else {
 return null;
}
}
else if(object instanceof Analysis) {
if(DatasetAccessSolver.getInstance().canAccessAnalysis((Analysis)object, datasets, accessiblePatients)){
return getCsvContentLine((Analysis)object);
}
else {
 return null;
}
}
else if(object instanceof AnalysisData) {
if(DatasetAccessSolver.getInstance().canAccessAnalysisData((AnalysisData)object, datasets, accessiblePatients)){
return getCsvContentLine((AnalysisData)object);
}
else {
 return null;
}
}
else if(object instanceof TestType) {
if(DatasetAccessSolver.getInstance().canAccessTestType((TestType)object, datasets, accessiblePatients)){
return getCsvContentLine((TestType)object);
}
else {
 return null;
}
}
else if(object instanceof Genome) {
if(DatasetAccessSolver.getInstance().canAccessGenome((Genome)object, datasets, accessiblePatients)){
return getCsvContentLine((Genome)object);
}
else {
 return null;
}
}
else if(object instanceof DrugGeneric) {
if(DatasetAccessSolver.getInstance().canAccessDrugGeneric((DrugGeneric)object, datasets, accessiblePatients)){
return getCsvContentLine((DrugGeneric)object);
}
else {
 return null;
}
}
else if(object instanceof OpenReadingFrame) {
if(DatasetAccessSolver.getInstance().canAccessOpenReadingFrame((OpenReadingFrame)object, datasets, accessiblePatients)){
return getCsvContentLine((OpenReadingFrame)object);
}
else {
 return null;
}
}
else if(object instanceof Protein) {
if(DatasetAccessSolver.getInstance().canAccessProtein((Protein)object, datasets, accessiblePatients)){
return getCsvContentLine((Protein)object);
}
else {
 return null;
}
}
else if(object instanceof SplicingPosition) {
if(DatasetAccessSolver.getInstance().canAccessSplicingPosition((SplicingPosition)object, datasets, accessiblePatients)){
return getCsvContentLine((SplicingPosition)object);
}
else {
 return null;
}
}
else if(object instanceof TestObject) {
if(DatasetAccessSolver.getInstance().canAccessTestObject((TestObject)object, datasets, accessiblePatients)){
return getCsvContentLine((TestObject)object);
}
else {
 return null;
}
}
else if(object instanceof TestNominalValue) {
if(DatasetAccessSolver.getInstance().canAccessTestNominalValue((TestNominalValue)object, datasets, accessiblePatients)){
return getCsvContentLine((TestNominalValue)object);
}
else {
 return null;
}
}
else if(object instanceof PatientAttributeValue) {
if(DatasetAccessSolver.getInstance().canAccessPatientAttributeValue((PatientAttributeValue)object, datasets, accessiblePatients)){
return getCsvContentLine((PatientAttributeValue)object);
}
else {
 return null;
}
}
else if(object instanceof Attribute) {
if(DatasetAccessSolver.getInstance().canAccessAttribute((Attribute)object, datasets, accessiblePatients)){
return getCsvContentLine((Attribute)object);
}
else {
 return null;
}
}
else if(object instanceof AttributeGroup) {
if(DatasetAccessSolver.getInstance().canAccessAttributeGroup((AttributeGroup)object, datasets, accessiblePatients)){
return getCsvContentLine((AttributeGroup)object);
}
else {
 return null;
}
}
else if(object instanceof AttributeNominalValue) {
if(DatasetAccessSolver.getInstance().canAccessAttributeNominalValue((AttributeNominalValue)object, datasets, accessiblePatients)){
return getCsvContentLine((AttributeNominalValue)object);
}
else {
 return null;
}
}
else if(object instanceof ViralIsolate) {
if(DatasetAccessSolver.getInstance().canAccessViralIsolate((ViralIsolate)object, datasets, accessiblePatients)){
return getCsvContentLine((ViralIsolate)object);
}
else {
 return null;
}
}
else if(object instanceof NtSequence) {
if(DatasetAccessSolver.getInstance().canAccessNtSequence((NtSequence)object, datasets, accessiblePatients)){
return getCsvContentLine((NtSequence)object);
}
else {
 return null;
}
}
else if(object instanceof AaSequence) {
if(DatasetAccessSolver.getInstance().canAccessAaSequence((AaSequence)object, datasets, accessiblePatients)){
return getCsvContentLine((AaSequence)object);
}
else {
 return null;
}
}
else if(object instanceof AaMutation) {
if(DatasetAccessSolver.getInstance().canAccessAaMutation((AaMutation)object, datasets, accessiblePatients)){
return getCsvContentLine((AaMutation)object);
}
else {
 return null;
}
}
else if(object instanceof AaInsertion) {
if(DatasetAccessSolver.getInstance().canAccessAaInsertion((AaInsertion)object, datasets, accessiblePatients)){
return getCsvContentLine((AaInsertion)object);
}
else {
 return null;
}
}
else if(object instanceof Therapy) {
if(DatasetAccessSolver.getInstance().canAccessTherapy((Therapy)object, datasets, accessiblePatients)){
return getCsvContentLine((Therapy)object);
}
else {
 return null;
}
}
else if(object instanceof TherapyCommercial) {
if(DatasetAccessSolver.getInstance().canAccessTherapyCommercial((TherapyCommercial)object, datasets, accessiblePatients)){
return getCsvContentLine((TherapyCommercial)object);
}
else {
 return null;
}
}
else if(object instanceof DrugCommercial) {
if(DatasetAccessSolver.getInstance().canAccessDrugCommercial((DrugCommercial)object, datasets, accessiblePatients)){
return getCsvContentLine((DrugCommercial)object);
}
else {
 return null;
}
}
else if(object instanceof TherapyGeneric) {
if(DatasetAccessSolver.getInstance().canAccessTherapyGeneric((TherapyGeneric)object, datasets, accessiblePatients)){
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
else if(object instanceof PatientEventValue) {
return getCsvHeaderLinePatientEventValue();
}
else if(object instanceof Event) {
return getCsvHeaderLineEvent();
}
else if(object instanceof ValueType) {
return getCsvHeaderLineValueType();
}
else if(object instanceof EventNominalValue) {
return getCsvHeaderLineEventNominalValue();
}
else if(object instanceof Dataset) {
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
else if(object instanceof Genome) {
return getCsvHeaderLineGenome();
}
else if(object instanceof DrugGeneric) {
return getCsvHeaderLineDrugGeneric();
}
else if(object instanceof OpenReadingFrame) {
return getCsvHeaderLineOpenReadingFrame();
}
else if(object instanceof Protein) {
return getCsvHeaderLineProtein();
}
else if(object instanceof SplicingPosition) {
return getCsvHeaderLineSplicingPosition();
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
else if(object instanceof DrugCommercial) {
return getCsvHeaderLineDrugCommercial();
}
else if(object instanceof TherapyGeneric) {
return getCsvHeaderLineTherapyGeneric();
}

 return null;
}

}