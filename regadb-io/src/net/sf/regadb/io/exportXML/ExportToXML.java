package net.sf.regadb.io.exportXML;

import net.sf.regadb.db.Analysis;
import net.sf.regadb.db.QueryDefinitionRunParameter;
import net.sf.regadb.db.QueryDefinitionRun;
import net.sf.regadb.db.ValueType;
import net.sf.regadb.db.AnalysisData;
import net.sf.regadb.db.SettingsUser;
import net.sf.regadb.db.QueryDefinitionParameterType;
import net.sf.regadb.db.DrugClass;
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
import net.sf.regadb.db.TestNominalValue;
import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Protein;
import net.sf.regadb.db.Attribute;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.AnalysisType;
import net.sf.regadb.db.TestType;
import net.sf.regadb.db.AaInsertion;
import net.sf.regadb.db.AaMutation;
import net.sf.regadb.db.TherapyCommercial;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.PatientDataset;
import net.sf.regadb.db.Dataset;
import net.sf.regadb.db.DrugCommercial;
import net.sf.regadb.db.TherapyGeneric;
import net.sf.regadb.util.xml.XMLTools;
import org.jdom.Element;
import java.util.HashMap;

public class ExportToXML 
{
	HashMap<Test, Integer> TestPMap = new HashMap<Test, Integer>();
	HashMap<TestType, Integer> TestTypePMap = new HashMap<TestType, Integer>();
	HashMap<ValueType, Integer> ValueTypePMap = new HashMap<ValueType, Integer>();
	HashMap<TestObject, Integer> TestObjectPMap = new HashMap<TestObject, Integer>();
	HashMap<TestNominalValue, Integer> TestNominalValuePMap = new HashMap<TestNominalValue, Integer>();
	HashMap<Attribute, Integer> AttributePMap = new HashMap<Attribute, Integer>();
	HashMap<AttributeGroup, Integer> AttributeGroupPMap = new HashMap<AttributeGroup, Integer>();
	HashMap<AttributeNominalValue, Integer> AttributeNominalValuePMap = new HashMap<AttributeNominalValue, Integer>();
	HashMap<Analysis, Integer> AnalysisPMap = new HashMap<Analysis, Integer>();
	HashMap<AnalysisData, Integer> AnalysisDataPMap = new HashMap<AnalysisData, Integer>();
	public void writeAaSequence(AaSequence AaSequencevar, Element rootNode)
	{
		Element parentNode = new Element("AaSequence");
		rootNode.addContent(parentNode);
		if(AaSequencevar==null)
		{
			return;
		}
		if(AaSequencevar.getProtein()!=null &&AaSequencevar.getProtein().getAbbreviation()!=null)
		{
			Element proteinvar = new Element("protein");
			parentNode.addContent(proteinvar);
			proteinvar.addContent(AaSequencevar.getProtein().getAbbreviation());
		}
		Element firstAaPosprimitiveValEl = new Element("firstAaPos");
		firstAaPosprimitiveValEl.addContent(String.valueOf(AaSequencevar.getFirstAaPos()));
		parentNode.addContent(firstAaPosprimitiveValEl);
		Element lastAaPosprimitiveValEl = new Element("lastAaPos");
		lastAaPosprimitiveValEl.addContent(String.valueOf(AaSequencevar.getLastAaPos()));
		parentNode.addContent(lastAaPosprimitiveValEl);
		Element aaMutationsEl = new Element("aaMutations");
		parentNode.addContent(aaMutationsEl);
		for (AaMutation AaMutationloopvar : AaSequencevar.getAaMutations())
		{
			Element aaMutations_elEl = new Element("aaMutations-el");
			aaMutationsEl.addContent(aaMutations_elEl);
			writeAaMutation(AaMutationloopvar,aaMutations_elEl);
		}
		Element aaInsertionsEl = new Element("aaInsertions");
		parentNode.addContent(aaInsertionsEl);
		for (AaInsertion AaInsertionloopvar : AaSequencevar.getAaInsertions())
		{
			Element aaInsertions_elEl = new Element("aaInsertions-el");
			aaInsertionsEl.addContent(aaInsertions_elEl);
			writeAaInsertion(AaInsertionloopvar,aaInsertions_elEl);
		}
	}
	public void writeTest(Test Testvar, Element rootNode)
	{
		Element parentNode = new Element("Test");
		rootNode.addContent(parentNode);
		if(Testvar==null)
		{
			return;
		}
		if(Testvar.getAnalysis()!=null)
		{
			Integer indexanalysis = AnalysisPMap.get(Testvar.getAnalysis());
			Element wrapperanalysis = new Element("analysis");
			parentNode.addContent(wrapperanalysis);
			if(indexanalysis!=null)
			{
				Element refElementanalysis= new Element("reference");
				wrapperanalysis.addContent(refElementanalysis);
				refElementanalysis.addContent(indexanalysis.toString());
			}
			else
			{
				indexanalysis = new Integer(AnalysisPMap.size());
				Element refElementanalysis= new Element("reference");
				wrapperanalysis.addContent(refElementanalysis);
				refElementanalysis.addContent(indexanalysis.toString());
				AnalysisPMap.put(Testvar.getAnalysis(),indexanalysis);
				writeAnalysis(Testvar.getAnalysis(),wrapperanalysis);
			}
		}
		if(Testvar.getTestType()!=null)
		{
			Integer indextestType = TestTypePMap.get(Testvar.getTestType());
			Element wrappertestType = new Element("testType");
			parentNode.addContent(wrappertestType);
			if(indextestType!=null)
			{
				Element refElementtestType= new Element("reference");
				wrappertestType.addContent(refElementtestType);
				refElementtestType.addContent(indextestType.toString());
			}
			else
			{
				indextestType = new Integer(TestTypePMap.size());
				Element refElementtestType= new Element("reference");
				wrappertestType.addContent(refElementtestType);
				refElementtestType.addContent(indextestType.toString());
				TestTypePMap.put(Testvar.getTestType(),indextestType);
				writeTestType(Testvar.getTestType(),wrappertestType);
			}
		}
		if(Testvar.getDescription()!=null)
		{
			Element descriptionprimitiveValEl = new Element("description");
			descriptionprimitiveValEl.addContent(Testvar.getDescription().toString());
			parentNode.addContent(descriptionprimitiveValEl);
		}
	}
	public void writeTherapyCommercial(TherapyCommercial TherapyCommercialvar, Element rootNode)
	{
		Element parentNode = new Element("TherapyCommercial");
		rootNode.addContent(parentNode);
		if(TherapyCommercialvar==null)
		{
			return;
		}
		if(TherapyCommercialvar.getId().getDrugCommercial()!=null &&TherapyCommercialvar.getId().getDrugCommercial().getName()!=null)
		{
			Element drugCommercialvar = new Element("drugCommercial");
			parentNode.addContent(drugCommercialvar);
			drugCommercialvar.addContent(TherapyCommercialvar.getId().getDrugCommercial().getName());
		}
		if(TherapyCommercialvar.getDayDosageUnits()!=null)
		{
			Element dayDosageUnitsprimitiveValEl = new Element("dayDosageUnits");
			dayDosageUnitsprimitiveValEl.addContent(TherapyCommercialvar.getDayDosageUnits().toString());
			parentNode.addContent(dayDosageUnitsprimitiveValEl);
		}
	}
	public void writeAnalysis(Analysis Analysisvar, Element rootNode)
	{
		Element parentNode = new Element("Analysis");
		rootNode.addContent(parentNode);
		if(Analysisvar==null)
		{
			return;
		}
		if(Analysisvar.getAnalysisType()!=null &&Analysisvar.getAnalysisType().getType()!=null)
		{
			Element analysisTypevar = new Element("analysisType");
			parentNode.addContent(analysisTypevar);
			analysisTypevar.addContent(Analysisvar.getAnalysisType().getType());
		}
		if(Analysisvar.getType()!=null)
		{
			Element typeprimitiveValEl = new Element("type");
			typeprimitiveValEl.addContent(Analysisvar.getType().toString());
			parentNode.addContent(typeprimitiveValEl);
		}
		if(Analysisvar.getUrl()!=null)
		{
			Element urlprimitiveValEl = new Element("url");
			urlprimitiveValEl.addContent(Analysisvar.getUrl().toString());
			parentNode.addContent(urlprimitiveValEl);
		}
		if(Analysisvar.getAccount()!=null)
		{
			Element accountprimitiveValEl = new Element("account");
			accountprimitiveValEl.addContent(Analysisvar.getAccount().toString());
			parentNode.addContent(accountprimitiveValEl);
		}
		if(Analysisvar.getPassword()!=null)
		{
			Element passwordprimitiveValEl = new Element("password");
			passwordprimitiveValEl.addContent(Analysisvar.getPassword().toString());
			parentNode.addContent(passwordprimitiveValEl);
		}
		if(Analysisvar.getBaseinputfile()!=null)
		{
			Element baseinputfileprimitiveValEl = new Element("baseinputfile");
			baseinputfileprimitiveValEl.addContent(Analysisvar.getBaseinputfile().toString());
			parentNode.addContent(baseinputfileprimitiveValEl);
		}
		if(Analysisvar.getBaseoutputfile()!=null)
		{
			Element baseoutputfileprimitiveValEl = new Element("baseoutputfile");
			baseoutputfileprimitiveValEl.addContent(Analysisvar.getBaseoutputfile().toString());
			parentNode.addContent(baseoutputfileprimitiveValEl);
		}
		if(Analysisvar.getServiceName()!=null)
		{
			Element serviceNameprimitiveValEl = new Element("serviceName");
			serviceNameprimitiveValEl.addContent(Analysisvar.getServiceName().toString());
			parentNode.addContent(serviceNameprimitiveValEl);
		}
		Element forParenttests = new Element("tests");
		parentNode.addContent(forParenttests);
		if(Analysisvar.getTests().size()!=0)
		{
			Element forParentLoopVar;
			for(Test testsloopvar :Analysisvar.getTests())
			{
				forParentLoopVar = new Element("tests-el");
				forParenttests.addContent(forParentLoopVar);
				if(testsloopvar!=null)
				{
					Integer indextestsloopvar = TestPMap.get(testsloopvar);
					Element wrappertestsloopvar = forParentLoopVar;
					if(indextestsloopvar!=null)
					{
						Element refElementtestsloopvar= new Element("reference");
						wrappertestsloopvar.addContent(refElementtestsloopvar);
						refElementtestsloopvar.addContent(indextestsloopvar.toString());
					}
					else
					{
						indextestsloopvar = new Integer(TestPMap.size());
						Element refElementtestsloopvar= new Element("reference");
						wrappertestsloopvar.addContent(refElementtestsloopvar);
						refElementtestsloopvar.addContent(indextestsloopvar.toString());
						TestPMap.put(testsloopvar,indextestsloopvar);
						writeTest(testsloopvar,wrappertestsloopvar);
					}
				}
			}
		}
		Element forParentanalysisDatas = new Element("analysisDatas");
		parentNode.addContent(forParentanalysisDatas);
		if(Analysisvar.getAnalysisDatas().size()!=0)
		{
			Element forParentLoopVar;
			for(AnalysisData analysisDatasloopvar :Analysisvar.getAnalysisDatas())
			{
				forParentLoopVar = new Element("analysisDatas-el");
				forParentanalysisDatas.addContent(forParentLoopVar);
				if(analysisDatasloopvar!=null)
				{
					Integer indexanalysisDatasloopvar = AnalysisDataPMap.get(analysisDatasloopvar);
					Element wrapperanalysisDatasloopvar = forParentLoopVar;
					if(indexanalysisDatasloopvar!=null)
					{
						Element refElementanalysisDatasloopvar= new Element("reference");
						wrapperanalysisDatasloopvar.addContent(refElementanalysisDatasloopvar);
						refElementanalysisDatasloopvar.addContent(indexanalysisDatasloopvar.toString());
					}
					else
					{
						indexanalysisDatasloopvar = new Integer(AnalysisDataPMap.size());
						Element refElementanalysisDatasloopvar= new Element("reference");
						wrapperanalysisDatasloopvar.addContent(refElementanalysisDatasloopvar);
						refElementanalysisDatasloopvar.addContent(indexanalysisDatasloopvar.toString());
						AnalysisDataPMap.put(analysisDatasloopvar,indexanalysisDatasloopvar);
						writeAnalysisData(analysisDatasloopvar,wrapperanalysisDatasloopvar);
					}
				}
			}
		}
	}
	public void writeNtSequence(NtSequence NtSequencevar, Element rootNode)
	{
		Element parentNode = new Element("NtSequence");
		rootNode.addContent(parentNode);
		if(NtSequencevar==null)
		{
			return;
		}
		if(NtSequencevar.getNucleotides()!=null)
		{
			Element nucleotidesprimitiveValEl = new Element("nucleotides");
			nucleotidesprimitiveValEl.addContent(NtSequencevar.getNucleotides().toString());
			parentNode.addContent(nucleotidesprimitiveValEl);
		}
		if(NtSequencevar.getLabel()!=null)
		{
			Element labelprimitiveValEl = new Element("label");
			labelprimitiveValEl.addContent(NtSequencevar.getLabel().toString());
			parentNode.addContent(labelprimitiveValEl);
		}
		if(NtSequencevar.getSequenceDate()!=null)
		{
			Element sequenceDateprimitiveValEl = new Element("sequenceDate");
			sequenceDateprimitiveValEl.addContent(XMLTools.dateToRelaxNgString(NtSequencevar.getSequenceDate()));
			parentNode.addContent(sequenceDateprimitiveValEl);
		}
		Element aaSequencesEl = new Element("aaSequences");
		parentNode.addContent(aaSequencesEl);
		for (AaSequence AaSequenceloopvar : NtSequencevar.getAaSequences())
		{
			Element aaSequences_elEl = new Element("aaSequences-el");
			aaSequencesEl.addContent(aaSequences_elEl);
			writeAaSequence(AaSequenceloopvar,aaSequences_elEl);
		}
		Element testResultsEl = new Element("testResults");
		parentNode.addContent(testResultsEl);
		for (TestResult TestResultloopvar : NtSequencevar.getTestResults())
		{
			Element testResults_elEl = new Element("testResults-el");
			testResultsEl.addContent(testResults_elEl);
			writeTestResult(TestResultloopvar,testResults_elEl);
		}
	}
	public void writeAaMutation(AaMutation AaMutationvar, Element rootNode)
	{
		Element parentNode = new Element("AaMutation");
		rootNode.addContent(parentNode);
		if(AaMutationvar==null)
		{
			return;
		}
		Element positionprimitiveValEl = new Element("position");
		positionprimitiveValEl.addContent(String.valueOf(AaMutationvar.getId().getPosition()));
		parentNode.addContent(positionprimitiveValEl);
		if(AaMutationvar.getAaReference()!=null)
		{
			Element aaReferenceprimitiveValEl = new Element("aaReference");
			aaReferenceprimitiveValEl.addContent(AaMutationvar.getAaReference().toString());
			parentNode.addContent(aaReferenceprimitiveValEl);
		}
		if(AaMutationvar.getAaMutation()!=null)
		{
			Element aaMutationprimitiveValEl = new Element("aaMutation");
			aaMutationprimitiveValEl.addContent(AaMutationvar.getAaMutation().toString());
			parentNode.addContent(aaMutationprimitiveValEl);
		}
		if(AaMutationvar.getNtReferenceCodon()!=null)
		{
			Element ntReferenceCodonprimitiveValEl = new Element("ntReferenceCodon");
			ntReferenceCodonprimitiveValEl.addContent(AaMutationvar.getNtReferenceCodon().toString());
			parentNode.addContent(ntReferenceCodonprimitiveValEl);
		}
		if(AaMutationvar.getNtMutationCodon()!=null)
		{
			Element ntMutationCodonprimitiveValEl = new Element("ntMutationCodon");
			ntMutationCodonprimitiveValEl.addContent(AaMutationvar.getNtMutationCodon().toString());
			parentNode.addContent(ntMutationCodonprimitiveValEl);
		}
	}
	public void writeValueType(ValueType ValueTypevar, Element rootNode)
	{
		Element parentNode = new Element("ValueType");
		rootNode.addContent(parentNode);
		if(ValueTypevar==null)
		{
			return;
		}
		if(ValueTypevar.getDescription()!=null)
		{
			Element descriptionprimitiveValEl = new Element("description");
			descriptionprimitiveValEl.addContent(ValueTypevar.getDescription().toString());
			parentNode.addContent(descriptionprimitiveValEl);
		}
		if(ValueTypevar.getMin()!=null)
		{
			Element minprimitiveValEl = new Element("min");
			minprimitiveValEl.addContent(ValueTypevar.getMin().toString());
			parentNode.addContent(minprimitiveValEl);
		}
		if(ValueTypevar.getMax()!=null)
		{
			Element maxprimitiveValEl = new Element("max");
			maxprimitiveValEl.addContent(ValueTypevar.getMax().toString());
			parentNode.addContent(maxprimitiveValEl);
		}
		if(ValueTypevar.getMultiple()!=null)
		{
			Element multipleprimitiveValEl = new Element("multiple");
			multipleprimitiveValEl.addContent(ValueTypevar.getMultiple().toString());
			parentNode.addContent(multipleprimitiveValEl);
		}
	}
	public void writeTestObject(TestObject TestObjectvar, Element rootNode)
	{
		Element parentNode = new Element("TestObject");
		rootNode.addContent(parentNode);
		if(TestObjectvar==null)
		{
			return;
		}
		if(TestObjectvar.getDescription()!=null)
		{
			Element descriptionprimitiveValEl = new Element("description");
			descriptionprimitiveValEl.addContent(TestObjectvar.getDescription().toString());
			parentNode.addContent(descriptionprimitiveValEl);
		}
		if(TestObjectvar.getTestObjectId()!=null)
		{
			Element testObjectIdprimitiveValEl = new Element("testObjectId");
			testObjectIdprimitiveValEl.addContent(TestObjectvar.getTestObjectId().toString());
			parentNode.addContent(testObjectIdprimitiveValEl);
		}
	}
	public void writePatient(Patient Patientvar, Element rootNode)
	{
		Element parentNode = new Element("Patient");
		rootNode.addContent(parentNode);
		if(Patientvar==null)
		{
			return;
		}
		if(Patientvar.getPatientId()!=null)
		{
			Element patientIdprimitiveValEl = new Element("patientId");
			patientIdprimitiveValEl.addContent(Patientvar.getPatientId().toString());
			parentNode.addContent(patientIdprimitiveValEl);
		}
		if(Patientvar.getLastName()!=null)
		{
			Element lastNameprimitiveValEl = new Element("lastName");
			lastNameprimitiveValEl.addContent(Patientvar.getLastName().toString());
			parentNode.addContent(lastNameprimitiveValEl);
		}
		if(Patientvar.getFirstName()!=null)
		{
			Element firstNameprimitiveValEl = new Element("firstName");
			firstNameprimitiveValEl.addContent(Patientvar.getFirstName().toString());
			parentNode.addContent(firstNameprimitiveValEl);
		}
		if(Patientvar.getBirthDate()!=null)
		{
			Element birthDateprimitiveValEl = new Element("birthDate");
			birthDateprimitiveValEl.addContent(XMLTools.dateToRelaxNgString(Patientvar.getBirthDate()));
			parentNode.addContent(birthDateprimitiveValEl);
		}
		if(Patientvar.getDeathDate()!=null)
		{
			Element deathDateprimitiveValEl = new Element("deathDate");
			deathDateprimitiveValEl.addContent(XMLTools.dateToRelaxNgString(Patientvar.getDeathDate()));
			parentNode.addContent(deathDateprimitiveValEl);
		}
		Element DatasetsEl = new Element("Datasets");
		parentNode.addContent(DatasetsEl);
		for (Dataset Datasetloopvar : Patientvar.getDatasets())
		{
			Element Datasets_elEl = new Element("Datasets-el");
			DatasetsEl.addContent(Datasets_elEl);
			writeDataset(Datasetloopvar,Datasets_elEl);
		}
		Element testResultsEl = new Element("testResults");
		parentNode.addContent(testResultsEl);
		for (TestResult TestResultloopvar : Patientvar.getTestResults())
		{
			Element testResults_elEl = new Element("testResults-el");
			testResultsEl.addContent(testResults_elEl);
			writeTestResult(TestResultloopvar,testResults_elEl);
		}
		Element patientAttributeValuesEl = new Element("patientAttributeValues");
		parentNode.addContent(patientAttributeValuesEl);
		for (PatientAttributeValue PatientAttributeValueloopvar : Patientvar.getPatientAttributeValues())
		{
			Element patientAttributeValues_elEl = new Element("patientAttributeValues-el");
			patientAttributeValuesEl.addContent(patientAttributeValues_elEl);
			writePatientAttributeValue(PatientAttributeValueloopvar,patientAttributeValues_elEl);
		}
		Element viralIsolatesEl = new Element("viralIsolates");
		parentNode.addContent(viralIsolatesEl);
		for (ViralIsolate ViralIsolateloopvar : Patientvar.getViralIsolates())
		{
			Element viralIsolates_elEl = new Element("viralIsolates-el");
			viralIsolatesEl.addContent(viralIsolates_elEl);
			writeViralIsolate(ViralIsolateloopvar,viralIsolates_elEl);
		}
		Element therapiesEl = new Element("therapies");
		parentNode.addContent(therapiesEl);
		for (Therapy Therapyloopvar : Patientvar.getTherapies())
		{
			Element therapies_elEl = new Element("therapies-el");
			therapiesEl.addContent(therapies_elEl);
			writeTherapy(Therapyloopvar,therapies_elEl);
		}
	}
	public void writeAttributeGroup(AttributeGroup AttributeGroupvar, Element rootNode)
	{
		Element parentNode = new Element("AttributeGroup");
		rootNode.addContent(parentNode);
		if(AttributeGroupvar==null)
		{
			return;
		}
		if(AttributeGroupvar.getGroupName()!=null)
		{
			Element groupNameprimitiveValEl = new Element("groupName");
			groupNameprimitiveValEl.addContent(AttributeGroupvar.getGroupName().toString());
			parentNode.addContent(groupNameprimitiveValEl);
		}
	}
	public void writeAaInsertion(AaInsertion AaInsertionvar, Element rootNode)
	{
		Element parentNode = new Element("AaInsertion");
		rootNode.addContent(parentNode);
		if(AaInsertionvar==null)
		{
			return;
		}
		Element positionprimitiveValEl = new Element("position");
		positionprimitiveValEl.addContent(String.valueOf(AaInsertionvar.getId().getPosition()));
		parentNode.addContent(positionprimitiveValEl);
		Element insertionOrderprimitiveValEl = new Element("insertionOrder");
		insertionOrderprimitiveValEl.addContent(String.valueOf(AaInsertionvar.getId().getInsertionOrder()));
		parentNode.addContent(insertionOrderprimitiveValEl);
		if(AaInsertionvar.getAaInsertion()!=null)
		{
			Element aaInsertionprimitiveValEl = new Element("aaInsertion");
			aaInsertionprimitiveValEl.addContent(AaInsertionvar.getAaInsertion().toString());
			parentNode.addContent(aaInsertionprimitiveValEl);
		}
		if(AaInsertionvar.getNtInsertionCodon()!=null)
		{
			Element ntInsertionCodonprimitiveValEl = new Element("ntInsertionCodon");
			ntInsertionCodonprimitiveValEl.addContent(AaInsertionvar.getNtInsertionCodon().toString());
			parentNode.addContent(ntInsertionCodonprimitiveValEl);
		}
	}
	public void writeViralIsolate(ViralIsolate ViralIsolatevar, Element rootNode)
	{
		Element parentNode = new Element("ViralIsolate");
		rootNode.addContent(parentNode);
		if(ViralIsolatevar==null)
		{
			return;
		}
		if(ViralIsolatevar.getSampleId()!=null)
		{
			Element sampleIdprimitiveValEl = new Element("sampleId");
			sampleIdprimitiveValEl.addContent(ViralIsolatevar.getSampleId().toString());
			parentNode.addContent(sampleIdprimitiveValEl);
		}
		if(ViralIsolatevar.getSampleDate()!=null)
		{
			Element sampleDateprimitiveValEl = new Element("sampleDate");
			sampleDateprimitiveValEl.addContent(XMLTools.dateToRelaxNgString(ViralIsolatevar.getSampleDate()));
			parentNode.addContent(sampleDateprimitiveValEl);
		}
		Element ntSequencesEl = new Element("ntSequences");
		parentNode.addContent(ntSequencesEl);
		for (NtSequence NtSequenceloopvar : ViralIsolatevar.getNtSequences())
		{
			Element ntSequences_elEl = new Element("ntSequences-el");
			ntSequencesEl.addContent(ntSequences_elEl);
			writeNtSequence(NtSequenceloopvar,ntSequences_elEl);
		}
		Element testResultsEl = new Element("testResults");
		parentNode.addContent(testResultsEl);
		for (TestResult TestResultloopvar : ViralIsolatevar.getTestResults())
		{
			Element testResults_elEl = new Element("testResults-el");
			testResultsEl.addContent(testResults_elEl);
			writeTestResult(TestResultloopvar,testResults_elEl);
		}
	}
	public void writeDataset(Dataset Datasetvar, Element rootNode)
	{
		Element parentNode = new Element("Dataset");
		rootNode.addContent(parentNode);
		if(Datasetvar==null)
		{
			return;
		}
		if(Datasetvar.getDescription()!=null)
		{
			Element descriptionprimitiveValEl = new Element("description");
			descriptionprimitiveValEl.addContent(Datasetvar.getDescription().toString());
			parentNode.addContent(descriptionprimitiveValEl);
		}
		if(Datasetvar.getCreationDate()!=null)
		{
			Element creationDateprimitiveValEl = new Element("creationDate");
			creationDateprimitiveValEl.addContent(XMLTools.dateToRelaxNgString(Datasetvar.getCreationDate()));
			parentNode.addContent(creationDateprimitiveValEl);
		}
		if(Datasetvar.getClosedDate()!=null)
		{
			Element closedDateprimitiveValEl = new Element("closedDate");
			closedDateprimitiveValEl.addContent(XMLTools.dateToRelaxNgString(Datasetvar.getClosedDate()));
			parentNode.addContent(closedDateprimitiveValEl);
		}
		if(Datasetvar.getRevision()!=null)
		{
			Element revisionprimitiveValEl = new Element("revision");
			revisionprimitiveValEl.addContent(Datasetvar.getRevision().toString());
			parentNode.addContent(revisionprimitiveValEl);
		}
	}
	public void writeTestType(TestType TestTypevar, Element rootNode)
	{
		Element parentNode = new Element("TestType");
		rootNode.addContent(parentNode);
		if(TestTypevar==null)
		{
			return;
		}
		if(TestTypevar.getValueType()!=null)
		{
			Integer indexvalueType = ValueTypePMap.get(TestTypevar.getValueType());
			Element wrappervalueType = new Element("valueType");
			parentNode.addContent(wrappervalueType);
			if(indexvalueType!=null)
			{
				Element refElementvalueType= new Element("reference");
				wrappervalueType.addContent(refElementvalueType);
				refElementvalueType.addContent(indexvalueType.toString());
			}
			else
			{
				indexvalueType = new Integer(ValueTypePMap.size());
				Element refElementvalueType= new Element("reference");
				wrappervalueType.addContent(refElementvalueType);
				refElementvalueType.addContent(indexvalueType.toString());
				ValueTypePMap.put(TestTypevar.getValueType(),indexvalueType);
				writeValueType(TestTypevar.getValueType(),wrappervalueType);
			}
		}
		if(TestTypevar.getTestObject()!=null)
		{
			Integer indextestObject = TestObjectPMap.get(TestTypevar.getTestObject());
			Element wrappertestObject = new Element("testObject");
			parentNode.addContent(wrappertestObject);
			if(indextestObject!=null)
			{
				Element refElementtestObject= new Element("reference");
				wrappertestObject.addContent(refElementtestObject);
				refElementtestObject.addContent(indextestObject.toString());
			}
			else
			{
				indextestObject = new Integer(TestObjectPMap.size());
				Element refElementtestObject= new Element("reference");
				wrappertestObject.addContent(refElementtestObject);
				refElementtestObject.addContent(indextestObject.toString());
				TestObjectPMap.put(TestTypevar.getTestObject(),indextestObject);
				writeTestObject(TestTypevar.getTestObject(),wrappertestObject);
			}
		}
		if(TestTypevar.getDescription()!=null)
		{
			Element descriptionprimitiveValEl = new Element("description");
			descriptionprimitiveValEl.addContent(TestTypevar.getDescription().toString());
			parentNode.addContent(descriptionprimitiveValEl);
		}
		Element forParenttestNominalValues = new Element("testNominalValues");
		parentNode.addContent(forParenttestNominalValues);
		if(TestTypevar.getTestNominalValues().size()!=0)
		{
			Element forParentLoopVar;
			for(TestNominalValue testNominalValuesloopvar :TestTypevar.getTestNominalValues())
			{
				forParentLoopVar = new Element("testNominalValues-el");
				forParenttestNominalValues.addContent(forParentLoopVar);
				if(testNominalValuesloopvar!=null)
				{
					Integer indextestNominalValuesloopvar = TestNominalValuePMap.get(testNominalValuesloopvar);
					Element wrappertestNominalValuesloopvar = forParentLoopVar;
					if(indextestNominalValuesloopvar!=null)
					{
						Element refElementtestNominalValuesloopvar= new Element("reference");
						wrappertestNominalValuesloopvar.addContent(refElementtestNominalValuesloopvar);
						refElementtestNominalValuesloopvar.addContent(indextestNominalValuesloopvar.toString());
					}
					else
					{
						indextestNominalValuesloopvar = new Integer(TestNominalValuePMap.size());
						Element refElementtestNominalValuesloopvar= new Element("reference");
						wrappertestNominalValuesloopvar.addContent(refElementtestNominalValuesloopvar);
						refElementtestNominalValuesloopvar.addContent(indextestNominalValuesloopvar.toString());
						TestNominalValuePMap.put(testNominalValuesloopvar,indextestNominalValuesloopvar);
						writeTestNominalValue(testNominalValuesloopvar,wrappertestNominalValuesloopvar);
					}
				}
			}
		}
	}
	public void writeAttribute(Attribute Attributevar, Element rootNode)
	{
		Element parentNode = new Element("Attribute");
		rootNode.addContent(parentNode);
		if(Attributevar==null)
		{
			return;
		}
		if(Attributevar.getValueType()!=null)
		{
			Integer indexvalueType = ValueTypePMap.get(Attributevar.getValueType());
			Element wrappervalueType = new Element("valueType");
			parentNode.addContent(wrappervalueType);
			if(indexvalueType!=null)
			{
				Element refElementvalueType= new Element("reference");
				wrappervalueType.addContent(refElementvalueType);
				refElementvalueType.addContent(indexvalueType.toString());
			}
			else
			{
				indexvalueType = new Integer(ValueTypePMap.size());
				Element refElementvalueType= new Element("reference");
				wrappervalueType.addContent(refElementvalueType);
				refElementvalueType.addContent(indexvalueType.toString());
				ValueTypePMap.put(Attributevar.getValueType(),indexvalueType);
				writeValueType(Attributevar.getValueType(),wrappervalueType);
			}
		}
		if(Attributevar.getAttributeGroup()!=null)
		{
			Integer indexattributeGroup = AttributeGroupPMap.get(Attributevar.getAttributeGroup());
			Element wrapperattributeGroup = new Element("attributeGroup");
			parentNode.addContent(wrapperattributeGroup);
			if(indexattributeGroup!=null)
			{
				Element refElementattributeGroup= new Element("reference");
				wrapperattributeGroup.addContent(refElementattributeGroup);
				refElementattributeGroup.addContent(indexattributeGroup.toString());
			}
			else
			{
				indexattributeGroup = new Integer(AttributeGroupPMap.size());
				Element refElementattributeGroup= new Element("reference");
				wrapperattributeGroup.addContent(refElementattributeGroup);
				refElementattributeGroup.addContent(indexattributeGroup.toString());
				AttributeGroupPMap.put(Attributevar.getAttributeGroup(),indexattributeGroup);
				writeAttributeGroup(Attributevar.getAttributeGroup(),wrapperattributeGroup);
			}
		}
		if(Attributevar.getName()!=null)
		{
			Element nameprimitiveValEl = new Element("name");
			nameprimitiveValEl.addContent(Attributevar.getName().toString());
			parentNode.addContent(nameprimitiveValEl);
		}
		Element forParentattributeNominalValues = new Element("attributeNominalValues");
		parentNode.addContent(forParentattributeNominalValues);
		if(Attributevar.getAttributeNominalValues().size()!=0)
		{
			Element forParentLoopVar;
			for(AttributeNominalValue attributeNominalValuesloopvar :Attributevar.getAttributeNominalValues())
			{
				forParentLoopVar = new Element("attributeNominalValues-el");
				forParentattributeNominalValues.addContent(forParentLoopVar);
				if(attributeNominalValuesloopvar!=null)
				{
					Integer indexattributeNominalValuesloopvar = AttributeNominalValuePMap.get(attributeNominalValuesloopvar);
					Element wrapperattributeNominalValuesloopvar = forParentLoopVar;
					if(indexattributeNominalValuesloopvar!=null)
					{
						Element refElementattributeNominalValuesloopvar= new Element("reference");
						wrapperattributeNominalValuesloopvar.addContent(refElementattributeNominalValuesloopvar);
						refElementattributeNominalValuesloopvar.addContent(indexattributeNominalValuesloopvar.toString());
					}
					else
					{
						indexattributeNominalValuesloopvar = new Integer(AttributeNominalValuePMap.size());
						Element refElementattributeNominalValuesloopvar= new Element("reference");
						wrapperattributeNominalValuesloopvar.addContent(refElementattributeNominalValuesloopvar);
						refElementattributeNominalValuesloopvar.addContent(indexattributeNominalValuesloopvar.toString());
						AttributeNominalValuePMap.put(attributeNominalValuesloopvar,indexattributeNominalValuesloopvar);
						writeAttributeNominalValue(attributeNominalValuesloopvar,wrapperattributeNominalValuesloopvar);
					}
				}
			}
		}
	}
	public void writeTestResult(TestResult TestResultvar, Element rootNode)
	{
		Element parentNode = new Element("TestResult");
		rootNode.addContent(parentNode);
		if(TestResultvar==null)
		{
			return;
		}
		if(TestResultvar.getTest()!=null)
		{
			Integer indextest = TestPMap.get(TestResultvar.getTest());
			Element wrappertest = new Element("test");
			parentNode.addContent(wrappertest);
			if(indextest!=null)
			{
				Element refElementtest= new Element("reference");
				wrappertest.addContent(refElementtest);
				refElementtest.addContent(indextest.toString());
			}
			else
			{
				indextest = new Integer(TestPMap.size());
				Element refElementtest= new Element("reference");
				wrappertest.addContent(refElementtest);
				refElementtest.addContent(indextest.toString());
				TestPMap.put(TestResultvar.getTest(),indextest);
				writeTest(TestResultvar.getTest(),wrappertest);
			}
		}
		if(TestResultvar.getDrugGeneric()!=null &&TestResultvar.getDrugGeneric().getGenericId()!=null)
		{
			Element drugGenericvar = new Element("drugGeneric");
			parentNode.addContent(drugGenericvar);
			drugGenericvar.addContent(TestResultvar.getDrugGeneric().getGenericId());
		}
		if(TestResultvar.getTestNominalValue()!=null)
		{
			Integer indextestNominalValue = TestNominalValuePMap.get(TestResultvar.getTestNominalValue());
			Element wrappertestNominalValue = new Element("testNominalValue");
			parentNode.addContent(wrappertestNominalValue);
			if(indextestNominalValue!=null)
			{
				Element refElementtestNominalValue= new Element("reference");
				wrappertestNominalValue.addContent(refElementtestNominalValue);
				refElementtestNominalValue.addContent(indextestNominalValue.toString());
			}
			else
			{
				indextestNominalValue = new Integer(TestNominalValuePMap.size());
				Element refElementtestNominalValue= new Element("reference");
				wrappertestNominalValue.addContent(refElementtestNominalValue);
				refElementtestNominalValue.addContent(indextestNominalValue.toString());
				TestNominalValuePMap.put(TestResultvar.getTestNominalValue(),indextestNominalValue);
				writeTestNominalValue(TestResultvar.getTestNominalValue(),wrappertestNominalValue);
			}
		}
		if(TestResultvar.getValue()!=null)
		{
			Element valueprimitiveValEl = new Element("value");
			valueprimitiveValEl.addContent(TestResultvar.getValue().toString());
			parentNode.addContent(valueprimitiveValEl);
		}
		if(TestResultvar.getTestDate()!=null)
		{
			Element testDateprimitiveValEl = new Element("testDate");
			testDateprimitiveValEl.addContent(XMLTools.dateToRelaxNgString(TestResultvar.getTestDate()));
			parentNode.addContent(testDateprimitiveValEl);
		}
		if(TestResultvar.getSampleId()!=null)
		{
			Element sampleIdprimitiveValEl = new Element("sampleId");
			sampleIdprimitiveValEl.addContent(TestResultvar.getSampleId().toString());
			parentNode.addContent(sampleIdprimitiveValEl);
		}
	}
	public void writeTherapy(Therapy Therapyvar, Element rootNode)
	{
		Element parentNode = new Element("Therapy");
		rootNode.addContent(parentNode);
		if(Therapyvar==null)
		{
			return;
		}
		if(Therapyvar.getStartDate()!=null)
		{
			Element startDateprimitiveValEl = new Element("startDate");
			startDateprimitiveValEl.addContent(XMLTools.dateToRelaxNgString(Therapyvar.getStartDate()));
			parentNode.addContent(startDateprimitiveValEl);
		}
		if(Therapyvar.getStopDate()!=null)
		{
			Element stopDateprimitiveValEl = new Element("stopDate");
			stopDateprimitiveValEl.addContent(XMLTools.dateToRelaxNgString(Therapyvar.getStopDate()));
			parentNode.addContent(stopDateprimitiveValEl);
		}
		if(Therapyvar.getComment()!=null)
		{
			Element commentprimitiveValEl = new Element("comment");
			commentprimitiveValEl.addContent(Therapyvar.getComment().toString());
			parentNode.addContent(commentprimitiveValEl);
		}
		Element therapyCommercialsEl = new Element("therapyCommercials");
		parentNode.addContent(therapyCommercialsEl);
		for (TherapyCommercial TherapyCommercialloopvar : Therapyvar.getTherapyCommercials())
		{
			Element therapyCommercials_elEl = new Element("therapyCommercials-el");
			therapyCommercialsEl.addContent(therapyCommercials_elEl);
			writeTherapyCommercial(TherapyCommercialloopvar,therapyCommercials_elEl);
		}
		Element therapyGenericsEl = new Element("therapyGenerics");
		parentNode.addContent(therapyGenericsEl);
		for (TherapyGeneric TherapyGenericloopvar : Therapyvar.getTherapyGenerics())
		{
			Element therapyGenerics_elEl = new Element("therapyGenerics-el");
			therapyGenericsEl.addContent(therapyGenerics_elEl);
			writeTherapyGeneric(TherapyGenericloopvar,therapyGenerics_elEl);
		}
	}
	public void writeAttributeNominalValue(AttributeNominalValue AttributeNominalValuevar, Element rootNode)
	{
		Element parentNode = new Element("AttributeNominalValue");
		rootNode.addContent(parentNode);
		if(AttributeNominalValuevar==null)
		{
			return;
		}
		if(AttributeNominalValuevar.getValue()!=null)
		{
			Element valueprimitiveValEl = new Element("value");
			valueprimitiveValEl.addContent(AttributeNominalValuevar.getValue().toString());
			parentNode.addContent(valueprimitiveValEl);
		}
	}
	public void writeAnalysisData(AnalysisData AnalysisDatavar, Element rootNode)
	{
		Element parentNode = new Element("AnalysisData");
		rootNode.addContent(parentNode);
		if(AnalysisDatavar==null)
		{
			return;
		}
		if(AnalysisDatavar.getAnalysis()!=null)
		{
			Integer indexanalysis = AnalysisPMap.get(AnalysisDatavar.getAnalysis());
			Element wrapperanalysis = new Element("analysis");
			parentNode.addContent(wrapperanalysis);
			if(indexanalysis!=null)
			{
				Element refElementanalysis= new Element("reference");
				wrapperanalysis.addContent(refElementanalysis);
				refElementanalysis.addContent(indexanalysis.toString());
			}
			else
			{
				indexanalysis = new Integer(AnalysisPMap.size());
				Element refElementanalysis= new Element("reference");
				wrapperanalysis.addContent(refElementanalysis);
				refElementanalysis.addContent(indexanalysis.toString());
				AnalysisPMap.put(AnalysisDatavar.getAnalysis(),indexanalysis);
				writeAnalysis(AnalysisDatavar.getAnalysis(),wrapperanalysis);
			}
		}
		if(AnalysisDatavar.getName()!=null)
		{
			Element nameprimitiveValEl = new Element("name");
			nameprimitiveValEl.addContent(AnalysisDatavar.getName().toString());
			parentNode.addContent(nameprimitiveValEl);
		}
		if(AnalysisDatavar.getData()!=null)
		{
			Element dataprimitiveValEl = new Element("data");
			dataprimitiveValEl.addContent(XMLTools.base64Encoding(AnalysisDatavar.getData()));
			parentNode.addContent(dataprimitiveValEl);
		}
		if(AnalysisDatavar.getMimetype()!=null)
		{
			Element mimetypeprimitiveValEl = new Element("mimetype");
			mimetypeprimitiveValEl.addContent(AnalysisDatavar.getMimetype().toString());
			parentNode.addContent(mimetypeprimitiveValEl);
		}
	}
	public void writeTherapyGeneric(TherapyGeneric TherapyGenericvar, Element rootNode)
	{
		Element parentNode = new Element("TherapyGeneric");
		rootNode.addContent(parentNode);
		if(TherapyGenericvar==null)
		{
			return;
		}
		if(TherapyGenericvar.getId().getDrugGeneric()!=null &&TherapyGenericvar.getId().getDrugGeneric().getGenericId()!=null)
		{
			Element drugGenericvar = new Element("drugGeneric");
			parentNode.addContent(drugGenericvar);
			drugGenericvar.addContent(TherapyGenericvar.getId().getDrugGeneric().getGenericId());
		}
		if(TherapyGenericvar.getDayDosageMg()!=null)
		{
			Element dayDosageMgprimitiveValEl = new Element("dayDosageMg");
			dayDosageMgprimitiveValEl.addContent(TherapyGenericvar.getDayDosageMg().toString());
			parentNode.addContent(dayDosageMgprimitiveValEl);
		}
	}
	public void writePatientAttributeValue(PatientAttributeValue PatientAttributeValuevar, Element rootNode)
	{
		Element parentNode = new Element("PatientAttributeValue");
		rootNode.addContent(parentNode);
		if(PatientAttributeValuevar==null)
		{
			return;
		}
		if(PatientAttributeValuevar.getId().getAttribute()!=null)
		{
			Integer indexattribute = AttributePMap.get(PatientAttributeValuevar.getId().getAttribute());
			Element wrapperattribute = new Element("attribute");
			parentNode.addContent(wrapperattribute);
			if(indexattribute!=null)
			{
				Element refElementattribute= new Element("reference");
				wrapperattribute.addContent(refElementattribute);
				refElementattribute.addContent(indexattribute.toString());
			}
			else
			{
				indexattribute = new Integer(AttributePMap.size());
				Element refElementattribute= new Element("reference");
				wrapperattribute.addContent(refElementattribute);
				refElementattribute.addContent(indexattribute.toString());
				AttributePMap.put(PatientAttributeValuevar.getId().getAttribute(),indexattribute);
				writeAttribute(PatientAttributeValuevar.getId().getAttribute(),wrapperattribute);
			}
		}
		if(PatientAttributeValuevar.getAttributeNominalValue()!=null)
		{
			Integer indexattributeNominalValue = AttributeNominalValuePMap.get(PatientAttributeValuevar.getAttributeNominalValue());
			Element wrapperattributeNominalValue = new Element("attributeNominalValue");
			parentNode.addContent(wrapperattributeNominalValue);
			if(indexattributeNominalValue!=null)
			{
				Element refElementattributeNominalValue= new Element("reference");
				wrapperattributeNominalValue.addContent(refElementattributeNominalValue);
				refElementattributeNominalValue.addContent(indexattributeNominalValue.toString());
			}
			else
			{
				indexattributeNominalValue = new Integer(AttributeNominalValuePMap.size());
				Element refElementattributeNominalValue= new Element("reference");
				wrapperattributeNominalValue.addContent(refElementattributeNominalValue);
				refElementattributeNominalValue.addContent(indexattributeNominalValue.toString());
				AttributeNominalValuePMap.put(PatientAttributeValuevar.getAttributeNominalValue(),indexattributeNominalValue);
				writeAttributeNominalValue(PatientAttributeValuevar.getAttributeNominalValue(),wrapperattributeNominalValue);
			}
		}
		if(PatientAttributeValuevar.getValue()!=null)
		{
			Element valueprimitiveValEl = new Element("value");
			valueprimitiveValEl.addContent(PatientAttributeValuevar.getValue().toString());
			parentNode.addContent(valueprimitiveValEl);
		}
	}
	public void writeTestNominalValue(TestNominalValue TestNominalValuevar, Element rootNode)
	{
		Element parentNode = new Element("TestNominalValue");
		rootNode.addContent(parentNode);
		if(TestNominalValuevar==null)
		{
			return;
		}
		if(TestNominalValuevar.getTestType()!=null)
		{
			Integer indextestType = TestTypePMap.get(TestNominalValuevar.getTestType());
			Element wrappertestType = new Element("testType");
			parentNode.addContent(wrappertestType);
			if(indextestType!=null)
			{
				Element refElementtestType= new Element("reference");
				wrappertestType.addContent(refElementtestType);
				refElementtestType.addContent(indextestType.toString());
			}
			else
			{
				indextestType = new Integer(TestTypePMap.size());
				Element refElementtestType= new Element("reference");
				wrappertestType.addContent(refElementtestType);
				refElementtestType.addContent(indextestType.toString());
				TestTypePMap.put(TestNominalValuevar.getTestType(),indextestType);
				writeTestType(TestNominalValuevar.getTestType(),wrappertestType);
			}
		}
		if(TestNominalValuevar.getValue()!=null)
		{
			Element valueprimitiveValEl = new Element("value");
			valueprimitiveValEl.addContent(TestNominalValuevar.getValue().toString());
			parentNode.addContent(valueprimitiveValEl);
		}
	}
}
