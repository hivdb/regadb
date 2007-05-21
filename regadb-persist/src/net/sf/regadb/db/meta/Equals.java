/*
 * Created on May 16, 2007
 *
 * To change the template o2or this generated o2ile go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package net.sf.regadb.db.meta;

import net.sf.regadb.db.AaInsertion;
import net.sf.regadb.db.AaMutation;
import net.sf.regadb.db.AaSequence;
import net.sf.regadb.db.Analysis;
import net.sf.regadb.db.AnalysisData;
import net.sf.regadb.db.Attribute;
import net.sf.regadb.db.AttributeGroup;
import net.sf.regadb.db.AttributeNominalValue;
import net.sf.regadb.db.Dataset;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.PatientAttributeValue;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestNominalValue;
import net.sf.regadb.db.TestObject;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.TestType;
import net.sf.regadb.db.Therapy;
import net.sf.regadb.db.TherapyCommercial;
import net.sf.regadb.db.TherapyGeneric;
import net.sf.regadb.db.ValueType;
import net.sf.regadb.db.ViralIsolate;

public class Equals {

    public static boolean isSameTest(Test o1, Test o2) {
        return o1 == o2
        || (o1 != null && o2 != null && o1.getDescription().equals(o2.getDescription()));
    }

    public static boolean isSameTestNominalValue(TestNominalValue o1, TestNominalValue o2) {
        return o1 == o2
        || (o1 != null && o2 != null && o1.getValue().equals(o2.getValue()));
    }

    public static boolean isSameTestType(TestType o1, TestType o2) {
        return o1 == o2
        || (o1 != null && o2 != null && o1.getDescription().equals(o2.getDescription()));
    }

    public static boolean isSameDataset(Dataset o1, Dataset o2) {
        return o1 == o2
        || (o1 != null && o2 != null && o1.getDescription().equals(o2.getDescription()));
    }

    public static boolean isSameTestResult(TestResult o1, TestResult o2) {
        return o1 == o2
        || (o1 != null && o2 != null && o1.getTestDate().equals(o2.getTestDate())
                && isSameTest(o1.getTest(), o2.getTest()));
    }

    public static boolean isSamePatientAttributeValue(PatientAttributeValue o1, PatientAttributeValue o2) {
        return o1 == o2
        || (o1 != null && o2 != null && isSameAttribute(o1.getId().getAttribute(), o2.getId().getAttribute()));
    }

    public static boolean isSameViralIsolate(ViralIsolate o1, ViralIsolate o2) {
        return o1 == o2
        || (o1 != null && o2 != null && o1.getSampleId().equals(o2.getSampleId()));
    }

    public static boolean isSameTherapy(Therapy o1, Therapy o2) {
        return o1 == o2
        || (o1 != null && o2 != null && o1.getStartDate().equals(o2.getStartDate()));
    }

    public static boolean isSameNtSequence(NtSequence o1, NtSequence o2) {
        return o1 == o2
        || (o1 != null && o2 != null && o1.getLabel().equals(o2.getLabel()));
    }

    public static boolean isSameAaSequence(AaSequence o1, AaSequence o2) {
        return o1 == o2
        || (o1 != null && o2 != null && o1.getProtein() == o2.getProtein());
    }

    public static boolean isSameAaInsertion(AaInsertion o1, AaInsertion o2) {
        return o1 == o2
        || (o1 != null && o2 != null && o1.getId().getPosition() == o2.getId().getPosition()
                && o1.getId().getInsertionOrder() == o2.getId().getInsertionOrder());
    }

    public static boolean isSameTherapyCommercial(TherapyCommercial o1, TherapyCommercial o2) {
        return o1 == o2
        || (o1 != null && o2 != null && o1.getId().getDrugCommercial() == o2.getId().getDrugCommercial());
    }

    public static boolean isSameTherapyGeneric(TherapyGeneric o1, TherapyGeneric o2) {
        return o1 == o2
        || (o1 != null && o2 != null && o1.getId().getDrugGeneric() == o2.getId().getDrugGeneric());
    }

    public static boolean isSameAaMutation(AaMutation o1, AaMutation o2) {
        return o1 == o2
        || (o1 != null && o2 != null && o1.getId().getPosition() == o2.getId().getPosition());
    }

    public static boolean isSameAttribute(Attribute o1, Attribute o2) {
        return o1 == o2
        || (o1 != null && o2 != null && o1.getName().equals(o2.getName()));
    }

    public static boolean isSameAttributeNominalValue(AttributeNominalValue o1, AttributeNominalValue o2) {
        return o1 == o2
        || (o1 != null && o2 != null && o1.getValue().equals(o2.getValue()));
    }

    public static boolean isSameValueType(ValueType o1, ValueType o2) {
        return o1 == o2
        || (o1 != null && o2 != null && o1.getDescription().equals(o2.getDescription()));
    }

    public static boolean isSameTestObject(TestObject o1, TestObject o2) {
        return o1 == o2
        || (o1 != null && o2 != null && o1.getDescription().equals(o2.getDescription()));
    }
    
    public static boolean isSameAttributeGroup(AttributeGroup o1, AttributeGroup o2) {
        return o1 == o2
        || (o1 != null && o2 != null && o1.getGroupName().equals(o2.getGroupName()));
    }

    public static boolean isSameAnalysis(Analysis o1, Analysis o2) {
        return o1 == o2
        || (o1 != null && o2 !=null);
    }

    public static boolean isSameAnalysisData(AnalysisData o1, AnalysisData o2) {
        return o1 == o2
        || (o1 != null && o2 !=null && o1.getName().equals(o2.getName()));
    }

}
