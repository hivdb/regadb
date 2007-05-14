package net.sf.regadb.io.importXML;
import java.util.*;
import net.sf.regadb.db.*;
import org.xml.sax.*;

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

    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (false) {
        } else if (currentState() == ParseState.statePatient) {
            if ("patient".equals(qName)|| "patients-el".equals(qName)) {
                popState();
                Patient elPatient = null;
                if (false) {
                } else if (currentState() == ParseState.TopLevel) {
                    elPatient = patient;
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
                if (false) {
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
                if (false) {
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
                if (false) {
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
                if (false) {
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
                if (false) {
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
                if (false) {
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
                if (false) {
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
                if (false) {
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
                if (false) {
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
                if (false) {
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
                if (false) {
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
                if (false) {
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
                if (false) {
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
                if (false) {
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
                if (false) {
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
                if (false) {
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
                if (false) {
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
                if (false) {
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

}
