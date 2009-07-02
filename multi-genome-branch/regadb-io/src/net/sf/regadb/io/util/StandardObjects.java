/*
 * Created on May 10, 2007
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package net.sf.regadb.io.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import net.sf.regadb.db.Attribute;
import net.sf.regadb.db.AttributeGroup;
import net.sf.regadb.db.AttributeNominalValue;
import net.sf.regadb.db.Event;
import net.sf.regadb.db.EventNominalValue;
import net.sf.regadb.db.Genome;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestNominalValue;
import net.sf.regadb.db.TestObject;
import net.sf.regadb.db.TestType;
import net.sf.regadb.db.ValueType;

public class StandardObjects {
    private static List<Genome> allGenomes = new ArrayList<Genome>();
    private static List<TestType> allTestTypes = new ArrayList<TestType>();
    private static List<Test> allTests = new ArrayList<Test>();
    private static List<Attribute> allAttributes = new ArrayList<Attribute>();
    private static List<Event> allEvents = new ArrayList<Event>();
    private static List<AttributeGroup> allAttributeGroups = new ArrayList<AttributeGroup>();
    private static List<ValueType> allValueTypes = new ArrayList<ValueType>();
    
    private static Map<String, Map<String, TestType>> standardGenomeTestTypes = new HashMap<String, Map<String, TestType>>();
    private static Map<String, Map<String, Test>> standardGenomeTests = new HashMap<String, Map<String, Test>>();
    
    private static String viralLoadDescription = "Viral Load (copies/ml)";
    private static String viralLoadLog10Description = "Viral Load (log10)";
    private static String seroStatusDescription = "Serostatus";
    private static String seroconversionDescription = "Seroconversion";
    private static String gssDescription = "Genotypic Susceptibility Score (GSS)";
    
    private static Genome hiv1Genome;
    private static Genome hiv2aGenome;
    private static Genome hiv2bGenome;
    private static Genome hcvGenome;
    
    private static TestObject patientTestObject;
    private static TestObject resistanceTestObject;
    private static TestObject sequenceAnalysisTestObject;
    private static TestObject genericDrugTestObject;
    private static TestObject viralIsolateAnalysisTestObject;

    private static ValueType numberValueType;
    private static ValueType limitedNumberValueType;
    private static ValueType nominalValueType;
    private static ValueType stringValueType;
    private static ValueType dateValueType;
    
    private static AttributeGroup personalAttributeGroup;
    private static AttributeGroup demographicsAttributeGroup;
    private static AttributeGroup clinicalAttributeGroup;
    
    private static Attribute firstNameAttribute;
    private static Attribute lastNameAttribute;
    private static Attribute birthDateAttribute;
    private static Attribute deathDateAttribute;
    private static Attribute genderAttribute;
    private static Attribute ethnicityAttribute;
    private static Attribute geographicOriginAttribute;
    private static Attribute transmissionGroupAttribute;
    private static Attribute clinicalFileNumberAttribute;
//    private static Attribute countryOfOriginAttribute;
    
    private static Test followUpTest;
    private static Test contactTest;

    private static Test genericCD3Test;
    private static Test genericCD3PercentTest;
    private static Test genericCD4Test;
    private static Test genericCD4PercentageTest;
    private static Test genericCD8Test;
    private static Test genericCD8PercentageTest;
    
    private static Test genericHBVViralLoadTest;
    private static Test genericHCVAbTest;
    
    private static Test genericHBcAbTest;
    private static Test genericHBcAgTest;
    private static Test genericHBeAbTest;
    private static Test genericHBeAgTest;
    private static Test genericHBsAbTest;
    private static Test genericHBsAgTest;
    
    
    private static Test genericCMVIgGTest;
    private static Test genericCMVIgMTest;
    
    private static Test genericToxoIgGTest;
    private static Test genericToxoIgMTest;
    
    private static Test genericHAVIgGTest;
    private static Test genericHAVIgMTest;
       
//    private static Test anrs200607Test;
//    private static Test hivdb429Test;
//    private static Test rega641Test;
//    private static Test rega71Test;

    
    private static Event aidsDefiningIllnessEvent;
    private static Event pregnancyEvent;


    static {
        hiv1Genome = createGenome("HIV-1", "");
        hiv2aGenome = createGenome("HIV-2A", "");
        hiv2bGenome = createGenome("HIV-2B", "");
        hcvGenome = createGenome("HCV","");
        
        numberValueType         = createValueType("number");
        limitedNumberValueType  = createValueType("limited number (<,=,>)");
        nominalValueType        = createValueType("nominal value");
        stringValueType         = createValueType("string");
        dateValueType           = createValueType("date");
        
        personalAttributeGroup = createAttributeGroup("Personal");
        demographicsAttributeGroup = createAttributeGroup("Demographics");
        clinicalAttributeGroup = createAttributeGroup("Clinical");
        
        firstNameAttribute          = createAttribute(Patient.FIRST_NAME, getStringValueType(), getPersonalAttributeGroup());
        lastNameAttribute           = createAttribute(Patient.LAST_NAME, getStringValueType(), getPersonalAttributeGroup());
        birthDateAttribute          = createAttribute(Patient.BIRTH_DATE, getDateValueType(), getPersonalAttributeGroup());
        deathDateAttribute          = createAttribute(Patient.DEATH_DATE, getDateValueType(), getPersonalAttributeGroup());
        
        genderAttribute             = createGender();
        ethnicityAttribute          = createEthnicity();
        geographicOriginAttribute   = createGeographicOrigin();
        transmissionGroupAttribute  = createTransmissionGroup();
        clinicalFileNumberAttribute = createAttribute("Clinical File Number", getStringValueType(), getClinicalAttributeGroup());
//        countryOfOriginAttribute    = createCountryOfOrigin();
        
        patientTestObject           = new TestObject("Patient test", 0);
        sequenceAnalysisTestObject  = new TestObject("Sequence analysis", 1);
        genericDrugTestObject       = new TestObject("Generic drug test", 2);
        resistanceTestObject        = new TestObject("Resistance test", 3);
        viralIsolateAnalysisTestObject = new TestObject("Viral Isolate analysis", 4);
        
        TestType tt;
        List<TestType> genomeTestTypes = new ArrayList<TestType>();
        
        tt = new TestType(patientTestObject, getSeroconversionDescription());
        tt.setGenome(null);
        tt.setValueType(nominalValueType);
        tt.getTestNominalValues().add(new TestNominalValue(tt, "Positive"));
        tt.getTestNominalValues().add(new TestNominalValue(tt, "Negative"));
        genomeTestTypes.add(tt);
        
        tt = new TestType(patientTestObject, getSeroStatusDescription());
        tt.setGenome(null);
        tt.setValueType(nominalValueType);
        tt.getTestNominalValues().add(new TestNominalValue(tt, "Positive"));
        tt.getTestNominalValues().add(new TestNominalValue(tt, "Negative"));
        genomeTestTypes.add(tt);
        
        tt = new TestType(limitedNumberValueType, null, patientTestObject, getViralLoadDescription(), new TreeSet<TestNominalValue>());
        genomeTestTypes.add(tt);
        
        tt = new TestType(limitedNumberValueType, null, patientTestObject, getViralLoadLog10Description(), new TreeSet<TestNominalValue>());
        genomeTestTypes.add(tt);
                
        createStandardGenomeTestTypes(allGenomes,genomeTestTypes,true);

        //create test types without a generic test 
        genomeTestTypes.clear();
        tt = new TestType(numberValueType, null, resistanceTestObject, getGssDescription(), new TreeSet<TestNominalValue>());
        genomeTestTypes.add(tt);
        
        createStandardGenomeTestTypes(allGenomes,genomeTestTypes,false);

        
        genericCD4Test          = createTest(createTestType(numberValueType, null, patientTestObject, "CD4 Count (cells/ul)", new TreeSet<TestNominalValue>()), "CD4 Count (generic)");
        genericCD4PercentageTest= createTest(createTestType(numberValueType, null, patientTestObject, "CD4 Count (%)", new TreeSet<TestNominalValue>()), "CD4 Count % (generic)");
        genericCD8Test          = createTest(createTestType(numberValueType, null, patientTestObject, "CD8 Count", new TreeSet<TestNominalValue>()), "CD8 Count (generic)");
        genericCD8PercentageTest= createTest(createTestType(numberValueType, null, patientTestObject, "CD8 Count (%)", new TreeSet<TestNominalValue>()), "CD8 Count % (generic)");
        followUpTest            = createTest(createTestType(dateValueType, null, patientTestObject, "Follow up",new TreeSet<TestNominalValue>()), "Follow up");
        contactTest             = createTest(createTestType(dateValueType, null, patientTestObject,"Contact",new TreeSet<TestNominalValue>()), "General contact");
        
        genericHBVViralLoadTest = createGenericTest("HBV Viral Load", getLimitedNumberValueType(), null, getPatientTestObject());
        genericHCVAbTest 		= createGenericTest("HCVAb", getNumberValueType(), null, getPatientTestObject());
        genericHBcAbTest 		= createGenericTest("HBcAb", getNumberValueType(), null, getPatientTestObject());
        genericHBcAgTest 		= createGenericTest("HBcAg", getNumberValueType(), null, getPatientTestObject());
        genericHBeAbTest 		= createGenericTest("HBeAb", getNumberValueType(), null, getPatientTestObject());
        genericHBeAgTest 		= createGenericTest("HBeAg", getNumberValueType(), null, getPatientTestObject());
        genericHBsAbTest 		= createGenericTest("HBsAb", getNumberValueType(), null, getPatientTestObject());
        genericHBsAgTest 		= createGenericTest("HBsAg", getNumberValueType(), null, getPatientTestObject());
        genericCD3Test 			= createGenericTest("CD3 Count (cells/ul)", getNumberValueType(), null, getPatientTestObject());
        genericCD3PercentTest 	= createGenericTest("CD3 Count (%)", getNumberValueType(), null, getPatientTestObject());
        genericCMVIgGTest 		= createGenericTest("CMV IgG", getNumberValueType(), null, getPatientTestObject());
        genericCMVIgMTest 		= createGenericTest("CMV IgM", getNumberValueType(), null, getPatientTestObject());
        genericToxoIgGTest 		= createGenericTest("Toxo IgG", getNumberValueType(), null, getPatientTestObject());
        genericToxoIgMTest 		= createGenericTest("Toxo IgM", getNumberValueType(), null, getPatientTestObject());
        genericHAVIgGTest 		= createGenericTest("HAV IgG", getNumberValueType(), null, getPatientTestObject());
        genericHAVIgMTest 		= createGenericTest("HAV IgM", getNumberValueType(), null, getPatientTestObject()); 
        
//        anrs200607Test = createResistanceTest("ANRSV2006.07.xml", "ANRS 2006.07");
//        hivdb429Test = createResistanceTest("HIVDBv4.2.9.xml", "HIVDB 4.2.9");
//        rega641Test = createResistanceTest("RegaV6.4.1.xml", "REGA v6.4.1");
//        rega71Test = createResistanceTest("RegaHIV1V7.1.xml", "REGA v7.1");
        
        aidsDefiningIllnessEvent = createAidsDefiningIllnessEvent();
        pregnancyEvent = createPregnancyEvent();
    }
    
    private static Genome createGenome(String organismName, String organismDescription){
        Genome g = new Genome(organismName, organismDescription);
        allGenomes.add(g);
        return g;
    }
    
    private static TestType createTestType(ValueType vt, Genome g, TestObject to, String description, Set<TestNominalValue> values){
        TestType tt = new TestType(vt, g, to,description,values);
        allTestTypes.add(tt);
        return tt;
    }
    
    private static Test createTest(TestType tt, String description){
        Test t = new Test(tt, description);
        allTests.add(t);
        return t;
    }
    
    private static Event createEvent(String description, ValueType vt){
        Event e = new Event(description);
        e.setValueType(vt);
        allEvents.add(e);
        return e;
    }
    
    private static AttributeGroup createAttributeGroup(String name){
    	AttributeGroup ag = new AttributeGroup(name);
    	allAttributeGroups.add(ag);
    	return ag;
    }
    
    private static ValueType createValueType(String description){
    	ValueType vt = new ValueType(description);
    	allValueTypes.add(vt);
    	return vt;
    }
    
    public static List<Genome> getGenomes(){
        return allGenomes;
    }
    public static List<TestType> getTestTypes(){
        return allTestTypes;
    }
    public static List<Test> getTests(){
        return allTests;
    }
    public static List<Attribute> getAttributes(){
        return allAttributes;
    }
    public static List<Event> getEvents(){
        return allEvents;
    }
    public static List<AttributeGroup> getAttributeGroups(){
    	return allAttributeGroups;
    }
    public static List<ValueType> getValueTypes(){
    	return allValueTypes;
    }
    
    public static Map<String, Map<String, Test>> getStandardGenomeTests(){
        return standardGenomeTests;
    }
    
    private static void createStandardGenomeTestTypes(Collection<Genome> genomes, Collection<TestType> testTypes, boolean genericTest){
        for(Genome g : genomes)
            createStandardGenomeTestTypes(g, testTypes, genericTest);
    }
    private static void createStandardGenomeTestTypes(Genome g, Collection<TestType> testTypes, boolean genericTest){
        Map<String, TestType> ttmap = standardGenomeTestTypes.get(g.getOrganismName());
        if(ttmap == null){
            ttmap = new HashMap<String, TestType>();
            standardGenomeTestTypes.put(g.getOrganismName(), ttmap);
        }
        Map<String, Test> tmap = standardGenomeTests.get(g.getOrganismName());
        if(tmap == null){
            tmap = new HashMap<String, Test>();
            standardGenomeTests.put(g.getOrganismName(), tmap);
        }
        
        for(TestType tt : testTypes){
            TestType ntt = createTestType(tt.getValueType(), g, tt.getTestObject(), tt.getDescription(), new HashSet<TestNominalValue>());
            
            for(TestNominalValue tnv : tt.getTestNominalValues()){
                ntt.getTestNominalValues().add(new TestNominalValue(ntt, tnv.getValue()));
            }
            
            ttmap.put(ntt.getDescription(), ntt);
            
            if(genericTest){
                Test nt = createTest(ntt, ntt.getDescription() +" (generic)");
                tmap.put(ntt.getDescription(), nt);
            }
        }
    }
    
    public static Test getGenericTest(String testTypeDescription, Genome genome){
        Map<String, Test> map = standardGenomeTests.get(genome.getOrganismName());
        if(map == null)
            return null;
        return map.get(testTypeDescription);
    }
    public static TestType getTestType(String testTypeDescription, Genome genome){
        Map<String, TestType> map = standardGenomeTestTypes.get(genome.getOrganismName());
        if(map == null)
            return null;
        return map.get(testTypeDescription);
    }
    
    private static Test createGenericTest(String name, ValueType valueType, Genome genome, TestObject testObject){
        return createTest(createTestType(valueType, genome, testObject,
				name, new TreeSet<TestNominalValue>()),
				name + " (generic)");
    }
    
    
    public static String getViralLoadDescription() {
        return viralLoadDescription;
    }
    public static String getViralLoadLog10Description() {
        return viralLoadLog10Description;
    }
    public static String getSeroStatusDescription() {
        return seroStatusDescription;
    }
    public static String getSeroconversionDescription() {
        return seroconversionDescription;
    }
    public static String getGssDescription() {
        return gssDescription;
    }
    
    public static Genome getHiv1Genome(){
        return hiv1Genome;
    }
    public static Genome getHiv2AGenome(){
        return hiv2aGenome;
    }
    public static Genome getHiv2BGenome(){
        return hiv2bGenome;
    }
    public static Genome getHcvGenome(){
        return hcvGenome;
    }


    public static ValueType getLimitedNumberValueType() {
        return limitedNumberValueType;
    }
    public static ValueType getNumberValueType() {
        return numberValueType;
    }
    public static ValueType getNominalValueType() {
        return nominalValueType;
    }
    public static ValueType getDateValueType(){
        return dateValueType;
    }
    public static ValueType getStringValueType() {
        return stringValueType;
    }

    
    public static TestObject getPatientTestObject() {
        return patientTestObject;
    }
    public static TestObject getViralIsolateAnalysisTestObject() {
        return viralIsolateAnalysisTestObject;
    }
    public static TestObject getSequenceAnalysisTestObject() {
        return sequenceAnalysisTestObject;
    }
    public static TestObject getGenericDrugTestObject() {
        return genericDrugTestObject;
    }
    public static TestObject getResistanceTestObject() {
        return resistanceTestObject;
    }


    public static AttributeGroup getPersonalAttributeGroup(){
        return personalAttributeGroup;
    }
    public static AttributeGroup getDemographicsAttributeGroup(){
        return demographicsAttributeGroup;
    }
    public static AttributeGroup getClinicalAttributeGroup(){
        return clinicalAttributeGroup;
    }
    
    public static Attribute getFirstNameAttribute(){
        return firstNameAttribute;
    }
    public static Attribute getLastNameAttribute(){
        return lastNameAttribute;
    }
    public static Attribute getBirthDateAttribute(){
        return birthDateAttribute;
    }
    public static Attribute getDeathDateAttribute(){
        return deathDateAttribute;
    }
    public static Attribute getGenderAttribute(){
        return genderAttribute;
    }
    public static Attribute getEthnicityAttribute(){
        return ethnicityAttribute;
    }
    public static Attribute getGeoGraphicOriginAttribute(){
        return geographicOriginAttribute;
    }
    public static Attribute getTransmissionGroupAttribute(){
        return transmissionGroupAttribute;
    }
    public static Attribute getClinicalFileNumberAttribute(){
        return clinicalFileNumberAttribute;
    }
//    public static Attribute getCountryOfOriginAttribute(){
//        return countryOfOriginAttribute;
//    }

    public static TestType getCD3TestType(){
        return genericCD3Test.getTestType();
    }
    public static Test getGenericCD3Test(){
        return genericCD3Test;
    }
    public static TestType getCD3PercentTestType(){
        return genericCD3PercentTest.getTestType();
    }
    public static Test getGenericCD3PercentTest(){
        return genericCD3PercentTest;
    }
    
    public static TestType getCd4TestType() {
        return getGenericCD4Test().getTestType();
    }
    public static Test getGenericCD4Test() {
        return genericCD4Test;
    }
    public static TestType getCd4PercentageTestType() {
        return getGenericCD4PercentageTest().getTestType();
    }
    public static Test getGenericCD4PercentageTest() {
        return genericCD4PercentageTest;
    }
    public static boolean isCD4(TestType tt) {
        return getCd4TestType().getDescription().equals(tt.getDescription());
    }
    
    public static TestType getCd8TestType() {
        return getGenericCD8Test().getTestType();
    }
    public static Test getGenericCD8Test() {
        return genericCD8Test;
    }   
    public static TestType getCd8PercentageTestType() {
        return getGenericCD8PercentageTest().getTestType();
    }
    public static Test getGenericCD8PercentageTest() {
        return genericCD8PercentageTest;
    }
    
    public static TestType getFollowUpTestType(){
        return getFollowUpTest().getTestType();
    }
    public static Test getFollowUpTest(){
        return followUpTest;
    }
    
    public static TestType getContactTestType(){
    	return getContactTest().getTestType();
    }
    public static Test getContactTest(){
    	return contactTest;
    }

    public static Test getGenericHiv1ViralLoadTest() {
        return getGenericTest(getViralLoadDescription(), getHiv1Genome());
    }
    public static Test getGenericHiv1ViralLoadLog10Test() {
        return getGenericTest(getViralLoadLog10Description(), getHiv1Genome());
    }
    public static TestType getHiv1ViralLoadTestType() {
        return getGenericHiv1ViralLoadTest().getTestType();
    }
    public static TestType getHiv1ViralLoadLog10TestType() {
        return getGenericHiv1ViralLoadLog10Test().getTestType();
    }
    public static boolean isViralLoad(TestType tt) {
        return getViralLoadDescription().equals(tt.getDescription());
    }
    public static boolean isViralLoadLog10(TestType tt) {
        return getViralLoadLog10Description().equals(tt.getDescription());
    }
    
    public static Test getGenericHiv1SeroStatusTest() {
        return getGenericTest(getSeroStatusDescription(), getHiv1Genome());
    }
    public static TestType getHiv1SeroStatusTestType() {
        return getGenericHiv1SeroStatusTest().getTestType();
    }
    
    public static Test getHiv1SeroconversionTest() {
        return getGenericTest(getSeroconversionDescription(), getHiv1Genome());
    }
    public static TestType getHiv1SeroconversionTestType() {
        return getHiv1SeroconversionTest().getTestType();
    }
    
    public static Test getGenericHCVAbTest(){
    	return genericHCVAbTest;
    }
    public static TestType getHCVAbTestType(){
    	return genericHCVAbTest.getTestType();
    }

    public static Test getGenericHBVViralLoadTest(){
    	return genericHBVViralLoadTest;
    }
    public static TestType getHBVViralLoadTestType(){
    	return genericHBVViralLoadTest.getTestType();
    }
    
    public static Test getGenericHCVViralLoadTest(){
    	return getGenericTest(getViralLoadDescription(), getHcvGenome());
    }
    public static TestType getHCVViralLoadTestType(){
    	return getGenericHCVViralLoadTest().getTestType();
    }

    public static Test getGenericHBcAbTest(){
    	return genericHBcAbTest;
    }
    public static TestType getHBcAbTestType(){
    	return genericHBcAbTest.getTestType();
    }

    public static Test getGenericHBcAgTest(){
    	return genericHBcAgTest;
    }
    public static TestType getHBcAgTestType(){
    	return genericHBcAgTest.getTestType();
    }

    public static Test getGenericHBeAbTest(){
    	return genericHBeAbTest;
    }
    public static TestType getHBeAbTestType(){
    	return genericHBeAbTest.getTestType();
    }

    public static Test getGenericHBeAgTest(){
    	return genericHBeAgTest;
    }
    public static TestType getHBeAgTestType(){
    	return genericHBeAgTest.getTestType();
    }

    public static Test getGenericHBsAbTest(){
    	return genericHBsAbTest;
    }
    public static TestType getHBsAbTestType(){
    	return genericHBsAbTest.getTestType();
    }

    public static Test getGenericHBsAgTest(){
    	return genericHBsAgTest;
    }
    public static TestType getHBsAgTestType(){
    	return genericHBsAgTest.getTestType();
    }

    public static Test getGenericCMVIgGTest(){
    	return genericCMVIgGTest;
    }
    public static TestType getCMVIgGTestType(){
    	return genericCMVIgGTest.getTestType();
    }

    public static Test getGenericCMVIgMTest(){
    	return genericCMVIgMTest;
    }
    public static TestType getCMVIgMTestType(){
    	return genericCMVIgMTest.getTestType();
    }

    public static Test getGenericToxoIgGTest(){
    	return genericToxoIgGTest;
    }
    public static TestType getToxoIgGTestType(){
    	return genericToxoIgGTest.getTestType();
    }

    public static Test getGenericToxoIgMTest(){
    	return genericToxoIgMTest;
    }
    public static TestType getToxoIgMTestType(){
    	return genericToxoIgMTest.getTestType();
    }

    public static Test getGenericHAVIgGTest(){
    	return genericHAVIgGTest;
    }
    public static TestType getHAVIgGTestType(){
    	return genericHAVIgGTest.getTestType();
    }

    public static Test getGenericHAVIgMTest(){
    	return genericHAVIgMTest;
    }
    public static TestType getHAVIgMTestType(){
    	return genericHAVIgMTest.getTestType();
    }
    
//    public static Test getAnrs200607Test(){
//        return anrs200607Test;
//    }
//    public static Test getHivdb429Test(){
//        return hivdb429Test;
//    }
//    public static Test getRega641Test(){
//        return rega641Test;
//    }
//    public static Test getRega71Test(){
//        return rega71Test;
//    }
    
    public static Event getAidsDefiningIllnessEvent(){
        return aidsDefiningIllnessEvent;
    }
    
    public static Event getPregnancyEvent() {
    	return pregnancyEvent;
    }
    
    public static int getHqlQueryQueryType(){
        return 1;
    }
    public static int getQueryToolQueryType(){
        return 2;
    }

    
    
    
    private static Event createAidsDefiningIllnessEvent(){
    	Event e = createEvent("Aids defining illness", getNominalValueType());
        
        e.getEventNominalValues().add(new EventNominalValue(e,"Bacillary angiomatosis"));
        e.getEventNominalValues().add(new EventNominalValue(e,"Candidiasis of bronchi, trachea, or lungs"));
        e.getEventNominalValues().add(new EventNominalValue(e,"Candidiasis, esophageal"));
        e.getEventNominalValues().add(new EventNominalValue(e,"Candidiasis, oropharyngeal (thrush)"));
        e.getEventNominalValues().add(new EventNominalValue(e,"Candidiasis, vulvovaginal; persistent, frequent, or poorly responsive to therapy"));
        e.getEventNominalValues().add(new EventNominalValue(e,"Cervical cancer, invasive"));
        e.getEventNominalValues().add(new EventNominalValue(e,"Cervical dysplasia (moderate or severe)/cervical carcinoma in situ"));
        e.getEventNominalValues().add(new EventNominalValue(e,"Coccidioidomycosis, disseminated or extrapulmonary"));
        e.getEventNominalValues().add(new EventNominalValue(e,"Constitutional symptoms, such as fever (38.5 C) or diarrhea lasting greater than 1 month"));
        e.getEventNominalValues().add(new EventNominalValue(e,"Cryptococcosis, extrapulmonary"));
        e.getEventNominalValues().add(new EventNominalValue(e,"Cryptosporidiosis, chronic intestinal (greater than 1 month's duration)"));
        e.getEventNominalValues().add(new EventNominalValue(e,"Cytomegalovirus disease (other than liver, spleen, or nodes)"));
        e.getEventNominalValues().add(new EventNominalValue(e,"Cytomegalovirus retinitis (with loss of vision)"));
        e.getEventNominalValues().add(new EventNominalValue(e,"Encephalopathy, HIV-related"));
        e.getEventNominalValues().add(new EventNominalValue(e,"Hairy leukoplakia, oral"));
        e.getEventNominalValues().add(new EventNominalValue(e,"Herpes simplex: chronic ulcer(s) (greater than 1 month's duration); or bronchitis, pneumonitis, or esophagitis"));
        e.getEventNominalValues().add(new EventNominalValue(e,"Herpes zoster (shingles), involving at least two distinct episodes or more than one dermatome"));
        e.getEventNominalValues().add(new EventNominalValue(e,"Histoplasmosis, disseminated or extrapulmonary"));
        e.getEventNominalValues().add(new EventNominalValue(e,"Idiopathic thrombocytopenic purpura"));
        e.getEventNominalValues().add(new EventNominalValue(e,"Isosporiasis, chronic intestinal (greater than 1 month's duration)"));
        e.getEventNominalValues().add(new EventNominalValue(e,"Kaposi's sarcoma"));
        e.getEventNominalValues().add(new EventNominalValue(e,"Listeriosis"));
        e.getEventNominalValues().add(new EventNominalValue(e,"Hodgkin's lymphoma"));
        e.getEventNominalValues().add(new EventNominalValue(e,"Lymphoma, Burkitt's (or equivalent term)"));
        e.getEventNominalValues().add(new EventNominalValue(e,"Lymphoma, immunoblastic (or equivalent term)"));
        e.getEventNominalValues().add(new EventNominalValue(e,"Lymphoma, primary, of brain"));
        e.getEventNominalValues().add(new EventNominalValue(e,"Mycobacterium avium complex or M. kansasii, disseminated or extrapulmonary"));
        e.getEventNominalValues().add(new EventNominalValue(e,"Mycobacterium tuberculosis, any site (pulmonary or extrapulmonary)"));
        e.getEventNominalValues().add(new EventNominalValue(e,"Mycobacterium, other species or unidentified species, disseminated or extrapulmonary"));
        e.getEventNominalValues().add(new EventNominalValue(e,"Pelvic inflammatory disease, particularly if complicated by tubo-ovarian abscess"));
        e.getEventNominalValues().add(new EventNominalValue(e,"Peripheral neuropathy"));
        e.getEventNominalValues().add(new EventNominalValue(e,"Pneumocystis carinii pneumonia"));
        e.getEventNominalValues().add(new EventNominalValue(e,"Pneumonia, recurrent"));
        e.getEventNominalValues().add(new EventNominalValue(e,"Progressive multifocal leukoencephalopathy"));
        e.getEventNominalValues().add(new EventNominalValue(e,"Salmonella septicemia, recurrent"));
        e.getEventNominalValues().add(new EventNominalValue(e,"Toxoplasmosis of brain"));
        e.getEventNominalValues().add(new EventNominalValue(e,"Wasting syndrome due to HIV"));
        e.getEventNominalValues().add(new EventNominalValue(e,"Visceral leishmaniasis"));
        e.getEventNominalValues().add(new EventNominalValue(e,"Strongyloidiasis, disseminated"));
        
        return e;
    }
    
    private static Event createPregnancyEvent(){
    	Event e = createEvent("Pregnancy",getNominalValueType());
        
        e.getEventNominalValues().add(new EventNominalValue(e,"Positive"));
        e.getEventNominalValues().add(new EventNominalValue(e,"Negative"));
      
        return e;
    }

    private static Attribute createAttribute(String name, ValueType vt, AttributeGroup ag){
        Attribute a = new Attribute(name);
        a.setAttributeGroup(ag);
        a.setValueType(vt);
        allAttributes.add(a);
        return a;
    }
    
    private static Attribute createGender()
    {
        Attribute transmissionGroup = createAttribute("Gender", getNominalValueType(), getPersonalAttributeGroup());
        
        transmissionGroup.getAttributeNominalValues().add(new AttributeNominalValue(transmissionGroup, "male"));
        transmissionGroup.getAttributeNominalValues().add(new AttributeNominalValue(transmissionGroup, "female"));
        
        return transmissionGroup;
    }
    
    private static Attribute createTransmissionGroup()
    {
        Attribute transmissionGroup = createAttribute("Transmission group",getNominalValueType(),getClinicalAttributeGroup());
        
        transmissionGroup.getAttributeNominalValues().add(new AttributeNominalValue(transmissionGroup, "bisexual"));
        transmissionGroup.getAttributeNominalValues().add(new AttributeNominalValue(transmissionGroup, "heterosexual"));
        transmissionGroup.getAttributeNominalValues().add(new AttributeNominalValue(transmissionGroup, "homosexual"));
        transmissionGroup.getAttributeNominalValues().add(new AttributeNominalValue(transmissionGroup, "IVDU"));
        transmissionGroup.getAttributeNominalValues().add(new AttributeNominalValue(transmissionGroup, "other"));
        transmissionGroup.getAttributeNominalValues().add(new AttributeNominalValue(transmissionGroup, "vertical"));
        transmissionGroup.getAttributeNominalValues().add(new AttributeNominalValue(transmissionGroup, "transfusion"));
        transmissionGroup.getAttributeNominalValues().add(new AttributeNominalValue(transmissionGroup, "occupational exposure"));
        transmissionGroup.getAttributeNominalValues().add(new AttributeNominalValue(transmissionGroup, "unknown"));
        
        return transmissionGroup;
    }
    
    private static Attribute createGeographicOrigin()
    {
        Attribute geographicOrigin = createAttribute("Geographic origin", getNominalValueType(),getDemographicsAttributeGroup());
        
        geographicOrigin.getAttributeNominalValues().add(new AttributeNominalValue(geographicOrigin, "Africa"));
        geographicOrigin.getAttributeNominalValues().add(new AttributeNominalValue(geographicOrigin, "Asia"));
        geographicOrigin.getAttributeNominalValues().add(new AttributeNominalValue(geographicOrigin, "North America"));
        geographicOrigin.getAttributeNominalValues().add(new AttributeNominalValue(geographicOrigin, "South America"));
        geographicOrigin.getAttributeNominalValues().add(new AttributeNominalValue(geographicOrigin, "Europe"));
        geographicOrigin.getAttributeNominalValues().add(new AttributeNominalValue(geographicOrigin, "Subsaharan Africa"));
        geographicOrigin.getAttributeNominalValues().add(new AttributeNominalValue(geographicOrigin, "North Africa"));
        geographicOrigin.getAttributeNominalValues().add(new AttributeNominalValue(geographicOrigin, "Eastern Europe"));
        
        return geographicOrigin;
    }
    
    private static Attribute createEthnicity()
    {
        Attribute ethnicity = createAttribute("Ethnicity", getNominalValueType(), getDemographicsAttributeGroup());
        
        ethnicity.getAttributeNominalValues().add(new AttributeNominalValue(ethnicity, "african"));
        ethnicity.getAttributeNominalValues().add(new AttributeNominalValue(ethnicity, "asian"));
        ethnicity.getAttributeNominalValues().add(new AttributeNominalValue(ethnicity, "caucasian"));
        
        return ethnicity;
    }

    public static TestType getGssTestType(Genome genome) {
        return getTestType(getGssDescription(), genome);
    }

   
    
//    private static Attribute createCountryOfOrigin()
//    {
//        Table countries = null;
//        Attribute country = new Attribute("Country of origin");
//        country.setAttributeGroup(getRegaDBAttributeGroup());
//        country.setValueType(getNominalValueType());
//        
//        try 
//        {
//            countries = new Table(new BufferedInputStream(new FileInputStream("io-assist-files"+File.separatorChar+"countrylist.csv")), false);
//        } 
//        catch (FileNotFoundException e) 
//        {
//            e.printStackTrace();
//        }
//        
//        ArrayList<String> countryList = countries.getColumn(1);
//        ArrayList<String> typeList = countries.getColumn(3);
//        for(int i = 1; i < countryList.size(); i++)
//        {
//            if(typeList.get(i).equals("Independent State"))
//            {
//                AttributeNominalValue anv = new AttributeNominalValue(country, countryList.get(i));
//                country.getAttributeNominalValues().add(anv);
//            }
//        }
//        
//        return country;
//    }
//    
//    private static Test createResistanceTest(String baseFileName, String algorithm)
//    {
//        TestType resistanceTestType = new TestType(new TestObject("Resistance test", 3), getGssId());
//        resistanceTestType.setValueType(getNumberValueType());
//        Test resistanceTest = new Test(resistanceTestType, algorithm);
//        
//        Analysis analysis = new Analysis();
//        analysis.setUrl(RegaDBWtsServer.url_);
//        analysis.setAnalysisType(new AnalysisType("wts"));
//        analysis.setAccount("public");
//        analysis.setPassword("public");
//        analysis.setBaseinputfile("viral_isolate");
//        analysis.setBaseoutputfile("interpretation");
//        analysis.setServiceName("regadb-hiv-resist");
//        
//        byte[] algo = null;
//        try 
//        {
//            algo = FileUtils.readFileToByteArray(new File("io-assist-files"+File.separatorChar+baseFileName));
//        } 
//        catch (IOException e)
//        {
//            e.printStackTrace();
//        }
//        analysis.getAnalysisDatas().add(new AnalysisData(analysis, "asi_rules", algo, "application/xml"));
//
//        resistanceTest.setAnalysis(analysis);
//        
//        return resistanceTest;
//    }
}
