/*
 * Created on Jun 11, 2007
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package net.sf.regadb.db.meta;

import net.sf.regadb.db.Analysis;
import net.sf.regadb.db.Attribute;
import net.sf.regadb.db.AttributeGroup;
import net.sf.regadb.db.AttributeNominalValue;
import net.sf.regadb.db.DrugCommercial;
import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestNominalValue;
import net.sf.regadb.db.TestObject;
import net.sf.regadb.db.TestType;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.ValueType;

public class Retrieve {

    public static Test retrieve(Transaction t, Test test) {
        return t.getTest(test.getDescription());
    }

    public static TestObject retrieve(Transaction t, TestObject testObject) {
        return t.getTestObject(testObject.getDescription());
    }

    public static TestType retrieve(Transaction t, TestType testType) {
        return t.getTestType(testType.getDescription());
    }

    public static Attribute retrieve(Transaction t, Attribute attribute) {
        return t.getAttribute(attribute.getName());
    }

    public static AttributeNominalValue retrieve(Transaction t, AttributeNominalValue attributeNominalValue) {
        return t.getAttributeNominalValue(retrieve(t, attributeNominalValue.getAttribute()), attributeNominalValue.getValue());
    }

    public static AttributeGroup retrieve(Transaction t, AttributeGroup attributeGroup) {
        return t.getAttributeGroup(attributeGroup.getGroupName());
    }

    public static TestNominalValue retrieve(Transaction t, TestNominalValue testNominalValue) {
        return t.getTestNominalValue(retrieve(t, testNominalValue.getTestType()), testNominalValue.getValue());
    }

    public static Analysis retrieve(Transaction t, Analysis analysis) {
        return null;
    }

    public static ValueType retrieve(Transaction t, ValueType valueType) {
        return t.getValueType(valueType.getDescription());
    }

    public static Patient retrieve(Transaction t, Patient o) {
        return t.getPatient(o.getSourceDataset(), o.getPatientId());
    }

    public static DrugGeneric retrieve(Transaction t, DrugGeneric o) {
        return t.getGenericDrug(o.getGenericId());
    }

    public static DrugCommercial retrieve(Transaction t, DrugCommercial o) {
        return t.getCommercialDrug(o.getName());
    }
}
