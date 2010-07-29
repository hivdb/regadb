package net.sf.regadb.io.importXML;
import java.util.*;
import net.sf.regadb.db.*;
import net.sf.regadb.db.meta.*;
import org.xml.sax.*;
import org.xml.sax.helpers.XMLReaderFactory;
import java.io.IOException;
import net.sf.regadb.io.util.StandardObjects;

public class ImportFromXML extends ImportFromXMLBase {
    enum ParseState { TopLevel, statePatient, stateDataset, stateTestResult, stateTest, stateAnalysis, stateAnalysisData, statePatientEventValue, stateTestType, stateTestObject, stateTestNominalValue, statePatientAttributeValue, stateAttribute, stateAttributeGroup, stateAttributeNominalValue, stateViralIsolate, stateNtSequence, stateAaSequence, stateEvent, stateProtein, stateOpenReadingFrame, stateAaMutation, stateAaInsertion, stateTherapy, stateTherapyCommercial, stateTherapyGeneric, stateValueType, stateEventNominalValue };

    public ImportFromXML() {
        parseStateStack.add(ParseState.TopLevel);
    }

    private ArrayList<ParseState> parseStateStack = new ArrayList<ParseState>();

    void pushState(ParseState state) {
        parseStateStack.add(state);
    }

    void popState() {
        parseStateStack.remove(parseStateStack.size() - 1);
    }

    ParseState currentState() {
        return parseStateStack.get(parseStateStack.size() - 1);
    }

    List topLevelObjects = new ArrayList();
    ImportHandler importHandler = null;
    Class topLevelClass = null;

    private Map<String, Test> refTestMap = new HashMap<String, Test>();
    private String referenceTest = null;
    private Set<Test> syncedTestSet = new HashSet<Test>();
    private Map<String, Analysis> refAnalysisMap = new HashMap<String, Analysis>();
    private String referenceAnalysis = null;
    private Set<Analysis> syncedAnalysisSet = new HashSet<Analysis>();
    private Map<String, AnalysisData> refAnalysisDataMap = new HashMap<String, AnalysisData>();
    private String referenceAnalysisData = null;
    private Set<AnalysisData> syncedAnalysisDataSet = new HashSet<AnalysisData>();
    private Map<String, TestType> refTestTypeMap = new HashMap<String, TestType>();
    private String referenceTestType = null;
    private Set<TestType> syncedTestTypeSet = new HashSet<TestType>();
    private Map<String, TestObject> refTestObjectMap = new HashMap<String, TestObject>();
    private String referenceTestObject = null;
    private Set<TestObject> syncedTestObjectSet = new HashSet<TestObject>();
    private Map<String, TestNominalValue> refTestNominalValueMap = new HashMap<String, TestNominalValue>();
    private String referenceTestNominalValue = null;
    private Set<TestNominalValue> syncedTestNominalValueSet = new HashSet<TestNominalValue>();
    private Map<String, Attribute> refAttributeMap = new HashMap<String, Attribute>();
    private String referenceAttribute = null;
    private Set<Attribute> syncedAttributeSet = new HashSet<Attribute>();
    private Map<String, AttributeGroup> refAttributeGroupMap = new HashMap<String, AttributeGroup>();
    private String referenceAttributeGroup = null;
    private Set<AttributeGroup> syncedAttributeGroupSet = new HashSet<AttributeGroup>();
    private Map<String, AttributeNominalValue> refAttributeNominalValueMap = new HashMap<String, AttributeNominalValue>();
    private String referenceAttributeNominalValue = null;
    private Set<AttributeNominalValue> syncedAttributeNominalValueSet = new HashSet<AttributeNominalValue>();
    private Map<String, Event> refEventMap = new HashMap<String, Event>();
    private String referenceEvent = null;
    private Set<Event> syncedEventSet = new HashSet<Event>();
    private Map<String, Protein> refProteinMap = new HashMap<String, Protein>();
    private String referenceProtein = null;
    private Set<Protein> syncedProteinSet = new HashSet<Protein>();
    private Map<String, OpenReadingFrame> refOpenReadingFrameMap = new HashMap<String, OpenReadingFrame>();
    private String referenceOpenReadingFrame = null;
    private Set<OpenReadingFrame> syncedOpenReadingFrameSet = new HashSet<OpenReadingFrame>();
    private Map<String, ValueType> refValueTypeMap = new HashMap<String, ValueType>();
    private String referenceValueType = null;
    private Set<ValueType> syncedValueTypeSet = new HashSet<ValueType>();
    private Map<String, EventNominalValue> refEventNominalValueMap = new HashMap<String, EventNominalValue>();
    private String referenceEventNominalValue = null;
    private Set<EventNominalValue> syncedEventNominalValueSet = new HashSet<EventNominalValue>();
    private String fieldPatient_patientId;
    private Set<PatientEventValue> fieldPatient_patientEventValues;
    private Set<Dataset> fieldPatient_patientDatasets;
    private Set<TestResult> fieldPatient_testResults;
    private Set<PatientAttributeValue> fieldPatient_patientAttributeValues;
    private Set<ViralIsolate> fieldPatient_viralIsolates;
    private Set<Therapy> fieldPatient_therapies;
    private String fieldDataset_description;
    private Date fieldDataset_creationDate;
    private Date fieldDataset_closedDate;
    private Integer fieldDataset_revision;
    private Test fieldTestResult_test;
    private DrugGeneric fieldTestResult_drugGeneric;
    private TestNominalValue fieldTestResult_testNominalValue;
    private String fieldTestResult_value;
    private Date fieldTestResult_testDate;
    private String fieldTestResult_sampleId;
    private byte[] fieldTestResult_data;
    private Analysis fieldTest_analysis;
    private TestType fieldTest_testType;
    private String fieldTest_description;
    private AnalysisType fieldAnalysis_analysisType;
    private String fieldAnalysis_url;
    private String fieldAnalysis_account;
    private String fieldAnalysis_password;
    private String fieldAnalysis_baseinputfile;
    private String fieldAnalysis_baseoutputfile;
    private String fieldAnalysis_serviceName;
    private String fieldAnalysis_dataoutputfile;
    private Set<AnalysisData> fieldAnalysis_analysisDatas;
    private String fieldAnalysisData_name;
    private byte[] fieldAnalysisData_data;
    private String fieldAnalysisData_mimetype;
    private EventNominalValue fieldPatientEventValue_eventNominalValue;
    private Event fieldPatientEventValue_event;
    private String fieldPatientEventValue_value;
    private Date fieldPatientEventValue_startDate;
    private Date fieldPatientEventValue_endDate;
    private ValueType fieldTestType_valueType;
    private Genome fieldTestType_genome;
    private TestObject fieldTestType_testObject;
    private String fieldTestType_description;
    private Set<TestNominalValue> fieldTestType_testNominalValues;
    private String fieldTestObject_description;
    private Integer fieldTestObject_testObjectId;
    private String fieldTestNominalValue_value;
    private Attribute fieldPatientAttributeValue_attribute;
    private AttributeNominalValue fieldPatientAttributeValue_attributeNominalValue;
    private String fieldPatientAttributeValue_value;
    private ValueType fieldAttribute_valueType;
    private AttributeGroup fieldAttribute_attributeGroup;
    private String fieldAttribute_name;
    private Set<AttributeNominalValue> fieldAttribute_attributeNominalValues;
    private String fieldAttributeGroup_groupName;
    private String fieldAttributeNominalValue_value;
    private String fieldViralIsolate_sampleId;
    private Date fieldViralIsolate_sampleDate;
    private Genome fieldViralIsolate_genome;
    private Set<NtSequence> fieldViralIsolate_ntSequences;
    private Set<TestResult> fieldViralIsolate_testResults;
    private String fieldNtSequence_label;
    private Date fieldNtSequence_sequenceDate;
    private String fieldNtSequence_nucleotides;
    private boolean fieldNtSequence_aligned;
    private Set<AaSequence> fieldNtSequence_aaSequences;
    private Set<TestResult> fieldNtSequence_testResults;
    private Protein fieldAaSequence_protein;
    private short fieldAaSequence_firstAaPos;
    private short fieldAaSequence_lastAaPos;
    private Set<AaMutation> fieldAaSequence_aaMutations;
    private Set<AaInsertion> fieldAaSequence_aaInsertions;
    private ValueType fieldEvent_valueType;
    private String fieldEvent_name;
    private Set<EventNominalValue> fieldEvent_eventNominalValues;
    private OpenReadingFrame fieldProtein_openReadingFrame;
    private String fieldProtein_abbreviation;
    private Genome fieldOpenReadingFrame_genome;
    private String fieldOpenReadingFrame_name;
    private short fieldAaMutation_mutationPosition;
    private String fieldAaMutation_aaReference;
    private String fieldAaMutation_aaMutation;
    private String fieldAaMutation_ntReferenceCodon;
    private String fieldAaMutation_ntMutationCodon;
    private short fieldAaInsertion_insertionPosition;
    private short fieldAaInsertion_insertionOrder;
    private String fieldAaInsertion_aaInsertion;
    private String fieldAaInsertion_ntInsertionCodon;
    private TherapyMotivation fieldTherapy_therapyMotivation;
    private Date fieldTherapy_startDate;
    private Date fieldTherapy_stopDate;
    private String fieldTherapy_comment;
    private Set<TherapyCommercial> fieldTherapy_therapyCommercials;
    private Set<TherapyGeneric> fieldTherapy_therapyGenerics;
    private DrugCommercial fieldTherapyCommercial_drugCommercial;
    private Double fieldTherapyCommercial_dayDosageUnits;
    private boolean fieldTherapyCommercial_placebo;
    private boolean fieldTherapyCommercial_blind;
    private Long fieldTherapyCommercial_frequency;
    private DrugGeneric fieldTherapyGeneric_drugGeneric;
    private Double fieldTherapyGeneric_dayDosageMg;
    private boolean fieldTherapyGeneric_placebo;
    private boolean fieldTherapyGeneric_blind;
    private Long fieldTherapyGeneric_frequency;
    private String fieldValueType_description;
    private Double fieldValueType_minimum;
    private Double fieldValueType_maximum;
    private Boolean fieldValueType_multiple;
    private String fieldEventNominalValue_value;

    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        value = null;
        if (false) {
        } else if ("Patient".equals(qName)) {
        } else if ("patients-el".equals(qName)|| "patients-el".equals(qName)) {
            pushState(ParseState.statePatient);
            patient = new Patient();
            fieldPatient_patientId = nullValueString();
            fieldPatient_patientEventValues = new HashSet<PatientEventValue>();
            fieldPatient_patientDatasets = new HashSet<Dataset>();
            fieldPatient_testResults = new HashSet<TestResult>();
            fieldPatient_patientAttributeValues = new HashSet<PatientAttributeValue>();
            fieldPatient_viralIsolates = new HashSet<ViralIsolate>();
            fieldPatient_therapies = new HashSet<Therapy>();
        } else if ("Dataset".equals(qName)) {
        } else if ("datasets-el".equals(qName)|| "patientDatasets-el".equals(qName)) {
            pushState(ParseState.stateDataset);
            fieldDataset_description = nullValueString();
            fieldDataset_creationDate = nullValueDate();
            fieldDataset_closedDate = nullValueDate();
            fieldDataset_revision = nullValueInteger();
        } else if ("TestResult".equals(qName)) {
        } else if ("testResults-el".equals(qName)|| "testResults-el".equals(qName)|| "testResults-el".equals(qName)|| "testResults-el".equals(qName)) {
            pushState(ParseState.stateTestResult);
            fieldTestResult_test = null;
            fieldTestResult_drugGeneric = null;
            fieldTestResult_testNominalValue = null;
            fieldTestResult_value = nullValueString();
            fieldTestResult_testDate = nullValueDate();
            fieldTestResult_sampleId = nullValueString();
            fieldTestResult_data = nullValuebyteArray();
        } else if ("Test".equals(qName)) {
        } else if ("tests-el".equals(qName)|| "test".equals(qName)) {
            pushState(ParseState.stateTest);
            referenceTest = null;
            fieldTest_analysis = null;
            fieldTest_testType = null;
            fieldTest_description = nullValueString();
        } else if ("Analysis".equals(qName)) {
        } else if ("analysiss-el".equals(qName)|| "analysis".equals(qName)) {
            pushState(ParseState.stateAnalysis);
            referenceAnalysis = null;
            fieldAnalysis_analysisType = null;
            fieldAnalysis_url = nullValueString();
            fieldAnalysis_account = nullValueString();
            fieldAnalysis_password = nullValueString();
            fieldAnalysis_baseinputfile = nullValueString();
            fieldAnalysis_baseoutputfile = nullValueString();
            fieldAnalysis_serviceName = nullValueString();
            fieldAnalysis_dataoutputfile = nullValueString();
            fieldAnalysis_analysisDatas = new HashSet<AnalysisData>();
        } else if ("AnalysisData".equals(qName)) {
        } else if ("analysisDatas-el".equals(qName)|| "analysisDatas-el".equals(qName)) {
            pushState(ParseState.stateAnalysisData);
            referenceAnalysisData = null;
            fieldAnalysisData_name = nullValueString();
            fieldAnalysisData_data = nullValuebyteArray();
            fieldAnalysisData_mimetype = nullValueString();
        } else if ("PatientEventValue".equals(qName)) {
        } else if ("patientEventValues-el".equals(qName)|| "patientEventValues-el".equals(qName)) {
            pushState(ParseState.statePatientEventValue);
            fieldPatientEventValue_eventNominalValue = null;
            fieldPatientEventValue_event = null;
            fieldPatientEventValue_value = nullValueString();
            fieldPatientEventValue_startDate = nullValueDate();
            fieldPatientEventValue_endDate = nullValueDate();
        } else if ("TestType".equals(qName)) {
        } else if ("testTypes-el".equals(qName)|| "testType".equals(qName)) {
            pushState(ParseState.stateTestType);
            referenceTestType = null;
            fieldTestType_valueType = null;
            fieldTestType_genome = null;
            fieldTestType_testObject = null;
            fieldTestType_description = nullValueString();
            fieldTestType_testNominalValues = new HashSet<TestNominalValue>();
        } else if ("TestObject".equals(qName)) {
        } else if ("testObjects-el".equals(qName)|| "testObject".equals(qName)) {
            pushState(ParseState.stateTestObject);
            referenceTestObject = null;
            fieldTestObject_description = nullValueString();
            fieldTestObject_testObjectId = nullValueInteger();
        } else if ("TestNominalValue".equals(qName)) {
        } else if ("testNominalValues-el".equals(qName)|| "testNominalValue".equals(qName)|| "testNominalValues-el".equals(qName)) {
            pushState(ParseState.stateTestNominalValue);
            referenceTestNominalValue = null;
            fieldTestNominalValue_value = nullValueString();
        } else if ("PatientAttributeValue".equals(qName)) {
        } else if ("patientAttributeValues-el".equals(qName)|| "patientAttributeValues-el".equals(qName)) {
            pushState(ParseState.statePatientAttributeValue);
            fieldPatientAttributeValue_attribute = null;
            fieldPatientAttributeValue_attributeNominalValue = null;
            fieldPatientAttributeValue_value = nullValueString();
        } else if ("Attribute".equals(qName)) {
        } else if ("attributes-el".equals(qName)|| "attribute".equals(qName)) {
            pushState(ParseState.stateAttribute);
            referenceAttribute = null;
            fieldAttribute_valueType = null;
            fieldAttribute_attributeGroup = null;
            fieldAttribute_name = nullValueString();
            fieldAttribute_attributeNominalValues = new HashSet<AttributeNominalValue>();
        } else if ("AttributeGroup".equals(qName)) {
        } else if ("attributeGroups-el".equals(qName)|| "attributeGroup".equals(qName)) {
            pushState(ParseState.stateAttributeGroup);
            referenceAttributeGroup = null;
            fieldAttributeGroup_groupName = nullValueString();
        } else if ("AttributeNominalValue".equals(qName)) {
        } else if ("attributeNominalValues-el".equals(qName)|| "attributeNominalValue".equals(qName)|| "attributeNominalValues-el".equals(qName)) {
            pushState(ParseState.stateAttributeNominalValue);
            referenceAttributeNominalValue = null;
            fieldAttributeNominalValue_value = nullValueString();
        } else if ("ViralIsolate".equals(qName)) {
        } else if ("viralIsolates-el".equals(qName)|| "viralIsolates-el".equals(qName)) {
            pushState(ParseState.stateViralIsolate);
            fieldViralIsolate_sampleId = nullValueString();
            fieldViralIsolate_sampleDate = nullValueDate();
            fieldViralIsolate_genome = null;
            fieldViralIsolate_ntSequences = new HashSet<NtSequence>();
            fieldViralIsolate_testResults = new HashSet<TestResult>();
        } else if ("NtSequence".equals(qName)) {
        } else if ("ntSequences-el".equals(qName)|| "ntSequences-el".equals(qName)) {
            pushState(ParseState.stateNtSequence);
            fieldNtSequence_label = nullValueString();
            fieldNtSequence_sequenceDate = nullValueDate();
            fieldNtSequence_nucleotides = nullValueString();
            fieldNtSequence_aligned = nullValueboolean();
            fieldNtSequence_aaSequences = new HashSet<AaSequence>();
            fieldNtSequence_testResults = new HashSet<TestResult>();
        } else if ("AaSequence".equals(qName)) {
        } else if ("aaSequences-el".equals(qName)|| "aaSequences-el".equals(qName)) {
            pushState(ParseState.stateAaSequence);
            fieldAaSequence_protein = null;
            fieldAaSequence_firstAaPos = nullValueshort();
            fieldAaSequence_lastAaPos = nullValueshort();
            fieldAaSequence_aaMutations = new HashSet<AaMutation>();
            fieldAaSequence_aaInsertions = new HashSet<AaInsertion>();
        } else if ("Event".equals(qName)) {
        } else if ("events-el".equals(qName)|| "event".equals(qName)) {
            pushState(ParseState.stateEvent);
            referenceEvent = null;
            fieldEvent_valueType = null;
            fieldEvent_name = nullValueString();
            fieldEvent_eventNominalValues = new HashSet<EventNominalValue>();
        } else if ("Protein".equals(qName)) {
        } else if ("proteins-el".equals(qName)|| "protein".equals(qName)) {
            pushState(ParseState.stateProtein);
            referenceProtein = null;
            fieldProtein_openReadingFrame = null;
            fieldProtein_abbreviation = nullValueString();
        } else if ("OpenReadingFrame".equals(qName)) {
        } else if ("openReadingFrames-el".equals(qName)|| "openReadingFrame".equals(qName)) {
            pushState(ParseState.stateOpenReadingFrame);
            referenceOpenReadingFrame = null;
            fieldOpenReadingFrame_genome = null;
            fieldOpenReadingFrame_name = nullValueString();
        } else if ("AaMutation".equals(qName)) {
        } else if ("aaMutations-el".equals(qName)|| "aaMutations-el".equals(qName)) {
            pushState(ParseState.stateAaMutation);
            fieldAaMutation_mutationPosition = nullValueshort();
            fieldAaMutation_aaReference = nullValueString();
            fieldAaMutation_aaMutation = nullValueString();
            fieldAaMutation_ntReferenceCodon = nullValueString();
            fieldAaMutation_ntMutationCodon = nullValueString();
        } else if ("AaInsertion".equals(qName)) {
        } else if ("aaInsertions-el".equals(qName)|| "aaInsertions-el".equals(qName)) {
            pushState(ParseState.stateAaInsertion);
            fieldAaInsertion_insertionPosition = nullValueshort();
            fieldAaInsertion_insertionOrder = nullValueshort();
            fieldAaInsertion_aaInsertion = nullValueString();
            fieldAaInsertion_ntInsertionCodon = nullValueString();
        } else if ("Therapy".equals(qName)) {
        } else if ("therapys-el".equals(qName)|| "therapies-el".equals(qName)) {
            pushState(ParseState.stateTherapy);
            fieldTherapy_therapyMotivation = null;
            fieldTherapy_startDate = nullValueDate();
            fieldTherapy_stopDate = nullValueDate();
            fieldTherapy_comment = nullValueString();
            fieldTherapy_therapyCommercials = new HashSet<TherapyCommercial>();
            fieldTherapy_therapyGenerics = new HashSet<TherapyGeneric>();
        } else if ("TherapyCommercial".equals(qName)) {
        } else if ("therapyCommercials-el".equals(qName)|| "therapyCommercials-el".equals(qName)) {
            pushState(ParseState.stateTherapyCommercial);
            fieldTherapyCommercial_drugCommercial = null;
            fieldTherapyCommercial_dayDosageUnits = nullValueDouble();
            fieldTherapyCommercial_placebo = nullValueboolean();
            fieldTherapyCommercial_blind = nullValueboolean();
            fieldTherapyCommercial_frequency = nullValueLong();
        } else if ("TherapyGeneric".equals(qName)) {
        } else if ("therapyGenerics-el".equals(qName)|| "therapyGenerics-el".equals(qName)) {
            pushState(ParseState.stateTherapyGeneric);
            fieldTherapyGeneric_drugGeneric = null;
            fieldTherapyGeneric_dayDosageMg = nullValueDouble();
            fieldTherapyGeneric_placebo = nullValueboolean();
            fieldTherapyGeneric_blind = nullValueboolean();
            fieldTherapyGeneric_frequency = nullValueLong();
        } else if ("ValueType".equals(qName)) {
        } else if ("valueTypes-el".equals(qName)|| "valueType".equals(qName)|| "valueType".equals(qName)|| "valueType".equals(qName)) {
            pushState(ParseState.stateValueType);
            referenceValueType = null;
            fieldValueType_description = nullValueString();
            fieldValueType_minimum = nullValueDouble();
            fieldValueType_maximum = nullValueDouble();
            fieldValueType_multiple = nullValueBoolean();
        } else if ("EventNominalValue".equals(qName)) {
        } else if ("eventNominalValues-el".equals(qName)|| "eventNominalValue".equals(qName)|| "eventNominalValues-el".equals(qName)) {
            pushState(ParseState.stateEventNominalValue);
            referenceEventNominalValue = null;
            fieldEventNominalValue_value = nullValueString();
        }
    }

    @SuppressWarnings("unchecked")
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (false) {
        } else if ("Patient".equals(qName)) {
        } else if (currentState() == ParseState.statePatient) {
            if ("patients-el".equals(qName)|| "patients-el".equals(qName)) {
                popState();
                Patient elPatient = null;
                if (currentState() == ParseState.TopLevel) {
                    if (topLevelClass == Patient.class) {
                        elPatient = patient;
                    } else {
                        throw new SAXException(new ImportException("Unexpected top level object: " + qName));
                    }
                } else {
                    throw new SAXException(new ImportException("Nested object problem: " + qName));
                }
                {
                    elPatient.setPatientId(fieldPatient_patientId);
                }
                {
                }
                {
                }
                {
                }
                {
                }
                {
                }
                {
                }
                if (currentState() == ParseState.TopLevel) {
                    if (importHandler != null)
                        importHandler.importObject(elPatient);
                    else
                        topLevelObjects.add(elPatient);
                }
            } else if ("patientId".equals(qName)) {
                fieldPatient_patientId = parseString(value == null ? null : value.toString());
            } else if ("patientEventValues".equals(qName)) {
            } else if ("patientDatasets".equals(qName)) {
            } else if ("testResults".equals(qName)) {
            } else if ("patientAttributeValues".equals(qName)) {
            } else if ("viralIsolates".equals(qName)) {
            } else if ("therapies".equals(qName)) {
            } else {
                //throw new SAXException(new ImportException("Unrecognized element: " + qName));
                System.err.println("Unrecognized element: " + qName);
            }
        } else if ("Dataset".equals(qName)) {
        } else if (currentState() == ParseState.stateDataset) {
            if ("datasets-el".equals(qName)|| "patientDatasets-el".equals(qName)) {
                popState();
                Dataset elDataset = null;
                if (currentState() == ParseState.TopLevel) {
                    if (topLevelClass == Dataset.class) {
                        elDataset = new Dataset();
                    } else {
                        throw new SAXException(new ImportException("Unexpected top level object: " + qName));
                    }
                } else if (currentState() == ParseState.statePatient) {
                    elDataset = resolveDataset(fieldDataset_description);
                    addDataset(patient, elDataset);
                } else {
                    throw new SAXException(new ImportException("Nested object problem: " + qName));
                }
                {
                    elDataset.setDescription(fieldDataset_description);
                }
                {
                    elDataset.setCreationDate(fieldDataset_creationDate);
                }
                {
                    elDataset.setClosedDate(fieldDataset_closedDate);
                }
                {
                    elDataset.setRevision(fieldDataset_revision);
                }
                if (currentState() == ParseState.TopLevel) {
                    if (importHandler != null)
                        importHandler.importObject(elDataset);
                    else
                        topLevelObjects.add(elDataset);
                }
            } else if ("description".equals(qName)) {
                fieldDataset_description = parseString(value == null ? null : value.toString());
            } else if ("creationDate".equals(qName)) {
                fieldDataset_creationDate = parseDate(value == null ? null : value.toString());
            } else if ("closedDate".equals(qName)) {
                fieldDataset_closedDate = parseDate(value == null ? null : value.toString());
            } else if ("revision".equals(qName)) {
                fieldDataset_revision = parseInteger(value == null ? null : value.toString());
            } else {
                //throw new SAXException(new ImportException("Unrecognized element: " + qName));
                System.err.println("Unrecognized element: " + qName);
            }
        } else if ("TestResult".equals(qName)) {
        } else if (currentState() == ParseState.stateTestResult) {
            if ("testResults-el".equals(qName)|| "testResults-el".equals(qName)|| "testResults-el".equals(qName)|| "testResults-el".equals(qName)) {
                popState();
                TestResult elTestResult = null;
                if (currentState() == ParseState.TopLevel) {
                    if (topLevelClass == TestResult.class) {
                        elTestResult = new TestResult();
                    } else {
                        throw new SAXException(new ImportException("Unexpected top level object: " + qName));
                    }
                } else if (currentState() == ParseState.statePatient) {
                    elTestResult = patient.createTestResult(fieldTestResult_test);
                    fieldPatient_testResults.add(elTestResult);
                } else if (currentState() == ParseState.stateViralIsolate) {
                    elTestResult = new TestResult(fieldTestResult_test);
                    patient.addTestResult(elTestResult);
                    fieldViralIsolate_testResults.add(elTestResult);
                } else if (currentState() == ParseState.stateNtSequence) {
                    elTestResult = new TestResult(fieldTestResult_test);
                    patient.addTestResult(elTestResult);
                    fieldNtSequence_testResults.add(elTestResult);
                } else {
                    throw new SAXException(new ImportException("Nested object problem: " + qName));
                }
                {
                    elTestResult.setTest(fieldTestResult_test);
                }
                {
                    elTestResult.setDrugGeneric(fieldTestResult_drugGeneric);
                }
                {
                    elTestResult.setTestNominalValue(fieldTestResult_testNominalValue);
                }
                {
                    elTestResult.setValue(fieldTestResult_value);
                }
                {
                    elTestResult.setTestDate(fieldTestResult_testDate);
                }
                {
                    elTestResult.setSampleId(fieldTestResult_sampleId);
                }
                {
                    elTestResult.setData(fieldTestResult_data);
                }
                if (currentState() == ParseState.TopLevel) {
                    if (importHandler != null)
                        importHandler.importObject(elTestResult);
                    else
                        topLevelObjects.add(elTestResult);
                }
            } else if ("test".equals(qName)) {
            } else if ("drugGeneric".equals(qName)) {
                fieldTestResult_drugGeneric = resolveDrugGeneric(value == null ? null : value.toString());
            } else if ("testNominalValue".equals(qName)) {
            } else if ("value".equals(qName)) {
                fieldTestResult_value = parseString(value == null ? null : value.toString());
            } else if ("testDate".equals(qName)) {
                fieldTestResult_testDate = parseDate(value == null ? null : value.toString());
            } else if ("sampleId".equals(qName)) {
                fieldTestResult_sampleId = parseString(value == null ? null : value.toString());
            } else if ("data".equals(qName)) {
                fieldTestResult_data = parsebyteArray(value == null ? null : value.toString());
            } else {
                //throw new SAXException(new ImportException("Unrecognized element: " + qName));
                System.err.println("Unrecognized element: " + qName);
            }
        } else if ("Test".equals(qName)) {
        } else if (currentState() == ParseState.stateTest) {
            if ("tests-el".equals(qName)|| "test".equals(qName)) {
                popState();
                Test elTest = null;
                boolean referenceResolved = false;
                if (currentState() == ParseState.TopLevel) {
                    if (topLevelClass == Test.class) {
                        elTest = new Test();
                    } else {
                        throw new SAXException(new ImportException("Unexpected top level object: " + qName));
                    }
                } else if (currentState() == ParseState.stateTestResult) {
                    if (referenceTest != null) { 
                        elTest = refTestMap.get(referenceTest);
                        referenceResolved = elTest != null;
                    }
                    if (!referenceResolved) {
                        elTest = new Test();
                        if (referenceTest!= null)
                            refTestMap.put(referenceTest, elTest);
                    }
                    fieldTestResult_test = elTest;
                } else {
                    throw new SAXException(new ImportException("Nested object problem: " + qName));
                }
                if (referenceResolved && fieldTest_analysis != null)
                    throw new SAXException(new ImportException("Cannot modify resolved reference"));
                if (!referenceResolved) {
                    elTest.setAnalysis(fieldTest_analysis);
                }
                if (referenceResolved && fieldTest_testType != null)
                    throw new SAXException(new ImportException("Cannot modify resolved reference"));
                if (!referenceResolved) {
                    elTest.setTestType(fieldTest_testType);
                }
                if (referenceResolved && fieldTest_description != nullValueString())
                    throw new SAXException(new ImportException("Cannot modify resolved reference"));
                if (!referenceResolved) {
                    elTest.setDescription(fieldTest_description);
                }
                if (currentState() == ParseState.TopLevel) {
                    if (importHandler != null)
                        importHandler.importObject(elTest);
                    else
                        topLevelObjects.add(elTest);
                }
            } else if ("analysis".equals(qName)) {
            } else if ("testType".equals(qName)) {
            } else if ("description".equals(qName)) {
                fieldTest_description = parseString(value == null ? null : value.toString());
            } else if ("reference".equals(qName)) {
                referenceTest = (value == null ? null : value.toString());
            } else {
                //throw new SAXException(new ImportException("Unrecognized element: " + qName));
                System.err.println("Unrecognized element: " + qName);
            }
        } else if ("Analysis".equals(qName)) {
        } else if (currentState() == ParseState.stateAnalysis) {
            if ("analysiss-el".equals(qName)|| "analysis".equals(qName)) {
                popState();
                Analysis elAnalysis = null;
                boolean referenceResolved = false;
                if (currentState() == ParseState.TopLevel) {
                    if (topLevelClass == Analysis.class) {
                        elAnalysis = new Analysis();
                    } else {
                        throw new SAXException(new ImportException("Unexpected top level object: " + qName));
                    }
                } else if (currentState() == ParseState.stateTest) {
                    if (referenceAnalysis != null) { 
                        elAnalysis = refAnalysisMap.get(referenceAnalysis);
                        referenceResolved = elAnalysis != null;
                    }
                    if (!referenceResolved) {
                        elAnalysis = new Analysis();
                        if (referenceAnalysis!= null)
                            refAnalysisMap.put(referenceAnalysis, elAnalysis);
                    }
                    fieldTest_analysis = elAnalysis;
                } else {
                    throw new SAXException(new ImportException("Nested object problem: " + qName));
                }
                if (referenceResolved && fieldAnalysis_analysisType != null)
                    throw new SAXException(new ImportException("Cannot modify resolved reference"));
                if (!referenceResolved) {
                    elAnalysis.setAnalysisType(fieldAnalysis_analysisType);
                }
                if (referenceResolved && fieldAnalysis_url != nullValueString())
                    throw new SAXException(new ImportException("Cannot modify resolved reference"));
                if (!referenceResolved) {
                    elAnalysis.setUrl(fieldAnalysis_url);
                }
                if (referenceResolved && fieldAnalysis_account != nullValueString())
                    throw new SAXException(new ImportException("Cannot modify resolved reference"));
                if (!referenceResolved) {
                    elAnalysis.setAccount(fieldAnalysis_account);
                }
                if (referenceResolved && fieldAnalysis_password != nullValueString())
                    throw new SAXException(new ImportException("Cannot modify resolved reference"));
                if (!referenceResolved) {
                    elAnalysis.setPassword(fieldAnalysis_password);
                }
                if (referenceResolved && fieldAnalysis_baseinputfile != nullValueString())
                    throw new SAXException(new ImportException("Cannot modify resolved reference"));
                if (!referenceResolved) {
                    elAnalysis.setBaseinputfile(fieldAnalysis_baseinputfile);
                }
                if (referenceResolved && fieldAnalysis_baseoutputfile != nullValueString())
                    throw new SAXException(new ImportException("Cannot modify resolved reference"));
                if (!referenceResolved) {
                    elAnalysis.setBaseoutputfile(fieldAnalysis_baseoutputfile);
                }
                if (referenceResolved && fieldAnalysis_serviceName != nullValueString())
                    throw new SAXException(new ImportException("Cannot modify resolved reference"));
                if (!referenceResolved) {
                    elAnalysis.setServiceName(fieldAnalysis_serviceName);
                }
                if (referenceResolved && fieldAnalysis_dataoutputfile != nullValueString())
                    throw new SAXException(new ImportException("Cannot modify resolved reference"));
                if (!referenceResolved) {
                    elAnalysis.setDataoutputfile(fieldAnalysis_dataoutputfile);
                }
                if (referenceResolved && !fieldAnalysis_analysisDatas.isEmpty())
                    throw new SAXException(new ImportException("Cannot modify resolved reference"));
                if (!referenceResolved) {
                    elAnalysis.setAnalysisDatas(fieldAnalysis_analysisDatas);
                    for (AnalysisData o : fieldAnalysis_analysisDatas)
                        o.setAnalysis(elAnalysis);
                }
                if (currentState() == ParseState.TopLevel) {
                    if (importHandler != null)
                        importHandler.importObject(elAnalysis);
                    else
                        topLevelObjects.add(elAnalysis);
                }
            } else if ("analysisType".equals(qName)) {
                fieldAnalysis_analysisType = resolveAnalysisType(value == null ? null : value.toString());
            } else if ("url".equals(qName)) {
                fieldAnalysis_url = parseString(value == null ? null : value.toString());
            } else if ("account".equals(qName)) {
                fieldAnalysis_account = parseString(value == null ? null : value.toString());
            } else if ("password".equals(qName)) {
                fieldAnalysis_password = parseString(value == null ? null : value.toString());
            } else if ("baseinputfile".equals(qName)) {
                fieldAnalysis_baseinputfile = parseString(value == null ? null : value.toString());
            } else if ("baseoutputfile".equals(qName)) {
                fieldAnalysis_baseoutputfile = parseString(value == null ? null : value.toString());
            } else if ("serviceName".equals(qName)) {
                fieldAnalysis_serviceName = parseString(value == null ? null : value.toString());
            } else if ("dataoutputfile".equals(qName)) {
                fieldAnalysis_dataoutputfile = parseString(value == null ? null : value.toString());
            } else if ("analysisDatas".equals(qName)) {
            } else if ("reference".equals(qName)) {
                referenceAnalysis = (value == null ? null : value.toString());
            } else {
                //throw new SAXException(new ImportException("Unrecognized element: " + qName));
                System.err.println("Unrecognized element: " + qName);
            }
        } else if ("AnalysisData".equals(qName)) {
        } else if (currentState() == ParseState.stateAnalysisData) {
            if ("analysisDatas-el".equals(qName)|| "analysisDatas-el".equals(qName)) {
                popState();
                AnalysisData elAnalysisData = null;
                boolean referenceResolved = false;
                if (currentState() == ParseState.TopLevel) {
                    if (topLevelClass == AnalysisData.class) {
                        elAnalysisData = new AnalysisData();
                    } else {
                        throw new SAXException(new ImportException("Unexpected top level object: " + qName));
                    }
                } else if (currentState() == ParseState.stateAnalysis) {
                    if (referenceAnalysisData != null) { 
                        elAnalysisData = refAnalysisDataMap.get(referenceAnalysisData);
                        referenceResolved = elAnalysisData != null;
                    }
                    if (!referenceResolved) {
                        elAnalysisData = new AnalysisData();
                        if (referenceAnalysisData!= null)
                            refAnalysisDataMap.put(referenceAnalysisData, elAnalysisData);
                    }
                    fieldAnalysis_analysisDatas.add(elAnalysisData);
                } else {
                    throw new SAXException(new ImportException("Nested object problem: " + qName));
                }
                if (referenceResolved && fieldAnalysisData_name != nullValueString())
                    throw new SAXException(new ImportException("Cannot modify resolved reference"));
                if (!referenceResolved) {
                    elAnalysisData.setName(fieldAnalysisData_name);
                }
                if (referenceResolved && fieldAnalysisData_data != nullValuebyteArray())
                    throw new SAXException(new ImportException("Cannot modify resolved reference"));
                if (!referenceResolved) {
                    elAnalysisData.setData(fieldAnalysisData_data);
                }
                if (referenceResolved && fieldAnalysisData_mimetype != nullValueString())
                    throw new SAXException(new ImportException("Cannot modify resolved reference"));
                if (!referenceResolved) {
                    elAnalysisData.setMimetype(fieldAnalysisData_mimetype);
                }
                if (currentState() == ParseState.TopLevel) {
                    if (importHandler != null)
                        importHandler.importObject(elAnalysisData);
                    else
                        topLevelObjects.add(elAnalysisData);
                }
            } else if ("name".equals(qName)) {
                fieldAnalysisData_name = parseString(value == null ? null : value.toString());
            } else if ("data".equals(qName)) {
                fieldAnalysisData_data = parsebyteArray(value == null ? null : value.toString());
            } else if ("mimetype".equals(qName)) {
                fieldAnalysisData_mimetype = parseString(value == null ? null : value.toString());
            } else if ("reference".equals(qName)) {
                referenceAnalysisData = (value == null ? null : value.toString());
            } else {
                //throw new SAXException(new ImportException("Unrecognized element: " + qName));
                System.err.println("Unrecognized element: " + qName);
            }
        } else if ("PatientEventValue".equals(qName)) {
        } else if (currentState() == ParseState.statePatientEventValue) {
            if ("patientEventValues-el".equals(qName)|| "patientEventValues-el".equals(qName)) {
                popState();
                PatientEventValue elPatientEventValue = null;
                if (currentState() == ParseState.TopLevel) {
                    if (topLevelClass == PatientEventValue.class) {
                        elPatientEventValue = new PatientEventValue();
                    } else {
                        throw new SAXException(new ImportException("Unexpected top level object: " + qName));
                    }
                } else if (currentState() == ParseState.statePatient) {
                    elPatientEventValue = patient.createPatientEventValue(fieldPatientEventValue_event);
                } else {
                    throw new SAXException(new ImportException("Nested object problem: " + qName));
                }
                {
                    elPatientEventValue.setEventNominalValue(fieldPatientEventValue_eventNominalValue);
                }
                {
                    elPatientEventValue.setEvent(fieldPatientEventValue_event);
                }
                {
                    elPatientEventValue.setValue(fieldPatientEventValue_value);
                }
                {
                    elPatientEventValue.setStartDate(fieldPatientEventValue_startDate);
                }
                {
                    elPatientEventValue.setEndDate(fieldPatientEventValue_endDate);
                }
                if (currentState() == ParseState.TopLevel) {
                    if (importHandler != null)
                        importHandler.importObject(elPatientEventValue);
                    else
                        topLevelObjects.add(elPatientEventValue);
                }
            } else if ("eventNominalValue".equals(qName)) {
            } else if ("event".equals(qName)) {
            } else if ("value".equals(qName)) {
                fieldPatientEventValue_value = parseString(value == null ? null : value.toString());
            } else if ("startDate".equals(qName)) {
                fieldPatientEventValue_startDate = parseDate(value == null ? null : value.toString());
            } else if ("endDate".equals(qName)) {
                fieldPatientEventValue_endDate = parseDate(value == null ? null : value.toString());
            } else {
                //throw new SAXException(new ImportException("Unrecognized element: " + qName));
                System.err.println("Unrecognized element: " + qName);
            }
        } else if ("TestType".equals(qName)) {
        } else if (currentState() == ParseState.stateTestType) {
            if ("testTypes-el".equals(qName)|| "testType".equals(qName)) {
                popState();
                TestType elTestType = null;
                boolean referenceResolved = false;
                if (currentState() == ParseState.TopLevel) {
                    if (topLevelClass == TestType.class) {
                        elTestType = new TestType();
                    } else {
                        throw new SAXException(new ImportException("Unexpected top level object: " + qName));
                    }
                } else if (currentState() == ParseState.stateTest) {
                    if (referenceTestType != null) { 
                        elTestType = refTestTypeMap.get(referenceTestType);
                        referenceResolved = elTestType != null;
                    }
                    if (!referenceResolved) {
                        elTestType = new TestType();
                        if (referenceTestType!= null)
                            refTestTypeMap.put(referenceTestType, elTestType);
                    }
                    fieldTest_testType = elTestType;
                } else {
                    throw new SAXException(new ImportException("Nested object problem: " + qName));
                }
                if (referenceResolved && fieldTestType_valueType != null)
                    throw new SAXException(new ImportException("Cannot modify resolved reference"));
                if (!referenceResolved) {
                    elTestType.setValueType(fieldTestType_valueType);
                }
                if (referenceResolved && fieldTestType_genome != null)
                    throw new SAXException(new ImportException("Cannot modify resolved reference"));
                if (!referenceResolved) {
                    elTestType.setGenome(fieldTestType_genome);
                }
                if (referenceResolved && fieldTestType_testObject != null)
                    throw new SAXException(new ImportException("Cannot modify resolved reference"));
                if (!referenceResolved) {
                    elTestType.setTestObject(fieldTestType_testObject);
                }
                if (referenceResolved && fieldTestType_description != nullValueString())
                    throw new SAXException(new ImportException("Cannot modify resolved reference"));
                if (!referenceResolved) {
                    elTestType.setDescription(fieldTestType_description);
                }
                if (referenceResolved && !fieldTestType_testNominalValues.isEmpty())
                    throw new SAXException(new ImportException("Cannot modify resolved reference"));
                if (!referenceResolved) {
                    elTestType.setTestNominalValues(fieldTestType_testNominalValues);
                    for (TestNominalValue o : fieldTestType_testNominalValues)
                        o.setTestType(elTestType);
                }
                if (currentState() == ParseState.TopLevel) {
                    if (importHandler != null)
                        importHandler.importObject(elTestType);
                    else
                        topLevelObjects.add(elTestType);
                }
            } else if ("valueType".equals(qName)) {
            } else if ("genome".equals(qName)) {
                fieldTestType_genome = resolveGenome(value == null ? null : value.toString());
            } else if ("testObject".equals(qName)) {
            } else if ("description".equals(qName)) {
                fieldTestType_description = parseString(value == null ? null : value.toString());
            } else if ("testNominalValues".equals(qName)) {
            } else if ("reference".equals(qName)) {
                referenceTestType = (value == null ? null : value.toString());
            } else {
                //throw new SAXException(new ImportException("Unrecognized element: " + qName));
                System.err.println("Unrecognized element: " + qName);
            }
        } else if ("TestObject".equals(qName)) {
        } else if (currentState() == ParseState.stateTestObject) {
            if ("testObjects-el".equals(qName)|| "testObject".equals(qName)) {
                popState();
                TestObject elTestObject = null;
                boolean referenceResolved = false;
                if (currentState() == ParseState.TopLevel) {
                    if (topLevelClass == TestObject.class) {
                        elTestObject = new TestObject();
                    } else {
                        throw new SAXException(new ImportException("Unexpected top level object: " + qName));
                    }
                } else if (currentState() == ParseState.stateTestType) {
                    if (referenceTestObject != null) { 
                        elTestObject = refTestObjectMap.get(referenceTestObject);
                        referenceResolved = elTestObject != null;
                    }
                    if (!referenceResolved) {
                        elTestObject = new TestObject();
                        if (referenceTestObject!= null)
                            refTestObjectMap.put(referenceTestObject, elTestObject);
                    }
                    fieldTestType_testObject = elTestObject;
                } else {
                    throw new SAXException(new ImportException("Nested object problem: " + qName));
                }
                if (referenceResolved && fieldTestObject_description != nullValueString())
                    throw new SAXException(new ImportException("Cannot modify resolved reference"));
                if (!referenceResolved) {
                    elTestObject.setDescription(fieldTestObject_description);
                }
                if (referenceResolved && fieldTestObject_testObjectId != nullValueInteger())
                    throw new SAXException(new ImportException("Cannot modify resolved reference"));
                if (!referenceResolved) {
                    elTestObject.setTestObjectId(fieldTestObject_testObjectId);
                }
                if (currentState() == ParseState.TopLevel) {
                    if (importHandler != null)
                        importHandler.importObject(elTestObject);
                    else
                        topLevelObjects.add(elTestObject);
                }
            } else if ("description".equals(qName)) {
                fieldTestObject_description = parseString(value == null ? null : value.toString());
            } else if ("testObjectId".equals(qName)) {
                fieldTestObject_testObjectId = parseInteger(value == null ? null : value.toString());
            } else if ("reference".equals(qName)) {
                referenceTestObject = (value == null ? null : value.toString());
            } else {
                //throw new SAXException(new ImportException("Unrecognized element: " + qName));
                System.err.println("Unrecognized element: " + qName);
            }
        } else if ("TestNominalValue".equals(qName)) {
        } else if (currentState() == ParseState.stateTestNominalValue) {
            if ("testNominalValues-el".equals(qName)|| "testNominalValue".equals(qName)|| "testNominalValues-el".equals(qName)) {
                popState();
                TestNominalValue elTestNominalValue = null;
                boolean referenceResolved = false;
                if (currentState() == ParseState.TopLevel) {
                    if (topLevelClass == TestNominalValue.class) {
                        elTestNominalValue = new TestNominalValue();
                    } else {
                        throw new SAXException(new ImportException("Unexpected top level object: " + qName));
                    }
                } else if (currentState() == ParseState.stateTestResult) {
                    if (referenceTestNominalValue != null) { 
                        elTestNominalValue = refTestNominalValueMap.get(referenceTestNominalValue);
                        referenceResolved = elTestNominalValue != null;
                    }
                    if (!referenceResolved) {
                        elTestNominalValue = new TestNominalValue();
                        if (referenceTestNominalValue!= null)
                            refTestNominalValueMap.put(referenceTestNominalValue, elTestNominalValue);
                    }
                    fieldTestResult_testNominalValue = elTestNominalValue;
                } else if (currentState() == ParseState.stateTestType) {
                    if (referenceTestNominalValue != null) { 
                        elTestNominalValue = refTestNominalValueMap.get(referenceTestNominalValue);
                        referenceResolved = elTestNominalValue != null;
                    }
                    if (!referenceResolved) {
                        elTestNominalValue = new TestNominalValue();
                        if (referenceTestNominalValue!= null)
                            refTestNominalValueMap.put(referenceTestNominalValue, elTestNominalValue);
                    }
                    fieldTestType_testNominalValues.add(elTestNominalValue);
                } else {
                    throw new SAXException(new ImportException("Nested object problem: " + qName));
                }
                if (referenceResolved && fieldTestNominalValue_value != nullValueString())
                    throw new SAXException(new ImportException("Cannot modify resolved reference"));
                if (!referenceResolved) {
                    elTestNominalValue.setValue(fieldTestNominalValue_value);
                }
                if (currentState() == ParseState.TopLevel) {
                    if (importHandler != null)
                        importHandler.importObject(elTestNominalValue);
                    else
                        topLevelObjects.add(elTestNominalValue);
                }
            } else if ("value".equals(qName)) {
                fieldTestNominalValue_value = parseString(value == null ? null : value.toString());
            } else if ("reference".equals(qName)) {
                referenceTestNominalValue = (value == null ? null : value.toString());
            } else {
                //throw new SAXException(new ImportException("Unrecognized element: " + qName));
                System.err.println("Unrecognized element: " + qName);
            }
        } else if ("PatientAttributeValue".equals(qName)) {
        } else if (currentState() == ParseState.statePatientAttributeValue) {
            if ("patientAttributeValues-el".equals(qName)|| "patientAttributeValues-el".equals(qName)) {
                popState();
                PatientAttributeValue elPatientAttributeValue = null;
                if (currentState() == ParseState.TopLevel) {
                    if (topLevelClass == PatientAttributeValue.class) {
                        elPatientAttributeValue = new PatientAttributeValue();
                    } else {
                        throw new SAXException(new ImportException("Unexpected top level object: " + qName));
                    }
                } else if (currentState() == ParseState.statePatient) {
                    elPatientAttributeValue = patient.createPatientAttributeValue(fieldPatientAttributeValue_attribute);
                } else {
                    throw new SAXException(new ImportException("Nested object problem: " + qName));
                }
                {
                    elPatientAttributeValue.setAttribute(fieldPatientAttributeValue_attribute);
                }
                {
                    elPatientAttributeValue.setAttributeNominalValue(fieldPatientAttributeValue_attributeNominalValue);
                }
                {
                    elPatientAttributeValue.setValue(fieldPatientAttributeValue_value);
                }
                if (currentState() == ParseState.TopLevel) {
                    if (importHandler != null)
                        importHandler.importObject(elPatientAttributeValue);
                    else
                        topLevelObjects.add(elPatientAttributeValue);
                }
            } else if ("attribute".equals(qName)) {
            } else if ("attributeNominalValue".equals(qName)) {
            } else if ("value".equals(qName)) {
                fieldPatientAttributeValue_value = parseString(value == null ? null : value.toString());
            } else {
                //throw new SAXException(new ImportException("Unrecognized element: " + qName));
                System.err.println("Unrecognized element: " + qName);
            }
        } else if ("Attribute".equals(qName)) {
        } else if (currentState() == ParseState.stateAttribute) {
            if ("attributes-el".equals(qName)|| "attribute".equals(qName)) {
                popState();
                Attribute elAttribute = null;
                boolean referenceResolved = false;
                if (currentState() == ParseState.TopLevel) {
                    if (topLevelClass == Attribute.class) {
                        elAttribute = new Attribute();
                    } else {
                        throw new SAXException(new ImportException("Unexpected top level object: " + qName));
                    }
                } else if (currentState() == ParseState.statePatientAttributeValue) {
                    if (referenceAttribute != null) { 
                        elAttribute = refAttributeMap.get(referenceAttribute);
                        referenceResolved = elAttribute != null;
                    }
                    if (!referenceResolved) {
                        elAttribute = new Attribute();
                        if (referenceAttribute!= null)
                            refAttributeMap.put(referenceAttribute, elAttribute);
                    }
                    fieldPatientAttributeValue_attribute = elAttribute;
                } else {
                    throw new SAXException(new ImportException("Nested object problem: " + qName));
                }
                if (referenceResolved && fieldAttribute_valueType != null)
                    throw new SAXException(new ImportException("Cannot modify resolved reference"));
                if (!referenceResolved) {
                    elAttribute.setValueType(fieldAttribute_valueType);
                }
                if (referenceResolved && fieldAttribute_attributeGroup != null)
                    throw new SAXException(new ImportException("Cannot modify resolved reference"));
                if (!referenceResolved) {
                    elAttribute.setAttributeGroup(fieldAttribute_attributeGroup);
                }
                if (referenceResolved && fieldAttribute_name != nullValueString())
                    throw new SAXException(new ImportException("Cannot modify resolved reference"));
                if (!referenceResolved) {
                    elAttribute.setName(fieldAttribute_name);
                }
                if (referenceResolved && !fieldAttribute_attributeNominalValues.isEmpty())
                    throw new SAXException(new ImportException("Cannot modify resolved reference"));
                if (!referenceResolved) {
                    elAttribute.setAttributeNominalValues(fieldAttribute_attributeNominalValues);
                    for (AttributeNominalValue o : fieldAttribute_attributeNominalValues)
                        o.setAttribute(elAttribute);
                }
                if (currentState() == ParseState.TopLevel) {
                    if (importHandler != null)
                        importHandler.importObject(elAttribute);
                    else
                        topLevelObjects.add(elAttribute);
                }
            } else if ("valueType".equals(qName)) {
            } else if ("attributeGroup".equals(qName)) {
            } else if ("name".equals(qName)) {
                fieldAttribute_name = parseString(value == null ? null : value.toString());
            } else if ("attributeNominalValues".equals(qName)) {
            } else if ("reference".equals(qName)) {
                referenceAttribute = (value == null ? null : value.toString());
            } else {
                //throw new SAXException(new ImportException("Unrecognized element: " + qName));
                System.err.println("Unrecognized element: " + qName);
            }
        } else if ("AttributeGroup".equals(qName)) {
        } else if (currentState() == ParseState.stateAttributeGroup) {
            if ("attributeGroups-el".equals(qName)|| "attributeGroup".equals(qName)) {
                popState();
                AttributeGroup elAttributeGroup = null;
                boolean referenceResolved = false;
                if (currentState() == ParseState.TopLevel) {
                    if (topLevelClass == AttributeGroup.class) {
                        elAttributeGroup = new AttributeGroup();
                    } else {
                        throw new SAXException(new ImportException("Unexpected top level object: " + qName));
                    }
                } else if (currentState() == ParseState.stateAttribute) {
                    if (referenceAttributeGroup != null) { 
                        elAttributeGroup = refAttributeGroupMap.get(referenceAttributeGroup);
                        referenceResolved = elAttributeGroup != null;
                    }
                    if (!referenceResolved) {
                        elAttributeGroup = new AttributeGroup();
                        if (referenceAttributeGroup!= null)
                            refAttributeGroupMap.put(referenceAttributeGroup, elAttributeGroup);
                    }
                    fieldAttribute_attributeGroup = elAttributeGroup;
                } else {
                    throw new SAXException(new ImportException("Nested object problem: " + qName));
                }
                if (referenceResolved && fieldAttributeGroup_groupName != nullValueString())
                    throw new SAXException(new ImportException("Cannot modify resolved reference"));
                if (!referenceResolved) {
                    elAttributeGroup.setGroupName(fieldAttributeGroup_groupName);
                }
                if (currentState() == ParseState.TopLevel) {
                    if (importHandler != null)
                        importHandler.importObject(elAttributeGroup);
                    else
                        topLevelObjects.add(elAttributeGroup);
                }
            } else if ("groupName".equals(qName)) {
                fieldAttributeGroup_groupName = parseString(value == null ? null : value.toString());
            } else if ("reference".equals(qName)) {
                referenceAttributeGroup = (value == null ? null : value.toString());
            } else {
                //throw new SAXException(new ImportException("Unrecognized element: " + qName));
                System.err.println("Unrecognized element: " + qName);
            }
        } else if ("AttributeNominalValue".equals(qName)) {
        } else if (currentState() == ParseState.stateAttributeNominalValue) {
            if ("attributeNominalValues-el".equals(qName)|| "attributeNominalValue".equals(qName)|| "attributeNominalValues-el".equals(qName)) {
                popState();
                AttributeNominalValue elAttributeNominalValue = null;
                boolean referenceResolved = false;
                if (currentState() == ParseState.TopLevel) {
                    if (topLevelClass == AttributeNominalValue.class) {
                        elAttributeNominalValue = new AttributeNominalValue();
                    } else {
                        throw new SAXException(new ImportException("Unexpected top level object: " + qName));
                    }
                } else if (currentState() == ParseState.statePatientAttributeValue) {
                    if (referenceAttributeNominalValue != null) { 
                        elAttributeNominalValue = refAttributeNominalValueMap.get(referenceAttributeNominalValue);
                        referenceResolved = elAttributeNominalValue != null;
                    }
                    if (!referenceResolved) {
                        elAttributeNominalValue = new AttributeNominalValue();
                        if (referenceAttributeNominalValue!= null)
                            refAttributeNominalValueMap.put(referenceAttributeNominalValue, elAttributeNominalValue);
                    }
                    fieldPatientAttributeValue_attributeNominalValue = elAttributeNominalValue;
                } else if (currentState() == ParseState.stateAttribute) {
                    if (referenceAttributeNominalValue != null) { 
                        elAttributeNominalValue = refAttributeNominalValueMap.get(referenceAttributeNominalValue);
                        referenceResolved = elAttributeNominalValue != null;
                    }
                    if (!referenceResolved) {
                        elAttributeNominalValue = new AttributeNominalValue();
                        if (referenceAttributeNominalValue!= null)
                            refAttributeNominalValueMap.put(referenceAttributeNominalValue, elAttributeNominalValue);
                    }
                    fieldAttribute_attributeNominalValues.add(elAttributeNominalValue);
                } else {
                    throw new SAXException(new ImportException("Nested object problem: " + qName));
                }
                if (referenceResolved && fieldAttributeNominalValue_value != nullValueString())
                    throw new SAXException(new ImportException("Cannot modify resolved reference"));
                if (!referenceResolved) {
                    elAttributeNominalValue.setValue(fieldAttributeNominalValue_value);
                }
                if (currentState() == ParseState.TopLevel) {
                    if (importHandler != null)
                        importHandler.importObject(elAttributeNominalValue);
                    else
                        topLevelObjects.add(elAttributeNominalValue);
                }
            } else if ("value".equals(qName)) {
                fieldAttributeNominalValue_value = parseString(value == null ? null : value.toString());
            } else if ("reference".equals(qName)) {
                referenceAttributeNominalValue = (value == null ? null : value.toString());
            } else {
                //throw new SAXException(new ImportException("Unrecognized element: " + qName));
                System.err.println("Unrecognized element: " + qName);
            }
        } else if ("ViralIsolate".equals(qName)) {
        } else if (currentState() == ParseState.stateViralIsolate) {
            if ("viralIsolates-el".equals(qName)|| "viralIsolates-el".equals(qName)) {
                popState();
                ViralIsolate elViralIsolate = null;
                if (currentState() == ParseState.TopLevel) {
                    if (topLevelClass == ViralIsolate.class) {
                        elViralIsolate = new ViralIsolate();
                    } else {
                        throw new SAXException(new ImportException("Unexpected top level object: " + qName));
                    }
                } else if (currentState() == ParseState.statePatient) {
                    elViralIsolate = patient.createViralIsolate();
                } else {
                    throw new SAXException(new ImportException("Nested object problem: " + qName));
                }
                {
                    elViralIsolate.setSampleId(fieldViralIsolate_sampleId);
                }
                {
                    elViralIsolate.setSampleDate(fieldViralIsolate_sampleDate);
                }
                {
                    elViralIsolate.setGenome(fieldViralIsolate_genome);
                }
                {
                    elViralIsolate.setNtSequences(fieldViralIsolate_ntSequences);
                    for (NtSequence o : fieldViralIsolate_ntSequences)
                        o.setViralIsolate(elViralIsolate);
                }
                {
                    elViralIsolate.setTestResults(fieldViralIsolate_testResults);
                    for (TestResult o : fieldViralIsolate_testResults)
                        o.setViralIsolate(elViralIsolate);
                }
                if (currentState() == ParseState.TopLevel) {
                    if (importHandler != null)
                        importHandler.importObject(elViralIsolate);
                    else
                        topLevelObjects.add(elViralIsolate);
                }
            } else if ("sampleId".equals(qName)) {
                fieldViralIsolate_sampleId = parseString(value == null ? null : value.toString());
            } else if ("sampleDate".equals(qName)) {
                fieldViralIsolate_sampleDate = parseDate(value == null ? null : value.toString());
            } else if ("genome".equals(qName)) {
                fieldViralIsolate_genome = resolveGenome(value == null ? null : value.toString());
            } else if ("ntSequences".equals(qName)) {
            } else if ("testResults".equals(qName)) {
            } else {
                //throw new SAXException(new ImportException("Unrecognized element: " + qName));
                System.err.println("Unrecognized element: " + qName);
            }
        } else if ("NtSequence".equals(qName)) {
        } else if (currentState() == ParseState.stateNtSequence) {
            if ("ntSequences-el".equals(qName)|| "ntSequences-el".equals(qName)) {
                popState();
                NtSequence elNtSequence = null;
                if (currentState() == ParseState.TopLevel) {
                    if (topLevelClass == NtSequence.class) {
                        elNtSequence = new NtSequence();
                    } else {
                        throw new SAXException(new ImportException("Unexpected top level object: " + qName));
                    }
                } else if (currentState() == ParseState.stateViralIsolate) {
                    elNtSequence = new NtSequence();
                    fieldViralIsolate_ntSequences.add(elNtSequence);
                } else {
                    throw new SAXException(new ImportException("Nested object problem: " + qName));
                }
                {
                    elNtSequence.setLabel(fieldNtSequence_label);
                }
                {
                    elNtSequence.setSequenceDate(fieldNtSequence_sequenceDate);
                }
                {
                    elNtSequence.setNucleotides(fieldNtSequence_nucleotides);
                }
                {
                    elNtSequence.setAligned(fieldNtSequence_aligned);
                }
                {
                    elNtSequence.setAaSequences(fieldNtSequence_aaSequences);
                    for (AaSequence o : fieldNtSequence_aaSequences)
                        o.setNtSequence(elNtSequence);
                }
                {
                    elNtSequence.setTestResults(fieldNtSequence_testResults);
                    for (TestResult o : fieldNtSequence_testResults)
                        o.setNtSequence(elNtSequence);
                }
                if (currentState() == ParseState.TopLevel) {
                    if (importHandler != null)
                        importHandler.importObject(elNtSequence);
                    else
                        topLevelObjects.add(elNtSequence);
                }
            } else if ("label".equals(qName)) {
                fieldNtSequence_label = parseString(value == null ? null : value.toString());
            } else if ("sequenceDate".equals(qName)) {
                fieldNtSequence_sequenceDate = parseDate(value == null ? null : value.toString());
            } else if ("nucleotides".equals(qName)) {
                fieldNtSequence_nucleotides = parseString(value == null ? null : value.toString());
            } else if ("aligned".equals(qName)) {
                fieldNtSequence_aligned = parseboolean(value == null ? null : value.toString());
            } else if ("aaSequences".equals(qName)) {
            } else if ("testResults".equals(qName)) {
            } else {
                //throw new SAXException(new ImportException("Unrecognized element: " + qName));
                System.err.println("Unrecognized element: " + qName);
            }
        } else if ("AaSequence".equals(qName)) {
        } else if (currentState() == ParseState.stateAaSequence) {
            if ("aaSequences-el".equals(qName)|| "aaSequences-el".equals(qName)) {
                popState();
                AaSequence elAaSequence = null;
                if (currentState() == ParseState.TopLevel) {
                    if (topLevelClass == AaSequence.class) {
                        elAaSequence = new AaSequence();
                    } else {
                        throw new SAXException(new ImportException("Unexpected top level object: " + qName));
                    }
                } else if (currentState() == ParseState.stateNtSequence) {
                    elAaSequence = new AaSequence();
                    fieldNtSequence_aaSequences.add(elAaSequence);
                } else {
                    throw new SAXException(new ImportException("Nested object problem: " + qName));
                }
                {
                    elAaSequence.setProtein(fieldAaSequence_protein);
                }
                {
                    elAaSequence.setFirstAaPos(fieldAaSequence_firstAaPos);
                }
                {
                    elAaSequence.setLastAaPos(fieldAaSequence_lastAaPos);
                }
                {
                    elAaSequence.setAaMutations(fieldAaSequence_aaMutations);
                    for (AaMutation o : fieldAaSequence_aaMutations)
                        o.getId().setAaSequence(elAaSequence);
                }
                {
                    elAaSequence.setAaInsertions(fieldAaSequence_aaInsertions);
                    for (AaInsertion o : fieldAaSequence_aaInsertions)
                        o.getId().setAaSequence(elAaSequence);
                }
                if (currentState() == ParseState.TopLevel) {
                    if (importHandler != null)
                        importHandler.importObject(elAaSequence);
                    else
                        topLevelObjects.add(elAaSequence);
                }
            } else if ("protein".equals(qName)) {
            } else if ("firstAaPos".equals(qName)) {
                fieldAaSequence_firstAaPos = parseshort(value == null ? null : value.toString());
            } else if ("lastAaPos".equals(qName)) {
                fieldAaSequence_lastAaPos = parseshort(value == null ? null : value.toString());
            } else if ("aaMutations".equals(qName)) {
            } else if ("aaInsertions".equals(qName)) {
            } else {
                //throw new SAXException(new ImportException("Unrecognized element: " + qName));
                System.err.println("Unrecognized element: " + qName);
            }
        } else if ("Event".equals(qName)) {
        } else if (currentState() == ParseState.stateEvent) {
            if ("events-el".equals(qName)|| "event".equals(qName)) {
                popState();
                Event elEvent = null;
                boolean referenceResolved = false;
                if (currentState() == ParseState.TopLevel) {
                    if (topLevelClass == Event.class) {
                        elEvent = new Event();
                    } else {
                        throw new SAXException(new ImportException("Unexpected top level object: " + qName));
                    }
                } else if (currentState() == ParseState.statePatientEventValue) {
                    if (referenceEvent != null) { 
                        elEvent = refEventMap.get(referenceEvent);
                        referenceResolved = elEvent != null;
                    }
                    if (!referenceResolved) {
                        elEvent = new Event();
                        if (referenceEvent!= null)
                            refEventMap.put(referenceEvent, elEvent);
                    }
                    fieldPatientEventValue_event = elEvent;
                } else {
                    throw new SAXException(new ImportException("Nested object problem: " + qName));
                }
                if (referenceResolved && fieldEvent_valueType != null)
                    throw new SAXException(new ImportException("Cannot modify resolved reference"));
                if (!referenceResolved) {
                    elEvent.setValueType(fieldEvent_valueType);
                }
                if (referenceResolved && fieldEvent_name != nullValueString())
                    throw new SAXException(new ImportException("Cannot modify resolved reference"));
                if (!referenceResolved) {
                    elEvent.setName(fieldEvent_name);
                }
                if (referenceResolved && !fieldEvent_eventNominalValues.isEmpty())
                    throw new SAXException(new ImportException("Cannot modify resolved reference"));
                if (!referenceResolved) {
                    elEvent.setEventNominalValues(fieldEvent_eventNominalValues);
                    for (EventNominalValue o : fieldEvent_eventNominalValues)
                        o.setEvent(elEvent);
                }
                if (currentState() == ParseState.TopLevel) {
                    if (importHandler != null)
                        importHandler.importObject(elEvent);
                    else
                        topLevelObjects.add(elEvent);
                }
            } else if ("valueType".equals(qName)) {
            } else if ("name".equals(qName)) {
                fieldEvent_name = parseString(value == null ? null : value.toString());
            } else if ("eventNominalValues".equals(qName)) {
            } else if ("reference".equals(qName)) {
                referenceEvent = (value == null ? null : value.toString());
            } else {
                //throw new SAXException(new ImportException("Unrecognized element: " + qName));
                System.err.println("Unrecognized element: " + qName);
            }
        } else if ("Protein".equals(qName)) {
        } else if (currentState() == ParseState.stateProtein) {
            if ("proteins-el".equals(qName)|| "protein".equals(qName)) {
                popState();
                Protein elProtein = null;
                boolean referenceResolved = false;
                if (currentState() == ParseState.TopLevel) {
                    if (topLevelClass == Protein.class) {
                        elProtein = new Protein();
                    } else {
                        throw new SAXException(new ImportException("Unexpected top level object: " + qName));
                    }
                } else if (currentState() == ParseState.stateAaSequence) {
                    if (referenceProtein != null) { 
                        elProtein = refProteinMap.get(referenceProtein);
                        referenceResolved = elProtein != null;
                    }
                    if (!referenceResolved) {
                        elProtein = new Protein();
                        if (referenceProtein!= null)
                            refProteinMap.put(referenceProtein, elProtein);
                    }
                    fieldAaSequence_protein = elProtein;
                } else {
                    throw new SAXException(new ImportException("Nested object problem: " + qName));
                }
                if (referenceResolved && fieldProtein_openReadingFrame != null)
                    throw new SAXException(new ImportException("Cannot modify resolved reference"));
                if (!referenceResolved) {
                    elProtein.setOpenReadingFrame(fieldProtein_openReadingFrame);
                }
                if (referenceResolved && fieldProtein_abbreviation != nullValueString())
                    throw new SAXException(new ImportException("Cannot modify resolved reference"));
                if (!referenceResolved) {
                    elProtein.setAbbreviation(fieldProtein_abbreviation);
                }
                if (currentState() == ParseState.TopLevel) {
                    if (importHandler != null)
                        importHandler.importObject(elProtein);
                    else
                        topLevelObjects.add(elProtein);
                }
            } else if ("openReadingFrame".equals(qName)) {
            } else if ("abbreviation".equals(qName)) {
                fieldProtein_abbreviation = parseString(value == null ? null : value.toString());
            } else if ("reference".equals(qName)) {
                referenceProtein = (value == null ? null : value.toString());
            } else {
                //throw new SAXException(new ImportException("Unrecognized element: " + qName));
                System.err.println("Unrecognized element: " + qName);
            }
        } else if ("OpenReadingFrame".equals(qName)) {
        } else if (currentState() == ParseState.stateOpenReadingFrame) {
            if ("openReadingFrames-el".equals(qName)|| "openReadingFrame".equals(qName)) {
                popState();
                OpenReadingFrame elOpenReadingFrame = null;
                boolean referenceResolved = false;
                if (currentState() == ParseState.TopLevel) {
                    if (topLevelClass == OpenReadingFrame.class) {
                        elOpenReadingFrame = new OpenReadingFrame();
                    } else {
                        throw new SAXException(new ImportException("Unexpected top level object: " + qName));
                    }
                } else if (currentState() == ParseState.stateProtein) {
                    if (referenceOpenReadingFrame != null) { 
                        elOpenReadingFrame = refOpenReadingFrameMap.get(referenceOpenReadingFrame);
                        referenceResolved = elOpenReadingFrame != null;
                    }
                    if (!referenceResolved) {
                        elOpenReadingFrame = new OpenReadingFrame();
                        if (referenceOpenReadingFrame!= null)
                            refOpenReadingFrameMap.put(referenceOpenReadingFrame, elOpenReadingFrame);
                    }
                    fieldProtein_openReadingFrame = elOpenReadingFrame;
                } else {
                    throw new SAXException(new ImportException("Nested object problem: " + qName));
                }
                if (referenceResolved && fieldOpenReadingFrame_genome != null)
                    throw new SAXException(new ImportException("Cannot modify resolved reference"));
                if (!referenceResolved) {
                    elOpenReadingFrame.setGenome(fieldOpenReadingFrame_genome);
                }
                if (referenceResolved && fieldOpenReadingFrame_name != nullValueString())
                    throw new SAXException(new ImportException("Cannot modify resolved reference"));
                if (!referenceResolved) {
                    elOpenReadingFrame.setName(fieldOpenReadingFrame_name);
                }
                if (currentState() == ParseState.TopLevel) {
                    if (importHandler != null)
                        importHandler.importObject(elOpenReadingFrame);
                    else
                        topLevelObjects.add(elOpenReadingFrame);
                }
            } else if ("genome".equals(qName)) {
                fieldOpenReadingFrame_genome = resolveGenome(value == null ? null : value.toString());
            } else if ("name".equals(qName)) {
                fieldOpenReadingFrame_name = parseString(value == null ? null : value.toString());
            } else if ("reference".equals(qName)) {
                referenceOpenReadingFrame = (value == null ? null : value.toString());
            } else {
                //throw new SAXException(new ImportException("Unrecognized element: " + qName));
                System.err.println("Unrecognized element: " + qName);
            }
        } else if ("AaMutation".equals(qName)) {
        } else if (currentState() == ParseState.stateAaMutation) {
            if ("aaMutations-el".equals(qName)|| "aaMutations-el".equals(qName)) {
                popState();
                AaMutation elAaMutation = null;
                if (currentState() == ParseState.TopLevel) {
                    if (topLevelClass == AaMutation.class) {
                        elAaMutation = new AaMutation();
                        elAaMutation.setId(new AaMutationId());
                    } else {
                        throw new SAXException(new ImportException("Unexpected top level object: " + qName));
                    }
                } else if (currentState() == ParseState.stateAaSequence) {
                    elAaMutation = new AaMutation();
                    elAaMutation.setId(new AaMutationId());
                    fieldAaSequence_aaMutations.add(elAaMutation);
                } else {
                    throw new SAXException(new ImportException("Nested object problem: " + qName));
                }
                {
                    elAaMutation.getId().setMutationPosition(fieldAaMutation_mutationPosition);
                }
                {
                    elAaMutation.setAaReference(fieldAaMutation_aaReference);
                }
                {
                    elAaMutation.setAaMutation(fieldAaMutation_aaMutation);
                }
                {
                    elAaMutation.setNtReferenceCodon(fieldAaMutation_ntReferenceCodon);
                }
                {
                    elAaMutation.setNtMutationCodon(fieldAaMutation_ntMutationCodon);
                }
                if (currentState() == ParseState.TopLevel) {
                    if (importHandler != null)
                        importHandler.importObject(elAaMutation);
                    else
                        topLevelObjects.add(elAaMutation);
                }
            } else if ("mutationPosition".equals(qName)) {
                fieldAaMutation_mutationPosition = parseshort(value == null ? null : value.toString());
            } else if ("aaReference".equals(qName)) {
                fieldAaMutation_aaReference = parseString(value == null ? null : value.toString());
            } else if ("aaMutation".equals(qName)) {
                fieldAaMutation_aaMutation = parseString(value == null ? null : value.toString());
            } else if ("ntReferenceCodon".equals(qName)) {
                fieldAaMutation_ntReferenceCodon = parseString(value == null ? null : value.toString());
            } else if ("ntMutationCodon".equals(qName)) {
                fieldAaMutation_ntMutationCodon = parseString(value == null ? null : value.toString());
            } else {
                //throw new SAXException(new ImportException("Unrecognized element: " + qName));
                System.err.println("Unrecognized element: " + qName);
            }
        } else if ("AaInsertion".equals(qName)) {
        } else if (currentState() == ParseState.stateAaInsertion) {
            if ("aaInsertions-el".equals(qName)|| "aaInsertions-el".equals(qName)) {
                popState();
                AaInsertion elAaInsertion = null;
                if (currentState() == ParseState.TopLevel) {
                    if (topLevelClass == AaInsertion.class) {
                        elAaInsertion = new AaInsertion();
                        elAaInsertion.setId(new AaInsertionId());
                    } else {
                        throw new SAXException(new ImportException("Unexpected top level object: " + qName));
                    }
                } else if (currentState() == ParseState.stateAaSequence) {
                    elAaInsertion = new AaInsertion();
                    elAaInsertion.setId(new AaInsertionId());
                    fieldAaSequence_aaInsertions.add(elAaInsertion);
                } else {
                    throw new SAXException(new ImportException("Nested object problem: " + qName));
                }
                {
                    elAaInsertion.getId().setInsertionPosition(fieldAaInsertion_insertionPosition);
                }
                {
                    elAaInsertion.getId().setInsertionOrder(fieldAaInsertion_insertionOrder);
                }
                {
                    elAaInsertion.setAaInsertion(fieldAaInsertion_aaInsertion);
                }
                {
                    elAaInsertion.setNtInsertionCodon(fieldAaInsertion_ntInsertionCodon);
                }
                if (currentState() == ParseState.TopLevel) {
                    if (importHandler != null)
                        importHandler.importObject(elAaInsertion);
                    else
                        topLevelObjects.add(elAaInsertion);
                }
            } else if ("insertionPosition".equals(qName)) {
                fieldAaInsertion_insertionPosition = parseshort(value == null ? null : value.toString());
            } else if ("insertionOrder".equals(qName)) {
                fieldAaInsertion_insertionOrder = parseshort(value == null ? null : value.toString());
            } else if ("aaInsertion".equals(qName)) {
                fieldAaInsertion_aaInsertion = parseString(value == null ? null : value.toString());
            } else if ("ntInsertionCodon".equals(qName)) {
                fieldAaInsertion_ntInsertionCodon = parseString(value == null ? null : value.toString());
            } else {
                //throw new SAXException(new ImportException("Unrecognized element: " + qName));
                System.err.println("Unrecognized element: " + qName);
            }
        } else if ("Therapy".equals(qName)) {
        } else if (currentState() == ParseState.stateTherapy) {
            if ("therapys-el".equals(qName)|| "therapies-el".equals(qName)) {
                popState();
                Therapy elTherapy = null;
                if (currentState() == ParseState.TopLevel) {
                    if (topLevelClass == Therapy.class) {
                        elTherapy = new Therapy();
                    } else {
                        throw new SAXException(new ImportException("Unexpected top level object: " + qName));
                    }
                } else if (currentState() == ParseState.statePatient) {
                    elTherapy = patient.createTherapy(fieldTherapy_startDate);
                } else {
                    throw new SAXException(new ImportException("Nested object problem: " + qName));
                }
                {
                    elTherapy.setTherapyMotivation(fieldTherapy_therapyMotivation);
                }
                {
                    elTherapy.setStartDate(fieldTherapy_startDate);
                }
                {
                    elTherapy.setStopDate(fieldTherapy_stopDate);
                }
                {
                    elTherapy.setComment(fieldTherapy_comment);
                }
                {
                    elTherapy.setTherapyCommercials(fieldTherapy_therapyCommercials);
                    for (TherapyCommercial o : fieldTherapy_therapyCommercials)
                        o.getId().setTherapy(elTherapy);
                }
                {
                    elTherapy.setTherapyGenerics(fieldTherapy_therapyGenerics);
                    for (TherapyGeneric o : fieldTherapy_therapyGenerics)
                        o.getId().setTherapy(elTherapy);
                }
                if (currentState() == ParseState.TopLevel) {
                    if (importHandler != null)
                        importHandler.importObject(elTherapy);
                    else
                        topLevelObjects.add(elTherapy);
                }
            } else if ("therapyMotivation".equals(qName)) {
                fieldTherapy_therapyMotivation = resolveTherapyMotivation(value == null ? null : value.toString());
            } else if ("startDate".equals(qName)) {
                fieldTherapy_startDate = parseDate(value == null ? null : value.toString());
            } else if ("stopDate".equals(qName)) {
                fieldTherapy_stopDate = parseDate(value == null ? null : value.toString());
            } else if ("comment".equals(qName)) {
                fieldTherapy_comment = parseString(value == null ? null : value.toString());
            } else if ("therapyCommercials".equals(qName)) {
            } else if ("therapyGenerics".equals(qName)) {
            } else {
                //throw new SAXException(new ImportException("Unrecognized element: " + qName));
                System.err.println("Unrecognized element: " + qName);
            }
        } else if ("TherapyCommercial".equals(qName)) {
        } else if (currentState() == ParseState.stateTherapyCommercial) {
            if ("therapyCommercials-el".equals(qName)|| "therapyCommercials-el".equals(qName)) {
                popState();
                TherapyCommercial elTherapyCommercial = null;
                if (currentState() == ParseState.TopLevel) {
                    if (topLevelClass == TherapyCommercial.class) {
                        elTherapyCommercial = new TherapyCommercial();
                        elTherapyCommercial.setId(new TherapyCommercialId());
                    } else {
                        throw new SAXException(new ImportException("Unexpected top level object: " + qName));
                    }
                } else if (currentState() == ParseState.stateTherapy) {
                    elTherapyCommercial = new TherapyCommercial();
                    elTherapyCommercial.setId(new TherapyCommercialId());
                    fieldTherapy_therapyCommercials.add(elTherapyCommercial);
                } else {
                    throw new SAXException(new ImportException("Nested object problem: " + qName));
                }
                {
                    elTherapyCommercial.getId().setDrugCommercial(fieldTherapyCommercial_drugCommercial);
                }
                {
                    elTherapyCommercial.setDayDosageUnits(fieldTherapyCommercial_dayDosageUnits);
                }
                {
                    elTherapyCommercial.setPlacebo(fieldTherapyCommercial_placebo);
                }
                {
                    elTherapyCommercial.setBlind(fieldTherapyCommercial_blind);
                }
                {
                    elTherapyCommercial.setFrequency(fieldTherapyCommercial_frequency);
                }
                if (currentState() == ParseState.TopLevel) {
                    if (importHandler != null)
                        importHandler.importObject(elTherapyCommercial);
                    else
                        topLevelObjects.add(elTherapyCommercial);
                }
            } else if ("drugCommercial".equals(qName)) {
                fieldTherapyCommercial_drugCommercial = resolveDrugCommercial(value == null ? null : value.toString());
            } else if ("dayDosageUnits".equals(qName)) {
                fieldTherapyCommercial_dayDosageUnits = parseDouble(value == null ? null : value.toString());
            } else if ("placebo".equals(qName)) {
                fieldTherapyCommercial_placebo = parseboolean(value == null ? null : value.toString());
            } else if ("blind".equals(qName)) {
                fieldTherapyCommercial_blind = parseboolean(value == null ? null : value.toString());
            } else if ("frequency".equals(qName)) {
                fieldTherapyCommercial_frequency = parseLong(value == null ? null : value.toString());
            } else {
                //throw new SAXException(new ImportException("Unrecognized element: " + qName));
                System.err.println("Unrecognized element: " + qName);
            }
        } else if ("TherapyGeneric".equals(qName)) {
        } else if (currentState() == ParseState.stateTherapyGeneric) {
            if ("therapyGenerics-el".equals(qName)|| "therapyGenerics-el".equals(qName)) {
                popState();
                TherapyGeneric elTherapyGeneric = null;
                if (currentState() == ParseState.TopLevel) {
                    if (topLevelClass == TherapyGeneric.class) {
                        elTherapyGeneric = new TherapyGeneric();
                        elTherapyGeneric.setId(new TherapyGenericId());
                    } else {
                        throw new SAXException(new ImportException("Unexpected top level object: " + qName));
                    }
                } else if (currentState() == ParseState.stateTherapy) {
                    elTherapyGeneric = new TherapyGeneric();
                    elTherapyGeneric.setId(new TherapyGenericId());
                    fieldTherapy_therapyGenerics.add(elTherapyGeneric);
                } else {
                    throw new SAXException(new ImportException("Nested object problem: " + qName));
                }
                {
                    elTherapyGeneric.getId().setDrugGeneric(fieldTherapyGeneric_drugGeneric);
                }
                {
                    elTherapyGeneric.setDayDosageMg(fieldTherapyGeneric_dayDosageMg);
                }
                {
                    elTherapyGeneric.setPlacebo(fieldTherapyGeneric_placebo);
                }
                {
                    elTherapyGeneric.setBlind(fieldTherapyGeneric_blind);
                }
                {
                    elTherapyGeneric.setFrequency(fieldTherapyGeneric_frequency);
                }
                if (currentState() == ParseState.TopLevel) {
                    if (importHandler != null)
                        importHandler.importObject(elTherapyGeneric);
                    else
                        topLevelObjects.add(elTherapyGeneric);
                }
            } else if ("drugGeneric".equals(qName)) {
                fieldTherapyGeneric_drugGeneric = resolveDrugGeneric(value == null ? null : value.toString());
            } else if ("dayDosageMg".equals(qName)) {
                fieldTherapyGeneric_dayDosageMg = parseDouble(value == null ? null : value.toString());
            } else if ("placebo".equals(qName)) {
                fieldTherapyGeneric_placebo = parseboolean(value == null ? null : value.toString());
            } else if ("blind".equals(qName)) {
                fieldTherapyGeneric_blind = parseboolean(value == null ? null : value.toString());
            } else if ("frequency".equals(qName)) {
                fieldTherapyGeneric_frequency = parseLong(value == null ? null : value.toString());
            } else {
                //throw new SAXException(new ImportException("Unrecognized element: " + qName));
                System.err.println("Unrecognized element: " + qName);
            }
        } else if ("ValueType".equals(qName)) {
        } else if (currentState() == ParseState.stateValueType) {
            if ("valueTypes-el".equals(qName)|| "valueType".equals(qName)|| "valueType".equals(qName)|| "valueType".equals(qName)) {
                popState();
                ValueType elValueType = null;
                boolean referenceResolved = false;
                if (currentState() == ParseState.TopLevel) {
                    if (topLevelClass == ValueType.class) {
                        elValueType = new ValueType();
                    } else {
                        throw new SAXException(new ImportException("Unexpected top level object: " + qName));
                    }
                } else if (currentState() == ParseState.stateTestType) {
                    if (referenceValueType != null) { 
                        elValueType = refValueTypeMap.get(referenceValueType);
                        referenceResolved = elValueType != null;
                    }
                    if (!referenceResolved) {
                        elValueType = new ValueType();
                        if (referenceValueType!= null)
                            refValueTypeMap.put(referenceValueType, elValueType);
                    }
                    fieldTestType_valueType = elValueType;
                } else if (currentState() == ParseState.stateAttribute) {
                    if (referenceValueType != null) { 
                        elValueType = refValueTypeMap.get(referenceValueType);
                        referenceResolved = elValueType != null;
                    }
                    if (!referenceResolved) {
                        elValueType = new ValueType();
                        if (referenceValueType!= null)
                            refValueTypeMap.put(referenceValueType, elValueType);
                    }
                    fieldAttribute_valueType = elValueType;
                } else if (currentState() == ParseState.stateEvent) {
                    if (referenceValueType != null) { 
                        elValueType = refValueTypeMap.get(referenceValueType);
                        referenceResolved = elValueType != null;
                    }
                    if (!referenceResolved) {
                        elValueType = new ValueType();
                        if (referenceValueType!= null)
                            refValueTypeMap.put(referenceValueType, elValueType);
                    }
                    fieldEvent_valueType = elValueType;
                } else {
                    throw new SAXException(new ImportException("Nested object problem: " + qName));
                }
                if (referenceResolved && fieldValueType_description != nullValueString())
                    throw new SAXException(new ImportException("Cannot modify resolved reference"));
                if (!referenceResolved) {
                    elValueType.setDescription(fieldValueType_description);
                }
                if (referenceResolved && fieldValueType_minimum != nullValueDouble())
                    throw new SAXException(new ImportException("Cannot modify resolved reference"));
                if (!referenceResolved) {
                    elValueType.setMinimum(fieldValueType_minimum);
                }
                if (referenceResolved && fieldValueType_maximum != nullValueDouble())
                    throw new SAXException(new ImportException("Cannot modify resolved reference"));
                if (!referenceResolved) {
                    elValueType.setMaximum(fieldValueType_maximum);
                }
                if (referenceResolved && fieldValueType_multiple != nullValueBoolean())
                    throw new SAXException(new ImportException("Cannot modify resolved reference"));
                if (!referenceResolved) {
                    elValueType.setMultiple(fieldValueType_multiple);
                }
                if (currentState() == ParseState.TopLevel) {
                    if (importHandler != null)
                        importHandler.importObject(elValueType);
                    else
                        topLevelObjects.add(elValueType);
                }
            } else if ("description".equals(qName)) {
                fieldValueType_description = parseString(value == null ? null : value.toString());
            } else if ("minimum".equals(qName)) {
                fieldValueType_minimum = parseDouble(value == null ? null : value.toString());
            } else if ("maximum".equals(qName)) {
                fieldValueType_maximum = parseDouble(value == null ? null : value.toString());
            } else if ("multiple".equals(qName)) {
                fieldValueType_multiple = parseBoolean(value == null ? null : value.toString());
            } else if ("reference".equals(qName)) {
                referenceValueType = (value == null ? null : value.toString());
            } else {
                //throw new SAXException(new ImportException("Unrecognized element: " + qName));
                System.err.println("Unrecognized element: " + qName);
            }
        } else if ("EventNominalValue".equals(qName)) {
        } else if (currentState() == ParseState.stateEventNominalValue) {
            if ("eventNominalValues-el".equals(qName)|| "eventNominalValue".equals(qName)|| "eventNominalValues-el".equals(qName)) {
                popState();
                EventNominalValue elEventNominalValue = null;
                boolean referenceResolved = false;
                if (currentState() == ParseState.TopLevel) {
                    if (topLevelClass == EventNominalValue.class) {
                        elEventNominalValue = new EventNominalValue();
                    } else {
                        throw new SAXException(new ImportException("Unexpected top level object: " + qName));
                    }
                } else if (currentState() == ParseState.statePatientEventValue) {
                    if (referenceEventNominalValue != null) { 
                        elEventNominalValue = refEventNominalValueMap.get(referenceEventNominalValue);
                        referenceResolved = elEventNominalValue != null;
                    }
                    if (!referenceResolved) {
                        elEventNominalValue = new EventNominalValue();
                        if (referenceEventNominalValue!= null)
                            refEventNominalValueMap.put(referenceEventNominalValue, elEventNominalValue);
                    }
                    fieldPatientEventValue_eventNominalValue = elEventNominalValue;
                } else if (currentState() == ParseState.stateEvent) {
                    if (referenceEventNominalValue != null) { 
                        elEventNominalValue = refEventNominalValueMap.get(referenceEventNominalValue);
                        referenceResolved = elEventNominalValue != null;
                    }
                    if (!referenceResolved) {
                        elEventNominalValue = new EventNominalValue();
                        if (referenceEventNominalValue!= null)
                            refEventNominalValueMap.put(referenceEventNominalValue, elEventNominalValue);
                    }
                    fieldEvent_eventNominalValues.add(elEventNominalValue);
                } else {
                    throw new SAXException(new ImportException("Nested object problem: " + qName));
                }
                if (referenceResolved && fieldEventNominalValue_value != nullValueString())
                    throw new SAXException(new ImportException("Cannot modify resolved reference"));
                if (!referenceResolved) {
                    elEventNominalValue.setValue(fieldEventNominalValue_value);
                }
                if (currentState() == ParseState.TopLevel) {
                    if (importHandler != null)
                        importHandler.importObject(elEventNominalValue);
                    else
                        topLevelObjects.add(elEventNominalValue);
                }
            } else if ("value".equals(qName)) {
                fieldEventNominalValue_value = parseString(value == null ? null : value.toString());
            } else if ("reference".equals(qName)) {
                referenceEventNominalValue = (value == null ? null : value.toString());
            } else {
                //throw new SAXException(new ImportException("Unrecognized element: " + qName));
                System.err.println("Unrecognized element: " + qName);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public List<Patient> readPatients(InputSource source, ImportHandler<Patient> handler) throws SAXException, IOException {
        topLevelClass = Patient.class;
        importHandler = handler;
        parse(source);
        return topLevelObjects;
    }

    @SuppressWarnings("unchecked")
    public List<Dataset> readDatasets(InputSource source, ImportHandler<Dataset> handler) throws SAXException, IOException {
        topLevelClass = Dataset.class;
        importHandler = handler;
        parse(source);
        return topLevelObjects;
    }

    @SuppressWarnings("unchecked")
    public List<TestResult> readTestResults(InputSource source, ImportHandler<TestResult> handler) throws SAXException, IOException {
        topLevelClass = TestResult.class;
        importHandler = handler;
        parse(source);
        return topLevelObjects;
    }

    @SuppressWarnings("unchecked")
    public List<Test> readTests(InputSource source, ImportHandler<Test> handler) throws SAXException, IOException {
        topLevelClass = Test.class;
        importHandler = handler;
        parse(source);
        return topLevelObjects;
    }

    @SuppressWarnings("unchecked")
    public List<Analysis> readAnalysiss(InputSource source, ImportHandler<Analysis> handler) throws SAXException, IOException {
        topLevelClass = Analysis.class;
        importHandler = handler;
        parse(source);
        return topLevelObjects;
    }

    @SuppressWarnings("unchecked")
    public List<AnalysisData> readAnalysisDatas(InputSource source, ImportHandler<AnalysisData> handler) throws SAXException, IOException {
        topLevelClass = AnalysisData.class;
        importHandler = handler;
        parse(source);
        return topLevelObjects;
    }

    @SuppressWarnings("unchecked")
    public List<PatientEventValue> readPatientEventValues(InputSource source, ImportHandler<PatientEventValue> handler) throws SAXException, IOException {
        topLevelClass = PatientEventValue.class;
        importHandler = handler;
        parse(source);
        return topLevelObjects;
    }

    @SuppressWarnings("unchecked")
    public List<TestType> readTestTypes(InputSource source, ImportHandler<TestType> handler) throws SAXException, IOException {
        topLevelClass = TestType.class;
        importHandler = handler;
        parse(source);
        return topLevelObjects;
    }

    @SuppressWarnings("unchecked")
    public List<TestObject> readTestObjects(InputSource source, ImportHandler<TestObject> handler) throws SAXException, IOException {
        topLevelClass = TestObject.class;
        importHandler = handler;
        parse(source);
        return topLevelObjects;
    }

    @SuppressWarnings("unchecked")
    public List<TestNominalValue> readTestNominalValues(InputSource source, ImportHandler<TestNominalValue> handler) throws SAXException, IOException {
        topLevelClass = TestNominalValue.class;
        importHandler = handler;
        parse(source);
        return topLevelObjects;
    }

    @SuppressWarnings("unchecked")
    public List<PatientAttributeValue> readPatientAttributeValues(InputSource source, ImportHandler<PatientAttributeValue> handler) throws SAXException, IOException {
        topLevelClass = PatientAttributeValue.class;
        importHandler = handler;
        parse(source);
        return topLevelObjects;
    }

    @SuppressWarnings("unchecked")
    public List<Attribute> readAttributes(InputSource source, ImportHandler<Attribute> handler) throws SAXException, IOException {
        topLevelClass = Attribute.class;
        importHandler = handler;
        parse(source);
        return topLevelObjects;
    }

    @SuppressWarnings("unchecked")
    public List<AttributeGroup> readAttributeGroups(InputSource source, ImportHandler<AttributeGroup> handler) throws SAXException, IOException {
        topLevelClass = AttributeGroup.class;
        importHandler = handler;
        parse(source);
        return topLevelObjects;
    }

    @SuppressWarnings("unchecked")
    public List<AttributeNominalValue> readAttributeNominalValues(InputSource source, ImportHandler<AttributeNominalValue> handler) throws SAXException, IOException {
        topLevelClass = AttributeNominalValue.class;
        importHandler = handler;
        parse(source);
        return topLevelObjects;
    }

    @SuppressWarnings("unchecked")
    public List<ViralIsolate> readViralIsolates(InputSource source, ImportHandler<ViralIsolate> handler) throws SAXException, IOException {
        topLevelClass = ViralIsolate.class;
        importHandler = handler;
        parse(source);
        return topLevelObjects;
    }

    @SuppressWarnings("unchecked")
    public List<NtSequence> readNtSequences(InputSource source, ImportHandler<NtSequence> handler) throws SAXException, IOException {
        topLevelClass = NtSequence.class;
        importHandler = handler;
        parse(source);
        return topLevelObjects;
    }

    @SuppressWarnings("unchecked")
    public List<AaSequence> readAaSequences(InputSource source, ImportHandler<AaSequence> handler) throws SAXException, IOException {
        topLevelClass = AaSequence.class;
        importHandler = handler;
        parse(source);
        return topLevelObjects;
    }

    @SuppressWarnings("unchecked")
    public List<Event> readEvents(InputSource source, ImportHandler<Event> handler) throws SAXException, IOException {
        topLevelClass = Event.class;
        importHandler = handler;
        parse(source);
        return topLevelObjects;
    }

    @SuppressWarnings("unchecked")
    public List<Protein> readProteins(InputSource source, ImportHandler<Protein> handler) throws SAXException, IOException {
        topLevelClass = Protein.class;
        importHandler = handler;
        parse(source);
        return topLevelObjects;
    }

    @SuppressWarnings("unchecked")
    public List<OpenReadingFrame> readOpenReadingFrames(InputSource source, ImportHandler<OpenReadingFrame> handler) throws SAXException, IOException {
        topLevelClass = OpenReadingFrame.class;
        importHandler = handler;
        parse(source);
        return topLevelObjects;
    }

    @SuppressWarnings("unchecked")
    public List<AaMutation> readAaMutations(InputSource source, ImportHandler<AaMutation> handler) throws SAXException, IOException {
        topLevelClass = AaMutation.class;
        importHandler = handler;
        parse(source);
        return topLevelObjects;
    }

    @SuppressWarnings("unchecked")
    public List<AaInsertion> readAaInsertions(InputSource source, ImportHandler<AaInsertion> handler) throws SAXException, IOException {
        topLevelClass = AaInsertion.class;
        importHandler = handler;
        parse(source);
        return topLevelObjects;
    }

    @SuppressWarnings("unchecked")
    public List<Therapy> readTherapys(InputSource source, ImportHandler<Therapy> handler) throws SAXException, IOException {
        topLevelClass = Therapy.class;
        importHandler = handler;
        parse(source);
        return topLevelObjects;
    }

    @SuppressWarnings("unchecked")
    public List<TherapyCommercial> readTherapyCommercials(InputSource source, ImportHandler<TherapyCommercial> handler) throws SAXException, IOException {
        topLevelClass = TherapyCommercial.class;
        importHandler = handler;
        parse(source);
        return topLevelObjects;
    }

    @SuppressWarnings("unchecked")
    public List<TherapyGeneric> readTherapyGenerics(InputSource source, ImportHandler<TherapyGeneric> handler) throws SAXException, IOException {
        topLevelClass = TherapyGeneric.class;
        importHandler = handler;
        parse(source);
        return topLevelObjects;
    }

    @SuppressWarnings("unchecked")
    public List<ValueType> readValueTypes(InputSource source, ImportHandler<ValueType> handler) throws SAXException, IOException {
        topLevelClass = ValueType.class;
        importHandler = handler;
        parse(source);
        return topLevelObjects;
    }

    @SuppressWarnings("unchecked")
    public List<EventNominalValue> readEventNominalValues(InputSource source, ImportHandler<EventNominalValue> handler) throws SAXException, IOException {
        topLevelClass = EventNominalValue.class;
        importHandler = handler;
        parse(source);
        return topLevelObjects;
    }

    private void parse(InputSource source)  throws SAXException, IOException {
        XMLReader xmlReader = XMLReaderFactory.createXMLReader();
        xmlReader.setContentHandler(this);
        xmlReader.setErrorHandler(this);
        xmlReader.parse(source);
    }

    Patient patientDbo = null;
    public boolean syncPair(Transaction t, Patient o, Patient dbo, SyncMode syncMode, boolean simulate) throws ImportException {
        patientDbo = dbo;
        boolean changed = false;
        if (o == null)
            return changed;
        if (dbo != null) {
            if (!equals(dbo.getPatientId(), o.getPatientId())){                if (!simulate)
                    dbo.setPatientId(o.getPatientId());
                log.append(Describe.describe(o) + ": changed patientId\n");
                changed = true;
            }
        }
        for(PatientEventValue e : o.getPatientEventValues()) {
            if (dbo == null) {
                if (syncPair(t, e, (PatientEventValue)null, syncMode, simulate)) changed = true;
            } else {
                PatientEventValue dbe = null;
                for(PatientEventValue f : dbo.getPatientEventValues()) {
                    if (Equals.isSamePatientEventValue(e, f)) {
                        dbe = f; break;
                    }
                }
                if (dbe == null && doAdd("PatientEventValue")) {
                    log.append(Describe.describe(dbo) + ": New " + Describe.describe(e) + "\n");
                    syncPair(t, e, null, syncMode, simulate);
                    changed = true;
                    if (!simulate) {
                        dbo.addPatientEventValue(e);
                    }
                } else if(doUpdate("PatientEventValue")){
                    if (syncPair(t, e, dbe, syncMode, simulate)) changed = true;
                }
            }
        }
        if (dbo != null && doDelete("PatientEventValue")) {
            for(Iterator<PatientEventValue> i = dbo.getPatientEventValues().iterator(); i.hasNext();) {
                PatientEventValue dbe = i.next();
                PatientEventValue e = null;
                for(PatientEventValue f : o.getPatientEventValues()) {
                    if (Equals.isSamePatientEventValue(dbe, f)) {
                        e = f; break;
                    }
                }
                if (e == null) {
                    log.append(Describe.describe(dbo) + ": Removed " + Describe.describe(dbe) + "\n");
                    changed = true;
                    if (!simulate) {
                        i.remove();
                        t.delete(dbe);
                    }
                }
            }
        }
        for(Dataset e : o.getDatasets()) {
            if (dbo == null) {
                if (syncPair(t, e, (Dataset)null, syncMode, simulate)) changed = true;
            } else {
                Dataset dbe = null;
                for(Dataset f : dbo.getDatasets()) {
                    if (Equals.isSameDataset(e, f)) {
                        dbe = f; break;
                    }
                }
                if (dbe == null && doAdd("Dataset")) {
                    log.append(Describe.describe(dbo) + ": New " + Describe.describe(e) + "\n");
                    syncPair(t, e, null, syncMode, simulate);
                    changed = true;
                    if (!simulate) {
                        dbo.addDataset(e);
                    }
                } else if(doUpdate("Dataset")){
                    if (syncPair(t, e, dbe, syncMode, simulate)) changed = true;
                }
            }
        }
        if (dbo != null && doDelete("Dataset")) {
            for(Iterator<Dataset> i = dbo.getDatasets().iterator(); i.hasNext();) {
                Dataset dbe = i.next();
                Dataset e = null;
                for(Dataset f : o.getDatasets()) {
                    if (Equals.isSameDataset(dbe, f)) {
                        e = f; break;
                    }
                }
                if (e == null) {
                    log.append(Describe.describe(dbo) + ": Removed " + Describe.describe(dbe) + "\n");
                    changed = true;
                    if (!simulate) {
                        i.remove();
                        t.delete(dbe);
                    }
                }
            }
        }
        for(TestResult e : o.getTestResults()) {
            if(!e.getTest().getTestType().getTestObject().getTestObjectId().equals(StandardObjects.getPatientTestObject().getTestObjectId())) continue;
            if (dbo == null) {
                if (syncPair(t, e, (TestResult)null, syncMode, simulate)) changed = true;
            } else {
                TestResult dbe = null;
                for(TestResult f : dbo.getTestResults()) {
                    if (Equals.isSameTestResult(e, f)) {
                        dbe = f; break;
                    }
                }
                if (dbe == null && doAdd("TestResult")) {
                    log.append(Describe.describe(dbo) + ": New " + Describe.describe(e) + "\n");
                    syncPair(t, e, null, syncMode, simulate);
                    changed = true;
                    if (!simulate) {
                        dbo.addTestResult(e);
                    }
                } else if(doUpdate("TestResult")){
                    if (syncPair(t, e, dbe, syncMode, simulate)) changed = true;
                }
            }
        }
        if (dbo != null && doDelete("TestResult")) {
            for(Iterator<TestResult> i = dbo.getTestResults().iterator(); i.hasNext();) {
                TestResult dbe = i.next();
                if(!dbe.getTest().getTestType().getTestObject().getTestObjectId().equals(StandardObjects.getPatientTestObject().getTestObjectId())) continue;
                TestResult e = null;
                for(TestResult f : o.getTestResults()) {
                    if (Equals.isSameTestResult(dbe, f)) {
                        e = f; break;
                    }
                }
                if (e == null) {
                    log.append(Describe.describe(dbo) + ": Removed " + Describe.describe(dbe) + "\n");
                    changed = true;
                    if (!simulate) {
                        i.remove();
                        t.delete(dbe);
                    }
                }
            }
        }
        for(PatientAttributeValue e : o.getPatientAttributeValues()) {
            if (dbo == null) {
                if (syncPair(t, e, (PatientAttributeValue)null, syncMode, simulate)) changed = true;
            } else {
                PatientAttributeValue dbe = null;
                for(PatientAttributeValue f : dbo.getPatientAttributeValues()) {
                    if (Equals.isSamePatientAttributeValue(e, f)) {
                        dbe = f; break;
                    }
                }
                if (dbe == null && doAdd("PatientAttributeValue")) {
                    log.append(Describe.describe(dbo) + ": New " + Describe.describe(e) + "\n");
                    syncPair(t, e, null, syncMode, simulate);
                    changed = true;
                    if (!simulate) {
                        dbo.addPatientAttributeValue(e);
                    }
                } else if(doUpdate("PatientAttributeValue")){
                    if (syncPair(t, e, dbe, syncMode, simulate)) changed = true;
                }
            }
        }
        if (dbo != null && doDelete("PatientAttributeValue")) {
            for(Iterator<PatientAttributeValue> i = dbo.getPatientAttributeValues().iterator(); i.hasNext();) {
                PatientAttributeValue dbe = i.next();
                PatientAttributeValue e = null;
                for(PatientAttributeValue f : o.getPatientAttributeValues()) {
                    if (Equals.isSamePatientAttributeValue(dbe, f)) {
                        e = f; break;
                    }
                }
                if (e == null) {
                    log.append(Describe.describe(dbo) + ": Removed " + Describe.describe(dbe) + "\n");
                    changed = true;
                    if (!simulate) {
                        i.remove();
                        t.delete(dbe);
                    }
                }
            }
        }
        for(ViralIsolate e : o.getViralIsolates()) {
            if (dbo == null) {
                if (syncPair(t, e, (ViralIsolate)null, syncMode, simulate)) changed = true;
            } else {
                ViralIsolate dbe = null;
                for(ViralIsolate f : dbo.getViralIsolates()) {
                    if (Equals.isSameViralIsolate(e, f)) {
                        dbe = f; break;
                    }
                }
                if (dbe == null && doAdd("ViralIsolate")) {
                    log.append(Describe.describe(dbo) + ": New " + Describe.describe(e) + "\n");
                    syncPair(t, e, null, syncMode, simulate);
                    changed = true;
                    if (!simulate) {
                        dbo.addViralIsolate(e);
                    }
                } else if(doUpdate("ViralIsolate")){
                    if (syncPair(t, e, dbe, syncMode, simulate)) changed = true;
                }
            }
        }
        if (dbo != null && doDelete("ViralIsolate")) {
            for(Iterator<ViralIsolate> i = dbo.getViralIsolates().iterator(); i.hasNext();) {
                ViralIsolate dbe = i.next();
                ViralIsolate e = null;
                for(ViralIsolate f : o.getViralIsolates()) {
                    if (Equals.isSameViralIsolate(dbe, f)) {
                        e = f; break;
                    }
                }
                if (e == null) {
                    log.append(Describe.describe(dbo) + ": Removed " + Describe.describe(dbe) + "\n");
                    changed = true;
                    if (!simulate) {
                        i.remove();
                        t.delete(dbe);
                    }
                }
            }
        }
        for(Therapy e : o.getTherapies()) {
            if (dbo == null) {
                if (syncPair(t, e, (Therapy)null, syncMode, simulate)) changed = true;
            } else {
                Therapy dbe = null;
                for(Therapy f : dbo.getTherapies()) {
                    if (Equals.isSameTherapy(e, f)) {
                        dbe = f; break;
                    }
                }
                if (dbe == null && doAdd("Therapy")) {
                    log.append(Describe.describe(dbo) + ": New " + Describe.describe(e) + "\n");
                    syncPair(t, e, null, syncMode, simulate);
                    changed = true;
                    if (!simulate) {
                        dbo.addTherapy(e);
                    }
                } else if(doUpdate("Therapy")){
                    if (syncPair(t, e, dbe, syncMode, simulate)) changed = true;
                }
            }
        }
        if (dbo != null && doDelete("Therapy")) {
            for(Iterator<Therapy> i = dbo.getTherapies().iterator(); i.hasNext();) {
                Therapy dbe = i.next();
                Therapy e = null;
                for(Therapy f : o.getTherapies()) {
                    if (Equals.isSameTherapy(dbe, f)) {
                        e = f; break;
                    }
                }
                if (e == null) {
                    log.append(Describe.describe(dbo) + ": Removed " + Describe.describe(dbe) + "\n");
                    changed = true;
                    if (!simulate) {
                        i.remove();
                        t.delete(dbe);
                    }
                }
            }
        }
        return changed;
    }

    public boolean syncPair(Transaction t, Dataset o, Dataset dbo, SyncMode syncMode, boolean simulate) throws ImportException {
        boolean changed = false;
        if (o == null)
            return changed;
        if (dbo != null) {
            if (!equals(dbo.getDescription(), o.getDescription())){                if (!simulate)
                    dbo.setDescription(o.getDescription());
                log.append(Describe.describe(o) + ": changed description\n");
                changed = true;
            }
        }
        if (dbo != null) {
            if (!equals(dbo.getCreationDate(), o.getCreationDate())){                if (!simulate)
                    dbo.setCreationDate(o.getCreationDate());
                log.append(Describe.describe(o) + ": changed creationDate\n");
                changed = true;
            }
        }
        if (dbo != null) {
            if (!equals(dbo.getClosedDate(), o.getClosedDate())){                if (!simulate)
                    dbo.setClosedDate(o.getClosedDate());
                log.append(Describe.describe(o) + ": changed closedDate\n");
                changed = true;
            }
        }
        if (dbo != null) {
            if (!equals(dbo.getRevision(), o.getRevision())){                if (!simulate)
                    dbo.setRevision(o.getRevision());
                log.append(Describe.describe(o) + ": changed revision\n");
                changed = true;
            }
        }
        return changed;
    }

    public boolean syncPair(Transaction t, TestResult o, TestResult dbo, SyncMode syncMode, boolean simulate) throws ImportException {
        boolean changed = false;
        if (o == null)
            return changed;
        {
            Test dbf = null;
            if (dbo == null) {
                if (o.getTest() != null)
                    dbf = Retrieve.retrieve(t, o.getTest());
            } else {
                if (Equals.isSameTest(o.getTest(), dbo.getTest()))
                    dbf = dbo.getTest();
                else
                    dbf = Retrieve.retrieve(t, o.getTest());
            }
            if (o.getTest() != null) {
                if (dbf == null) {
                    log.append("New " + Describe.describe(o.getTest()) + "\n");
                    syncPair(t, o.getTest(), (Test)null, syncMode, simulate);
                    changed = true;
                    dbf = o.getTest();
                } else {
                    if (syncMode == SyncMode.Update || syncMode == SyncMode.Clean) {
                        if (syncPair(t, o.getTest(), dbf, syncMode, true)) {
                            throw new ImportException("Imported " + Describe.describe(o) + " is different, synchronize them first !");
                        }
                    } else
                    if (syncPair(t, o.getTest(), dbf, syncMode, simulate)) changed = true;
                }
            }
            if (dbo == null) {
                if (dbf != null) {
                    if (!simulate)
                        o.setTest(dbf);
                }
            } else {
                if (dbf != dbo.getTest()) {
                    if (!simulate)
                        dbo.setTest(dbf);
                    log.append(Describe.describe(o) + ": changed test\n");
                    changed = true;
                }
            }
        }
        if (dbo != null) {
            if (!equals(dbo.getDrugGeneric(), o.getDrugGeneric())){                if (!simulate)
                    dbo.setDrugGeneric(o.getDrugGeneric());
                log.append(Describe.describe(o) + ": changed drugGeneric\n");
                changed = true;
            }
        }
        {
            TestNominalValue dbf = null;
            if (dbo == null) {
                if (o.getTestNominalValue() != null)
                    dbf = Retrieve.retrieve(t, o.getTestNominalValue());
            } else {
                if (Equals.isSameTestNominalValue(o.getTestNominalValue(), dbo.getTestNominalValue()))
                    dbf = dbo.getTestNominalValue();
                else
                    dbf = Retrieve.retrieve(t, o.getTestNominalValue());
            }
            if (o.getTestNominalValue() != null) {
                if (dbf == null) {
                    log.append("New " + Describe.describe(o.getTestNominalValue()) + "\n");
                    syncPair(t, o.getTestNominalValue(), (TestNominalValue)null, syncMode, simulate);
                    changed = true;
                    dbf = o.getTestNominalValue();
                } else {
                    if (syncMode == SyncMode.Update || syncMode == SyncMode.Clean) {
                        if (syncPair(t, o.getTestNominalValue(), dbf, syncMode, true)) {
                            throw new ImportException("Imported " + Describe.describe(o) + " is different, synchronize them first !");
                        }
                    } else
                    if (syncPair(t, o.getTestNominalValue(), dbf, syncMode, simulate)) changed = true;
                }
            }
            if (dbo == null) {
                if (dbf != null) {
                    if (!simulate)
                        o.setTestNominalValue(dbf);
                }
            } else {
                if (dbf != dbo.getTestNominalValue()) {
                    if (!simulate)
                        dbo.setTestNominalValue(dbf);
                    log.append(Describe.describe(o) + ": changed testNominalValue\n");
                    changed = true;
                }
            }
        }
        if (dbo != null) {
            if (!equals(dbo.getValue(), o.getValue())){                if (!simulate)
                    dbo.setValue(o.getValue());
                log.append(Describe.describe(o) + ": changed value\n");
                changed = true;
            }
        }
        if (dbo != null) {
            if (!equals(dbo.getTestDate(), o.getTestDate())){                if (!simulate)
                    dbo.setTestDate(o.getTestDate());
                log.append(Describe.describe(o) + ": changed testDate\n");
                changed = true;
            }
        }
        if (dbo != null) {
            if (!equals(dbo.getSampleId(), o.getSampleId())){                if (!simulate)
                    dbo.setSampleId(o.getSampleId());
                log.append(Describe.describe(o) + ": changed sampleId\n");
                changed = true;
            }
        }
        if (dbo != null) {
            if (!equals(dbo.getData(), o.getData())){                if (!simulate)
                    dbo.setData(o.getData());
                log.append(Describe.describe(o) + ": changed data\n");
                changed = true;
            }
        }
        return changed;
    }

    public boolean syncPair(Transaction t, Test o, Test dbo, SyncMode syncMode, boolean simulate) throws ImportException {
        if (syncedTestSet.contains(o))
            return false;
        else
            syncedTestSet.add(o);
        boolean changed = false;
        if (o == null)
            return changed;
        {
            Analysis dbf = null;
            if (dbo == null) {
                if (o.getAnalysis() != null)
                    dbf = Retrieve.retrieve(t, o.getAnalysis());
            } else {
                if (Equals.isSameAnalysis(o.getAnalysis(), dbo.getAnalysis()))
                    dbf = dbo.getAnalysis();
                else
                    dbf = Retrieve.retrieve(t, o.getAnalysis());
            }
            if (o.getAnalysis() != null) {
                if (dbf == null) {
                    log.append("New " + Describe.describe(o.getAnalysis()) + "\n");
                    syncPair(t, o.getAnalysis(), (Analysis)null, syncMode, simulate);
                    changed = true;
                    dbf = o.getAnalysis();
                } else {
                    if (syncMode == SyncMode.Update || syncMode == SyncMode.Clean) {
                        if (syncPair(t, o.getAnalysis(), dbf, syncMode, true)) {
                            throw new ImportException("Imported " + Describe.describe(o) + " is different, synchronize them first !");
                        }
                    } else
                    if (syncPair(t, o.getAnalysis(), dbf, syncMode, simulate)) changed = true;
                }
            }
            if (dbo == null) {
                if (dbf != null) {
                    if (!simulate)
                        o.setAnalysis(dbf);
                }
            } else {
                if (dbf != dbo.getAnalysis()) {
                    if (!simulate)
                        dbo.setAnalysis(dbf);
                    log.append(Describe.describe(o) + ": changed analysis\n");
                    changed = true;
                }
            }
        }
        {
            TestType dbf = null;
            if (dbo == null) {
                if (o.getTestType() != null)
                    dbf = Retrieve.retrieve(t, o.getTestType());
            } else {
                if (Equals.isSameTestType(o.getTestType(), dbo.getTestType()))
                    dbf = dbo.getTestType();
                else
                    dbf = Retrieve.retrieve(t, o.getTestType());
            }
            if (o.getTestType() != null) {
                if (dbf == null) {
                    log.append("New " + Describe.describe(o.getTestType()) + "\n");
                    syncPair(t, o.getTestType(), (TestType)null, syncMode, simulate);
                    changed = true;
                    dbf = o.getTestType();
                } else {
                    if (syncMode == SyncMode.Update || syncMode == SyncMode.Clean) {
                        if (syncPair(t, o.getTestType(), dbf, syncMode, true)) {
                            throw new ImportException("Imported " + Describe.describe(o) + " is different, synchronize them first !");
                        }
                    } else
                    if (syncPair(t, o.getTestType(), dbf, syncMode, simulate)) changed = true;
                }
            }
            if (dbo == null) {
                if (dbf != null) {
                    if (!simulate)
                        o.setTestType(dbf);
                }
            } else {
                if (dbf != dbo.getTestType()) {
                    if (!simulate)
                        dbo.setTestType(dbf);
                    log.append(Describe.describe(o) + ": changed testType\n");
                    changed = true;
                }
            }
        }
        if (dbo != null) {
            if (!equals(dbo.getDescription(), o.getDescription())){                if (!simulate)
                    dbo.setDescription(o.getDescription());
                log.append(Describe.describe(o) + ": changed description\n");
                changed = true;
            }
        }
        return changed;
    }

    public boolean syncPair(Transaction t, Analysis o, Analysis dbo, SyncMode syncMode, boolean simulate) throws ImportException {
        if (syncedAnalysisSet.contains(o))
            return false;
        else
            syncedAnalysisSet.add(o);
        boolean changed = false;
        if (o == null)
            return changed;
        if (dbo != null) {
            if (!equals(dbo.getAnalysisType(), o.getAnalysisType())){                if (!simulate)
                    dbo.setAnalysisType(o.getAnalysisType());
                log.append(Describe.describe(o) + ": changed analysisType\n");
                changed = true;
            }
        }
        if (dbo != null) {
            if (!equals(dbo.getUrl(), o.getUrl())){                if (!simulate)
                    dbo.setUrl(o.getUrl());
                log.append(Describe.describe(o) + ": changed url\n");
                changed = true;
            }
        }
        if (dbo != null) {
            if (!equals(dbo.getAccount(), o.getAccount())){                if (!simulate)
                    dbo.setAccount(o.getAccount());
                log.append(Describe.describe(o) + ": changed account\n");
                changed = true;
            }
        }
        if (dbo != null) {
            if (!equals(dbo.getPassword(), o.getPassword())){                if (!simulate)
                    dbo.setPassword(o.getPassword());
                log.append(Describe.describe(o) + ": changed password\n");
                changed = true;
            }
        }
        if (dbo != null) {
            if (!equals(dbo.getBaseinputfile(), o.getBaseinputfile())){                if (!simulate)
                    dbo.setBaseinputfile(o.getBaseinputfile());
                log.append(Describe.describe(o) + ": changed baseinputfile\n");
                changed = true;
            }
        }
        if (dbo != null) {
            if (!equals(dbo.getBaseoutputfile(), o.getBaseoutputfile())){                if (!simulate)
                    dbo.setBaseoutputfile(o.getBaseoutputfile());
                log.append(Describe.describe(o) + ": changed baseoutputfile\n");
                changed = true;
            }
        }
        if (dbo != null) {
            if (!equals(dbo.getServiceName(), o.getServiceName())){                if (!simulate)
                    dbo.setServiceName(o.getServiceName());
                log.append(Describe.describe(o) + ": changed serviceName\n");
                changed = true;
            }
        }
        if (dbo != null) {
            if (!equals(dbo.getDataoutputfile(), o.getDataoutputfile())){                if (!simulate)
                    dbo.setDataoutputfile(o.getDataoutputfile());
                log.append(Describe.describe(o) + ": changed dataoutputfile\n");
                changed = true;
            }
        }
        for(AnalysisData e : o.getAnalysisDatas()) {
            if (dbo == null) {
                if (syncPair(t, e, (AnalysisData)null, syncMode, simulate)) changed = true;
            } else {
                AnalysisData dbe = null;
                for(AnalysisData f : dbo.getAnalysisDatas()) {
                    if (Equals.isSameAnalysisData(e, f)) {
                        dbe = f; break;
                    }
                }
                if (dbe == null && doAdd("AnalysisData")) {
                    log.append(Describe.describe(dbo) + ": New " + Describe.describe(e) + "\n");
                    syncPair(t, e, null, syncMode, simulate);
                    changed = true;
                    if (!simulate) {
                        dbo.getAnalysisDatas().add(e);
                        e.setAnalysis(dbo);
                    }
                } else if(doUpdate("AnalysisData")){
                    if (syncPair(t, e, dbe, syncMode, simulate)) changed = true;
                }
            }
        }
        if (dbo != null && doDelete("AnalysisData")) {
            for(Iterator<AnalysisData> i = dbo.getAnalysisDatas().iterator(); i.hasNext();) {
                AnalysisData dbe = i.next();
                AnalysisData e = null;
                for(AnalysisData f : o.getAnalysisDatas()) {
                    if (Equals.isSameAnalysisData(dbe, f)) {
                        e = f; break;
                    }
                }
                if (e == null) {
                    log.append(Describe.describe(dbo) + ": Removed " + Describe.describe(dbe) + "\n");
                    changed = true;
                    if (!simulate) {
                        i.remove();
                        t.delete(dbe);
                    }
                }
            }
        }
        return changed;
    }

    public boolean syncPair(Transaction t, AnalysisData o, AnalysisData dbo, SyncMode syncMode, boolean simulate) throws ImportException {
        if (syncedAnalysisDataSet.contains(o))
            return false;
        else
            syncedAnalysisDataSet.add(o);
        boolean changed = false;
        if (o == null)
            return changed;
        if (dbo != null) {
            if (!equals(dbo.getName(), o.getName())){                if (!simulate)
                    dbo.setName(o.getName());
                log.append(Describe.describe(o) + ": changed name\n");
                changed = true;
            }
        }
        if (dbo != null) {
            if (!equals(dbo.getData(), o.getData())){                if (!simulate)
                    dbo.setData(o.getData());
                log.append(Describe.describe(o) + ": changed data\n");
                changed = true;
            }
        }
        if (dbo != null) {
            if (!equals(dbo.getMimetype(), o.getMimetype())){                if (!simulate)
                    dbo.setMimetype(o.getMimetype());
                log.append(Describe.describe(o) + ": changed mimetype\n");
                changed = true;
            }
        }
        return changed;
    }

    public boolean syncPair(Transaction t, PatientEventValue o, PatientEventValue dbo, SyncMode syncMode, boolean simulate) throws ImportException {
        boolean changed = false;
        if (o == null)
            return changed;
        {
            EventNominalValue dbf = null;
            if (dbo == null) {
                if (o.getEventNominalValue() != null)
                    dbf = Retrieve.retrieve(t, o.getEventNominalValue());
            } else {
                if (Equals.isSameEventNominalValue(o.getEventNominalValue(), dbo.getEventNominalValue()))
                    dbf = dbo.getEventNominalValue();
                else
                    dbf = Retrieve.retrieve(t, o.getEventNominalValue());
            }
            if (o.getEventNominalValue() != null) {
                if (dbf == null) {
                    log.append("New " + Describe.describe(o.getEventNominalValue()) + "\n");
                    syncPair(t, o.getEventNominalValue(), (EventNominalValue)null, syncMode, simulate);
                    changed = true;
                    dbf = o.getEventNominalValue();
                } else {
                    if (syncMode == SyncMode.Update || syncMode == SyncMode.Clean) {
                        if (syncPair(t, o.getEventNominalValue(), dbf, syncMode, true)) {
                            throw new ImportException("Imported " + Describe.describe(o) + " is different, synchronize them first !");
                        }
                    } else
                    if (syncPair(t, o.getEventNominalValue(), dbf, syncMode, simulate)) changed = true;
                }
            }
            if (dbo == null) {
                if (dbf != null) {
                    if (!simulate)
                        o.setEventNominalValue(dbf);
                }
            } else {
                if (dbf != dbo.getEventNominalValue()) {
                    if (!simulate)
                        dbo.setEventNominalValue(dbf);
                    log.append(Describe.describe(o) + ": changed eventNominalValue\n");
                    changed = true;
                }
            }
        }
        {
            Event dbf = null;
            if (dbo == null) {
                if (o.getEvent() != null)
                    dbf = Retrieve.retrieve(t, o.getEvent());
            } else {
                if (Equals.isSameEvent(o.getEvent(), dbo.getEvent()))
                    dbf = dbo.getEvent();
                else
                    dbf = Retrieve.retrieve(t, o.getEvent());
            }
            if (o.getEvent() != null) {
                if (dbf == null) {
                    log.append("New " + Describe.describe(o.getEvent()) + "\n");
                    syncPair(t, o.getEvent(), (Event)null, syncMode, simulate);
                    changed = true;
                    dbf = o.getEvent();
                } else {
                    if (syncMode == SyncMode.Update || syncMode == SyncMode.Clean) {
                        if (syncPair(t, o.getEvent(), dbf, syncMode, true)) {
                            throw new ImportException("Imported " + Describe.describe(o) + " is different, synchronize them first !");
                        }
                    } else
                    if (syncPair(t, o.getEvent(), dbf, syncMode, simulate)) changed = true;
                }
            }
            if (dbo == null) {
                if (dbf != null) {
                    if (!simulate)
                        o.setEvent(dbf);
                }
            } else {
                if (dbf != dbo.getEvent()) {
                    if (!simulate)
                        dbo.setEvent(dbf);
                    log.append(Describe.describe(o) + ": changed event\n");
                    changed = true;
                }
            }
        }
        if (dbo != null) {
            if (!equals(dbo.getValue(), o.getValue())){                if (!simulate)
                    dbo.setValue(o.getValue());
                log.append(Describe.describe(o) + ": changed value\n");
                changed = true;
            }
        }
        if (dbo != null) {
            if (!equals(dbo.getStartDate(), o.getStartDate())){                if (!simulate)
                    dbo.setStartDate(o.getStartDate());
                log.append(Describe.describe(o) + ": changed startDate\n");
                changed = true;
            }
        }
        if (dbo != null) {
            if (!equals(dbo.getEndDate(), o.getEndDate())){                if (!simulate)
                    dbo.setEndDate(o.getEndDate());
                log.append(Describe.describe(o) + ": changed endDate\n");
                changed = true;
            }
        }
        return changed;
    }

    public boolean syncPair(Transaction t, TestType o, TestType dbo, SyncMode syncMode, boolean simulate) throws ImportException {
        if (syncedTestTypeSet.contains(o))
            return false;
        else
            syncedTestTypeSet.add(o);
        boolean changed = false;
        if (o == null)
            return changed;
        {
            ValueType dbf = null;
            if (dbo == null) {
                if (o.getValueType() != null)
                    dbf = Retrieve.retrieve(t, o.getValueType());
            } else {
                if (Equals.isSameValueType(o.getValueType(), dbo.getValueType()))
                    dbf = dbo.getValueType();
                else
                    dbf = Retrieve.retrieve(t, o.getValueType());
            }
            if (o.getValueType() != null) {
                if (dbf == null) {
                    log.append("New " + Describe.describe(o.getValueType()) + "\n");
                    syncPair(t, o.getValueType(), (ValueType)null, syncMode, simulate);
                    changed = true;
                    dbf = o.getValueType();
                } else {
                    if (syncMode == SyncMode.Update || syncMode == SyncMode.Clean) {
                        if (syncPair(t, o.getValueType(), dbf, syncMode, true)) {
                            throw new ImportException("Imported " + Describe.describe(o) + " is different, synchronize them first !");
                        }
                    } else
                    if (syncPair(t, o.getValueType(), dbf, syncMode, simulate)) changed = true;
                }
            }
            if (dbo == null) {
                if (dbf != null) {
                    if (!simulate)
                        o.setValueType(dbf);
                }
            } else {
                if (dbf != dbo.getValueType()) {
                    if (!simulate)
                        dbo.setValueType(dbf);
                    log.append(Describe.describe(o) + ": changed valueType\n");
                    changed = true;
                }
            }
        }
        if (dbo != null) {
            if (!equals(dbo.getGenome(), o.getGenome())){                if (!simulate)
                    dbo.setGenome(o.getGenome());
                log.append(Describe.describe(o) + ": changed genome\n");
                changed = true;
            }
        }
        {
            TestObject dbf = null;
            if (dbo == null) {
                if (o.getTestObject() != null)
                    dbf = Retrieve.retrieve(t, o.getTestObject());
            } else {
                if (Equals.isSameTestObject(o.getTestObject(), dbo.getTestObject()))
                    dbf = dbo.getTestObject();
                else
                    dbf = Retrieve.retrieve(t, o.getTestObject());
            }
            if (o.getTestObject() != null) {
                if (dbf == null) {
                    log.append("New " + Describe.describe(o.getTestObject()) + "\n");
                    syncPair(t, o.getTestObject(), (TestObject)null, syncMode, simulate);
                    changed = true;
                    dbf = o.getTestObject();
                } else {
                    if (syncMode == SyncMode.Update || syncMode == SyncMode.Clean) {
                        if (syncPair(t, o.getTestObject(), dbf, syncMode, true)) {
                            throw new ImportException("Imported " + Describe.describe(o) + " is different, synchronize them first !");
                        }
                    } else
                    if (syncPair(t, o.getTestObject(), dbf, syncMode, simulate)) changed = true;
                }
            }
            if (dbo == null) {
                if (dbf != null) {
                    if (!simulate)
                        o.setTestObject(dbf);
                }
            } else {
                if (dbf != dbo.getTestObject()) {
                    if (!simulate)
                        dbo.setTestObject(dbf);
                    log.append(Describe.describe(o) + ": changed testObject\n");
                    changed = true;
                }
            }
        }
        if (dbo != null) {
            if (!equals(dbo.getDescription(), o.getDescription())){                if (!simulate)
                    dbo.setDescription(o.getDescription());
                log.append(Describe.describe(o) + ": changed description\n");
                changed = true;
            }
        }
        for(TestNominalValue e : o.getTestNominalValues()) {
            if (dbo == null) {
                if (syncPair(t, e, (TestNominalValue)null, syncMode, simulate)) changed = true;
            } else {
                TestNominalValue dbe = null;
                for(TestNominalValue f : dbo.getTestNominalValues()) {
                    if (Equals.isSameTestNominalValue(e, f)) {
                        dbe = f; break;
                    }
                }
                if (dbe == null && doAdd("TestNominalValue")) {
                    log.append(Describe.describe(dbo) + ": New " + Describe.describe(e) + "\n");
                    syncPair(t, e, null, syncMode, simulate);
                    changed = true;
                    if (!simulate) {
                        dbo.getTestNominalValues().add(e);
                        e.setTestType(dbo);
                    }
                } else if(doUpdate("TestNominalValue")){
                    if (syncPair(t, e, dbe, syncMode, simulate)) changed = true;
                }
            }
        }
        if (dbo != null && doDelete("TestNominalValue")) {
            for(Iterator<TestNominalValue> i = dbo.getTestNominalValues().iterator(); i.hasNext();) {
                TestNominalValue dbe = i.next();
                TestNominalValue e = null;
                for(TestNominalValue f : o.getTestNominalValues()) {
                    if (Equals.isSameTestNominalValue(dbe, f)) {
                        e = f; break;
                    }
                }
                if (e == null) {
                    log.append(Describe.describe(dbo) + ": Removed " + Describe.describe(dbe) + "\n");
                    changed = true;
                    if (!simulate) {
                        i.remove();
                        t.delete(dbe);
                    }
                }
            }
        }
        return changed;
    }

    public boolean syncPair(Transaction t, TestObject o, TestObject dbo, SyncMode syncMode, boolean simulate) throws ImportException {
        if (syncedTestObjectSet.contains(o))
            return false;
        else
            syncedTestObjectSet.add(o);
        boolean changed = false;
        if (o == null)
            return changed;
        if (dbo != null) {
            if (!equals(dbo.getDescription(), o.getDescription())){                if (!simulate)
                    dbo.setDescription(o.getDescription());
                log.append(Describe.describe(o) + ": changed description\n");
                changed = true;
            }
        }
        if (dbo != null) {
            if (!equals(dbo.getTestObjectId(), o.getTestObjectId())){                if (!simulate)
                    dbo.setTestObjectId(o.getTestObjectId());
                log.append(Describe.describe(o) + ": changed testObjectId\n");
                changed = true;
            }
        }
        return changed;
    }

    public boolean syncPair(Transaction t, TestNominalValue o, TestNominalValue dbo, SyncMode syncMode, boolean simulate) throws ImportException {
        if (syncedTestNominalValueSet.contains(o))
            return false;
        else
            syncedTestNominalValueSet.add(o);
        boolean changed = false;
        if (o == null)
            return changed;
        if (dbo != null) {
            if (!equals(dbo.getValue(), o.getValue())){                if (!simulate)
                    dbo.setValue(o.getValue());
                log.append(Describe.describe(o) + ": changed value\n");
                changed = true;
            }
        }
        return changed;
    }

    public boolean syncPair(Transaction t, PatientAttributeValue o, PatientAttributeValue dbo, SyncMode syncMode, boolean simulate) throws ImportException {
        boolean changed = false;
        if (o == null)
            return changed;
        {
            Attribute dbf = null;
            if (dbo == null) {
                if (o.getAttribute() != null)
                    dbf = Retrieve.retrieve(t, o.getAttribute());
            } else {
                if (Equals.isSameAttribute(o.getAttribute(), dbo.getAttribute()))
                    dbf = dbo.getAttribute();
                else
                    dbf = Retrieve.retrieve(t, o.getAttribute());
            }
            if (o.getAttribute() != null) {
                if (dbf == null) {
                    log.append("New " + Describe.describe(o.getAttribute()) + "\n");
                    syncPair(t, o.getAttribute(), (Attribute)null, syncMode, simulate);
                    changed = true;
                    dbf = o.getAttribute();
                } else {
                    if (syncMode == SyncMode.Update || syncMode == SyncMode.Clean) {
                        if (syncPair(t, o.getAttribute(), dbf, syncMode, true)) {
                            throw new ImportException("Imported " + Describe.describe(o) + " is different, synchronize them first !");
                        }
                    } else
                    if (syncPair(t, o.getAttribute(), dbf, syncMode, simulate)) changed = true;
                }
            }
            if (dbo == null) {
                if (dbf != null) {
                    if (!simulate)
                        o.setAttribute(dbf);
                }
            } else {
                if (dbf != dbo.getAttribute()) {
                    if (!simulate)
                        dbo.setAttribute(dbf);
                    log.append(Describe.describe(o) + ": changed attribute\n");
                    changed = true;
                }
            }
        }
        {
            AttributeNominalValue dbf = null;
            if (dbo == null) {
                if (o.getAttributeNominalValue() != null)
                    dbf = Retrieve.retrieve(t, o.getAttributeNominalValue());
            } else {
                if (Equals.isSameAttributeNominalValue(o.getAttributeNominalValue(), dbo.getAttributeNominalValue()))
                    dbf = dbo.getAttributeNominalValue();
                else
                    dbf = Retrieve.retrieve(t, o.getAttributeNominalValue());
            }
            if (o.getAttributeNominalValue() != null) {
                if (dbf == null) {
                    log.append("New " + Describe.describe(o.getAttributeNominalValue()) + "\n");
                    syncPair(t, o.getAttributeNominalValue(), (AttributeNominalValue)null, syncMode, simulate);
                    changed = true;
                    dbf = o.getAttributeNominalValue();
                } else {
                    if (syncMode == SyncMode.Update || syncMode == SyncMode.Clean) {
                        if (syncPair(t, o.getAttributeNominalValue(), dbf, syncMode, true)) {
                            throw new ImportException("Imported " + Describe.describe(o) + " is different, synchronize them first !");
                        }
                    } else
                    if (syncPair(t, o.getAttributeNominalValue(), dbf, syncMode, simulate)) changed = true;
                }
            }
            if (dbo == null) {
                if (dbf != null) {
                    if (!simulate)
                        o.setAttributeNominalValue(dbf);
                }
            } else {
                if (dbf != dbo.getAttributeNominalValue()) {
                    if (!simulate)
                        dbo.setAttributeNominalValue(dbf);
                    log.append(Describe.describe(o) + ": changed attributeNominalValue\n");
                    changed = true;
                }
            }
        }
        if (dbo != null) {
            if (!equals(dbo.getValue(), o.getValue())){                if (!simulate)
                    dbo.setValue(o.getValue());
                log.append(Describe.describe(o) + ": changed value\n");
                changed = true;
            }
        }
        return changed;
    }

    public boolean syncPair(Transaction t, Attribute o, Attribute dbo, SyncMode syncMode, boolean simulate) throws ImportException {
        if (syncedAttributeSet.contains(o))
            return false;
        else
            syncedAttributeSet.add(o);
        boolean changed = false;
        if (o == null)
            return changed;
        {
            ValueType dbf = null;
            if (dbo == null) {
                if (o.getValueType() != null)
                    dbf = Retrieve.retrieve(t, o.getValueType());
            } else {
                if (Equals.isSameValueType(o.getValueType(), dbo.getValueType()))
                    dbf = dbo.getValueType();
                else
                    dbf = Retrieve.retrieve(t, o.getValueType());
            }
            if (o.getValueType() != null) {
                if (dbf == null) {
                    log.append("New " + Describe.describe(o.getValueType()) + "\n");
                    syncPair(t, o.getValueType(), (ValueType)null, syncMode, simulate);
                    changed = true;
                    dbf = o.getValueType();
                } else {
                    if (syncMode == SyncMode.Update || syncMode == SyncMode.Clean) {
                        if (syncPair(t, o.getValueType(), dbf, syncMode, true)) {
                            throw new ImportException("Imported " + Describe.describe(o) + " is different, synchronize them first !");
                        }
                    } else
                    if (syncPair(t, o.getValueType(), dbf, syncMode, simulate)) changed = true;
                }
            }
            if (dbo == null) {
                if (dbf != null) {
                    if (!simulate)
                        o.setValueType(dbf);
                }
            } else {
                if (dbf != dbo.getValueType()) {
                    if (!simulate)
                        dbo.setValueType(dbf);
                    log.append(Describe.describe(o) + ": changed valueType\n");
                    changed = true;
                }
            }
        }
        {
            AttributeGroup dbf = null;
            if (dbo == null) {
                if (o.getAttributeGroup() != null)
                    dbf = Retrieve.retrieve(t, o.getAttributeGroup());
            } else {
                if (Equals.isSameAttributeGroup(o.getAttributeGroup(), dbo.getAttributeGroup()))
                    dbf = dbo.getAttributeGroup();
                else
                    dbf = Retrieve.retrieve(t, o.getAttributeGroup());
            }
            if (o.getAttributeGroup() != null) {
                if (dbf == null) {
                    log.append("New " + Describe.describe(o.getAttributeGroup()) + "\n");
                    syncPair(t, o.getAttributeGroup(), (AttributeGroup)null, syncMode, simulate);
                    changed = true;
                    dbf = o.getAttributeGroup();
                } else {
                    if (syncMode == SyncMode.Update || syncMode == SyncMode.Clean) {
                        if (syncPair(t, o.getAttributeGroup(), dbf, syncMode, true)) {
                            throw new ImportException("Imported " + Describe.describe(o) + " is different, synchronize them first !");
                        }
                    } else
                    if (syncPair(t, o.getAttributeGroup(), dbf, syncMode, simulate)) changed = true;
                }
            }
            if (dbo == null) {
                if (dbf != null) {
                    if (!simulate)
                        o.setAttributeGroup(dbf);
                }
            } else {
                if (dbf != dbo.getAttributeGroup()) {
                    if (!simulate)
                        dbo.setAttributeGroup(dbf);
                    log.append(Describe.describe(o) + ": changed attributeGroup\n");
                    changed = true;
                }
            }
        }
        if (dbo != null) {
            if (!equals(dbo.getName(), o.getName())){                if (!simulate)
                    dbo.setName(o.getName());
                log.append(Describe.describe(o) + ": changed name\n");
                changed = true;
            }
        }
        for(AttributeNominalValue e : o.getAttributeNominalValues()) {
            if (dbo == null) {
                if (syncPair(t, e, (AttributeNominalValue)null, syncMode, simulate)) changed = true;
            } else {
                AttributeNominalValue dbe = null;
                for(AttributeNominalValue f : dbo.getAttributeNominalValues()) {
                    if (Equals.isSameAttributeNominalValue(e, f)) {
                        dbe = f; break;
                    }
                }
                if (dbe == null && doAdd("AttributeNominalValue")) {
                    log.append(Describe.describe(dbo) + ": New " + Describe.describe(e) + "\n");
                    syncPair(t, e, null, syncMode, simulate);
                    changed = true;
                    if (!simulate) {
                        dbo.getAttributeNominalValues().add(e);
                        e.setAttribute(dbo);
                    }
                } else if(doUpdate("AttributeNominalValue")){
                    if (syncPair(t, e, dbe, syncMode, simulate)) changed = true;
                }
            }
        }
        if (dbo != null && doDelete("AttributeNominalValue")) {
            for(Iterator<AttributeNominalValue> i = dbo.getAttributeNominalValues().iterator(); i.hasNext();) {
                AttributeNominalValue dbe = i.next();
                AttributeNominalValue e = null;
                for(AttributeNominalValue f : o.getAttributeNominalValues()) {
                    if (Equals.isSameAttributeNominalValue(dbe, f)) {
                        e = f; break;
                    }
                }
                if (e == null) {
                    log.append(Describe.describe(dbo) + ": Removed " + Describe.describe(dbe) + "\n");
                    changed = true;
                    if (!simulate) {
                        i.remove();
                        t.delete(dbe);
                    }
                }
            }
        }
        if (!simulate)
            t.save(dbo == null ? o : dbo);
        return changed;
    }

    public boolean syncPair(Transaction t, AttributeGroup o, AttributeGroup dbo, SyncMode syncMode, boolean simulate) throws ImportException {
        if (syncedAttributeGroupSet.contains(o))
            return false;
        else
            syncedAttributeGroupSet.add(o);
        boolean changed = false;
        if (o == null)
            return changed;
        if (dbo != null) {
            if (!equals(dbo.getGroupName(), o.getGroupName())){                if (!simulate)
                    dbo.setGroupName(o.getGroupName());
                log.append(Describe.describe(o) + ": changed groupName\n");
                changed = true;
            }
        }
        return changed;
    }

    public boolean syncPair(Transaction t, AttributeNominalValue o, AttributeNominalValue dbo, SyncMode syncMode, boolean simulate) throws ImportException {
        if (syncedAttributeNominalValueSet.contains(o))
            return false;
        else
            syncedAttributeNominalValueSet.add(o);
        boolean changed = false;
        if (o == null)
            return changed;
        if (dbo != null) {
            if (!equals(dbo.getValue(), o.getValue())){                if (!simulate)
                    dbo.setValue(o.getValue());
                log.append(Describe.describe(o) + ": changed value\n");
                changed = true;
            }
        }
        return changed;
    }

    public boolean syncPair(Transaction t, ViralIsolate o, ViralIsolate dbo, SyncMode syncMode, boolean simulate) throws ImportException {
        boolean changed = false;
        if (o == null)
            return changed;
        if (dbo != null) {
            if (!equals(dbo.getSampleId(), o.getSampleId())){                if (!simulate)
                    dbo.setSampleId(o.getSampleId());
                log.append(Describe.describe(o) + ": changed sampleId\n");
                changed = true;
            }
        }
        if (dbo != null) {
            if (!equals(dbo.getSampleDate(), o.getSampleDate())){                if (!simulate)
                    dbo.setSampleDate(o.getSampleDate());
                log.append(Describe.describe(o) + ": changed sampleDate\n");
                changed = true;
            }
        }
        if (dbo != null) {
            if (!equals(dbo.getGenome(), o.getGenome())){                if (!simulate)
                    dbo.setGenome(o.getGenome());
                log.append(Describe.describe(o) + ": changed genome\n");
                changed = true;
            }
        }
        for(NtSequence e : o.getNtSequences()) {
            if (dbo == null) {
                if (syncPair(t, e, (NtSequence)null, syncMode, simulate)) changed = true;
            } else {
                NtSequence dbe = null;
                for(NtSequence f : dbo.getNtSequences()) {
                    if (Equals.isSameNtSequence(e, f)) {
                        dbe = f; break;
                    }
                }
                if (dbe == null && doAdd("NtSequence")) {
                    log.append(Describe.describe(dbo) + ": New " + Describe.describe(e) + "\n");
                    syncPair(t, e, null, syncMode, simulate);
                    changed = true;
                    if (!simulate) {
                        dbo.getNtSequences().add(e);
                        e.setViralIsolate(dbo);
                    }
                } else if(doUpdate("NtSequence")){
                    if (syncPair(t, e, dbe, syncMode, simulate)) changed = true;
                }
            }
        }
        if (dbo != null && doDelete("NtSequence")) {
            for(Iterator<NtSequence> i = dbo.getNtSequences().iterator(); i.hasNext();) {
                NtSequence dbe = i.next();
                NtSequence e = null;
                for(NtSequence f : o.getNtSequences()) {
                    if (Equals.isSameNtSequence(dbe, f)) {
                        e = f; break;
                    }
                }
                if (e == null) {
                    log.append(Describe.describe(dbo) + ": Removed " + Describe.describe(dbe) + "\n");
                    changed = true;
                    if (!simulate) {
                        i.remove();
                        t.delete(dbe);
                    }
                }
            }
        }
        for(TestResult e : o.getTestResults()) {
            if (dbo == null) {
                if (syncPair(t, e, (TestResult)null, syncMode, simulate)) changed = true;
            } else {
                TestResult dbe = null;
                for(TestResult f : dbo.getTestResults()) {
                    if (Equals.isSameTestResult(e, f)) {
                        dbe = f; break;
                    }
                }
                if (dbe == null && doAdd("TestResult")) {
                    log.append(Describe.describe(dbo) + ": New " + Describe.describe(e) + "\n");
                    syncPair(t, e, null, syncMode, simulate);
                    changed = true;
                    if (!simulate) {
                        patientDbo.addTestResult(e);
                        dbo.getTestResults().add(e);
                        e.setViralIsolate(dbo);
                    }
                } else if(doUpdate("TestResult")){
                    if (syncPair(t, e, dbe, syncMode, simulate)) changed = true;
                }
            }
        }
        if (dbo != null && doDelete("TestResult")) {
            for(Iterator<TestResult> i = dbo.getTestResults().iterator(); i.hasNext();) {
                TestResult dbe = i.next();
                TestResult e = null;
                for(TestResult f : o.getTestResults()) {
                    if (Equals.isSameTestResult(dbe, f)) {
                        e = f; break;
                    }
                }
                if (e == null) {
                    log.append(Describe.describe(dbo) + ": Removed " + Describe.describe(dbe) + "\n");
                    changed = true;
                    if (!simulate) {
                        i.remove();
                        t.delete(dbe);
                    }
                }
            }
        }
        return changed;
    }

    public boolean syncPair(Transaction t, NtSequence o, NtSequence dbo, SyncMode syncMode, boolean simulate) throws ImportException {
        boolean changed = false;
        if (o == null)
            return changed;
        if (dbo != null) {
            if (!equals(dbo.getLabel(), o.getLabel())){                if (!simulate)
                    dbo.setLabel(o.getLabel());
                log.append(Describe.describe(o) + ": changed label\n");
                changed = true;
            }
        }
        if (dbo != null) {
            if (!equals(dbo.getSequenceDate(), o.getSequenceDate())){                if (!simulate)
                    dbo.setSequenceDate(o.getSequenceDate());
                log.append(Describe.describe(o) + ": changed sequenceDate\n");
                changed = true;
            }
        }
        if (dbo != null) {
            if (!equals(dbo.getNucleotides(), o.getNucleotides())){                if (!simulate)
                    dbo.setNucleotides(o.getNucleotides());
                log.append(Describe.describe(o) + ": changed nucleotides\n");
                changed = true;
            }
        }
        if (dbo != null) {
            if (!equals(dbo.isAligned(), o.isAligned())){                if (!simulate)
                    dbo.setAligned(o.isAligned());
                log.append(Describe.describe(o) + ": changed aligned\n");
                changed = true;
            }
        }
        for(AaSequence e : o.getAaSequences()) {
            if (dbo == null) {
                if (syncPair(t, e, (AaSequence)null, syncMode, simulate)) changed = true;
            } else {
                AaSequence dbe = null;
                for(AaSequence f : dbo.getAaSequences()) {
                    if (Equals.isSameAaSequence(e, f)) {
                        dbe = f; break;
                    }
                }
                if (dbe == null && doAdd("AaSequence")) {
                    log.append(Describe.describe(dbo) + ": New " + Describe.describe(e) + "\n");
                    syncPair(t, e, null, syncMode, simulate);
                    changed = true;
                    if (!simulate) {
                        dbo.getAaSequences().add(e);
                        e.setNtSequence(dbo);
                    }
                } else if(doUpdate("AaSequence")){
                    if (syncPair(t, e, dbe, syncMode, simulate)) changed = true;
                }
            }
        }
        if (dbo != null && doDelete("AaSequence")) {
            for(Iterator<AaSequence> i = dbo.getAaSequences().iterator(); i.hasNext();) {
                AaSequence dbe = i.next();
                AaSequence e = null;
                for(AaSequence f : o.getAaSequences()) {
                    if (Equals.isSameAaSequence(dbe, f)) {
                        e = f; break;
                    }
                }
                if (e == null) {
                    log.append(Describe.describe(dbo) + ": Removed " + Describe.describe(dbe) + "\n");
                    changed = true;
                    if (!simulate) {
                        i.remove();
                        t.delete(dbe);
                    }
                }
            }
        }
        for(TestResult e : o.getTestResults()) {
            if (dbo == null) {
                if (syncPair(t, e, (TestResult)null, syncMode, simulate)) changed = true;
            } else {
                TestResult dbe = null;
                for(TestResult f : dbo.getTestResults()) {
                    if (Equals.isSameTestResult(e, f)) {
                        dbe = f; break;
                    }
                }
                if (dbe == null && doAdd("TestResult")) {
                    log.append(Describe.describe(dbo) + ": New " + Describe.describe(e) + "\n");
                    syncPair(t, e, null, syncMode, simulate);
                    changed = true;
                    if (!simulate) {
                        patientDbo.addTestResult(e);
                        dbo.getTestResults().add(e);
                        e.setNtSequence(dbo);
                    }
                } else if(doUpdate("TestResult")){
                    if (syncPair(t, e, dbe, syncMode, simulate)) changed = true;
                }
            }
        }
        if (dbo != null && doDelete("TestResult")) {
            for(Iterator<TestResult> i = dbo.getTestResults().iterator(); i.hasNext();) {
                TestResult dbe = i.next();
                TestResult e = null;
                for(TestResult f : o.getTestResults()) {
                    if (Equals.isSameTestResult(dbe, f)) {
                        e = f; break;
                    }
                }
                if (e == null) {
                    log.append(Describe.describe(dbo) + ": Removed " + Describe.describe(dbe) + "\n");
                    changed = true;
                    if (!simulate) {
                        i.remove();
                        t.delete(dbe);
                    }
                }
            }
        }
        return changed;
    }

    public boolean syncPair(Transaction t, AaSequence o, AaSequence dbo, SyncMode syncMode, boolean simulate) throws ImportException {
        boolean changed = false;
        if (o == null)
            return changed;
        {
            Protein dbf = null;
            if (dbo == null) {
                if (o.getProtein() != null)
                    dbf = Retrieve.retrieve(t, o.getProtein());
            } else {
                if (Equals.isSameProtein(o.getProtein(), dbo.getProtein()))
                    dbf = dbo.getProtein();
                else
                    dbf = Retrieve.retrieve(t, o.getProtein());
            }
            if (o.getProtein() != null) {
                if (dbf == null) {
                    log.append("New " + Describe.describe(o.getProtein()) + "\n");
                    syncPair(t, o.getProtein(), (Protein)null, syncMode, simulate);
                    changed = true;
                    dbf = o.getProtein();
                } else {
                    if (syncMode == SyncMode.Update || syncMode == SyncMode.Clean) {
                        if (syncPair(t, o.getProtein(), dbf, syncMode, true)) {
                            throw new ImportException("Imported " + Describe.describe(o) + " is different, synchronize them first !");
                        }
                    } else
                    if (syncPair(t, o.getProtein(), dbf, syncMode, simulate)) changed = true;
                }
            }
            if (dbo == null) {
                if (dbf != null) {
                    if (!simulate)
                        o.setProtein(dbf);
                }
            } else {
                if (dbf != dbo.getProtein()) {
                    if (!simulate)
                        dbo.setProtein(dbf);
                    log.append(Describe.describe(o) + ": changed protein\n");
                    changed = true;
                }
            }
        }
        if (dbo != null) {
            if (!equals(dbo.getFirstAaPos(), o.getFirstAaPos())){                if (!simulate)
                    dbo.setFirstAaPos(o.getFirstAaPos());
                log.append(Describe.describe(o) + ": changed firstAaPos\n");
                changed = true;
            }
        }
        if (dbo != null) {
            if (!equals(dbo.getLastAaPos(), o.getLastAaPos())){                if (!simulate)
                    dbo.setLastAaPos(o.getLastAaPos());
                log.append(Describe.describe(o) + ": changed lastAaPos\n");
                changed = true;
            }
        }
        for(AaMutation e : o.getAaMutations()) {
            if (dbo == null) {
                if (syncPair(t, e, (AaMutation)null, syncMode, simulate)) changed = true;
            } else {
                AaMutation dbe = null;
                for(AaMutation f : dbo.getAaMutations()) {
                    if (Equals.isSameAaMutation(e, f)) {
                        dbe = f; break;
                    }
                }
                if (dbe == null && doAdd("AaMutation")) {
                    log.append(Describe.describe(dbo) + ": New " + Describe.describe(e) + "\n");
                    syncPair(t, e, null, syncMode, simulate);
                    changed = true;
                    if (!simulate) {
                        dbo.getAaMutations().add(e);
                        e.getId().setAaSequence(dbo);
                    }
                } else if(doUpdate("AaMutation")){
                    if (syncPair(t, e, dbe, syncMode, simulate)) changed = true;
                }
            }
        }
        if (dbo != null && doDelete("AaMutation")) {
            for(Iterator<AaMutation> i = dbo.getAaMutations().iterator(); i.hasNext();) {
                AaMutation dbe = i.next();
                AaMutation e = null;
                for(AaMutation f : o.getAaMutations()) {
                    if (Equals.isSameAaMutation(dbe, f)) {
                        e = f; break;
                    }
                }
                if (e == null) {
                    log.append(Describe.describe(dbo) + ": Removed " + Describe.describe(dbe) + "\n");
                    changed = true;
                    if (!simulate) {
                        i.remove();
                        t.delete(dbe);
                    }
                }
            }
        }
        for(AaInsertion e : o.getAaInsertions()) {
            if (dbo == null) {
                if (syncPair(t, e, (AaInsertion)null, syncMode, simulate)) changed = true;
            } else {
                AaInsertion dbe = null;
                for(AaInsertion f : dbo.getAaInsertions()) {
                    if (Equals.isSameAaInsertion(e, f)) {
                        dbe = f; break;
                    }
                }
                if (dbe == null && doAdd("AaInsertion")) {
                    log.append(Describe.describe(dbo) + ": New " + Describe.describe(e) + "\n");
                    syncPair(t, e, null, syncMode, simulate);
                    changed = true;
                    if (!simulate) {
                        dbo.getAaInsertions().add(e);
                        e.getId().setAaSequence(dbo);
                    }
                } else if(doUpdate("AaInsertion")){
                    if (syncPair(t, e, dbe, syncMode, simulate)) changed = true;
                }
            }
        }
        if (dbo != null && doDelete("AaInsertion")) {
            for(Iterator<AaInsertion> i = dbo.getAaInsertions().iterator(); i.hasNext();) {
                AaInsertion dbe = i.next();
                AaInsertion e = null;
                for(AaInsertion f : o.getAaInsertions()) {
                    if (Equals.isSameAaInsertion(dbe, f)) {
                        e = f; break;
                    }
                }
                if (e == null) {
                    log.append(Describe.describe(dbo) + ": Removed " + Describe.describe(dbe) + "\n");
                    changed = true;
                    if (!simulate) {
                        i.remove();
                        t.delete(dbe);
                    }
                }
            }
        }
        return changed;
    }

    public boolean syncPair(Transaction t, Event o, Event dbo, SyncMode syncMode, boolean simulate) throws ImportException {
        if (syncedEventSet.contains(o))
            return false;
        else
            syncedEventSet.add(o);
        boolean changed = false;
        if (o == null)
            return changed;
        {
            ValueType dbf = null;
            if (dbo == null) {
                if (o.getValueType() != null)
                    dbf = Retrieve.retrieve(t, o.getValueType());
            } else {
                if (Equals.isSameValueType(o.getValueType(), dbo.getValueType()))
                    dbf = dbo.getValueType();
                else
                    dbf = Retrieve.retrieve(t, o.getValueType());
            }
            if (o.getValueType() != null) {
                if (dbf == null) {
                    log.append("New " + Describe.describe(o.getValueType()) + "\n");
                    syncPair(t, o.getValueType(), (ValueType)null, syncMode, simulate);
                    changed = true;
                    dbf = o.getValueType();
                } else {
                    if (syncMode == SyncMode.Update || syncMode == SyncMode.Clean) {
                        if (syncPair(t, o.getValueType(), dbf, syncMode, true)) {
                            throw new ImportException("Imported " + Describe.describe(o) + " is different, synchronize them first !");
                        }
                    } else
                    if (syncPair(t, o.getValueType(), dbf, syncMode, simulate)) changed = true;
                }
            }
            if (dbo == null) {
                if (dbf != null) {
                    if (!simulate)
                        o.setValueType(dbf);
                }
            } else {
                if (dbf != dbo.getValueType()) {
                    if (!simulate)
                        dbo.setValueType(dbf);
                    log.append(Describe.describe(o) + ": changed valueType\n");
                    changed = true;
                }
            }
        }
        if (dbo != null) {
            if (!equals(dbo.getName(), o.getName())){                if (!simulate)
                    dbo.setName(o.getName());
                log.append(Describe.describe(o) + ": changed name\n");
                changed = true;
            }
        }
        for(EventNominalValue e : o.getEventNominalValues()) {
            if (dbo == null) {
                if (syncPair(t, e, (EventNominalValue)null, syncMode, simulate)) changed = true;
            } else {
                EventNominalValue dbe = null;
                for(EventNominalValue f : dbo.getEventNominalValues()) {
                    if (Equals.isSameEventNominalValue(e, f)) {
                        dbe = f; break;
                    }
                }
                if (dbe == null && doAdd("EventNominalValue")) {
                    log.append(Describe.describe(dbo) + ": New " + Describe.describe(e) + "\n");
                    syncPair(t, e, null, syncMode, simulate);
                    changed = true;
                    if (!simulate) {
                        dbo.getEventNominalValues().add(e);
                        e.setEvent(dbo);
                    }
                } else if(doUpdate("EventNominalValue")){
                    if (syncPair(t, e, dbe, syncMode, simulate)) changed = true;
                }
            }
        }
        if (dbo != null && doDelete("EventNominalValue")) {
            for(Iterator<EventNominalValue> i = dbo.getEventNominalValues().iterator(); i.hasNext();) {
                EventNominalValue dbe = i.next();
                EventNominalValue e = null;
                for(EventNominalValue f : o.getEventNominalValues()) {
                    if (Equals.isSameEventNominalValue(dbe, f)) {
                        e = f; break;
                    }
                }
                if (e == null) {
                    log.append(Describe.describe(dbo) + ": Removed " + Describe.describe(dbe) + "\n");
                    changed = true;
                    if (!simulate) {
                        i.remove();
                        t.delete(dbe);
                    }
                }
            }
        }
        return changed;
    }

    public boolean syncPair(Transaction t, Protein o, Protein dbo, SyncMode syncMode, boolean simulate) throws ImportException {
        if (syncedProteinSet.contains(o))
            return false;
        else
            syncedProteinSet.add(o);
        boolean changed = false;
        if (o == null)
            return changed;
        {
            OpenReadingFrame dbf = null;
            if (dbo == null) {
                if (o.getOpenReadingFrame() != null)
                    dbf = Retrieve.retrieve(t, o.getOpenReadingFrame());
            } else {
                if (Equals.isSameOpenReadingFrame(o.getOpenReadingFrame(), dbo.getOpenReadingFrame()))
                    dbf = dbo.getOpenReadingFrame();
                else
                    dbf = Retrieve.retrieve(t, o.getOpenReadingFrame());
            }
            if (o.getOpenReadingFrame() != null) {
                if (dbf == null) {
                    log.append("New " + Describe.describe(o.getOpenReadingFrame()) + "\n");
                    syncPair(t, o.getOpenReadingFrame(), (OpenReadingFrame)null, syncMode, simulate);
                    changed = true;
                    dbf = o.getOpenReadingFrame();
                } else {
                    if (syncMode == SyncMode.Update || syncMode == SyncMode.Clean) {
                        if (syncPair(t, o.getOpenReadingFrame(), dbf, syncMode, true)) {
                            throw new ImportException("Imported " + Describe.describe(o) + " is different, synchronize them first !");
                        }
                    } else
                    if (syncPair(t, o.getOpenReadingFrame(), dbf, syncMode, simulate)) changed = true;
                }
            }
            if (dbo == null) {
                if (dbf != null) {
                    if (!simulate)
                        o.setOpenReadingFrame(dbf);
                }
            } else {
                if (dbf != dbo.getOpenReadingFrame()) {
                    if (!simulate)
                        dbo.setOpenReadingFrame(dbf);
                    log.append(Describe.describe(o) + ": changed openReadingFrame\n");
                    changed = true;
                }
            }
        }
        if (dbo != null) {
            if (!equals(dbo.getAbbreviation(), o.getAbbreviation())){                if (!simulate)
                    dbo.setAbbreviation(o.getAbbreviation());
                log.append(Describe.describe(o) + ": changed abbreviation\n");
                changed = true;
            }
        }
        return changed;
    }

    public boolean syncPair(Transaction t, OpenReadingFrame o, OpenReadingFrame dbo, SyncMode syncMode, boolean simulate) throws ImportException {
        if (syncedOpenReadingFrameSet.contains(o))
            return false;
        else
            syncedOpenReadingFrameSet.add(o);
        boolean changed = false;
        if (o == null)
            return changed;
        if (dbo != null) {
            if (!equals(dbo.getGenome(), o.getGenome())){                if (!simulate)
                    dbo.setGenome(o.getGenome());
                log.append(Describe.describe(o) + ": changed genome\n");
                changed = true;
            }
        }
        if (dbo != null) {
            if (!equals(dbo.getName(), o.getName())){                if (!simulate)
                    dbo.setName(o.getName());
                log.append(Describe.describe(o) + ": changed name\n");
                changed = true;
            }
        }
        return changed;
    }

    public boolean syncPair(Transaction t, AaMutation o, AaMutation dbo, SyncMode syncMode, boolean simulate) throws ImportException {
        boolean changed = false;
        if (o == null)
            return changed;
        if (dbo != null) {
            if (!equals(dbo.getId().getMutationPosition(), o.getId().getMutationPosition())){                if (!simulate)
                    dbo.getId().setMutationPosition(o.getId().getMutationPosition());
                log.append(Describe.describe(o) + ": changed mutationPosition\n");
                changed = true;
            }
        }
        if (dbo != null) {
            if (!equals(dbo.getAaReference(), o.getAaReference())){                if (!simulate)
                    dbo.setAaReference(o.getAaReference());
                log.append(Describe.describe(o) + ": changed aaReference\n");
                changed = true;
            }
        }
        if (dbo != null) {
            if (!equals(dbo.getAaMutation(), o.getAaMutation())){                if (!simulate)
                    dbo.setAaMutation(o.getAaMutation());
                log.append(Describe.describe(o) + ": changed aaMutation\n");
                changed = true;
            }
        }
        if (dbo != null) {
            if (!equals(dbo.getNtReferenceCodon(), o.getNtReferenceCodon())){                if (!simulate)
                    dbo.setNtReferenceCodon(o.getNtReferenceCodon());
                log.append(Describe.describe(o) + ": changed ntReferenceCodon\n");
                changed = true;
            }
        }
        if (dbo != null) {
            if (!equals(dbo.getNtMutationCodon(), o.getNtMutationCodon())){                if (!simulate)
                    dbo.setNtMutationCodon(o.getNtMutationCodon());
                log.append(Describe.describe(o) + ": changed ntMutationCodon\n");
                changed = true;
            }
        }
        return changed;
    }

    public boolean syncPair(Transaction t, AaInsertion o, AaInsertion dbo, SyncMode syncMode, boolean simulate) throws ImportException {
        boolean changed = false;
        if (o == null)
            return changed;
        if (dbo != null) {
            if (!equals(dbo.getId().getInsertionPosition(), o.getId().getInsertionPosition())){                if (!simulate)
                    dbo.getId().setInsertionPosition(o.getId().getInsertionPosition());
                log.append(Describe.describe(o) + ": changed insertionPosition\n");
                changed = true;
            }
        }
        if (dbo != null) {
            if (!equals(dbo.getId().getInsertionOrder(), o.getId().getInsertionOrder())){                if (!simulate)
                    dbo.getId().setInsertionOrder(o.getId().getInsertionOrder());
                log.append(Describe.describe(o) + ": changed insertionOrder\n");
                changed = true;
            }
        }
        if (dbo != null) {
            if (!equals(dbo.getAaInsertion(), o.getAaInsertion())){                if (!simulate)
                    dbo.setAaInsertion(o.getAaInsertion());
                log.append(Describe.describe(o) + ": changed aaInsertion\n");
                changed = true;
            }
        }
        if (dbo != null) {
            if (!equals(dbo.getNtInsertionCodon(), o.getNtInsertionCodon())){                if (!simulate)
                    dbo.setNtInsertionCodon(o.getNtInsertionCodon());
                log.append(Describe.describe(o) + ": changed ntInsertionCodon\n");
                changed = true;
            }
        }
        return changed;
    }

    public boolean syncPair(Transaction t, Therapy o, Therapy dbo, SyncMode syncMode, boolean simulate) throws ImportException {
        boolean changed = false;
        if (o == null)
            return changed;
        if (dbo != null) {
            if (!equals(dbo.getTherapyMotivation(), o.getTherapyMotivation())){                if (!simulate)
                    dbo.setTherapyMotivation(o.getTherapyMotivation());
                log.append(Describe.describe(o) + ": changed therapyMotivation\n");
                changed = true;
            }
        }
        if (dbo != null) {
            if (!equals(dbo.getStartDate(), o.getStartDate())){                if (!simulate)
                    dbo.setStartDate(o.getStartDate());
                log.append(Describe.describe(o) + ": changed startDate\n");
                changed = true;
            }
        }
        if (dbo != null) {
            if (!equals(dbo.getStopDate(), o.getStopDate())){                if (!simulate)
                    dbo.setStopDate(o.getStopDate());
                log.append(Describe.describe(o) + ": changed stopDate\n");
                changed = true;
            }
        }
        if (dbo != null) {
            if (!equals(dbo.getComment(), o.getComment())){                if (!simulate)
                    dbo.setComment(o.getComment());
                log.append(Describe.describe(o) + ": changed comment\n");
                changed = true;
            }
        }
        for(TherapyCommercial e : o.getTherapyCommercials()) {
            if (dbo == null) {
                if (syncPair(t, e, (TherapyCommercial)null, syncMode, simulate)) changed = true;
            } else {
                TherapyCommercial dbe = null;
                for(TherapyCommercial f : dbo.getTherapyCommercials()) {
                    if (Equals.isSameTherapyCommercial(e, f)) {
                        dbe = f; break;
                    }
                }
                if (dbe == null && doAdd("TherapyCommercial")) {
                    log.append(Describe.describe(dbo) + ": New " + Describe.describe(e) + "\n");
                    syncPair(t, e, null, syncMode, simulate);
                    changed = true;
                    if (!simulate) {
                        dbo.getTherapyCommercials().add(e);
                        e.getId().setTherapy(dbo);
                    }
                } else if(doUpdate("TherapyCommercial")){
                    if (syncPair(t, e, dbe, syncMode, simulate)) changed = true;
                }
            }
        }
        if (dbo != null && doDelete("TherapyCommercial")) {
            for(Iterator<TherapyCommercial> i = dbo.getTherapyCommercials().iterator(); i.hasNext();) {
                TherapyCommercial dbe = i.next();
                TherapyCommercial e = null;
                for(TherapyCommercial f : o.getTherapyCommercials()) {
                    if (Equals.isSameTherapyCommercial(dbe, f)) {
                        e = f; break;
                    }
                }
                if (e == null) {
                    log.append(Describe.describe(dbo) + ": Removed " + Describe.describe(dbe) + "\n");
                    changed = true;
                    if (!simulate) {
                        i.remove();
                        t.delete(dbe);
                    }
                }
            }
        }
        for(TherapyGeneric e : o.getTherapyGenerics()) {
            if (dbo == null) {
                if (syncPair(t, e, (TherapyGeneric)null, syncMode, simulate)) changed = true;
            } else {
                TherapyGeneric dbe = null;
                for(TherapyGeneric f : dbo.getTherapyGenerics()) {
                    if (Equals.isSameTherapyGeneric(e, f)) {
                        dbe = f; break;
                    }
                }
                if (dbe == null && doAdd("TherapyGeneric")) {
                    log.append(Describe.describe(dbo) + ": New " + Describe.describe(e) + "\n");
                    syncPair(t, e, null, syncMode, simulate);
                    changed = true;
                    if (!simulate) {
                        dbo.getTherapyGenerics().add(e);
                        e.getId().setTherapy(dbo);
                    }
                } else if(doUpdate("TherapyGeneric")){
                    if (syncPair(t, e, dbe, syncMode, simulate)) changed = true;
                }
            }
        }
        if (dbo != null && doDelete("TherapyGeneric")) {
            for(Iterator<TherapyGeneric> i = dbo.getTherapyGenerics().iterator(); i.hasNext();) {
                TherapyGeneric dbe = i.next();
                TherapyGeneric e = null;
                for(TherapyGeneric f : o.getTherapyGenerics()) {
                    if (Equals.isSameTherapyGeneric(dbe, f)) {
                        e = f; break;
                    }
                }
                if (e == null) {
                    log.append(Describe.describe(dbo) + ": Removed " + Describe.describe(dbe) + "\n");
                    changed = true;
                    if (!simulate) {
                        i.remove();
                        t.delete(dbe);
                    }
                }
            }
        }
        return changed;
    }

    public boolean syncPair(Transaction t, TherapyCommercial o, TherapyCommercial dbo, SyncMode syncMode, boolean simulate) throws ImportException {
        boolean changed = false;
        if (o == null)
            return changed;
        if (dbo != null) {
            if (!equals(dbo.getId().getDrugCommercial(), o.getId().getDrugCommercial())){                if (!simulate)
                    dbo.getId().setDrugCommercial(o.getId().getDrugCommercial());
                log.append(Describe.describe(o) + ": changed drugCommercial\n");
                changed = true;
            }
        }
        if (dbo != null) {
            if (!equals(dbo.getDayDosageUnits(), o.getDayDosageUnits())){                if (!simulate)
                    dbo.setDayDosageUnits(o.getDayDosageUnits());
                log.append(Describe.describe(o) + ": changed dayDosageUnits\n");
                changed = true;
            }
        }
        if (dbo != null) {
            if (!equals(dbo.isPlacebo(), o.isPlacebo())){                if (!simulate)
                    dbo.setPlacebo(o.isPlacebo());
                log.append(Describe.describe(o) + ": changed placebo\n");
                changed = true;
            }
        }
        if (dbo != null) {
            if (!equals(dbo.isBlind(), o.isBlind())){                if (!simulate)
                    dbo.setBlind(o.isBlind());
                log.append(Describe.describe(o) + ": changed blind\n");
                changed = true;
            }
        }
        if (dbo != null) {
            if (!equals(dbo.getFrequency(), o.getFrequency())){                if (!simulate)
                    dbo.setFrequency(o.getFrequency());
                log.append(Describe.describe(o) + ": changed frequency\n");
                changed = true;
            }
        }
        return changed;
    }

    public boolean syncPair(Transaction t, TherapyGeneric o, TherapyGeneric dbo, SyncMode syncMode, boolean simulate) throws ImportException {
        boolean changed = false;
        if (o == null)
            return changed;
        if (dbo != null) {
            if (!equals(dbo.getId().getDrugGeneric(), o.getId().getDrugGeneric())){                if (!simulate)
                    dbo.getId().setDrugGeneric(o.getId().getDrugGeneric());
                log.append(Describe.describe(o) + ": changed drugGeneric\n");
                changed = true;
            }
        }
        if (dbo != null) {
            if (!equals(dbo.getDayDosageMg(), o.getDayDosageMg())){                if (!simulate)
                    dbo.setDayDosageMg(o.getDayDosageMg());
                log.append(Describe.describe(o) + ": changed dayDosageMg\n");
                changed = true;
            }
        }
        if (dbo != null) {
            if (!equals(dbo.isPlacebo(), o.isPlacebo())){                if (!simulate)
                    dbo.setPlacebo(o.isPlacebo());
                log.append(Describe.describe(o) + ": changed placebo\n");
                changed = true;
            }
        }
        if (dbo != null) {
            if (!equals(dbo.isBlind(), o.isBlind())){                if (!simulate)
                    dbo.setBlind(o.isBlind());
                log.append(Describe.describe(o) + ": changed blind\n");
                changed = true;
            }
        }
        if (dbo != null) {
            if (!equals(dbo.getFrequency(), o.getFrequency())){                if (!simulate)
                    dbo.setFrequency(o.getFrequency());
                log.append(Describe.describe(o) + ": changed frequency\n");
                changed = true;
            }
        }
        return changed;
    }

    public boolean syncPair(Transaction t, ValueType o, ValueType dbo, SyncMode syncMode, boolean simulate) throws ImportException {
        if (syncedValueTypeSet.contains(o))
            return false;
        else
            syncedValueTypeSet.add(o);
        boolean changed = false;
        if (o == null)
            return changed;
        if (dbo != null) {
            if (!equals(dbo.getDescription(), o.getDescription())){                if (!simulate)
                    dbo.setDescription(o.getDescription());
                log.append(Describe.describe(o) + ": changed description\n");
                changed = true;
            }
        }
        if (dbo != null) {
            if (!equals(dbo.getMinimum(), o.getMinimum())){                if (!simulate)
                    dbo.setMinimum(o.getMinimum());
                log.append(Describe.describe(o) + ": changed minimum\n");
                changed = true;
            }
        }
        if (dbo != null) {
            if (!equals(dbo.getMaximum(), o.getMaximum())){                if (!simulate)
                    dbo.setMaximum(o.getMaximum());
                log.append(Describe.describe(o) + ": changed maximum\n");
                changed = true;
            }
        }
        if (dbo != null) {
            if (!equals(dbo.getMultiple(), o.getMultiple())){                if (!simulate)
                    dbo.setMultiple(o.getMultiple());
                log.append(Describe.describe(o) + ": changed multiple\n");
                changed = true;
            }
        }
        return changed;
    }

    public boolean syncPair(Transaction t, EventNominalValue o, EventNominalValue dbo, SyncMode syncMode, boolean simulate) throws ImportException {
        if (syncedEventNominalValueSet.contains(o))
            return false;
        else
            syncedEventNominalValueSet.add(o);
        boolean changed = false;
        if (o == null)
            return changed;
        if (dbo != null) {
            if (!equals(dbo.getValue(), o.getValue())){                if (!simulate)
                    dbo.setValue(o.getValue());
                log.append(Describe.describe(o) + ": changed value\n");
                changed = true;
            }
        }
        return changed;
    }

    public Patient sync(Transaction t, Patient o, SyncMode mode, boolean simulate) throws ImportException {
        Patient dbo = Retrieve.retrieve(t, o);
        if (dbo != null) {
            if (mode == SyncMode.Clean || mode == SyncMode.CleanBase)
                throw new ImportException(Describe.describe(o) + " already exists");
            log.append("Synchronizing " + Describe.describe(o) + "\n");
            syncPair(t, o, dbo, mode, simulate);
            if (!simulate)
                t.update(dbo);
            return dbo;
        } else {
            log.append("New " + Describe.describe(o) + "\n");
            syncPair(t, o, (Patient)null, mode, simulate);
            if (!simulate)
                t.save(o);
            return o;
        }
    }

    public Attribute sync(Transaction t, Attribute o, SyncMode mode, boolean simulate) throws ImportException {
        Attribute dbo = Retrieve.retrieve(t, o);
        if (dbo != null) {
            if (mode == SyncMode.Clean || mode == SyncMode.CleanBase)
                throw new ImportException(Describe.describe(o) + " already exists");
            log.append("Synchronizing " + Describe.describe(o) + "\n");
            syncPair(t, o, dbo, mode, simulate);
            if (!simulate)
                t.update(dbo);
            return dbo;
        } else {
            log.append("New " + Describe.describe(o) + "\n");
            syncPair(t, o, (Attribute)null, mode, simulate);
            if (!simulate)
                t.save(o);
            return o;
        }
    }

    public Test sync(Transaction t, Test o, SyncMode mode, boolean simulate) throws ImportException {
        Test dbo = Retrieve.retrieve(t, o);
        if (dbo != null) {
            if (mode == SyncMode.Clean || mode == SyncMode.CleanBase)
                throw new ImportException(Describe.describe(o) + " already exists");
            log.append("Synchronizing " + Describe.describe(o) + "\n");
            syncPair(t, o, dbo, mode, simulate);
            if (!simulate)
                t.update(dbo);
            return dbo;
        } else {
            log.append("New " + Describe.describe(o) + "\n");
            syncPair(t, o, (Test)null, mode, simulate);
            if (!simulate)
                t.save(o);
            return o;
        }
    }

    public TestType sync(Transaction t, TestType o, SyncMode mode, boolean simulate) throws ImportException {
        TestType dbo = Retrieve.retrieve(t, o);
        if (dbo != null) {
            if (mode == SyncMode.Clean || mode == SyncMode.CleanBase)
                throw new ImportException(Describe.describe(o) + " already exists");
            log.append("Synchronizing " + Describe.describe(o) + "\n");
            syncPair(t, o, dbo, mode, simulate);
            if (!simulate)
                t.update(dbo);
            return dbo;
        } else {
            log.append("New " + Describe.describe(o) + "\n");
            syncPair(t, o, (TestType)null, mode, simulate);
            if (!simulate)
                t.save(o);
            return o;
        }
    }

    public Event sync(Transaction t, Event o, SyncMode mode, boolean simulate) throws ImportException {
        Event dbo = Retrieve.retrieve(t, o);
        if (dbo != null) {
            if (mode == SyncMode.Clean || mode == SyncMode.CleanBase)
                throw new ImportException(Describe.describe(o) + " already exists");
            log.append("Synchronizing " + Describe.describe(o) + "\n");
            syncPair(t, o, dbo, mode, simulate);
            if (!simulate)
                t.update(dbo);
            return dbo;
        } else {
            log.append("New " + Describe.describe(o) + "\n");
            syncPair(t, o, (Event)null, mode, simulate);
            if (!simulate)
                t.save(o);
            return o;
        }
    }

}
