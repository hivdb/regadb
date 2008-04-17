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

import net.sf.regadb.db.Protein;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestNominalValue;
import net.sf.regadb.db.TestObject;
import net.sf.regadb.db.TestType;
import net.sf.regadb.db.ValueType;

public class StandardObjects {
    private static TestObject patientObject = new TestObject("Patient test", 0);
    private static ValueType numberValueType = new ValueType("number");
    private static ValueType limitedNumberValueType = new ValueType("limited number (<,=,>)");
    private static ValueType nominalValueType = new ValueType("nominal value");
    private static ValueType stringValueType = new ValueType("string");
    private static ValueType dateValueType = new ValueType("date");
    private static TestType viralLoadTestType = new TestType(limitedNumberValueType, patientObject, "Viral Load (copies/ml)", new TreeSet<TestNominalValue>());
    private static TestType viralLoadLog10TestType = new TestType(limitedNumberValueType, patientObject, "Viral Load (log10)", new TreeSet<TestNominalValue>());
    private static TestType cd4TestType = new TestType(numberValueType, patientObject, "CD4 Count (cells/ul)", new TreeSet<TestNominalValue>());
    private static TestType cd4PercentageTestType = new TestType(numberValueType, patientObject, "CD4 Count (%)", new TreeSet<TestNominalValue>());
    private static TestType cd8TestType = new TestType(StandardObjects.getNumberValueType(), StandardObjects.getPatientObject(), "CD8 Count", new TreeSet<TestNominalValue>());
    private static TestType cd8PercentageTestType = new TestType(numberValueType, patientObject, "CD8 Count (%)", new TreeSet<TestNominalValue>());
    private static TestType hivSeroStatusTestType;
    private static TestType followUpTestType = new TestType(dateValueType, patientObject, "Follow up",new TreeSet<TestNominalValue>());
    private static Test genericViralLoadTest = new Test(viralLoadTestType, "Viral Load (generic)");
    private static Test genericViralLoadLog10Test = new Test(viralLoadLog10TestType, "Viral Load log10 (generic)");
    private static Test genericCD4Test = new Test(cd4TestType, "CD4 Count (generic)");
    private static Test genericCD4PercentageTest = new Test(cd4PercentageTestType, "CD4 Count % (generic)");
    private static Test genericCD8Test = new Test(cd8TestType, "CD8 Count (generic)");
    private static Test genericCD8PercentageTest = new Test(cd8PercentageTestType, "CD8 Count % (generic)");
    private static Test genericHivSeroStatusTest;
    private static Test followUpTest = new Test(followUpTestType, "Follow up");
    
    private static String gssId = "Genotypic Susceptibility Score (GSS)";
    private static String clinicalFileNumberAttribute = "Clinical File Number";

    static {
        hivSeroStatusTestType = new TestType(patientObject, "HIV Sero Status");
        hivSeroStatusTestType.setValueType(nominalValueType);
        hivSeroStatusTestType.getTestNominalValues().add(new TestNominalValue(hivSeroStatusTestType, "Positive"));
        hivSeroStatusTestType.getTestNominalValues().add(new TestNominalValue(hivSeroStatusTestType, "Negative"));
        genericHivSeroStatusTest = new Test(hivSeroStatusTestType, "HIV Sero Status (generic)");
    }
    
    public static Test createPregnancyTest() {
        TestType pregnancyType = new TestType(new TestObject("Patient test", 0), "Pregnancy");
        pregnancyType.setValueType(nominalValueType);
        pregnancyType.getTestNominalValues().add(new TestNominalValue(pregnancyType, "Positive"));
        pregnancyType.getTestNominalValues().add(new TestNominalValue(pregnancyType, "Negative"));
        
        Test pregnancy = new Test(pregnancyType, "Pregnancy");
        
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
    public static Test getGenericViralLoadTest() {
        return genericViralLoadTest;
    }
    public static Test getGenericViralLoadLog10Test() {
        return genericViralLoadLog10Test;
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
    public static TestType getViralLoadTestType() {
        return viralLoadTestType;
    }
    public static TestType getViralLoadLog10TestType() {
        return viralLoadLog10TestType;
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
    public static boolean isViralLoad(TestType tt) {
        return viralLoadTestType.getDescription().equals(tt.getDescription());
    }
    public static boolean isCD4(TestType tt) {
        return cd4TestType.getDescription().equals(tt.getDescription());
    }
    public static Test getGenericHivSeroStatusTest() {
        return genericHivSeroStatusTest;
    }
    public static TestType getHivSeroStatusTestType() {
        return hivSeroStatusTestType;
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
}
