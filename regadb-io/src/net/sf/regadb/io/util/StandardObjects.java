/*
 * Created on May 10, 2007
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package net.sf.regadb.io.util;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import net.sf.regadb.db.Event;
import net.sf.regadb.db.EventNominalValue;
import net.sf.regadb.db.Protein;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestNominalValue;
import net.sf.regadb.db.TestObject;
import net.sf.regadb.db.TestType;
import net.sf.regadb.db.ValueType;

public class StandardObjects {
    private static TestObject patientObject = new TestObject("Patient test", 0);
    private static TestObject viralIsolateObject = new TestObject("Viral Isolate analysis", 4);
    private static ValueType numberValueType = new ValueType("number");
    private static ValueType limitedNumberValueType = new ValueType("limited number (<,=,>)");
    private static ValueType nominalValueType = new ValueType("nominal value");
    private static ValueType stringValueType = new ValueType("string");
    private static ValueType dateValueType = new ValueType("date");
    private static TestType hiv1ViralLoadTestType = new TestType(limitedNumberValueType, patientObject, "HIV-1 Viral Load (copies/ml)", new TreeSet<TestNominalValue>());
    private static TestType hiv1ViralLoadLog10TestType = new TestType(limitedNumberValueType, patientObject, "HIV-1 Viral Load (log10)", new TreeSet<TestNominalValue>());
    private static TestType cd4TestType = new TestType(numberValueType, patientObject, "CD4 Count (cells/ul)", new TreeSet<TestNominalValue>());
    private static TestType cd4PercentageTestType = new TestType(numberValueType, patientObject, "CD4 Count (%)", new TreeSet<TestNominalValue>());
    private static TestType cd8TestType = new TestType(numberValueType, patientObject, "CD8 Count", new TreeSet<TestNominalValue>());
    private static TestType cd8PercentageTestType = new TestType(numberValueType, patientObject, "CD8 Count (%)", new TreeSet<TestNominalValue>());
    private static TestType hiv1SeroStatusTestType;
    private static TestType followUpTestType = new TestType(dateValueType, patientObject, "Follow up",new TreeSet<TestNominalValue>());
    private static Test genericHiv1ViralLoadTest = new Test(hiv1ViralLoadTestType, "HIV-1 Viral Load (generic)");
    private static Test genericHiv1ViralLoadLog10Test = new Test(hiv1ViralLoadLog10TestType, "HIV-1 Viral Load log10 (generic)");
    private static Test genericCD4Test = new Test(cd4TestType, "CD4 Count (generic)");
    private static Test genericCD4PercentageTest = new Test(cd4PercentageTestType, "CD4 Count % (generic)");
    private static Test genericCD8Test = new Test(cd8TestType, "CD8 Count (generic)");
    private static Test genericCD8PercentageTest = new Test(cd8PercentageTestType, "CD8 Count % (generic)");
    private static Test genericHiv1SeroStatusTest;
    private static Test followUpTest = new Test(followUpTestType, "Follow up");
    
    private static TestType contactTestType = new TestType(dateValueType,patientObject,"Contact",new TreeSet<TestNominalValue>());
    private static Test contactTest = new Test(contactTestType, "General contact");
    
    private static String gssId = "Genotypic Susceptibility Score (GSS)";
    private static String clinicalFileNumberAttribute = "Clinical File Number";
    
    private static Test pregnancy;
    
    private static TestType hiv1SeroconversionTestType;
    private static Test hiv1SeroconversionTest;

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
        hiv1SeroStatusTestType = new TestType(patientObject, "HIV-1 Sero Status");
        hiv1SeroStatusTestType.setValueType(nominalValueType);
        hiv1SeroStatusTestType.getTestNominalValues().add(new TestNominalValue(hiv1SeroStatusTestType, "Positive"));
        hiv1SeroStatusTestType.getTestNominalValues().add(new TestNominalValue(hiv1SeroStatusTestType, "Negative"));
        genericHiv1SeroStatusTest = new Test(hiv1SeroStatusTestType, "HIV-1 Sero Status (generic)");
        
        hiv1SeroconversionTestType = new TestType(patientObject, "HIV-1 Seroconversion");
        hiv1SeroconversionTestType.setValueType(nominalValueType);
        hiv1SeroconversionTestType.getTestNominalValues().add(new TestNominalValue(hiv1SeroconversionTestType, "Positive"));
        hiv1SeroconversionTestType.getTestNominalValues().add(new TestNominalValue(hiv1SeroconversionTestType, "Negative"));
        hiv1SeroconversionTest = new Test(hiv1SeroconversionTestType, "HIV-1 Seroconversion");

        TestType pregnancyType = new TestType(new TestObject("Patient test", 0), "Pregnancy");
        pregnancyType.setValueType(nominalValueType);
        pregnancyType.getTestNominalValues().add(new TestNominalValue(pregnancyType, "Positive"));
        pregnancyType.getTestNominalValues().add(new TestNominalValue(pregnancyType, "Negative"));
        
        pregnancy = new Test(pregnancyType, "Pregnancy");

        genericHBVViralLoadTest = createGenericTest("HBV Viral Load", getLimitedNumberValueType(), getPatientObject());
        genericHCVViralLoadTest = createGenericTest("HCV Viral Load", getLimitedNumberValueType(), getPatientObject());
        genericHCVAbTest 		= createGenericTest("HCVAb", getNumberValueType(), getPatientObject());
        genericHBcAbTest 		= createGenericTest("HBcAb", getNumberValueType(), getPatientObject());
        genericHBcAgTest 		= createGenericTest("HBcAg", getNumberValueType(), getPatientObject());
        genericHBeAbTest 		= createGenericTest("HBeAb", getNumberValueType(), getPatientObject());
        genericHBeAgTest 		= createGenericTest("HBeAg", getNumberValueType(), getPatientObject());
        genericHBsAbTest 		= createGenericTest("HBsAb", getNumberValueType(), getPatientObject());
        genericHBsAgTest 		= createGenericTest("HBsAg", getNumberValueType(), getPatientObject());
        genericCD3Test 			= createGenericTest("CD3 Count (cells/ul)", getNumberValueType(), getPatientObject());
        genericCD3PercentTest 	= createGenericTest("CD3 Count (%)", getNumberValueType(), getPatientObject());
        genericCMVIgGTest 		= createGenericTest("CMV IgG", getNumberValueType(), getPatientObject());
        genericCMVIgMTest 		= createGenericTest("CMV IgM", getNumberValueType(), getPatientObject());
        genericToxoIgGTest 		= createGenericTest("Toxo IgG", getNumberValueType(), getPatientObject());
        genericToxoIgMTest 		= createGenericTest("Toxo IgM", getNumberValueType(), getPatientObject());
        genericHAVIgGTest 		= createGenericTest("HAV IgG", getNumberValueType(), getPatientObject());
        genericHAVIgMTest 		= createGenericTest("HAV IgM", getNumberValueType(), getPatientObject());
        
        aidsDefiningIllnessEvent = createAidsDefiningIllnessEvent();
    }
    
    private static Test createGenericTest(String name, ValueType valueType, TestObject testObject){
        return new Test(new TestType(valueType, testObject,
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
    public static Test getGenericHiv1ViralLoadTest() {
        return genericHiv1ViralLoadTest;
    }
    public static Test getGenericHiv1ViralLoadLog10Test() {
        return genericHiv1ViralLoadLog10Test;
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
    public static TestType getHiv1ViralLoadTestType() {
        return hiv1ViralLoadTestType;
    }
    public static TestType getHiv1ViralLoadLog10TestType() {
        return hiv1ViralLoadLog10TestType;
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
    public static boolean isHiv1ViralLoad(TestType tt) {
        return hiv1ViralLoadTestType.getDescription().equals(tt.getDescription());
    }
    public static boolean isCD4(TestType tt) {
        return cd4TestType.getDescription().equals(tt.getDescription());
    }
    public static Test getGenericHiv1SeroStatusTest() {
        return genericHiv1SeroStatusTest;
    }
    public static TestType getHiv1SeroStatusTestType() {
        return hiv1SeroStatusTestType;
    }
    public static Test getHiv1SeroconversionTest() {
        return hiv1SeroconversionTest;
    }
    public static TestType getHiv1SeroconversionTestType() {
        return hiv1SeroconversionTestType;
    }
    public static Protein[] getProteins() {
        Protein[] proteins = new Protein[7];
        Protein p6 = new Protein("p6", "Transframe peptide (partially)");
        proteins[0] = p6;
        Protein pro = new Protein("PRO", "Protease");
        proteins[1] = pro;
        Protein rt = new Protein("RT", "Reverse Transcriptase");
        proteins[2] = rt;
        Protein in = new Protein("IN", "Integrase");
        proteins[3] = in;

        Protein sig = new Protein("sig", "Signal peptide");
        proteins[4] = sig;
        Protein gp120 = new Protein("gp120", "Envelope surface glycoprotein gp120");
        proteins[5] = gp120;
        Protein gp41 = new Protein("gp41", "Envelope transmembrane domain");
        proteins[6] = gp41;
        
        return proteins;
    }
    
    public static Map<String, Protein> getProteinMap() {
        Protein[] proteins = getProteins();
        Map<String, Protein> result = new HashMap<String, Protein>();
        
        for (Protein p:proteins)
            result.put(p.getAbbreviation(), p);
        

        return result;
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
}
