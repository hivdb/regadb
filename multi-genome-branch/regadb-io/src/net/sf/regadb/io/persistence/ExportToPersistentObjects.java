package net.sf.regadb.io.persistence;

import net.sf.regadb.db.Patient;
import org.hibernate.Hibernate;
import net.sf.regadb.db.Dataset;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.PatientAttributeValue;
import net.sf.regadb.db.PatientEventValue;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.db.Therapy;
import net.sf.regadb.db.SettingsUser;
import net.sf.regadb.db.DatasetAccess;
import net.sf.regadb.db.AttributeNominalValue;
import net.sf.regadb.db.Attribute;
import net.sf.regadb.db.EventNominalValue;
import net.sf.regadb.db.Event;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.TherapyMotivation;
import net.sf.regadb.db.TherapyCommercial;
import net.sf.regadb.db.TherapyGeneric;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.UserAttribute;
import net.sf.regadb.db.DatasetAccessId;
import net.sf.regadb.db.ValueType;
import net.sf.regadb.db.AttributeGroup;
import net.sf.regadb.db.AaSequence;
import net.sf.regadb.db.TherapyCommercialId;
import net.sf.regadb.db.TherapyGenericId;
import net.sf.regadb.db.Analysis;
import net.sf.regadb.db.TestType;
import net.sf.regadb.db.Protein;
import net.sf.regadb.db.AaMutation;
import net.sf.regadb.db.AaInsertion;
import net.sf.regadb.db.DrugCommercial;
import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.AnalysisType;
import net.sf.regadb.db.AnalysisData;
import net.sf.regadb.db.TestObject;
import net.sf.regadb.db.TestNominalValue;
import net.sf.regadb.db.AaMutationId;
import net.sf.regadb.db.AaInsertionId;
import net.sf.regadb.db.DrugClass;
public class ExportToPersistentObjects {
public void initialize(Patient p){
write(p);
}

private void write(Patient patient){
	Hibernate.initialize(patient.getPatientIi());
	Hibernate.initialize(patient.getDatasets());
	for(Dataset dataset:patient.getDatasets()){
	write(dataset);
	}
	Hibernate.initialize(patient.getPatientId());
	Hibernate.initialize(patient.getLastName());
	Hibernate.initialize(patient.getFirstName());
	Hibernate.initialize(patient.getBirthDate());
	Hibernate.initialize(patient.getDeathDate());
	Hibernate.initialize(patient.getTestResults());
	for(TestResult testResult:patient.getTestResults()){
	write(testResult);
	}
	Hibernate.initialize(patient.getPatientAttributeValues());
	for(PatientAttributeValue patientAttributeValue:patient.getPatientAttributeValues()){
	write(patientAttributeValue);
	}
	Hibernate.initialize(patient.getPatientEventValues());
	for(PatientEventValue patientEventValue:patient.getPatientEventValues()){
	write(patientEventValue);
	}
	Hibernate.initialize(patient.getViralIsolates());
	for(ViralIsolate viralIsolate:patient.getViralIsolates()){
	write(viralIsolate);
	}
	Hibernate.initialize(patient.getTherapies());
	for(Therapy therapy:patient.getTherapies()){
	write(therapy);
	}
	Hibernate.initialize(patient.getPrivileges());
	Hibernate.initialize(patient.getSourceDataset());
	Hibernate.initialize(patient.getVersion());
	Hibernate.initialize(patient.getClass());
}

private void write(Dataset dataset){
	Hibernate.initialize(dataset.getClosedDate());
	Hibernate.initialize(dataset.getDatasetIi());
	Hibernate.initialize(dataset.getSettingsUser());
	if(dataset.getSettingsUser() != null)write(dataset.getSettingsUser());
	Hibernate.initialize(dataset.getCreationDate());
	Hibernate.initialize(dataset.getRevision());
	Hibernate.initialize(dataset.getDatasetAccesses());
	for(DatasetAccess datasetAccess:dataset.getDatasetAccesses()){
	write(datasetAccess);
	}
	Hibernate.initialize(dataset.getVersion());
	Hibernate.initialize(dataset.getDescription());
	Hibernate.initialize(dataset.getClass());
}

private void write(PatientAttributeValue patientAttributeValue){
	Hibernate.initialize(patientAttributeValue.getPatient());
	Hibernate.initialize(patientAttributeValue.getPatientAttributeValueIi());
	Hibernate.initialize(patientAttributeValue.getAttributeNominalValue());
	if(patientAttributeValue.getAttributeNominalValue() != null)write(patientAttributeValue.getAttributeNominalValue());
	Hibernate.initialize(patientAttributeValue.getValue());
	Hibernate.initialize(patientAttributeValue.getVersion());
	Hibernate.initialize(patientAttributeValue.getAttribute());
	if(patientAttributeValue.getAttribute() != null)write(patientAttributeValue.getAttribute());
	Hibernate.initialize(patientAttributeValue.getClass());
}

private void write(PatientEventValue patientEventValue){
	Hibernate.initialize(patientEventValue.getPatient());
	Hibernate.initialize(patientEventValue.getPatientEventValueIi());
	Hibernate.initialize(patientEventValue.getEventNominalValue());
	if(patientEventValue.getEventNominalValue() != null)write(patientEventValue.getEventNominalValue());
	Hibernate.initialize(patientEventValue.getEvent());
	if(patientEventValue.getEvent() != null)write(patientEventValue.getEvent());
	Hibernate.initialize(patientEventValue.getStartDate());
	Hibernate.initialize(patientEventValue.getEndDate());
	Hibernate.initialize(patientEventValue.getValue());
	Hibernate.initialize(patientEventValue.getVersion());
	Hibernate.initialize(patientEventValue.getClass());
}

private void write(ViralIsolate viralIsolate){
	Hibernate.initialize(viralIsolate.getPatient());
	Hibernate.initialize(viralIsolate.getTestResults());
	Hibernate.initialize(viralIsolate.getViralIsolateIi());
	Hibernate.initialize(viralIsolate.getSampleId());
	Hibernate.initialize(viralIsolate.getSampleDate());
	Hibernate.initialize(viralIsolate.getNtSequences());
	for(NtSequence ntSequence:viralIsolate.getNtSequences()){
	write(ntSequence);
	}
	Hibernate.initialize(viralIsolate.getVersion());
	Hibernate.initialize(viralIsolate.getClass());
}

private void write(Therapy therapy){
	Hibernate.initialize(therapy.getPatient());
	Hibernate.initialize(therapy.getStartDate());
	Hibernate.initialize(therapy.getTherapyIi());
	Hibernate.initialize(therapy.getTherapyMotivation());
	if(therapy.getTherapyMotivation() != null)write(therapy.getTherapyMotivation());
	Hibernate.initialize(therapy.getStopDate());
	Hibernate.initialize(therapy.getTherapyCommercials());
	for(TherapyCommercial therapyCommercial:therapy.getTherapyCommercials()){
	write(therapyCommercial);
	}
	Hibernate.initialize(therapy.getTherapyGenerics());
	for(TherapyGeneric therapyGeneric:therapy.getTherapyGenerics()){
	write(therapyGeneric);
	}
	Hibernate.initialize(therapy.getVersion());
	Hibernate.initialize(therapy.getComment());
	Hibernate.initialize(therapy.getClass());
}

private void write(SettingsUser settingsUser){
	Hibernate.initialize(settingsUser.getDataset());
	Hibernate.initialize(settingsUser.getLastName());
	Hibernate.initialize(settingsUser.getFirstName());
	Hibernate.initialize(settingsUser.getTest());
	if(settingsUser.getTest() != null)write(settingsUser.getTest());
	Hibernate.initialize(settingsUser.getDatasetAccesses());
	Hibernate.initialize(settingsUser.getUid());
	Hibernate.initialize(settingsUser.getChartWidth());
	Hibernate.initialize(settingsUser.getChartHeight());
	Hibernate.initialize(settingsUser.getEmail());
	Hibernate.initialize(settingsUser.getAdmin());
	Hibernate.initialize(settingsUser.getEnabled());
	Hibernate.initialize(settingsUser.getUserAttributes());
	for(UserAttribute userAttribute:settingsUser.getUserAttributes()){
	write(userAttribute);
	}
	Hibernate.initialize(settingsUser.getVersion());
	Hibernate.initialize(settingsUser.getPassword());
	Hibernate.initialize(settingsUser.getClass());
}

private void write(DatasetAccess datasetAccess){
	Hibernate.initialize(datasetAccess.getPermissions());
	Hibernate.initialize(datasetAccess.getId());
	if(datasetAccess.getId() != null)write(datasetAccess.getId());
	Hibernate.initialize(datasetAccess.getProvider());
	Hibernate.initialize(datasetAccess.getVersion());
	Hibernate.initialize(datasetAccess.getClass());
}

private void write(AttributeNominalValue attributeNominalValue){
	Hibernate.initialize(attributeNominalValue.getNominalValueIi());
	Hibernate.initialize(attributeNominalValue.getValue());
	Hibernate.initialize(attributeNominalValue.getVersion());
	Hibernate.initialize(attributeNominalValue.getAttribute());
	Hibernate.initialize(attributeNominalValue.getClass());
}

private void write(Attribute attribute){
	Hibernate.initialize(attribute.getAttributeIi());
	Hibernate.initialize(attribute.getValueType());
	if(attribute.getValueType() != null)write(attribute.getValueType());
	Hibernate.initialize(attribute.getAttributeGroup());
	if(attribute.getAttributeGroup() != null)write(attribute.getAttributeGroup());
	Hibernate.initialize(attribute.getAttributeNominalValues());
	Hibernate.initialize(attribute.getName());
	Hibernate.initialize(attribute.getVersion());
	Hibernate.initialize(attribute.getClass());
}

private void write(EventNominalValue eventNominalValue){
	Hibernate.initialize(eventNominalValue.getEvent());
	Hibernate.initialize(eventNominalValue.getNominalValueIi());
	Hibernate.initialize(eventNominalValue.getValue());
	Hibernate.initialize(eventNominalValue.getVersion());
	Hibernate.initialize(eventNominalValue.getClass());
}

private void write(Event event){
	Hibernate.initialize(event.getValueType());
	Hibernate.initialize(event.getEventIi());
	Hibernate.initialize(event.getEventNominalValues());
	Hibernate.initialize(event.getName());
	Hibernate.initialize(event.getVersion());
	Hibernate.initialize(event.getClass());
}

private void write(NtSequence ntSequence){
	Hibernate.initialize(ntSequence.getTestResults());
	Hibernate.initialize(ntSequence.getViralIsolate());
	Hibernate.initialize(ntSequence.getNtSequenceIi());
	Hibernate.initialize(ntSequence.getSequenceDate());
	Hibernate.initialize(ntSequence.getNucleotides());
	Hibernate.initialize(ntSequence.getAaSequences());
	for(AaSequence aaSequence:ntSequence.getAaSequences()){
	write(aaSequence);
	}
	Hibernate.initialize(ntSequence.getLabel());
	Hibernate.initialize(ntSequence.getVersion());
	Hibernate.initialize(ntSequence.getClass());
}

private void write(TherapyMotivation therapyMotivation){
	Hibernate.initialize(therapyMotivation.getTherapyMotivationIi());
	Hibernate.initialize(therapyMotivation.getValue());
	Hibernate.initialize(therapyMotivation.getVersion());
	Hibernate.initialize(therapyMotivation.getClass());
}

private void write(TherapyCommercial therapyCommercial){
	Hibernate.initialize(therapyCommercial.getDayDosageUnits());
	Hibernate.initialize(therapyCommercial.getFrequency());
	Hibernate.initialize(therapyCommercial.getId());
	if(therapyCommercial.getId() != null)write(therapyCommercial.getId());
	Hibernate.initialize(therapyCommercial.getVersion());
	Hibernate.initialize(therapyCommercial.getClass());
}

private void write(TherapyGeneric therapyGeneric){
	Hibernate.initialize(therapyGeneric.getFrequency());
	Hibernate.initialize(therapyGeneric.getDayDosageMg());
	Hibernate.initialize(therapyGeneric.getId());
	if(therapyGeneric.getId() != null)write(therapyGeneric.getId());
	Hibernate.initialize(therapyGeneric.getVersion());
	Hibernate.initialize(therapyGeneric.getClass());
}

private void write(Test test){
	Hibernate.initialize(test.getTestIi());
	Hibernate.initialize(test.getAnalysis());
	if(test.getAnalysis() != null)write(test.getAnalysis());
	Hibernate.initialize(test.getTestType());
	if(test.getTestType() != null)write(test.getTestType());
	Hibernate.initialize(test.getVersion());
	Hibernate.initialize(test.getDescription());
	Hibernate.initialize(test.getClass());
}

private void write(UserAttribute userAttribute){
	Hibernate.initialize(userAttribute.getValueType());
	Hibernate.initialize(userAttribute.getSettingsUser());
	Hibernate.initialize(userAttribute.getUserAttributeIi());
	Hibernate.initialize(userAttribute.getName());
	Hibernate.initialize(userAttribute.getValue());
	Hibernate.initialize(userAttribute.getData());
	Hibernate.initialize(userAttribute.getClass());
}

private void write(DatasetAccessId datasetAccessId){
	Hibernate.initialize(datasetAccessId.getDataset());
	Hibernate.initialize(datasetAccessId.getSettingsUser());
	Hibernate.initialize(datasetAccessId.getClass());
}

private void write(ValueType valueType){
	Hibernate.initialize(valueType.getValueTypeIi());
	Hibernate.initialize(valueType.getMultiple());
	Hibernate.initialize(valueType.getVersion());
	Hibernate.initialize(valueType.getDescription());
	Hibernate.initialize(valueType.getMaximum());
	Hibernate.initialize(valueType.getMinimum());
	Hibernate.initialize(valueType.getClass());
}

private void write(AttributeGroup attributeGroup){
	Hibernate.initialize(attributeGroup.getAttributeGroupIi());
	Hibernate.initialize(attributeGroup.getGroupName());
	Hibernate.initialize(attributeGroup.getVersion());
	Hibernate.initialize(attributeGroup.getClass());
}

private void write(AaSequence aaSequence){
	Hibernate.initialize(aaSequence.getNtSequence());
	Hibernate.initialize(aaSequence.getProtein());
	if(aaSequence.getProtein() != null)write(aaSequence.getProtein());
	Hibernate.initialize(aaSequence.getAaSequenceIi());
	Hibernate.initialize(aaSequence.getFirstAaPos());
	Hibernate.initialize(aaSequence.getLastAaPos());
	Hibernate.initialize(aaSequence.getAaMutations());
	for(AaMutation aaMutation:aaSequence.getAaMutations()){
	write(aaMutation);
	}
	Hibernate.initialize(aaSequence.getAaInsertions());
	for(AaInsertion aaInsertion:aaSequence.getAaInsertions()){
	write(aaInsertion);
	}
	Hibernate.initialize(aaSequence.getVersion());
	Hibernate.initialize(aaSequence.getClass());
}

private void write(TherapyCommercialId therapyCommercialId){
	Hibernate.initialize(therapyCommercialId.getDrugCommercial());
	if(therapyCommercialId.getDrugCommercial() != null)write(therapyCommercialId.getDrugCommercial());
	Hibernate.initialize(therapyCommercialId.getTherapy());
	Hibernate.initialize(therapyCommercialId.getClass());
}

private void write(TherapyGenericId therapyGenericId){
	Hibernate.initialize(therapyGenericId.getDrugGeneric());
	if(therapyGenericId.getDrugGeneric() != null)write(therapyGenericId.getDrugGeneric());
	Hibernate.initialize(therapyGenericId.getTherapy());
	Hibernate.initialize(therapyGenericId.getClass());
}

private void write(Analysis analysis){
	Hibernate.initialize(analysis.getTests());
	Hibernate.initialize(analysis.getAnalysisIi());
	Hibernate.initialize(analysis.getAnalysisType());
	if(analysis.getAnalysisType() != null)write(analysis.getAnalysisType());
	Hibernate.initialize(analysis.getUrl());
	Hibernate.initialize(analysis.getAccount());
	Hibernate.initialize(analysis.getBaseinputfile());
	Hibernate.initialize(analysis.getBaseoutputfile());
	Hibernate.initialize(analysis.getServiceName());
	Hibernate.initialize(analysis.getDataoutputfile());
	Hibernate.initialize(analysis.getAnalysisDatas());
	for(AnalysisData analysisData:analysis.getAnalysisDatas()){
	write(analysisData);
	}
	Hibernate.initialize(analysis.getPassword());
	Hibernate.initialize(analysis.getClass());
}

private void write(TestType testType){
	Hibernate.initialize(testType.getValueType());
	Hibernate.initialize(testType.getTestTypeIi());
	Hibernate.initialize(testType.getTestObject());
	if(testType.getTestObject() != null)write(testType.getTestObject());
	Hibernate.initialize(testType.getTestNominalValues());
	for(TestNominalValue testNominalValue:testType.getTestNominalValues()){
	write(testNominalValue);
	}
	Hibernate.initialize(testType.getVersion());
	Hibernate.initialize(testType.getDescription());
	Hibernate.initialize(testType.getClass());
}

private void write(Protein protein){
	Hibernate.initialize(protein.getAbbreviation());
	Hibernate.initialize(protein.getProteinIi());
	Hibernate.initialize(protein.getFullName());
	Hibernate.initialize(protein.getVersion());
	Hibernate.initialize(protein.getClass());
}

private void write(AaMutation aaMutation){
	Hibernate.initialize(aaMutation.getAaReference());
	Hibernate.initialize(aaMutation.getAaMutation());
	Hibernate.initialize(aaMutation.getNtReferenceCodon());
	Hibernate.initialize(aaMutation.getNtMutationCodon());
	Hibernate.initialize(aaMutation.getId());
	if(aaMutation.getId() != null)write(aaMutation.getId());
	Hibernate.initialize(aaMutation.getVersion());
	Hibernate.initialize(aaMutation.getClass());
}

private void write(AaInsertion aaInsertion){
	Hibernate.initialize(aaInsertion.getAaInsertion());
	Hibernate.initialize(aaInsertion.getNtInsertionCodon());
	Hibernate.initialize(aaInsertion.getId());
	if(aaInsertion.getId() != null)write(aaInsertion.getId());
	Hibernate.initialize(aaInsertion.getVersion());
	Hibernate.initialize(aaInsertion.getClass());
}

private void write(DrugCommercial drugCommercial){
	Hibernate.initialize(drugCommercial.getCommercialIi());
	Hibernate.initialize(drugCommercial.getAtcCode());
	Hibernate.initialize(drugCommercial.getDrugGenerics());
	Hibernate.initialize(drugCommercial.getName());
	Hibernate.initialize(drugCommercial.getVersion());
	Hibernate.initialize(drugCommercial.getClass());
}

private void write(DrugGeneric drugGeneric){
	Hibernate.initialize(drugGeneric.getDrugClass());
	if(drugGeneric.getDrugClass() != null)write(drugGeneric.getDrugClass());
	Hibernate.initialize(drugGeneric.getAtcCode());
	Hibernate.initialize(drugGeneric.getGenericIi());
	Hibernate.initialize(drugGeneric.getGenericId());
	Hibernate.initialize(drugGeneric.getGenericName());
	Hibernate.initialize(drugGeneric.getResistanceTableOrder());
	Hibernate.initialize(drugGeneric.getDrugCommercials());
	Hibernate.initialize(drugGeneric.getVersion());
	Hibernate.initialize(drugGeneric.getClass());
}

private void write(AnalysisType analysisType){
	Hibernate.initialize(analysisType.getAnalysisTypeIi());
	Hibernate.initialize(analysisType.getType());
	Hibernate.initialize(analysisType.getClass());
}

private void write(AnalysisData analysisData){
	Hibernate.initialize(analysisData.getAnalysis());
	Hibernate.initialize(analysisData.getAnalysisDataIi());
	Hibernate.initialize(analysisData.getMimetype());
	Hibernate.initialize(analysisData.getName());
	Hibernate.initialize(analysisData.getData());
	Hibernate.initialize(analysisData.getClass());
}

private void write(TestObject testObject){
	Hibernate.initialize(testObject.getTestObjectIi());
	Hibernate.initialize(testObject.getTestObjectId());
	Hibernate.initialize(testObject.getVersion());
	Hibernate.initialize(testObject.getDescription());
	Hibernate.initialize(testObject.getClass());
}

private void write(TestNominalValue testNominalValue){
	Hibernate.initialize(testNominalValue.getTestType());
	Hibernate.initialize(testNominalValue.getNominalValueIi());
	Hibernate.initialize(testNominalValue.getValue());
	Hibernate.initialize(testNominalValue.getVersion());
	Hibernate.initialize(testNominalValue.getClass());
}

private void write(AaMutationId aaMutationId){
	Hibernate.initialize(aaMutationId.getMutationPosition());
	Hibernate.initialize(aaMutationId.getAaSequence());
	Hibernate.initialize(aaMutationId.getClass());
}

private void write(AaInsertionId aaInsertionId){
	Hibernate.initialize(aaInsertionId.getAaSequence());
	Hibernate.initialize(aaInsertionId.getInsertionPosition());
	Hibernate.initialize(aaInsertionId.getInsertionOrder());
	Hibernate.initialize(aaInsertionId.getClass());
}

private void write(DrugClass drugClass){
	Hibernate.initialize(drugClass.getDrugGenerics());
	Hibernate.initialize(drugClass.getResistanceTableOrder());
	Hibernate.initialize(drugClass.getDrugClassIi());
	Hibernate.initialize(drugClass.getClassId());
	Hibernate.initialize(drugClass.getClassName());
	Hibernate.initialize(drugClass.getVersion());
	Hibernate.initialize(drugClass.getClass());
}

private void write(TestResult testResult){
	Hibernate.initialize(testResult.getPatient());
	Hibernate.initialize(testResult.getSampleId());
	Hibernate.initialize(testResult.getTestResultIi());
	Hibernate.initialize(testResult.getTest());
	Hibernate.initialize(testResult.getDrugGeneric());
	Hibernate.initialize(testResult.getViralIsolate());
	Hibernate.initialize(testResult.getTestNominalValue());
	Hibernate.initialize(testResult.getNtSequence());
	Hibernate.initialize(testResult.getTestDate());
	Hibernate.initialize(testResult.getValue());
	Hibernate.initialize(testResult.getVersion());
	Hibernate.initialize(testResult.getData());
	Hibernate.initialize(testResult.getClass());
}


}