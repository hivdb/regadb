package net.sf.regadb.io.persistence;

import net.sf.regadb.db.Patient;
import org.hibernate.Hibernate;
import net.sf.regadb.db.Dataset;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.PatientAttributeValue;
import net.sf.regadb.db.PatientEventValue;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.db.Therapy;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.TestNominalValue;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.AttributeNominalValue;
import net.sf.regadb.db.Attribute;
import net.sf.regadb.db.EventNominalValue;
import net.sf.regadb.db.Event;
import net.sf.regadb.db.TherapyMotivation;
import net.sf.regadb.db.TherapyCommercial;
import net.sf.regadb.db.TherapyGeneric;
import net.sf.regadb.db.Analysis;
import net.sf.regadb.db.TestType;
import net.sf.regadb.db.DrugClass;
import net.sf.regadb.db.Genome;
import net.sf.regadb.db.DrugCommercial;
import net.sf.regadb.db.AaSequence;
import net.sf.regadb.db.ValueType;
import net.sf.regadb.db.AttributeGroup;
import net.sf.regadb.db.TherapyCommercialId;
import net.sf.regadb.db.TherapyGenericId;
import net.sf.regadb.db.AnalysisType;
import net.sf.regadb.db.AnalysisData;
import net.sf.regadb.db.TestObject;
import net.sf.regadb.db.OpenReadingFrame;
import net.sf.regadb.db.Protein;
import net.sf.regadb.db.AaMutation;
import net.sf.regadb.db.AaInsertion;
import net.sf.regadb.db.SplicingPosition;
import net.sf.regadb.db.AaMutationId;
import net.sf.regadb.db.AaInsertionId;
public class ExportToPersistentObjects {
public void initialize(Patient p){
write(p);
}

private void write(Patient patient){
	if(!Hibernate.isInitialized(patient.getDatasets())){
	Hibernate.initialize(patient.getDatasets());
	for(Dataset dataset:patient.getDatasets()){
	write(dataset);
	}
	}
	if(!Hibernate.isInitialized(patient.getTestResults())){
	Hibernate.initialize(patient.getTestResults());
	for(TestResult testResult:patient.getTestResults()){
	write(testResult);
	}
	}
	if(!Hibernate.isInitialized(patient.getPatientAttributeValues())){
	Hibernate.initialize(patient.getPatientAttributeValues());
	for(PatientAttributeValue patientAttributeValue:patient.getPatientAttributeValues()){
	write(patientAttributeValue);
	}
	}
	if(!Hibernate.isInitialized(patient.getPatientEventValues())){
	Hibernate.initialize(patient.getPatientEventValues());
	for(PatientEventValue patientEventValue:patient.getPatientEventValues()){
	write(patientEventValue);
	}
	}
	if(!Hibernate.isInitialized(patient.getViralIsolates())){
	Hibernate.initialize(patient.getViralIsolates());
	for(ViralIsolate viralIsolate:patient.getViralIsolates()){
	write(viralIsolate);
	}
	}
	if(!Hibernate.isInitialized(patient.getTherapies())){
	Hibernate.initialize(patient.getTherapies());
	for(Therapy therapy:patient.getTherapies()){
	write(therapy);
	}
	}
	if(!Hibernate.isInitialized(patient.getSourceDataset())){
	Hibernate.initialize(patient.getSourceDataset());
	if(patient.getSourceDataset() != null)write(patient.getSourceDataset());
	}
}

private void write(Dataset dataset){
}

private void write(TestResult testResult){
	if(!Hibernate.isInitialized(testResult.getTest())){
	Hibernate.initialize(testResult.getTest());
	if(testResult.getTest() != null)write(testResult.getTest());
	}
	if(!Hibernate.isInitialized(testResult.getDrugGeneric())){
	Hibernate.initialize(testResult.getDrugGeneric());
	if(testResult.getDrugGeneric() != null)write(testResult.getDrugGeneric());
	}
	if(!Hibernate.isInitialized(testResult.getViralIsolate())){
	Hibernate.initialize(testResult.getViralIsolate());
	if(testResult.getViralIsolate() != null)write(testResult.getViralIsolate());
	}
	if(!Hibernate.isInitialized(testResult.getTestNominalValue())){
	Hibernate.initialize(testResult.getTestNominalValue());
	if(testResult.getTestNominalValue() != null)write(testResult.getTestNominalValue());
	}
	if(!Hibernate.isInitialized(testResult.getNtSequence())){
	Hibernate.initialize(testResult.getNtSequence());
	if(testResult.getNtSequence() != null)write(testResult.getNtSequence());
	}
}

private void write(PatientAttributeValue patientAttributeValue){
	if(!Hibernate.isInitialized(patientAttributeValue.getAttributeNominalValue())){
	Hibernate.initialize(patientAttributeValue.getAttributeNominalValue());
	if(patientAttributeValue.getAttributeNominalValue() != null)write(patientAttributeValue.getAttributeNominalValue());
	}
	if(!Hibernate.isInitialized(patientAttributeValue.getAttribute())){
	Hibernate.initialize(patientAttributeValue.getAttribute());
	if(patientAttributeValue.getAttribute() != null)write(patientAttributeValue.getAttribute());
	}
}

private void write(PatientEventValue patientEventValue){
	if(!Hibernate.isInitialized(patientEventValue.getEventNominalValue())){
	Hibernate.initialize(patientEventValue.getEventNominalValue());
	if(patientEventValue.getEventNominalValue() != null)write(patientEventValue.getEventNominalValue());
	}
	if(!Hibernate.isInitialized(patientEventValue.getEvent())){
	Hibernate.initialize(patientEventValue.getEvent());
	if(patientEventValue.getEvent() != null)write(patientEventValue.getEvent());
	}
}

private void write(ViralIsolate viralIsolate){
	if(!Hibernate.isInitialized(viralIsolate.getTestResults())){
	Hibernate.initialize(viralIsolate.getTestResults());
	for(TestResult testResult:viralIsolate.getTestResults()){
	write(testResult);
	}
	}
	if(!Hibernate.isInitialized(viralIsolate.getNtSequences())){
	Hibernate.initialize(viralIsolate.getNtSequences());
	for(NtSequence ntSequence:viralIsolate.getNtSequences()){
	write(ntSequence);
	}
	}
}

private void write(Therapy therapy){
	if(!Hibernate.isInitialized(therapy.getTherapyMotivation())){
	Hibernate.initialize(therapy.getTherapyMotivation());
	if(therapy.getTherapyMotivation() != null)write(therapy.getTherapyMotivation());
	}
	if(!Hibernate.isInitialized(therapy.getTherapyCommercials())){
	Hibernate.initialize(therapy.getTherapyCommercials());
	for(TherapyCommercial therapyCommercial:therapy.getTherapyCommercials()){
	write(therapyCommercial);
	}
	}
	if(!Hibernate.isInitialized(therapy.getTherapyGenerics())){
	Hibernate.initialize(therapy.getTherapyGenerics());
	for(TherapyGeneric therapyGeneric:therapy.getTherapyGenerics()){
	write(therapyGeneric);
	}
	}
}

private void write(Test test){
	if(!Hibernate.isInitialized(test.getAnalysis())){
	Hibernate.initialize(test.getAnalysis());
	if(test.getAnalysis() != null)write(test.getAnalysis());
	}
	if(!Hibernate.isInitialized(test.getTestType())){
	Hibernate.initialize(test.getTestType());
	if(test.getTestType() != null)write(test.getTestType());
	}
}

private void write(DrugGeneric drugGeneric){
	if(!Hibernate.isInitialized(drugGeneric.getDrugClass())){
	Hibernate.initialize(drugGeneric.getDrugClass());
	if(drugGeneric.getDrugClass() != null)write(drugGeneric.getDrugClass());
	}
	if(!Hibernate.isInitialized(drugGeneric.getGenomes())){
	Hibernate.initialize(drugGeneric.getGenomes());
	for(Genome genome:drugGeneric.getGenomes()){
	write(genome);
	}
	}
	if(!Hibernate.isInitialized(drugGeneric.getDrugCommercials())){
	Hibernate.initialize(drugGeneric.getDrugCommercials());
	for(DrugCommercial drugCommercial:drugGeneric.getDrugCommercials()){
	write(drugCommercial);
	}
	}
}

private void write(TestNominalValue testNominalValue){
	if(!Hibernate.isInitialized(testNominalValue.getTestType())){
	Hibernate.initialize(testNominalValue.getTestType());
	if(testNominalValue.getTestType() != null)write(testNominalValue.getTestType());
	}
}

private void write(NtSequence ntSequence){
	if(!Hibernate.isInitialized(ntSequence.getTestResults())){
	Hibernate.initialize(ntSequence.getTestResults());
	for(TestResult testResult:ntSequence.getTestResults()){
	write(testResult);
	}
	}
	if(!Hibernate.isInitialized(ntSequence.getViralIsolate())){
	Hibernate.initialize(ntSequence.getViralIsolate());
	if(ntSequence.getViralIsolate() != null)write(ntSequence.getViralIsolate());
	}
	if(!Hibernate.isInitialized(ntSequence.getAaSequences())){
	Hibernate.initialize(ntSequence.getAaSequences());
	for(AaSequence aaSequence:ntSequence.getAaSequences()){
	write(aaSequence);
	}
	}
}

private void write(AttributeNominalValue attributeNominalValue){
	if(!Hibernate.isInitialized(attributeNominalValue.getAttribute())){
	Hibernate.initialize(attributeNominalValue.getAttribute());
	if(attributeNominalValue.getAttribute() != null)write(attributeNominalValue.getAttribute());
	}
}

private void write(Attribute attribute){
	if(!Hibernate.isInitialized(attribute.getValueType())){
	Hibernate.initialize(attribute.getValueType());
	if(attribute.getValueType() != null)write(attribute.getValueType());
	}
	if(!Hibernate.isInitialized(attribute.getAttributeGroup())){
	Hibernate.initialize(attribute.getAttributeGroup());
	if(attribute.getAttributeGroup() != null)write(attribute.getAttributeGroup());
	}
	if(!Hibernate.isInitialized(attribute.getAttributeNominalValues())){
	Hibernate.initialize(attribute.getAttributeNominalValues());
	for(AttributeNominalValue attributeNominalValue:attribute.getAttributeNominalValues()){
	write(attributeNominalValue);
	}
	}
}

private void write(EventNominalValue eventNominalValue){
	if(!Hibernate.isInitialized(eventNominalValue.getEvent())){
	Hibernate.initialize(eventNominalValue.getEvent());
	if(eventNominalValue.getEvent() != null)write(eventNominalValue.getEvent());
	}
}

private void write(Event event){
	if(!Hibernate.isInitialized(event.getValueType())){
	Hibernate.initialize(event.getValueType());
	if(event.getValueType() != null)write(event.getValueType());
	}
	if(!Hibernate.isInitialized(event.getEventNominalValues())){
	Hibernate.initialize(event.getEventNominalValues());
	for(EventNominalValue eventNominalValue:event.getEventNominalValues()){
	write(eventNominalValue);
	}
	}
}

private void write(TherapyMotivation therapyMotivation){
}

private void write(TherapyCommercial therapyCommercial){
	if(!Hibernate.isInitialized(therapyCommercial.getId())){
	Hibernate.initialize(therapyCommercial.getId());
	if(therapyCommercial.getId() != null)write(therapyCommercial.getId());
	}
}

private void write(TherapyGeneric therapyGeneric){
	if(!Hibernate.isInitialized(therapyGeneric.getId())){
	Hibernate.initialize(therapyGeneric.getId());
	if(therapyGeneric.getId() != null)write(therapyGeneric.getId());
	}
}

private void write(Analysis analysis){
	if(!Hibernate.isInitialized(analysis.getTests())){
	Hibernate.initialize(analysis.getTests());
	for(Test test:analysis.getTests()){
	write(test);
	}
	}
	if(!Hibernate.isInitialized(analysis.getAnalysisType())){
	Hibernate.initialize(analysis.getAnalysisType());
	if(analysis.getAnalysisType() != null)write(analysis.getAnalysisType());
	}
	if(!Hibernate.isInitialized(analysis.getAnalysisDatas())){
	Hibernate.initialize(analysis.getAnalysisDatas());
	for(AnalysisData analysisData:analysis.getAnalysisDatas()){
	write(analysisData);
	}
	}
}

private void write(TestType testType){
	if(!Hibernate.isInitialized(testType.getValueType())){
	Hibernate.initialize(testType.getValueType());
	if(testType.getValueType() != null)write(testType.getValueType());
	}
	if(!Hibernate.isInitialized(testType.getGenome())){
	Hibernate.initialize(testType.getGenome());
	if(testType.getGenome() != null)write(testType.getGenome());
	}
	if(!Hibernate.isInitialized(testType.getTestObject())){
	Hibernate.initialize(testType.getTestObject());
	if(testType.getTestObject() != null)write(testType.getTestObject());
	}
	if(!Hibernate.isInitialized(testType.getTestNominalValues())){
	Hibernate.initialize(testType.getTestNominalValues());
	for(TestNominalValue testNominalValue:testType.getTestNominalValues()){
	write(testNominalValue);
	}
	}
}

private void write(DrugClass drugClass){
	if(!Hibernate.isInitialized(drugClass.getDrugGenerics())){
	Hibernate.initialize(drugClass.getDrugGenerics());
	for(DrugGeneric drugGeneric:drugClass.getDrugGenerics()){
	write(drugGeneric);
	}
	}
}

private void write(Genome genome){
	if(!Hibernate.isInitialized(genome.getDrugGenerics())){
	Hibernate.initialize(genome.getDrugGenerics());
	for(DrugGeneric drugGeneric:genome.getDrugGenerics()){
	write(drugGeneric);
	}
	}
	if(!Hibernate.isInitialized(genome.getOpenReadingFrames())){
	Hibernate.initialize(genome.getOpenReadingFrames());
	for(OpenReadingFrame openReadingFrame:genome.getOpenReadingFrames()){
	write(openReadingFrame);
	}
	}
}

private void write(DrugCommercial drugCommercial){
	if(!Hibernate.isInitialized(drugCommercial.getDrugGenerics())){
	Hibernate.initialize(drugCommercial.getDrugGenerics());
	for(DrugGeneric drugGeneric:drugCommercial.getDrugGenerics()){
	write(drugGeneric);
	}
	}
}

private void write(AaSequence aaSequence){
	if(!Hibernate.isInitialized(aaSequence.getNtSequence())){
	Hibernate.initialize(aaSequence.getNtSequence());
	if(aaSequence.getNtSequence() != null)write(aaSequence.getNtSequence());
	}
	if(!Hibernate.isInitialized(aaSequence.getProtein())){
	Hibernate.initialize(aaSequence.getProtein());
	if(aaSequence.getProtein() != null)write(aaSequence.getProtein());
	}
	if(!Hibernate.isInitialized(aaSequence.getAaMutations())){
	Hibernate.initialize(aaSequence.getAaMutations());
	for(AaMutation aaMutation:aaSequence.getAaMutations()){
	write(aaMutation);
	}
	}
	if(!Hibernate.isInitialized(aaSequence.getAaInsertions())){
	Hibernate.initialize(aaSequence.getAaInsertions());
	for(AaInsertion aaInsertion:aaSequence.getAaInsertions()){
	write(aaInsertion);
	}
	}
}

private void write(ValueType valueType){
}

private void write(AttributeGroup attributeGroup){
}

private void write(TherapyCommercialId therapyCommercialId){
	if(!Hibernate.isInitialized(therapyCommercialId.getDrugCommercial())){
	Hibernate.initialize(therapyCommercialId.getDrugCommercial());
	if(therapyCommercialId.getDrugCommercial() != null)write(therapyCommercialId.getDrugCommercial());
	}
	if(!Hibernate.isInitialized(therapyCommercialId.getTherapy())){
	Hibernate.initialize(therapyCommercialId.getTherapy());
	if(therapyCommercialId.getTherapy() != null)write(therapyCommercialId.getTherapy());
	}
}

private void write(TherapyGenericId therapyGenericId){
	if(!Hibernate.isInitialized(therapyGenericId.getDrugGeneric())){
	Hibernate.initialize(therapyGenericId.getDrugGeneric());
	if(therapyGenericId.getDrugGeneric() != null)write(therapyGenericId.getDrugGeneric());
	}
	if(!Hibernate.isInitialized(therapyGenericId.getTherapy())){
	Hibernate.initialize(therapyGenericId.getTherapy());
	if(therapyGenericId.getTherapy() != null)write(therapyGenericId.getTherapy());
	}
}

private void write(AnalysisType analysisType){
}

private void write(AnalysisData analysisData){
	if(!Hibernate.isInitialized(analysisData.getAnalysis())){
	Hibernate.initialize(analysisData.getAnalysis());
	if(analysisData.getAnalysis() != null)write(analysisData.getAnalysis());
	}
}

private void write(TestObject testObject){
}

private void write(OpenReadingFrame openReadingFrame){
	if(!Hibernate.isInitialized(openReadingFrame.getProteins())){
	Hibernate.initialize(openReadingFrame.getProteins());
	for(Protein protein:openReadingFrame.getProteins()){
	write(protein);
	}
	}
	if(!Hibernate.isInitialized(openReadingFrame.getGenome())){
	Hibernate.initialize(openReadingFrame.getGenome());
	if(openReadingFrame.getGenome() != null)write(openReadingFrame.getGenome());
	}
}

private void write(Protein protein){
	if(!Hibernate.isInitialized(protein.getOpenReadingFrame())){
	Hibernate.initialize(protein.getOpenReadingFrame());
	if(protein.getOpenReadingFrame() != null)write(protein.getOpenReadingFrame());
	}
	if(!Hibernate.isInitialized(protein.getSplicingPositions())){
	Hibernate.initialize(protein.getSplicingPositions());
	for(SplicingPosition splicingPosition:protein.getSplicingPositions()){
	write(splicingPosition);
	}
	}
}

private void write(AaMutation aaMutation){
	if(!Hibernate.isInitialized(aaMutation.getId())){
	Hibernate.initialize(aaMutation.getId());
	if(aaMutation.getId() != null)write(aaMutation.getId());
	}
}

private void write(AaInsertion aaInsertion){
	if(!Hibernate.isInitialized(aaInsertion.getId())){
	Hibernate.initialize(aaInsertion.getId());
	if(aaInsertion.getId() != null)write(aaInsertion.getId());
	}
}

private void write(SplicingPosition splicingPosition){
	if(!Hibernate.isInitialized(splicingPosition.getProtein())){
	Hibernate.initialize(splicingPosition.getProtein());
	if(splicingPosition.getProtein() != null)write(splicingPosition.getProtein());
	}
}

private void write(AaMutationId aaMutationId){
}

private void write(AaInsertionId aaInsertionId){
}


}