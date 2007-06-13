/*
 * Created on May 10, 2007
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package net.sf.regadb.io.util;

import java.util.TreeSet;

import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestNominalValue;
import net.sf.regadb.db.TestObject;
import net.sf.regadb.db.TestType;
import net.sf.regadb.db.ValueType;

public class StandardObjects {
    private static TestObject patientObject = new TestObject("Patient test", 0);
    private static ValueType numberValueType = new ValueType("number");
    private static ValueType limitedNumberValueType = new ValueType("limited number (<,=,>)");
    private static TestType viralLoadTestType = new TestType(numberValueType, patientObject, "Viral Load (copies/ml)", new TreeSet<TestNominalValue>());
    private static TestType cd4TestType = new TestType(limitedNumberValueType, patientObject, "CD4 Count (cells/ul)", new TreeSet<TestNominalValue>());
    private static Test genericViralLoadTest = new Test(viralLoadTestType, "Viral Load (generic)");
    private static Test genericCD4Test = new Test(cd4TestType, "CD4 Count (generic)");

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

}
