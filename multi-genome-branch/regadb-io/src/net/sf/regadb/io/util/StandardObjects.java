/*
 * Created on May 10, 2007
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package net.sf.regadb.io.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import net.sf.regadb.db.Event;
import net.sf.regadb.db.EventNominalValue;
import net.sf.regadb.db.Genome;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestNominalValue;
import net.sf.regadb.db.TestObject;
import net.sf.regadb.db.TestType;
import net.sf.regadb.db.ValueType;

public class StandardObjects {
    private static String viralLoadDescription = "Viral Load (copies/ml)";
    private static String viralLoadLog10Description = "Viral Load (log10)";
    private static String seroStatusDescription = "Serostatus";
    private static String seroconversionDescription = "Serovoncersion";
    
    private static List<TestType> standardGenomeTestTypes = new ArrayList<TestType>();
    private static Map<String, Map<String, Test>> standardGenomeTests = new HashMap<String, Map<String, Test>>();
    
    private static Genome hiv1Genome = new Genome("HIV-1", "");
    
    private static TestObject patientObject = new TestObject("Patient test", 0);
    private static TestObject viralIsolateObject = new TestObject("Viral Isolate analysis", 4);
    private static ValueType numberValueType = new ValueType("number");
    private static ValueType limitedNumberValueType = new ValueType("limited number (<,=,>)");
    private static ValueType nominalValueType = new ValueType("nominal value");
    private static ValueType stringValueType = new ValueType("string");
    private static ValueType dateValueType = new ValueType("date");
    private static TestType cd4TestType = new TestType(numberValueType, null, patientObject, "CD4 Count (cells/ul)", new TreeSet<TestNominalValue>());
    private static TestType cd4PercentageTestType = new TestType(numberValueType, null, patientObject, "CD4 Count (%)", new TreeSet<TestNominalValue>());
    private static TestType cd8TestType = new TestType(numberValueType, null, patientObject, "CD8 Count", new TreeSet<TestNominalValue>());
    private static TestType cd8PercentageTestType = new TestType(numberValueType, null, patientObject, "CD8 Count (%)", new TreeSet<TestNominalValue>());
    private static TestType followUpTestType = new TestType(dateValueType, null, patientObject, "Follow up",new TreeSet<TestNominalValue>());
    private static Test genericCD4Test = new Test(cd4TestType, "CD4 Count (generic)");
    private static Test genericCD4PercentageTest = new Test(cd4PercentageTestType, "CD4 Count % (generic)");
    private static Test genericCD8Test = new Test(cd8TestType, "CD8 Count (generic)");
    private static Test genericCD8PercentageTest = new Test(cd8PercentageTestType, "CD8 Count % (generic)");
    private static Test followUpTest = new Test(followUpTestType, "Follow up");
    
    private static TestType contactTestType = new TestType(dateValueType, null, patientObject,"Contact",new TreeSet<TestNominalValue>());
    private static Test contactTest = new Test(contactTestType, "General contact");
    
    private static String gssId = "Genotypic Susceptibility Score (GSS)";
    private static String clinicalFileNumberAttribute = "Clinical File Number";
    
    private static Test pregnancy;
    
    private static Test genericHBVViralLoadTest;
    private static Test genericHCVViralLoadTest;
    private static Test genericHCVAbTest;
    
    private static Test genericHBcAbTest;
    private static Test genericHBcAgTest;
    private static Test genericHBeAbTest;
    private static Test genericHBeAgTest;
    private static Test genericHBsAbTest;
    private static Test genericHBsAgTest;
    
    private static Test genericCD3Test;
    private static Test genericCD3PercentTest;
    
    private static Test genericCMVIgGTest;
    private static Test genericCMVIgMTest;
    
    private static Test genericToxoIgGTest;
    private static Test genericToxoIgMTest;
    
    private static Test genericHAVIgGTest;
    private static Test genericHAVIgMTest;
    
    private static Event aidsDefiningIllnessEvent;

    static {
        TestType tt;
        
        tt = new TestType(nominalValueType, null, patientObject, getSeroconversionDescription(), new TreeSet<TestNominalValue>());
        tt.getTestNominalValues().add(new TestNominalValue(tt, "Positive"));
        tt.getTestNominalValues().add(new TestNominalValue(tt, "Negative"));
        standardGenomeTestTypes.add(tt);
        
        tt = new TestType(nominalValueType, null, patientObject, getSeroStatusDescription(), new TreeSet<TestNominalValue>());
        tt.getTestNominalValues().add(new TestNominalValue(tt, "Positive"));
        tt.getTestNominalValues().add(new TestNominalValue(tt, "Negative"));
        standardGenomeTestTypes.add(tt);
        
        tt = new TestType(limitedNumberValueType, null, patientObject, getViralLoadDescription(), new TreeSet<TestNominalValue>());
        standardGenomeTestTypes.add(tt);
        
        tt = new TestType(limitedNumberValueType, null, patientObject, getViralLoadLog10Description(), new TreeSet<TestNominalValue>());
        standardGenomeTestTypes.add(tt);
        
        createStandardGenomeTests(getHiv1Genome());
        
        TestType pregnancyType = new TestType(new TestObject("Patient test", 0), "Pregnancy");
        pregnancyType.setValueType(nominalValueType);
        pregnancyType.getTestNominalValues().add(new TestNominalValue(pregnancyType, "Positive"));
        pregnancyType.getTestNominalValues().add(new TestNominalValue(pregnancyType, "Negative"));
        
        pregnancy = new Test(pregnancyType, "Pregnancy");

        genericHBVViralLoadTest = createGenericTest("HBV Viral Load", getLimitedNumberValueType(), null, getPatientObject());
        genericHCVViralLoadTest = createGenericTest("HCV Viral Load", getLimitedNumberValueType(), null, getPatientObject());
        genericHCVAbTest 		= createGenericTest("HCVAb", getNumberValueType(), null, getPatientObject());
        genericHBcAbTest 		= createGenericTest("HBcAb", getNumberValueType(), null, getPatientObject());
        genericHBcAgTest 		= createGenericTest("HBcAg", getNumberValueType(), null, getPatientObject());
        genericHBeAbTest 		= createGenericTest("HBeAb", getNumberValueType(), null, getPatientObject());
        genericHBeAgTest 		= createGenericTest("HBeAg", getNumberValueType(), null, getPatientObject());
        genericHBsAbTest 		= createGenericTest("HBsAb", getNumberValueType(), null, getPatientObject());
        genericHBsAgTest 		= createGenericTest("HBsAg", getNumberValueType(), null, getPatientObject());
        genericCD3Test 			= createGenericTest("CD3 Count (cells/ul)", getNumberValueType(), null, getPatientObject());
        genericCD3PercentTest 	= createGenericTest("CD3 Count (%)", getNumberValueType(), null, getPatientObject());
        genericCMVIgGTest 		= createGenericTest("CMV IgG", getNumberValueType(), null, getPatientObject());
        genericCMVIgMTest 		= createGenericTest("CMV IgM", getNumberValueType(), null, getPatientObject());
        genericToxoIgGTest 		= createGenericTest("Toxo IgG", getNumberValueType(), null, getPatientObject());
        genericToxoIgMTest 		= createGenericTest("Toxo IgM", getNumberValueType(), null, getPatientObject());
        genericHAVIgGTest 		= createGenericTest("HAV IgG", getNumberValueType(), null, getPatientObject());
        genericHAVIgMTest 		= createGenericTest("HAV IgM", getNumberValueType(), null, getPatientObject());
        
        aidsDefiningIllnessEvent = createAidsDefiningIllnessEvent();
    }
    
    private static void createStandardGenomeTests(Genome g){
        Map<String, Test> map = standardGenomeTests.get(g.getOrganismName());
        if(map == null){
            map = new HashMap<String, Test>();
            standardGenomeTests.put(g.getOrganismName(), map);
        }
        
        for(TestType tt : standardGenomeTestTypes){
            TestType ntt = new TestType(tt.getValueType(), g, tt.getTestObject(), tt.getDescription(), new TreeSet<TestNominalValue>());
            
            for(TestNominalValue tnv : tt.getTestNominalValues()){
                ntt.getTestNominalValues().add(new TestNominalValue(ntt, tnv.getValue()));
            }
            
            Test nt = new Test(tt, tt.getDescription() +" (generic)");
            
            map.put(tt.getDescription(), nt);
        }
    }
    
    public static Test getGenericTest(String testTypeDescription, Genome genome){
        Map<String, Test> map = standardGenomeTests.get(genome.getOrganismName());
        if(map == null)
            return null;
        return map.get(testTypeDescription);
    }
    public static TestType getTestType(String testTypeDescription, Genome genome){
        Test t = getGenericTest(testTypeDescription, genome);
        return (t == null ? null : t.getTestType());
    }
    
    private static Test createGenericTest(String name, ValueType valueType, Genome genome, TestObject testObject){
        return new Test(new TestType(valueType, genome, testObject,
				name, new TreeSet<TestNominalValue>()),
				name + " (generic)");
    }
    
    public static Test getPregnancyTest() {
        return pregnancy;
    }
    
    public static TestType getCd4TestType() {
        return cd4TestType;
    }
    public static Test getGenericCD4Test() {
        return genericCD4Test;
    }
    public static TestType getCd4PercentageTestType() {
        return cd4PercentageTestType;
    }
    public static Test getGenericCD4PercentageTest() {
        return genericCD4PercentageTest;
    }
    public static ValueType getLimitedNumberValueType() {
        return limitedNumberValueType;
    }
    public static ValueType getNumberValueType() {
        return numberValueType;
    }
    public static TestObject getPatientObject() {
        return patientObject;
    }
    public static TestObject getViralIsolateObject() {
        return viralIsolateObject;
    }
    public static ValueType getNominalValueType() {
        return nominalValueType;
    }
    public static String getGssId() {
        return gssId;
    }
    public static String getClinicalFileNumber() {
        return clinicalFileNumberAttribute;
    }
    public static boolean isCD4(TestType tt) {
        return cd4TestType.getDescription().equals(tt.getDescription());
    }
    public static ValueType getStringValueType() {
        return stringValueType;
    }
    public static ValueType getDateValueType(){
        return dateValueType;
    }
    public static TestType getCd8TestType() {
        return cd8TestType;
    }
    public static Test getGenericCD8Test() {
        return genericCD8Test;
    }
    public static TestType getCd8PercentageTestType() {
        return cd8PercentageTestType;
    }
    public static Test getGenericCD8PercentageTest() {
        return genericCD8PercentageTest;
    }
    public static TestType getFollowUpTestType(){
        return followUpTestType;
    }
    public static Test getFollowUpTest(){
        return followUpTest;
    }
    
    public static TestType getContactTestType(){
    	return contactTestType;
    }
    public static Test getContactTest(){
    	return contactTest;
    }

    public static int getHqlQueryQueryType(){
        return 1;
    }

    public static int getQueryToolQueryType(){
        return 2;
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
    public static boolean isHiv1ViralLoad(TestType tt) {
        return getViralLoadDescription().equals(tt.getDescription());
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
    	return genericHCVViralLoadTest;
    }
    public static TestType getHCVViralLoadTestType(){
    	return genericHCVViralLoadTest.getTestType();
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

    public static Test getGenericCD3Test(){
    	return genericCD3Test;
    }
    public static TestType getCD3TestType(){
    	return genericCD3Test.getTestType();
    }

    public static Test getGenericCD3PercentTest(){
    	return genericCD3PercentTest;
    }
    public static TestType getCD3PercentTestType(){
    	return genericCD3PercentTest.getTestType();
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
    
    private static Event createAidsDefiningIllnessEvent(){
    	Event e = new Event();
        e.setValueType(getNominalValueType());
        e.setName("Aids defining illness");
        
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
    
    public static Event getAidsDefiningIllnessEvent(){
    	return aidsDefiningIllnessEvent;
    }
    
    public static Genome getHiv1Genome(){
        return hiv1Genome;
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
}
