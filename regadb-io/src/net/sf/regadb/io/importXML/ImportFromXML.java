package net.sf.regadb.io.importXML;
import java.util.*;
import net.sf.regadb.db.*;
import net.sf.regadb.db.meta.*;
import org.xml.sax.*;
import org.xml.sax.helpers.XMLReaderFactory;
import java.io.IOException;

public class ImportFromXML extends ImportFromXMLBase {
    enum ParseState { TopLevel, statePatient, stateDataset, stateAttributeNominalValue, stateViralIsolate, stateNtSequence, stateAaSequence, stateAaMutation, stateAaInsertion, stateTherapy, stateTherapyCommercial, stateTherapyGeneric, stateTestResult, stateTest, stateTestType, stateValueType, stateTestObject, stateTestNominalValue, statePatientAttributeValue, stateAttribute };

    public ImportFromXML() {
        parseStateStack.add(ParseState.TopLevel);
    }

    private ArrayList<ParseState> parseStateStack = new ArrayList<ParseState>();

    void pushState(ParseState state) {
        System.err.println("+ " + state.name());
        parseStateStack.add(state);
    }

    void popState() {
        System.err.println("- " + parseStateStack.get(parseStateStack.size() - 1).name());
        parseStateStack.remove(parseStateStack.size() - 1);
    }

    ParseState currentState() {
        return parseStateStack.get(parseStateStack.size() - 1);
    }

    List topLevelObjects = new ArrayList();
    Class topLevelClass = null;

    private Map<String, AttributeNominalValue> refAttributeNominalValueMap = new HashMap<String, AttributeNominalValue>();
    private String referenceAttributeNominalValue = null;
    private Map<String, Test> refTestMap = new HashMap<String, Test>();
    private String referenceTest = null;
    private Map<String, TestType> refTestTypeMap = new HashMap<String, TestType>();
    private String referenceTestType = null;
    private Map<String, ValueType> refValueTypeMap = new HashMap<String, ValueType>();
    private String referenceValueType = null;
    private Map<String, TestObject> refTestObjectMap = new HashMap<String, TestObject>();
    private String referenceTestObject = null;
    private Map<String, TestNominalValue> refTestNominalValueMap = new HashMap<String, TestNominalValue>();
    private String referenceTestNominalValue = null;
    private Map<String, Attribute> refAttributeMap = new HashMap<String, Attribute>();
    private String referenceAttribute = null;
    private String fieldPatient_patientId;
    private String fieldPatient_lastName;
    private String fieldPatient_firstName;
    private Date fieldPatient_birthDate;
    private Date fieldPatient_deathDate;
    private Set<Dataset> fieldPatient_patientDatasets;
    private Set<TestResult> fieldPatient_testResults;
    private Set<PatientAttributeValue> fieldPatient_patientAttributeValues;
    private Set<ViralIsolate> fieldPatient_viralIsolates;
    private Set<Therapy> fieldPatient_therapies;
    private String fieldDataset_description;
    private Date fieldDataset_creationDate;
    private Date fieldDataset_closedDate;
    private Integer fieldDataset_revision;
    private String fieldAttributeNominalValue_value;
    private String fieldViralIsolate_sampleId;
    private Date fieldViralIsolate_sampleDate;
    private Set<NtSequence> fieldViralIsolate_ntSequences;
    private Set<TestResult> fieldViralIsolate_testResults;
    private String fieldNtSequence_nucleotides;
    private String fieldNtSequence_label;
    private Date fieldNtSequence_sequenceDate;
    private Set<AaSequence> fieldNtSequence_aaSequences;
    private Set<TestResult> fieldNtSequence_testResults;
    private Protein fieldAaSequence_protein;
    private short fieldAaSequence_firstAaPos;
    private short fieldAaSequence_lastAaPos;
    private Set<AaMutation> fieldAaSequence_aaMutations;
    private Set<AaInsertion> fieldAaSequence_aaInsertions;
    private short fieldAaMutation_position;
    private String fieldAaMutation_aaReference;
    private String fieldAaMutation_aaMutation;
    private String fieldAaMutation_ntReferenceCodon;
    private String fieldAaMutation_ntMutationCodon;
    private short fieldAaInsertion_position;
    private short fieldAaInsertion_insertionOrder;
    private String fieldAaInsertion_aaInsertion;
    private String fieldAaInsertion_ntInsertionCodon;
    private Date fieldTherapy_startDate;
    private Date fieldTherapy_stopDate;
    private String fieldTherapy_comment;
    private Set<TherapyCommercial> fieldTherapy_therapyCommercials;
    private Set<TherapyGeneric> fieldTherapy_therapyGenerics;
    private DrugCommercial fieldTherapyCommercial_drugCommercial;
    private Double fieldTherapyCommercial_dayDosageUnits;
    private DrugGeneric fieldTherapyGeneric_drugGeneric;
    private Double fieldTherapyGeneric_dayDosageMg;
    private Test fieldTestResult_test;
    private DrugGeneric fieldTestResult_drugGeneric;
    private TestNominalValue fieldTestResult_testNominalValue;
    private String fieldTestResult_value;
    private Date fieldTestResult_testDate;
    private String fieldTestResult_sampleId;
    private TestType fieldTest_testType;
    private String fieldTest_description;
    private String fieldTest_serviceClass;
    private String fieldTest_serviceData;
    private String fieldTest_serviceConfig;
    private ValueType fieldTestType_valueType;
    private TestObject fieldTestType_testObject;
    private String fieldTestType_description;
    private Set<TestNominalValue> fieldTestType_testNominalValues;
    private String fieldValueType_description;
    private Double fieldValueType_min;
    private Double fieldValueType_max;
    private Boolean fieldValueType_multiple;
    private String fieldTestObject_description;
    private Integer fieldTestObject_testObjectId;
    private TestType fieldTestNominalValue_testType;
    private String fieldTestNominalValue_value;
    private Attribute fieldPatientAttributeValue_attribute;
    private AttributeNominalValue fieldPatientAttributeValue_attributeNominalValue;
    private String fieldPatientAttributeValue_value;
    private ValueType fieldAttribute_valueType;
    private String fieldAttribute_name;
    private Set<AttributeNominalValue> fieldAttribute_attributeNominalValues;

    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        value = null;
        if (false) {
        } else if ("patient".equals(qName)|| "patients-el".equals(qName)) {
            pushState(ParseState.statePatient);
            patient = new Patient();
            fieldPatient_patientId = nullValueString();
            fieldPatient_lastName = nullValueString();
            fieldPatient_firstName = nullValueString();
            fieldPatient_birthDate = nullValueDate();
            fieldPatient_deathDate = nullValueDate();
            fieldPatient_patientDatasets = new HashSet<Dataset>();
            fieldPatient_testResults = new HashSet<TestResult>();
            fieldPatient_patientAttributeValues = new HashSet<PatientAttributeValue>();
            fieldPatient_viralIsolates = new HashSet<ViralIsolate>();
            fieldPatient_therapies = new HashSet<Therapy>();
        } else if ("dataset".equals(qName)|| "patientDatasets-el".equals(qName)) {
            pushState(ParseState.stateDataset);
            fieldDataset_description = nullValueString();
            fieldDataset_creationDate = nullValueDate();
            fieldDataset_closedDate = nullValueDate();
            fieldDataset_revision = nullValueInteger();
        } else if ("attributeNominalValue".equals(qName)|| "attributeNominalValue".equals(qName)|| "attributeNominalValues-el".equals(qName)) {
            pushState(ParseState.stateAttributeNominalValue);
            referenceAttributeNominalValue = null;
            fieldAttributeNominalValue_value = nullValueString();
        } else if ("viralIsolate".equals(qName)|| "viralIsolates-el".equals(qName)) {
            pushState(ParseState.stateViralIsolate);
            fieldViralIsolate_sampleId = nullValueString();
            fieldViralIsolate_sampleDate = nullValueDate();
            fieldViralIsolate_ntSequences = new HashSet<NtSequence>();
            fieldViralIsolate_testResults = new HashSet<TestResult>();
        } else if ("ntSequence".equals(qName)|| "ntSequences-el".equals(qName)) {
            pushState(ParseState.stateNtSequence);
            fieldNtSequence_nucleotides = nullValueString();
            fieldNtSequence_label = nullValueString();
            fieldNtSequence_sequenceDate = nullValueDate();
            fieldNtSequence_aaSequences = new HashSet<AaSequence>();
            fieldNtSequence_testResults = new HashSet<TestResult>();
        } else if ("aaSequence".equals(qName)|| "aaSequences-el".equals(qName)) {
            pushState(ParseState.stateAaSequence);
            fieldAaSequence_protein = null;
            fieldAaSequence_firstAaPos = nullValueshort();
            fieldAaSequence_lastAaPos = nullValueshort();
            fieldAaSequence_aaMutations = new HashSet<AaMutation>();
            fieldAaSequence_aaInsertions = new HashSet<AaInsertion>();
        } else if ("aaMutation".equals(qName)|| "aaMutations-el".equals(qName)) {
            pushState(ParseState.stateAaMutation);
            fieldAaMutation_position = nullValueshort();
            fieldAaMutation_aaReference = nullValueString();
            fieldAaMutation_aaMutation = nullValueString();
            fieldAaMutation_ntReferenceCodon = nullValueString();
            fieldAaMutation_ntMutationCodon = nullValueString();
        } else if ("aaInsertion".equals(qName)|| "aaInsertions-el".equals(qName)) {
            pushState(ParseState.stateAaInsertion);
            fieldAaInsertion_position = nullValueshort();
            fieldAaInsertion_insertionOrder = nullValueshort();
            fieldAaInsertion_aaInsertion = nullValueString();
            fieldAaInsertion_ntInsertionCodon = nullValueString();
        } else if ("therapy".equals(qName)|| "therapies-el".equals(qName)) {
            pushState(ParseState.stateTherapy);
            fieldTherapy_startDate = nullValueDate();
            fieldTherapy_stopDate = nullValueDate();
            fieldTherapy_comment = nullValueString();
            fieldTherapy_therapyCommercials = new HashSet<TherapyCommercial>();
            fieldTherapy_therapyGenerics = new HashSet<TherapyGeneric>();
        } else if ("therapyCommercial".equals(qName)|| "therapyCommercials-el".equals(qName)) {
            pushState(ParseState.stateTherapyCommercial);
            fieldTherapyCommercial_drugCommercial = null;
            fieldTherapyCommercial_dayDosageUnits = nullValueDouble();
        } else if ("therapyGeneric".equals(qName)|| "therapyGenerics-el".equals(qName)) {
            pushState(ParseState.stateTherapyGeneric);
            fieldTherapyGeneric_drugGeneric = null;
            fieldTherapyGeneric_dayDosageMg = nullValueDouble();
        } else if ("testResult".equals(qName)|| "testResults-el".equals(qName)|| "testResults-el".equals(qName)|| "testResults-el".equals(qName)) {
            pushState(ParseState.stateTestResult);
            fieldTestResult_test = null;
            fieldTestResult_drugGeneric = null;
            fieldTestResult_testNominalValue = null;
            fieldTestResult_value = nullValueString();
            fieldTestResult_testDate = nullValueDate();
            fieldTestResult_sampleId = nullValueString();
        } else if ("test".equals(qName)|| "test".equals(qName)) {
            pushState(ParseState.stateTest);
            referenceTest = null;
            fieldTest_testType = null;
            fieldTest_description = nullValueString();
            fieldTest_serviceClass = nullValueString();
            fieldTest_serviceData = nullValueString();
            fieldTest_serviceConfig = nullValueString();
        } else if ("testType".equals(qName)|| "testType".equals(qName)|| "testType".equals(qName)) {
            pushState(ParseState.stateTestType);
            referenceTestType = null;
            fieldTestType_valueType = null;
            fieldTestType_testObject = null;
            fieldTestType_description = nullValueString();
            fieldTestType_testNominalValues = new HashSet<TestNominalValue>();
        } else if ("valueType".equals(qName)|| "valueType".equals(qName)|| "valueType".equals(qName)) {
            pushState(ParseState.stateValueType);
            referenceValueType = null;
            fieldValueType_description = nullValueString();
            fieldValueType_min = nullValueDouble();
            fieldValueType_max = nullValueDouble();
            fieldValueType_multiple = nullValueBoolean();
        } else if ("testObject".equals(qName)|| "testObject".equals(qName)) {
            pushState(ParseState.stateTestObject);
            referenceTestObject = null;
            fieldTestObject_description = nullValueString();
            fieldTestObject_testObjectId = nullValueInteger();
        } else if ("testNominalValue".equals(qName)|| "testNominalValue".equals(qName)|| "testNominalValues-el".equals(qName)) {
            pushState(ParseState.stateTestNominalValue);
            referenceTestNominalValue = null;
            fieldTestNominalValue_testType = null;
            fieldTestNominalValue_value = nullValueString();
        } else if ("patientAttributeValue".equals(qName)|| "patientAttributeValues-el".equals(qName)) {
            pushState(ParseState.statePatientAttributeValue);
            fieldPatientAttributeValue_attribute = null;
            fieldPatientAttributeValue_attributeNominalValue = null;
            fieldPatientAttributeValue_value = nullValueString();
        } else if ("attribute".equals(qName)|| "attribute".equals(qName)) {
            pushState(ParseState.stateAttribute);
            referenceAttribute = null;
            fieldAttribute_valueType = null;
            fieldAttribute_name = nullValueString();
            fieldAttribute_attributeNominalValues = new HashSet<AttributeNominalValue>();
        }
    }

    @SuppressWarnings("unchecked")
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (false) {
        } else if (currentState() == ParseState.statePatient) {
            if ("patient".equals(qName)|| "patients-el".equals(qName)) {
                popState();
                Patient elPatient = null;
                if (currentState() == ParseState.TopLevel) {
                    if (topLevelClass == Patient.class) {
                        elPatient = patient;
                        topLevelObjects.add(elPatient);
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
                    elPatient.setLastName(fieldPatient_lastName);
                }
                {
                    elPatient.setFirstName(fieldPatient_firstName);
                }
                {
                    elPatient.setBirthDate(fieldPatient_birthDate);
                }
                {
                    elPatient.setDeathDate(fieldPatient_deathDate);
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
                importPatient(patient);
            } else if ("patientId".equals(qName)) {
                fieldPatient_patientId = parseString(value);
            } else if ("lastName".equals(qName)) {
                fieldPatient_lastName = parseString(value);
            } else if ("firstName".equals(qName)) {
                fieldPatient_firstName = parseString(value);
            } else if ("birthDate".equals(qName)) {
                fieldPatient_birthDate = parseDate(value);
            } else if ("deathDate".equals(qName)) {
                fieldPatient_deathDate = parseDate(value);
            } else if ("patientDatasets".equals(qName)) {
            } else if ("testResults".equals(qName)) {
            } else if ("patientAttributeValues".equals(qName)) {
            } else if ("viralIsolates".equals(qName)) {
            } else if ("therapies".equals(qName)) {
            } else {
                //throw new SAXException(new ImportException("Unrecognized element: " + qName));
                System.err.println("Unrecognized element: " + qName);
            }
        } else if (currentState() == ParseState.stateDataset) {
            if ("dataset".equals(qName)|| "patientDatasets-el".equals(qName)) {
                popState();
                Dataset elDataset = null;
                if (currentState() == ParseState.TopLevel) {
                    if (topLevelClass == Dataset.class) {
                        elDataset = new Dataset();
                        topLevelObjects.add(elDataset);
                    } else {
                        throw new SAXException(new ImportException("Unexpected top level object: " + qName));
                    }
                } else if (currentState() == ParseState.statePatient) {
                    elDataset = null; // FIXME
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
            } else if ("description".equals(qName)) {
                fieldDataset_description = parseString(value);
            } else if ("creationDate".equals(qName)) {
                fieldDataset_creationDate = parseDate(value);
            } else if ("closedDate".equals(qName)) {
                fieldDataset_closedDate = parseDate(value);
            } else if ("revision".equals(qName)) {
                fieldDataset_revision = parseInteger(value);
            } else {
                //throw new SAXException(new ImportException("Unrecognized element: " + qName));
                System.err.println("Unrecognized element: " + qName);
            }
        } else if (currentState() == ParseState.stateAttributeNominalValue) {
            if ("attributeNominalValue".equals(qName)|| "attributeNominalValue".equals(qName)|| "attributeNominalValues-el".equals(qName)) {
                popState();
                AttributeNominalValue elAttributeNominalValue = null;
                boolean referenceResolved = false;
                if (currentState() == ParseState.TopLevel) {
                    if (topLevelClass == AttributeNominalValue.class) {
                        elAttributeNominalValue = new AttributeNominalValue();
                        topLevelObjects.add(elAttributeNominalValue);
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
            } else if ("value".equals(qName)) {
                fieldAttributeNominalValue_value = parseString(value);
            } else if ("reference".equals(qName)) {
                referenceAttributeNominalValue = value;
            } else {
                //throw new SAXException(new ImportException("Unrecognized element: " + qName));
                System.err.println("Unrecognized element: " + qName);
            }
        } else if (currentState() == ParseState.stateViralIsolate) {
            if ("viralIsolate".equals(qName)|| "viralIsolates-el".equals(qName)) {
                popState();
                ViralIsolate elViralIsolate = null;
                if (currentState() == ParseState.TopLevel) {
                    if (topLevelClass == ViralIsolate.class) {
                        elViralIsolate = new ViralIsolate();
                        topLevelObjects.add(elViralIsolate);
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
                    elViralIsolate.setNtSequences(fieldViralIsolate_ntSequences);
                    for (NtSequence o : fieldViralIsolate_ntSequences)
                        o.setViralIsolate(elViralIsolate);
                }
                {
                    elViralIsolate.setTestResults(fieldViralIsolate_testResults);
                    for (TestResult o : fieldViralIsolate_testResults)
                        o.setViralIsolate(elViralIsolate);
                }
            } else if ("sampleId".equals(qName)) {
                fieldViralIsolate_sampleId = parseString(value);
            } else if ("sampleDate".equals(qName)) {
                fieldViralIsolate_sampleDate = parseDate(value);
            } else if ("ntSequences".equals(qName)) {
            } else if ("testResults".equals(qName)) {
            } else {
                //throw new SAXException(new ImportException("Unrecognized element: " + qName));
                System.err.println("Unrecognized element: " + qName);
            }
        } else if (currentState() == ParseState.stateNtSequence) {
            if ("ntSequence".equals(qName)|| "ntSequences-el".equals(qName)) {
                popState();
                NtSequence elNtSequence = null;
                if (currentState() == ParseState.TopLevel) {
                    if (topLevelClass == NtSequence.class) {
                        elNtSequence = new NtSequence();
                        topLevelObjects.add(elNtSequence);
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
                    elNtSequence.setNucleotides(fieldNtSequence_nucleotides);
                }
                {
                    elNtSequence.setLabel(fieldNtSequence_label);
                }
                {
                    elNtSequence.setSequenceDate(fieldNtSequence_sequenceDate);
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
            } else if ("nucleotides".equals(qName)) {
                fieldNtSequence_nucleotides = parseString(value);
            } else if ("label".equals(qName)) {
                fieldNtSequence_label = parseString(value);
            } else if ("sequenceDate".equals(qName)) {
                fieldNtSequence_sequenceDate = parseDate(value);
            } else if ("aaSequences".equals(qName)) {
            } else if ("testResults".equals(qName)) {
            } else {
                //throw new SAXException(new ImportException("Unrecognized element: " + qName));
                System.err.println("Unrecognized element: " + qName);
            }
        } else if (currentState() == ParseState.stateAaSequence) {
            if ("aaSequence".equals(qName)|| "aaSequences-el".equals(qName)) {
                popState();
                AaSequence elAaSequence = null;
                if (currentState() == ParseState.TopLevel) {
                    if (topLevelClass == AaSequence.class) {
                        elAaSequence = new AaSequence();
                        topLevelObjects.add(elAaSequence);
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
            } else if ("protein".equals(qName)) {
                fieldAaSequence_protein = resolveProtein(value);
            } else if ("firstAaPos".equals(qName)) {
                fieldAaSequence_firstAaPos = parseshort(value);
            } else if ("lastAaPos".equals(qName)) {
                fieldAaSequence_lastAaPos = parseshort(value);
            } else if ("aaMutations".equals(qName)) {
            } else if ("aaInsertions".equals(qName)) {
            } else {
                //throw new SAXException(new ImportException("Unrecognized element: " + qName));
                System.err.println("Unrecognized element: " + qName);
            }
        } else if (currentState() == ParseState.stateAaMutation) {
            if ("aaMutation".equals(qName)|| "aaMutations-el".equals(qName)) {
                popState();
                AaMutation elAaMutation = null;
                if (currentState() == ParseState.TopLevel) {
                    if (topLevelClass == AaMutation.class) {
                        elAaMutation = new AaMutation();
                        elAaMutation.setId(new AaMutationId());
                        topLevelObjects.add(elAaMutation);
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
                    elAaMutation.getId().setPosition(fieldAaMutation_position);
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
            } else if ("position".equals(qName)) {
                fieldAaMutation_position = parseshort(value);
            } else if ("aaReference".equals(qName)) {
                fieldAaMutation_aaReference = parseString(value);
            } else if ("aaMutation".equals(qName)) {
                fieldAaMutation_aaMutation = parseString(value);
            } else if ("ntReferenceCodon".equals(qName)) {
                fieldAaMutation_ntReferenceCodon = parseString(value);
            } else if ("ntMutationCodon".equals(qName)) {
                fieldAaMutation_ntMutationCodon = parseString(value);
            } else {
                //throw new SAXException(new ImportException("Unrecognized element: " + qName));
                System.err.println("Unrecognized element: " + qName);
            }
        } else if (currentState() == ParseState.stateAaInsertion) {
            if ("aaInsertion".equals(qName)|| "aaInsertions-el".equals(qName)) {
                popState();
                AaInsertion elAaInsertion = null;
                if (currentState() == ParseState.TopLevel) {
                    if (topLevelClass == AaInsertion.class) {
                        elAaInsertion = new AaInsertion();
                        elAaInsertion.setId(new AaInsertionId());
                        topLevelObjects.add(elAaInsertion);
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
                    elAaInsertion.getId().setPosition(fieldAaInsertion_position);
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
            } else if ("position".equals(qName)) {
                fieldAaInsertion_position = parseshort(value);
            } else if ("insertionOrder".equals(qName)) {
                fieldAaInsertion_insertionOrder = parseshort(value);
            } else if ("aaInsertion".equals(qName)) {
                fieldAaInsertion_aaInsertion = parseString(value);
            } else if ("ntInsertionCodon".equals(qName)) {
                fieldAaInsertion_ntInsertionCodon = parseString(value);
            } else {
                //throw new SAXException(new ImportException("Unrecognized element: " + qName));
                System.err.println("Unrecognized element: " + qName);
            }
        } else if (currentState() == ParseState.stateTherapy) {
            if ("therapy".equals(qName)|| "therapies-el".equals(qName)) {
                popState();
                Therapy elTherapy = null;
                if (currentState() == ParseState.TopLevel) {
                    if (topLevelClass == Therapy.class) {
                        elTherapy = new Therapy();
                        topLevelObjects.add(elTherapy);
                    } else {
                        throw new SAXException(new ImportException("Unexpected top level object: " + qName));
                    }
                } else if (currentState() == ParseState.statePatient) {
                    elTherapy = patient.createTherapy(fieldTherapy_startDate);
                } else {
                    throw new SAXException(new ImportException("Nested object problem: " + qName));
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
            } else if ("startDate".equals(qName)) {
                fieldTherapy_startDate = parseDate(value);
            } else if ("stopDate".equals(qName)) {
                fieldTherapy_stopDate = parseDate(value);
            } else if ("comment".equals(qName)) {
                fieldTherapy_comment = parseString(value);
            } else if ("therapyCommercials".equals(qName)) {
            } else if ("therapyGenerics".equals(qName)) {
            } else {
                //throw new SAXException(new ImportException("Unrecognized element: " + qName));
                System.err.println("Unrecognized element: " + qName);
            }
        } else if (currentState() == ParseState.stateTherapyCommercial) {
            if ("therapyCommercial".equals(qName)|| "therapyCommercials-el".equals(qName)) {
                popState();
                TherapyCommercial elTherapyCommercial = null;
                if (currentState() == ParseState.TopLevel) {
                    if (topLevelClass == TherapyCommercial.class) {
                        elTherapyCommercial = new TherapyCommercial();
                        elTherapyCommercial.setId(new TherapyCommercialId());
                        topLevelObjects.add(elTherapyCommercial);
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
            } else if ("drugCommercial".equals(qName)) {
                fieldTherapyCommercial_drugCommercial = resolveDrugCommercial(value);
            } else if ("dayDosageUnits".equals(qName)) {
                fieldTherapyCommercial_dayDosageUnits = parseDouble(value);
            } else {
                //throw new SAXException(new ImportException("Unrecognized element: " + qName));
                System.err.println("Unrecognized element: " + qName);
            }
        } else if (currentState() == ParseState.stateTherapyGeneric) {
            if ("therapyGeneric".equals(qName)|| "therapyGenerics-el".equals(qName)) {
                popState();
                TherapyGeneric elTherapyGeneric = null;
                if (currentState() == ParseState.TopLevel) {
                    if (topLevelClass == TherapyGeneric.class) {
                        elTherapyGeneric = new TherapyGeneric();
                        elTherapyGeneric.setId(new TherapyGenericId());
                        topLevelObjects.add(elTherapyGeneric);
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
            } else if ("drugGeneric".equals(qName)) {
                fieldTherapyGeneric_drugGeneric = resolveDrugGeneric(value);
            } else if ("dayDosageMg".equals(qName)) {
                fieldTherapyGeneric_dayDosageMg = parseDouble(value);
            } else {
                //throw new SAXException(new ImportException("Unrecognized element: " + qName));
                System.err.println("Unrecognized element: " + qName);
            }
        } else if (currentState() == ParseState.stateTestResult) {
            if ("testResult".equals(qName)|| "testResults-el".equals(qName)|| "testResults-el".equals(qName)|| "testResults-el".equals(qName)) {
                popState();
                TestResult elTestResult = null;
                if (currentState() == ParseState.TopLevel) {
                    if (topLevelClass == TestResult.class) {
                        elTestResult = new TestResult();
                        topLevelObjects.add(elTestResult);
                    } else {
                        throw new SAXException(new ImportException("Unexpected top level object: " + qName));
                    }
                } else if (currentState() == ParseState.statePatient) {
                    elTestResult = patient.createTestResult(fieldTestResult_test);
                    fieldPatient_testResults.add(elTestResult);
                } else if (currentState() == ParseState.stateViralIsolate) {
                    elTestResult = patient.createTestResult(fieldTestResult_test);
                    fieldViralIsolate_testResults.add(elTestResult);
                } else if (currentState() == ParseState.stateNtSequence) {
                    elTestResult = patient.createTestResult(fieldTestResult_test);
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
            } else if ("test".equals(qName)) {
            } else if ("drugGeneric".equals(qName)) {
                fieldTestResult_drugGeneric = resolveDrugGeneric(value);
            } else if ("testNominalValue".equals(qName)) {
            } else if ("value".equals(qName)) {
                fieldTestResult_value = parseString(value);
            } else if ("testDate".equals(qName)) {
                fieldTestResult_testDate = parseDate(value);
            } else if ("sampleId".equals(qName)) {
                fieldTestResult_sampleId = parseString(value);
            } else {
                //throw new SAXException(new ImportException("Unrecognized element: " + qName));
                System.err.println("Unrecognized element: " + qName);
            }
        } else if (currentState() == ParseState.stateTest) {
            if ("test".equals(qName)|| "test".equals(qName)) {
                popState();
                Test elTest = null;
                boolean referenceResolved = false;
                if (currentState() == ParseState.TopLevel) {
                    if (topLevelClass == Test.class) {
                        elTest = new Test();
                        topLevelObjects.add(elTest);
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
                if (referenceResolved && fieldTest_serviceClass != nullValueString())
                    throw new SAXException(new ImportException("Cannot modify resolved reference"));
                if (!referenceResolved) {
                    elTest.setServiceClass(fieldTest_serviceClass);
                }
                if (referenceResolved && fieldTest_serviceData != nullValueString())
                    throw new SAXException(new ImportException("Cannot modify resolved reference"));
                if (!referenceResolved) {
                    elTest.setServiceData(fieldTest_serviceData);
                }
                if (referenceResolved && fieldTest_serviceConfig != nullValueString())
                    throw new SAXException(new ImportException("Cannot modify resolved reference"));
                if (!referenceResolved) {
                    elTest.setServiceConfig(fieldTest_serviceConfig);
                }
            } else if ("testType".equals(qName)) {
            } else if ("description".equals(qName)) {
                fieldTest_description = parseString(value);
            } else if ("serviceClass".equals(qName)) {
                fieldTest_serviceClass = parseString(value);
            } else if ("serviceData".equals(qName)) {
                fieldTest_serviceData = parseString(value);
            } else if ("serviceConfig".equals(qName)) {
                fieldTest_serviceConfig = parseString(value);
            } else if ("reference".equals(qName)) {
                referenceTest = value;
            } else {
                //throw new SAXException(new ImportException("Unrecognized element: " + qName));
                System.err.println("Unrecognized element: " + qName);
            }
        } else if (currentState() == ParseState.stateTestType) {
            if ("testType".equals(qName)|| "testType".equals(qName)|| "testType".equals(qName)) {
                popState();
                TestType elTestType = null;
                boolean referenceResolved = false;
                if (currentState() == ParseState.TopLevel) {
                    if (topLevelClass == TestType.class) {
                        elTestType = new TestType();
                        topLevelObjects.add(elTestType);
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
                } else if (currentState() == ParseState.stateTestNominalValue) {
                    if (referenceTestType != null) { 
                        elTestType = refTestTypeMap.get(referenceTestType);
                        referenceResolved = elTestType != null;
                    }
                    if (!referenceResolved) {
                        elTestType = new TestType();
                        if (referenceTestType!= null)
                            refTestTypeMap.put(referenceTestType, elTestType);
                    }
                    fieldTestNominalValue_testType = elTestType;
                } else {
                    throw new SAXException(new ImportException("Nested object problem: " + qName));
                }
                if (referenceResolved && fieldTestType_valueType != null)
                    throw new SAXException(new ImportException("Cannot modify resolved reference"));
                if (!referenceResolved) {
                    elTestType.setValueType(fieldTestType_valueType);
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
            } else if ("valueType".equals(qName)) {
            } else if ("testObject".equals(qName)) {
            } else if ("description".equals(qName)) {
                fieldTestType_description = parseString(value);
            } else if ("testNominalValues".equals(qName)) {
            } else if ("reference".equals(qName)) {
                referenceTestType = value;
            } else {
                //throw new SAXException(new ImportException("Unrecognized element: " + qName));
                System.err.println("Unrecognized element: " + qName);
            }
        } else if (currentState() == ParseState.stateValueType) {
            if ("valueType".equals(qName)|| "valueType".equals(qName)|| "valueType".equals(qName)) {
                popState();
                ValueType elValueType = null;
                boolean referenceResolved = false;
                if (currentState() == ParseState.TopLevel) {
                    if (topLevelClass == ValueType.class) {
                        elValueType = new ValueType();
                        topLevelObjects.add(elValueType);
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
                } else {
                    throw new SAXException(new ImportException("Nested object problem: " + qName));
                }
                if (referenceResolved && fieldValueType_description != nullValueString())
                    throw new SAXException(new ImportException("Cannot modify resolved reference"));
                if (!referenceResolved) {
                    elValueType.setDescription(fieldValueType_description);
                }
                if (referenceResolved && fieldValueType_min != nullValueDouble())
                    throw new SAXException(new ImportException("Cannot modify resolved reference"));
                if (!referenceResolved) {
                    elValueType.setMin(fieldValueType_min);
                }
                if (referenceResolved && fieldValueType_max != nullValueDouble())
                    throw new SAXException(new ImportException("Cannot modify resolved reference"));
                if (!referenceResolved) {
                    elValueType.setMax(fieldValueType_max);
                }
                if (referenceResolved && fieldValueType_multiple != nullValueBoolean())
                    throw new SAXException(new ImportException("Cannot modify resolved reference"));
                if (!referenceResolved) {
                    elValueType.setMultiple(fieldValueType_multiple);
                }
            } else if ("description".equals(qName)) {
                fieldValueType_description = parseString(value);
            } else if ("min".equals(qName)) {
                fieldValueType_min = parseDouble(value);
            } else if ("max".equals(qName)) {
                fieldValueType_max = parseDouble(value);
            } else if ("multiple".equals(qName)) {
                fieldValueType_multiple = parseBoolean(value);
            } else if ("reference".equals(qName)) {
                referenceValueType = value;
            } else {
                //throw new SAXException(new ImportException("Unrecognized element: " + qName));
                System.err.println("Unrecognized element: " + qName);
            }
        } else if (currentState() == ParseState.stateTestObject) {
            if ("testObject".equals(qName)|| "testObject".equals(qName)) {
                popState();
                TestObject elTestObject = null;
                boolean referenceResolved = false;
                if (currentState() == ParseState.TopLevel) {
                    if (topLevelClass == TestObject.class) {
                        elTestObject = new TestObject();
                        topLevelObjects.add(elTestObject);
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
            } else if ("description".equals(qName)) {
                fieldTestObject_description = parseString(value);
            } else if ("testObjectId".equals(qName)) {
                fieldTestObject_testObjectId = parseInteger(value);
            } else if ("reference".equals(qName)) {
                referenceTestObject = value;
            } else {
                //throw new SAXException(new ImportException("Unrecognized element: " + qName));
                System.err.println("Unrecognized element: " + qName);
            }
        } else if (currentState() == ParseState.stateTestNominalValue) {
            if ("testNominalValue".equals(qName)|| "testNominalValue".equals(qName)|| "testNominalValues-el".equals(qName)) {
                popState();
                TestNominalValue elTestNominalValue = null;
                boolean referenceResolved = false;
                if (currentState() == ParseState.TopLevel) {
                    if (topLevelClass == TestNominalValue.class) {
                        elTestNominalValue = new TestNominalValue();
                        topLevelObjects.add(elTestNominalValue);
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
                if (referenceResolved && fieldTestNominalValue_testType != null)
                    throw new SAXException(new ImportException("Cannot modify resolved reference"));
                if (!referenceResolved) {
                    elTestNominalValue.setTestType(fieldTestNominalValue_testType);
                }
                if (referenceResolved && fieldTestNominalValue_value != nullValueString())
                    throw new SAXException(new ImportException("Cannot modify resolved reference"));
                if (!referenceResolved) {
                    elTestNominalValue.setValue(fieldTestNominalValue_value);
                }
            } else if ("testType".equals(qName)) {
            } else if ("value".equals(qName)) {
                fieldTestNominalValue_value = parseString(value);
            } else if ("reference".equals(qName)) {
                referenceTestNominalValue = value;
            } else {
                //throw new SAXException(new ImportException("Unrecognized element: " + qName));
                System.err.println("Unrecognized element: " + qName);
            }
        } else if (currentState() == ParseState.statePatientAttributeValue) {
            if ("patientAttributeValue".equals(qName)|| "patientAttributeValues-el".equals(qName)) {
                popState();
                PatientAttributeValue elPatientAttributeValue = null;
                if (currentState() == ParseState.TopLevel) {
                    if (topLevelClass == PatientAttributeValue.class) {
                        elPatientAttributeValue = new PatientAttributeValue();
                        elPatientAttributeValue.setId(new PatientAttributeValueId());
                        topLevelObjects.add(elPatientAttributeValue);
                    } else {
                        throw new SAXException(new ImportException("Unexpected top level object: " + qName));
                    }
                } else if (currentState() == ParseState.statePatient) {
                    elPatientAttributeValue = patient.createPatientAttributeValue(fieldPatientAttributeValue_attribute);
                } else {
                    throw new SAXException(new ImportException("Nested object problem: " + qName));
                }
                {
                    elPatientAttributeValue.getId().setAttribute(fieldPatientAttributeValue_attribute);
                }
                {
                    elPatientAttributeValue.setAttributeNominalValue(fieldPatientAttributeValue_attributeNominalValue);
                }
                {
                    elPatientAttributeValue.setValue(fieldPatientAttributeValue_value);
                }
            } else if ("attribute".equals(qName)) {
            } else if ("attributeNominalValue".equals(qName)) {
            } else if ("value".equals(qName)) {
                fieldPatientAttributeValue_value = parseString(value);
            } else {
                //throw new SAXException(new ImportException("Unrecognized element: " + qName));
                System.err.println("Unrecognized element: " + qName);
            }
        } else if (currentState() == ParseState.stateAttribute) {
            if ("attribute".equals(qName)|| "attribute".equals(qName)) {
                popState();
                Attribute elAttribute = null;
                boolean referenceResolved = false;
                if (currentState() == ParseState.TopLevel) {
                    if (topLevelClass == Attribute.class) {
                        elAttribute = new Attribute();
                        topLevelObjects.add(elAttribute);
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
            } else if ("valueType".equals(qName)) {
            } else if ("name".equals(qName)) {
                fieldAttribute_name = parseString(value);
            } else if ("attributeNominalValues".equals(qName)) {
            } else if ("reference".equals(qName)) {
                referenceAttribute = value;
            } else {
                //throw new SAXException(new ImportException("Unrecognized element: " + qName));
                System.err.println("Unrecognized element: " + qName);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public List<Patient> readPatients(InputSource source) throws SAXException, IOException {
        topLevelClass = Patient.class;
        parse(source);
        return topLevelObjects;
    }

    @SuppressWarnings("unchecked")
    public List<Dataset> readDatasets(InputSource source) throws SAXException, IOException {
        topLevelClass = Dataset.class;
        parse(source);
        return topLevelObjects;
    }

    @SuppressWarnings("unchecked")
    public List<AttributeNominalValue> readAttributeNominalValues(InputSource source) throws SAXException, IOException {
        topLevelClass = AttributeNominalValue.class;
        parse(source);
        return topLevelObjects;
    }

    @SuppressWarnings("unchecked")
    public List<ViralIsolate> readViralIsolates(InputSource source) throws SAXException, IOException {
        topLevelClass = ViralIsolate.class;
        parse(source);
        return topLevelObjects;
    }

    @SuppressWarnings("unchecked")
    public List<NtSequence> readNtSequences(InputSource source) throws SAXException, IOException {
        topLevelClass = NtSequence.class;
        parse(source);
        return topLevelObjects;
    }

    @SuppressWarnings("unchecked")
    public List<AaSequence> readAaSequences(InputSource source) throws SAXException, IOException {
        topLevelClass = AaSequence.class;
        parse(source);
        return topLevelObjects;
    }

    @SuppressWarnings("unchecked")
    public List<AaMutation> readAaMutations(InputSource source) throws SAXException, IOException {
        topLevelClass = AaMutation.class;
        parse(source);
        return topLevelObjects;
    }

    @SuppressWarnings("unchecked")
    public List<AaInsertion> readAaInsertions(InputSource source) throws SAXException, IOException {
        topLevelClass = AaInsertion.class;
        parse(source);
        return topLevelObjects;
    }

    @SuppressWarnings("unchecked")
    public List<Therapy> readTherapys(InputSource source) throws SAXException, IOException {
        topLevelClass = Therapy.class;
        parse(source);
        return topLevelObjects;
    }

    @SuppressWarnings("unchecked")
    public List<TherapyCommercial> readTherapyCommercials(InputSource source) throws SAXException, IOException {
        topLevelClass = TherapyCommercial.class;
        parse(source);
        return topLevelObjects;
    }

    @SuppressWarnings("unchecked")
    public List<TherapyGeneric> readTherapyGenerics(InputSource source) throws SAXException, IOException {
        topLevelClass = TherapyGeneric.class;
        parse(source);
        return topLevelObjects;
    }

    @SuppressWarnings("unchecked")
    public List<TestResult> readTestResults(InputSource source) throws SAXException, IOException {
        topLevelClass = TestResult.class;
        parse(source);
        return topLevelObjects;
    }

    @SuppressWarnings("unchecked")
    public List<Test> readTests(InputSource source) throws SAXException, IOException {
        topLevelClass = Test.class;
        parse(source);
        return topLevelObjects;
    }

    @SuppressWarnings("unchecked")
    public List<TestType> readTestTypes(InputSource source) throws SAXException, IOException {
        topLevelClass = TestType.class;
        parse(source);
        return topLevelObjects;
    }

    @SuppressWarnings("unchecked")
    public List<ValueType> readValueTypes(InputSource source) throws SAXException, IOException {
        topLevelClass = ValueType.class;
        parse(source);
        return topLevelObjects;
    }

    @SuppressWarnings("unchecked")
    public List<TestObject> readTestObjects(InputSource source) throws SAXException, IOException {
        topLevelClass = TestObject.class;
        parse(source);
        return topLevelObjects;
    }

    @SuppressWarnings("unchecked")
    public List<TestNominalValue> readTestNominalValues(InputSource source) throws SAXException, IOException {
        topLevelClass = TestNominalValue.class;
        parse(source);
        return topLevelObjects;
    }

    @SuppressWarnings("unchecked")
    public List<PatientAttributeValue> readPatientAttributeValues(InputSource source) throws SAXException, IOException {
        topLevelClass = PatientAttributeValue.class;
        parse(source);
        return topLevelObjects;
    }

    @SuppressWarnings("unchecked")
    public List<Attribute> readAttributes(InputSource source) throws SAXException, IOException {
        topLevelClass = Attribute.class;
        parse(source);
        return topLevelObjects;
    }

    private void parse(InputSource source)  throws SAXException, IOException {
        XMLReader xmlReader = XMLReaderFactory.createXMLReader();
        xmlReader.setContentHandler(this);
        xmlReader.setErrorHandler(this);
        xmlReader.parse(source);
    }

    public void sync(Transaction t, Patient o, Patient dbo, boolean simulate) {
        if (!equals(dbo.getPatientId(), o.getPatientId())) {
            if (!simulate)
                dbo.setPatientId(o.getPatientId());
            log.append(Describe.describe(o) + ": updating patientId\n");
        }
        if (!equals(dbo.getLastName(), o.getLastName())) {
            if (!simulate)
                dbo.setLastName(o.getLastName());
            log.append(Describe.describe(o) + ": updating lastName\n");
        }
        if (!equals(dbo.getFirstName(), o.getFirstName())) {
            if (!simulate)
                dbo.setFirstName(o.getFirstName());
            log.append(Describe.describe(o) + ": updating firstName\n");
        }
        if (!equals(dbo.getBirthDate(), o.getBirthDate())) {
            if (!simulate)
                dbo.setBirthDate(o.getBirthDate());
            log.append(Describe.describe(o) + ": updating birthDate\n");
        }
        if (!equals(dbo.getDeathDate(), o.getDeathDate())) {
            if (!simulate)
                dbo.setDeathDate(o.getDeathDate());
            log.append(Describe.describe(o) + ": updating deathDate\n");
        }
        for(Dataset e : o.getDatasets()) {
            Dataset dbe = null;
            for(Dataset f : dbo.getDatasets()) {
                if (Equals.isSameDataset(e, f)) {
                    dbe = f; break;
                }
            }
            if (dbe == null) {
                log.append(Describe.describe(dbo) + ": adding " + Describe.describe(e) + "\n");
                if (!simulate) {
                    ;// TODO
                }
            } else
                sync(t, e, dbe, simulate);
        }
        for(Dataset dbe : dbo.getDatasets()) {
            Dataset e = null;
            for(Dataset f : o.getDatasets()) {
                if (Equals.isSameDataset(e, f)) {
                    e = f; break;
                }
            }
            if (e == null) {
                log.append(Describe.describe(dbo) + ": removing: " + Describe.describe(dbe) + "\n");
                if (!simulate)
                    dbo.getDatasets().remove(dbe);
            }
        }
        for(TestResult e : o.getTestResults()) {
            TestResult dbe = null;
            for(TestResult f : dbo.getTestResults()) {
                if (Equals.isSameTestResult(e, f)) {
                    dbe = f; break;
                }
            }
            if (dbe == null) {
                log.append(Describe.describe(dbo) + ": adding " + Describe.describe(e) + "\n");
                if (!simulate) {
                    o.addTestResult(e);
                }
            } else
                sync(t, e, dbe, simulate);
        }
        for(TestResult dbe : dbo.getTestResults()) {
            TestResult e = null;
            for(TestResult f : o.getTestResults()) {
                if (Equals.isSameTestResult(e, f)) {
                    e = f; break;
                }
            }
            if (e == null) {
                log.append(Describe.describe(dbo) + ": removing: " + Describe.describe(dbe) + "\n");
                if (!simulate)
                    dbo.getTestResults().remove(dbe);
            }
        }
        for(PatientAttributeValue e : o.getPatientAttributeValues()) {
            PatientAttributeValue dbe = null;
            for(PatientAttributeValue f : dbo.getPatientAttributeValues()) {
                if (Equals.isSamePatientAttributeValue(e, f)) {
                    dbe = f; break;
                }
            }
            if (dbe == null) {
                log.append(Describe.describe(dbo) + ": adding " + Describe.describe(e) + "\n");
                if (!simulate) {
                    o.addPatientAttributeValue(e);
                }
            } else
                sync(t, e, dbe, simulate);
        }
        for(PatientAttributeValue dbe : dbo.getPatientAttributeValues()) {
            PatientAttributeValue e = null;
            for(PatientAttributeValue f : o.getPatientAttributeValues()) {
                if (Equals.isSamePatientAttributeValue(e, f)) {
                    e = f; break;
                }
            }
            if (e == null) {
                log.append(Describe.describe(dbo) + ": removing: " + Describe.describe(dbe) + "\n");
                if (!simulate)
                    dbo.getPatientAttributeValues().remove(dbe);
            }
        }
        for(ViralIsolate e : o.getViralIsolates()) {
            ViralIsolate dbe = null;
            for(ViralIsolate f : dbo.getViralIsolates()) {
                if (Equals.isSameViralIsolate(e, f)) {
                    dbe = f; break;
                }
            }
            if (dbe == null) {
                log.append(Describe.describe(dbo) + ": adding " + Describe.describe(e) + "\n");
                if (!simulate) {
                    o.addViralIsolate(e);
                }
            } else
                sync(t, e, dbe, simulate);
        }
        for(ViralIsolate dbe : dbo.getViralIsolates()) {
            ViralIsolate e = null;
            for(ViralIsolate f : o.getViralIsolates()) {
                if (Equals.isSameViralIsolate(e, f)) {
                    e = f; break;
                }
            }
            if (e == null) {
                log.append(Describe.describe(dbo) + ": removing: " + Describe.describe(dbe) + "\n");
                if (!simulate)
                    dbo.getViralIsolates().remove(dbe);
            }
        }
        for(Therapy e : o.getTherapies()) {
            Therapy dbe = null;
            for(Therapy f : dbo.getTherapies()) {
                if (Equals.isSameTherapy(e, f)) {
                    dbe = f; break;
                }
            }
            if (dbe == null) {
                log.append(Describe.describe(dbo) + ": adding " + Describe.describe(e) + "\n");
                if (!simulate) {
                    o.addTherapy(e);
                }
            } else
                sync(t, e, dbe, simulate);
        }
        for(Therapy dbe : dbo.getTherapies()) {
            Therapy e = null;
            for(Therapy f : o.getTherapies()) {
                if (Equals.isSameTherapy(e, f)) {
                    e = f; break;
                }
            }
            if (e == null) {
                log.append(Describe.describe(dbo) + ": removing: " + Describe.describe(dbe) + "\n");
                if (!simulate)
                    dbo.getTherapies().remove(dbe);
            }
        }
    }

    public void sync(Transaction t, Dataset o, Dataset dbo, boolean simulate) {
        if (!equals(dbo.getDescription(), o.getDescription())) {
            if (!simulate)
                dbo.setDescription(o.getDescription());
            log.append(Describe.describe(o) + ": updating description\n");
        }
        if (!equals(dbo.getCreationDate(), o.getCreationDate())) {
            if (!simulate)
                dbo.setCreationDate(o.getCreationDate());
            log.append(Describe.describe(o) + ": updating creationDate\n");
        }
        if (!equals(dbo.getClosedDate(), o.getClosedDate())) {
            if (!simulate)
                dbo.setClosedDate(o.getClosedDate());
            log.append(Describe.describe(o) + ": updating closedDate\n");
        }
        if (!equals(dbo.getRevision(), o.getRevision())) {
            if (!simulate)
                dbo.setRevision(o.getRevision());
            log.append(Describe.describe(o) + ": updating revision\n");
        }
    }

    public void sync(Transaction t, AttributeNominalValue o, AttributeNominalValue dbo, boolean simulate) {
        if (!equals(dbo.getValue(), o.getValue())) {
            if (!simulate)
                dbo.setValue(o.getValue());
            log.append(Describe.describe(o) + ": updating value\n");
        }
    }

    public void sync(Transaction t, ViralIsolate o, ViralIsolate dbo, boolean simulate) {
        if (!equals(dbo.getSampleId(), o.getSampleId())) {
            if (!simulate)
                dbo.setSampleId(o.getSampleId());
            log.append(Describe.describe(o) + ": updating sampleId\n");
        }
        if (!equals(dbo.getSampleDate(), o.getSampleDate())) {
            if (!simulate)
                dbo.setSampleDate(o.getSampleDate());
            log.append(Describe.describe(o) + ": updating sampleDate\n");
        }
        for(NtSequence e : o.getNtSequences()) {
            NtSequence dbe = null;
            for(NtSequence f : dbo.getNtSequences()) {
                if (Equals.isSameNtSequence(e, f)) {
                    dbe = f; break;
                }
            }
            if (dbe == null) {
                log.append(Describe.describe(dbo) + ": adding " + Describe.describe(e) + "\n");
                if (!simulate) {
                    dbo.getNtSequences().add(e);
                    e.setViralIsolate(dbo);
                }
            } else
                sync(t, e, dbe, simulate);
        }
        for(NtSequence dbe : dbo.getNtSequences()) {
            NtSequence e = null;
            for(NtSequence f : o.getNtSequences()) {
                if (Equals.isSameNtSequence(e, f)) {
                    e = f; break;
                }
            }
            if (e == null) {
                log.append(Describe.describe(dbo) + ": removing: " + Describe.describe(dbe) + "\n");
                if (!simulate)
                    dbo.getNtSequences().remove(dbe);
            }
        }
        for(TestResult e : o.getTestResults()) {
            TestResult dbe = null;
            for(TestResult f : dbo.getTestResults()) {
                if (Equals.isSameTestResult(e, f)) {
                    dbe = f; break;
                }
            }
            if (dbe == null) {
                log.append(Describe.describe(dbo) + ": adding " + Describe.describe(e) + "\n");
                if (!simulate) {
                    dbo.getTestResults().add(e);
                    e.setViralIsolate(dbo);
                }
            } else
                sync(t, e, dbe, simulate);
        }
        for(TestResult dbe : dbo.getTestResults()) {
            TestResult e = null;
            for(TestResult f : o.getTestResults()) {
                if (Equals.isSameTestResult(e, f)) {
                    e = f; break;
                }
            }
            if (e == null) {
                log.append(Describe.describe(dbo) + ": removing: " + Describe.describe(dbe) + "\n");
                if (!simulate)
                    dbo.getTestResults().remove(dbe);
            }
        }
    }

    public void sync(Transaction t, NtSequence o, NtSequence dbo, boolean simulate) {
        if (!equals(dbo.getNucleotides(), o.getNucleotides())) {
            if (!simulate)
                dbo.setNucleotides(o.getNucleotides());
            log.append(Describe.describe(o) + ": updating nucleotides\n");
        }
        if (!equals(dbo.getLabel(), o.getLabel())) {
            if (!simulate)
                dbo.setLabel(o.getLabel());
            log.append(Describe.describe(o) + ": updating label\n");
        }
        if (!equals(dbo.getSequenceDate(), o.getSequenceDate())) {
            if (!simulate)
                dbo.setSequenceDate(o.getSequenceDate());
            log.append(Describe.describe(o) + ": updating sequenceDate\n");
        }
        for(AaSequence e : o.getAaSequences()) {
            AaSequence dbe = null;
            for(AaSequence f : dbo.getAaSequences()) {
                if (Equals.isSameAaSequence(e, f)) {
                    dbe = f; break;
                }
            }
            if (dbe == null) {
                log.append(Describe.describe(dbo) + ": adding " + Describe.describe(e) + "\n");
                if (!simulate) {
                    dbo.getAaSequences().add(e);
                    e.setNtSequence(dbo);
                }
            } else
                sync(t, e, dbe, simulate);
        }
        for(AaSequence dbe : dbo.getAaSequences()) {
            AaSequence e = null;
            for(AaSequence f : o.getAaSequences()) {
                if (Equals.isSameAaSequence(e, f)) {
                    e = f; break;
                }
            }
            if (e == null) {
                log.append(Describe.describe(dbo) + ": removing: " + Describe.describe(dbe) + "\n");
                if (!simulate)
                    dbo.getAaSequences().remove(dbe);
            }
        }
        for(TestResult e : o.getTestResults()) {
            TestResult dbe = null;
            for(TestResult f : dbo.getTestResults()) {
                if (Equals.isSameTestResult(e, f)) {
                    dbe = f; break;
                }
            }
            if (dbe == null) {
                log.append(Describe.describe(dbo) + ": adding " + Describe.describe(e) + "\n");
                if (!simulate) {
                    dbo.getTestResults().add(e);
                    e.setNtSequence(dbo);
                }
            } else
                sync(t, e, dbe, simulate);
        }
        for(TestResult dbe : dbo.getTestResults()) {
            TestResult e = null;
            for(TestResult f : o.getTestResults()) {
                if (Equals.isSameTestResult(e, f)) {
                    e = f; break;
                }
            }
            if (e == null) {
                log.append(Describe.describe(dbo) + ": removing: " + Describe.describe(dbe) + "\n");
                if (!simulate)
                    dbo.getTestResults().remove(dbe);
            }
        }
    }

    public void sync(Transaction t, AaSequence o, AaSequence dbo, boolean simulate) {
        if (!equals(dbo.getProtein(), o.getProtein())) {
            if (!simulate)
                dbo.setProtein(o.getProtein());
            log.append(Describe.describe(o) + ": updating protein\n");
        }
        if (!equals(dbo.getFirstAaPos(), o.getFirstAaPos())) {
            if (!simulate)
                dbo.setFirstAaPos(o.getFirstAaPos());
            log.append(Describe.describe(o) + ": updating firstAaPos\n");
        }
        if (!equals(dbo.getLastAaPos(), o.getLastAaPos())) {
            if (!simulate)
                dbo.setLastAaPos(o.getLastAaPos());
            log.append(Describe.describe(o) + ": updating lastAaPos\n");
        }
        for(AaMutation e : o.getAaMutations()) {
            AaMutation dbe = null;
            for(AaMutation f : dbo.getAaMutations()) {
                if (Equals.isSameAaMutation(e, f)) {
                    dbe = f; break;
                }
            }
            if (dbe == null) {
                log.append(Describe.describe(dbo) + ": adding " + Describe.describe(e) + "\n");
                if (!simulate) {
                    dbo.getAaMutations().add(e);
                    e.getId().setAaSequence(dbo);
                }
            } else
                sync(t, e, dbe, simulate);
        }
        for(AaMutation dbe : dbo.getAaMutations()) {
            AaMutation e = null;
            for(AaMutation f : o.getAaMutations()) {
                if (Equals.isSameAaMutation(e, f)) {
                    e = f; break;
                }
            }
            if (e == null) {
                log.append(Describe.describe(dbo) + ": removing: " + Describe.describe(dbe) + "\n");
                if (!simulate)
                    dbo.getAaMutations().remove(dbe);
            }
        }
        for(AaInsertion e : o.getAaInsertions()) {
            AaInsertion dbe = null;
            for(AaInsertion f : dbo.getAaInsertions()) {
                if (Equals.isSameAaInsertion(e, f)) {
                    dbe = f; break;
                }
            }
            if (dbe == null) {
                log.append(Describe.describe(dbo) + ": adding " + Describe.describe(e) + "\n");
                if (!simulate) {
                    dbo.getAaInsertions().add(e);
                    e.getId().setAaSequence(dbo);
                }
            } else
                sync(t, e, dbe, simulate);
        }
        for(AaInsertion dbe : dbo.getAaInsertions()) {
            AaInsertion e = null;
            for(AaInsertion f : o.getAaInsertions()) {
                if (Equals.isSameAaInsertion(e, f)) {
                    e = f; break;
                }
            }
            if (e == null) {
                log.append(Describe.describe(dbo) + ": removing: " + Describe.describe(dbe) + "\n");
                if (!simulate)
                    dbo.getAaInsertions().remove(dbe);
            }
        }
    }

    public void sync(Transaction t, AaMutation o, AaMutation dbo, boolean simulate) {
        if (!equals(dbo.getId().getPosition(), o.getId().getPosition())) {
            if (!simulate)
                dbo.getId().setPosition(o.getId().getPosition());
            log.append(Describe.describe(o) + ": updating position\n");
        }
        if (!equals(dbo.getAaReference(), o.getAaReference())) {
            if (!simulate)
                dbo.setAaReference(o.getAaReference());
            log.append(Describe.describe(o) + ": updating aaReference\n");
        }
        if (!equals(dbo.getAaMutation(), o.getAaMutation())) {
            if (!simulate)
                dbo.setAaMutation(o.getAaMutation());
            log.append(Describe.describe(o) + ": updating aaMutation\n");
        }
        if (!equals(dbo.getNtReferenceCodon(), o.getNtReferenceCodon())) {
            if (!simulate)
                dbo.setNtReferenceCodon(o.getNtReferenceCodon());
            log.append(Describe.describe(o) + ": updating ntReferenceCodon\n");
        }
        if (!equals(dbo.getNtMutationCodon(), o.getNtMutationCodon())) {
            if (!simulate)
                dbo.setNtMutationCodon(o.getNtMutationCodon());
            log.append(Describe.describe(o) + ": updating ntMutationCodon\n");
        }
    }

    public void sync(Transaction t, AaInsertion o, AaInsertion dbo, boolean simulate) {
        if (!equals(dbo.getId().getPosition(), o.getId().getPosition())) {
            if (!simulate)
                dbo.getId().setPosition(o.getId().getPosition());
            log.append(Describe.describe(o) + ": updating position\n");
        }
        if (!equals(dbo.getId().getInsertionOrder(), o.getId().getInsertionOrder())) {
            if (!simulate)
                dbo.getId().setInsertionOrder(o.getId().getInsertionOrder());
            log.append(Describe.describe(o) + ": updating insertionOrder\n");
        }
        if (!equals(dbo.getAaInsertion(), o.getAaInsertion())) {
            if (!simulate)
                dbo.setAaInsertion(o.getAaInsertion());
            log.append(Describe.describe(o) + ": updating aaInsertion\n");
        }
        if (!equals(dbo.getNtInsertionCodon(), o.getNtInsertionCodon())) {
            if (!simulate)
                dbo.setNtInsertionCodon(o.getNtInsertionCodon());
            log.append(Describe.describe(o) + ": updating ntInsertionCodon\n");
        }
    }

    public void sync(Transaction t, Therapy o, Therapy dbo, boolean simulate) {
        if (!equals(dbo.getStartDate(), o.getStartDate())) {
            if (!simulate)
                dbo.setStartDate(o.getStartDate());
            log.append(Describe.describe(o) + ": updating startDate\n");
        }
        if (!equals(dbo.getStopDate(), o.getStopDate())) {
            if (!simulate)
                dbo.setStopDate(o.getStopDate());
            log.append(Describe.describe(o) + ": updating stopDate\n");
        }
        if (!equals(dbo.getComment(), o.getComment())) {
            if (!simulate)
                dbo.setComment(o.getComment());
            log.append(Describe.describe(o) + ": updating comment\n");
        }
        for(TherapyCommercial e : o.getTherapyCommercials()) {
            TherapyCommercial dbe = null;
            for(TherapyCommercial f : dbo.getTherapyCommercials()) {
                if (Equals.isSameTherapyCommercial(e, f)) {
                    dbe = f; break;
                }
            }
            if (dbe == null) {
                log.append(Describe.describe(dbo) + ": adding " + Describe.describe(e) + "\n");
                if (!simulate) {
                    dbo.getTherapyCommercials().add(e);
                    e.getId().setTherapy(dbo);
                }
            } else
                sync(t, e, dbe, simulate);
        }
        for(TherapyCommercial dbe : dbo.getTherapyCommercials()) {
            TherapyCommercial e = null;
            for(TherapyCommercial f : o.getTherapyCommercials()) {
                if (Equals.isSameTherapyCommercial(e, f)) {
                    e = f; break;
                }
            }
            if (e == null) {
                log.append(Describe.describe(dbo) + ": removing: " + Describe.describe(dbe) + "\n");
                if (!simulate)
                    dbo.getTherapyCommercials().remove(dbe);
            }
        }
        for(TherapyGeneric e : o.getTherapyGenerics()) {
            TherapyGeneric dbe = null;
            for(TherapyGeneric f : dbo.getTherapyGenerics()) {
                if (Equals.isSameTherapyGeneric(e, f)) {
                    dbe = f; break;
                }
            }
            if (dbe == null) {
                log.append(Describe.describe(dbo) + ": adding " + Describe.describe(e) + "\n");
                if (!simulate) {
                    dbo.getTherapyGenerics().add(e);
                    e.getId().setTherapy(dbo);
                }
            } else
                sync(t, e, dbe, simulate);
        }
        for(TherapyGeneric dbe : dbo.getTherapyGenerics()) {
            TherapyGeneric e = null;
            for(TherapyGeneric f : o.getTherapyGenerics()) {
                if (Equals.isSameTherapyGeneric(e, f)) {
                    e = f; break;
                }
            }
            if (e == null) {
                log.append(Describe.describe(dbo) + ": removing: " + Describe.describe(dbe) + "\n");
                if (!simulate)
                    dbo.getTherapyGenerics().remove(dbe);
            }
        }
    }

    public void sync(Transaction t, TherapyCommercial o, TherapyCommercial dbo, boolean simulate) {
        if (!equals(dbo.getId().getDrugCommercial(), o.getId().getDrugCommercial())) {
            if (!simulate)
                dbo.getId().setDrugCommercial(o.getId().getDrugCommercial());
            log.append(Describe.describe(o) + ": updating drugCommercial\n");
        }
        if (!equals(dbo.getDayDosageUnits(), o.getDayDosageUnits())) {
            if (!simulate)
                dbo.setDayDosageUnits(o.getDayDosageUnits());
            log.append(Describe.describe(o) + ": updating dayDosageUnits\n");
        }
    }

    public void sync(Transaction t, TherapyGeneric o, TherapyGeneric dbo, boolean simulate) {
        if (!equals(dbo.getId().getDrugGeneric(), o.getId().getDrugGeneric())) {
            if (!simulate)
                dbo.getId().setDrugGeneric(o.getId().getDrugGeneric());
            log.append(Describe.describe(o) + ": updating drugGeneric\n");
        }
        if (!equals(dbo.getDayDosageMg(), o.getDayDosageMg())) {
            if (!simulate)
                dbo.setDayDosageMg(o.getDayDosageMg());
            log.append(Describe.describe(o) + ": updating dayDosageMg\n");
        }
    }

    public void sync(Transaction t, TestResult o, TestResult dbo, boolean simulate) {
        if (Equals.isSameTest(o.getTest(), dbo.getTest()))
            sync(t, o.getTest(), dbo.getTest(), simulate);
        else {
            if (!simulate)
                dbo.setTest(o.getTest());
            log.append(Describe.describe(o) + ": updating test\n");
        }
        if (!equals(dbo.getDrugGeneric(), o.getDrugGeneric())) {
            if (!simulate)
                dbo.setDrugGeneric(o.getDrugGeneric());
            log.append(Describe.describe(o) + ": updating drugGeneric\n");
        }
        if (Equals.isSameTestNominalValue(o.getTestNominalValue(), dbo.getTestNominalValue()))
            sync(t, o.getTestNominalValue(), dbo.getTestNominalValue(), simulate);
        else {
            if (!simulate)
                dbo.setTestNominalValue(o.getTestNominalValue());
            log.append(Describe.describe(o) + ": updating testNominalValue\n");
        }
        if (!equals(dbo.getValue(), o.getValue())) {
            if (!simulate)
                dbo.setValue(o.getValue());
            log.append(Describe.describe(o) + ": updating value\n");
        }
        if (!equals(dbo.getTestDate(), o.getTestDate())) {
            if (!simulate)
                dbo.setTestDate(o.getTestDate());
            log.append(Describe.describe(o) + ": updating testDate\n");
        }
        if (!equals(dbo.getSampleId(), o.getSampleId())) {
            if (!simulate)
                dbo.setSampleId(o.getSampleId());
            log.append(Describe.describe(o) + ": updating sampleId\n");
        }
    }

    public void sync(Transaction t, Test o, Test dbo, boolean simulate) {
        if (Equals.isSameTestType(o.getTestType(), dbo.getTestType()))
            sync(t, o.getTestType(), dbo.getTestType(), simulate);
        else {
            if (!simulate)
                dbo.setTestType(o.getTestType());
            log.append(Describe.describe(o) + ": updating testType\n");
        }
        if (!equals(dbo.getDescription(), o.getDescription())) {
            if (!simulate)
                dbo.setDescription(o.getDescription());
            log.append(Describe.describe(o) + ": updating description\n");
        }
        if (!equals(dbo.getServiceClass(), o.getServiceClass())) {
            if (!simulate)
                dbo.setServiceClass(o.getServiceClass());
            log.append(Describe.describe(o) + ": updating serviceClass\n");
        }
        if (!equals(dbo.getServiceData(), o.getServiceData())) {
            if (!simulate)
                dbo.setServiceData(o.getServiceData());
            log.append(Describe.describe(o) + ": updating serviceData\n");
        }
        if (!equals(dbo.getServiceConfig(), o.getServiceConfig())) {
            if (!simulate)
                dbo.setServiceConfig(o.getServiceConfig());
            log.append(Describe.describe(o) + ": updating serviceConfig\n");
        }
    }

    public void sync(Transaction t, TestType o, TestType dbo, boolean simulate) {
        if (Equals.isSameValueType(o.getValueType(), dbo.getValueType()))
            sync(t, o.getValueType(), dbo.getValueType(), simulate);
        else {
            if (!simulate)
                dbo.setValueType(o.getValueType());
            log.append(Describe.describe(o) + ": updating valueType\n");
        }
        if (Equals.isSameTestObject(o.getTestObject(), dbo.getTestObject()))
            sync(t, o.getTestObject(), dbo.getTestObject(), simulate);
        else {
            if (!simulate)
                dbo.setTestObject(o.getTestObject());
            log.append(Describe.describe(o) + ": updating testObject\n");
        }
        if (!equals(dbo.getDescription(), o.getDescription())) {
            if (!simulate)
                dbo.setDescription(o.getDescription());
            log.append(Describe.describe(o) + ": updating description\n");
        }
        for(TestNominalValue e : o.getTestNominalValues()) {
            TestNominalValue dbe = null;
            for(TestNominalValue f : dbo.getTestNominalValues()) {
                if (Equals.isSameTestNominalValue(e, f)) {
                    dbe = f; break;
                }
            }
            if (dbe == null) {
                log.append(Describe.describe(dbo) + ": adding " + Describe.describe(e) + "\n");
                if (!simulate) {
                    dbo.getTestNominalValues().add(e);
                    e.setTestType(dbo);
                }
            } else
                sync(t, e, dbe, simulate);
        }
        for(TestNominalValue dbe : dbo.getTestNominalValues()) {
            TestNominalValue e = null;
            for(TestNominalValue f : o.getTestNominalValues()) {
                if (Equals.isSameTestNominalValue(e, f)) {
                    e = f; break;
                }
            }
            if (e == null) {
                log.append(Describe.describe(dbo) + ": removing: " + Describe.describe(dbe) + "\n");
                if (!simulate)
                    dbo.getTestNominalValues().remove(dbe);
            }
        }
    }

    public void sync(Transaction t, ValueType o, ValueType dbo, boolean simulate) {
        if (!equals(dbo.getDescription(), o.getDescription())) {
            if (!simulate)
                dbo.setDescription(o.getDescription());
            log.append(Describe.describe(o) + ": updating description\n");
        }
        if (!equals(dbo.getMin(), o.getMin())) {
            if (!simulate)
                dbo.setMin(o.getMin());
            log.append(Describe.describe(o) + ": updating min\n");
        }
        if (!equals(dbo.getMax(), o.getMax())) {
            if (!simulate)
                dbo.setMax(o.getMax());
            log.append(Describe.describe(o) + ": updating max\n");
        }
        if (!equals(dbo.getMultiple(), o.getMultiple())) {
            if (!simulate)
                dbo.setMultiple(o.getMultiple());
            log.append(Describe.describe(o) + ": updating multiple\n");
        }
    }

    public void sync(Transaction t, TestObject o, TestObject dbo, boolean simulate) {
        if (!equals(dbo.getDescription(), o.getDescription())) {
            if (!simulate)
                dbo.setDescription(o.getDescription());
            log.append(Describe.describe(o) + ": updating description\n");
        }
        if (!equals(dbo.getTestObjectId(), o.getTestObjectId())) {
            if (!simulate)
                dbo.setTestObjectId(o.getTestObjectId());
            log.append(Describe.describe(o) + ": updating testObjectId\n");
        }
    }

    public void sync(Transaction t, TestNominalValue o, TestNominalValue dbo, boolean simulate) {
        if (Equals.isSameTestType(o.getTestType(), dbo.getTestType()))
            sync(t, o.getTestType(), dbo.getTestType(), simulate);
        else {
            if (!simulate)
                dbo.setTestType(o.getTestType());
            log.append(Describe.describe(o) + ": updating testType\n");
        }
        if (!equals(dbo.getValue(), o.getValue())) {
            if (!simulate)
                dbo.setValue(o.getValue());
            log.append(Describe.describe(o) + ": updating value\n");
        }
    }

    public void sync(Transaction t, PatientAttributeValue o, PatientAttributeValue dbo, boolean simulate) {
        if (Equals.isSameAttribute(o.getId().getAttribute(), dbo.getId().getAttribute()))
            sync(t, o.getId().getAttribute(), dbo.getId().getAttribute(), simulate);
        else {
            if (!simulate)
                dbo.getId().setAttribute(o.getId().getAttribute());
            log.append(Describe.describe(o) + ": updating attribute\n");
        }
        if (Equals.isSameAttributeNominalValue(o.getAttributeNominalValue(), dbo.getAttributeNominalValue()))
            sync(t, o.getAttributeNominalValue(), dbo.getAttributeNominalValue(), simulate);
        else {
            if (!simulate)
                dbo.setAttributeNominalValue(o.getAttributeNominalValue());
            log.append(Describe.describe(o) + ": updating attributeNominalValue\n");
        }
        if (!equals(dbo.getValue(), o.getValue())) {
            if (!simulate)
                dbo.setValue(o.getValue());
            log.append(Describe.describe(o) + ": updating value\n");
        }
    }

    public void sync(Transaction t, Attribute o, Attribute dbo, boolean simulate) {
        if (Equals.isSameValueType(o.getValueType(), dbo.getValueType()))
            sync(t, o.getValueType(), dbo.getValueType(), simulate);
        else {
            if (!simulate)
                dbo.setValueType(o.getValueType());
            log.append(Describe.describe(o) + ": updating valueType\n");
        }
        if (!equals(dbo.getName(), o.getName())) {
            if (!simulate)
                dbo.setName(o.getName());
            log.append(Describe.describe(o) + ": updating name\n");
        }
        for(AttributeNominalValue e : o.getAttributeNominalValues()) {
            AttributeNominalValue dbe = null;
            for(AttributeNominalValue f : dbo.getAttributeNominalValues()) {
                if (Equals.isSameAttributeNominalValue(e, f)) {
                    dbe = f; break;
                }
            }
            if (dbe == null) {
                log.append(Describe.describe(dbo) + ": adding " + Describe.describe(e) + "\n");
                if (!simulate) {
                    dbo.getAttributeNominalValues().add(e);
                    e.setAttribute(dbo);
                }
            } else
                sync(t, e, dbe, simulate);
        }
        for(AttributeNominalValue dbe : dbo.getAttributeNominalValues()) {
            AttributeNominalValue e = null;
            for(AttributeNominalValue f : o.getAttributeNominalValues()) {
                if (Equals.isSameAttributeNominalValue(e, f)) {
                    e = f; break;
                }
            }
            if (e == null) {
                log.append(Describe.describe(dbo) + ": removing: " + Describe.describe(dbe) + "\n");
                if (!simulate)
                    dbo.getAttributeNominalValues().remove(dbe);
            }
        }
    }

    enum SyncMode { Clean, Update };
    StringBuffer log = new StringBuffer();
    public Patient sync(Transaction t, Patient o, SyncMode mode, boolean simulate) throws ImportException {
        Patient dbo = dbFindPatient(t, o);
        if (dbo != null) {
            if (mode == SyncMode.Clean)
                throw new ImportException(Describe.describe(o) + " already exists");
            sync(t, o, dbo, simulate);
            if (!simulate)
                t.update(dbo);
            return dbo;
        } else {
            log.append("Adding: " + Describe.describe(o) + "\n");
            if (!simulate)
                t.save(o);
            return o;
        }
    }

    public Attribute sync(Transaction t, Attribute o, SyncMode mode, boolean simulate) throws ImportException {
        Attribute dbo = dbFindAttribute(t, o);
        if (dbo != null) {
            if (mode == SyncMode.Clean)
                throw new ImportException(Describe.describe(o) + " already exists");
            sync(t, o, dbo, simulate);
            if (!simulate)
                t.update(dbo);
            return dbo;
        } else {
            log.append("Adding: " + Describe.describe(o) + "\n");
            if (!simulate)
                t.save(o);
            return o;
        }
    }

    public Test sync(Transaction t, Test o, SyncMode mode, boolean simulate) throws ImportException {
        Test dbo = dbFindTest(t, o);
        if (dbo != null) {
            if (mode == SyncMode.Clean)
                throw new ImportException(Describe.describe(o) + " already exists");
            sync(t, o, dbo, simulate);
            if (!simulate)
                t.update(dbo);
            return dbo;
        } else {
            log.append("Adding: " + Describe.describe(o) + "\n");
            if (!simulate)
                t.save(o);
            return o;
        }
    }

    public TestType sync(Transaction t, TestType o, SyncMode mode, boolean simulate) throws ImportException {
        TestType dbo = dbFindTestType(t, o);
        if (dbo != null) {
            if (mode == SyncMode.Clean)
                throw new ImportException(Describe.describe(o) + " already exists");
            sync(t, o, dbo, simulate);
            if (!simulate)
                t.update(dbo);
            return dbo;
        } else {
            log.append("Adding: " + Describe.describe(o) + "\n");
            if (!simulate)
                t.save(o);
            return o;
        }
    }

    public DrugGeneric sync(Transaction t, DrugGeneric o, SyncMode mode, boolean simulate) throws ImportException {
        DrugGeneric dbo = dbFindDrugGeneric(t, o);
        if (dbo != null) {
            if (mode == SyncMode.Clean)
                throw new ImportException(Describe.describe(o) + " already exists");
            sync(t, o, dbo, simulate);
            if (!simulate)
                t.update(dbo);
            return dbo;
        } else {
            log.append("Adding: " + Describe.describe(o) + "\n");
            if (!simulate)
                t.save(o);
            return o;
        }
    }

    public DrugCommercial sync(Transaction t, DrugCommercial o, SyncMode mode, boolean simulate) throws ImportException {
        DrugCommercial dbo = dbFindDrugCommercial(t, o);
        if (dbo != null) {
            if (mode == SyncMode.Clean)
                throw new ImportException(Describe.describe(o) + " already exists");
            sync(t, o, dbo, simulate);
            if (!simulate)
                t.update(dbo);
            return dbo;
        } else {
            log.append("Adding: " + Describe.describe(o) + "\n");
            if (!simulate)
                t.save(o);
            return o;
        }
    }

}
