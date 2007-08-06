/*
 * Created on May 10, 2007
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package net.sf.regadb.io.util;

import java.util.HashMap;
import java.util.List;
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
    private static TestType viralLoadTestType = new TestType(limitedNumberValueType, patientObject, "Viral Load (copies/ml)", new TreeSet<TestNominalValue>());
    private static TestType cd4TestType = new TestType(numberValueType, patientObject, "CD4 Count (cells/ul)", new TreeSet<TestNominalValue>());
    private static Test genericViralLoadTest = new Test(viralLoadTestType, "Viral Load (generic)");
    private static Test genericCD4Test = new Test(cd4TestType, "CD4 Count (generic)");
    private static String gssId = "Genotypic Susceptibility Score (GSS)";
    private static String clinicalFileNumberAttribute = "Clinical File Number";

    public static TestType getCd4TestType() {
        return cd4TestType;
    }
    public static Test getGenericCD4Test() {
        return genericCD4Test;
    }
    public static Test getGenericViralLoadTest() {
        return genericViralLoadTest;
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
}
