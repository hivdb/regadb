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
import net.sf.regadb.db.Event;
import net.sf.regadb.db.EventNominalValue;
import net.sf.regadb.db.Genome;
import net.sf.regadb.db.OpenReadingFrame;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Protein;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestNominalValue;
import net.sf.regadb.db.TestObject;
import net.sf.regadb.db.TestType;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.ValueType;

public class Retrieve {

    public static Protein retrieve(Transaction t, Protein p) {
        return t.getProtein(retrieve(t,p.getOpenReadingFrame()),p.getAbbreviation());
    }	
	
    public static Test retrieve(Transaction t, Test test) {
        TestType tt = test.getTestType();
        Genome g = tt.getGenome();
        return t.getTest(test.getDescription(), tt.getDescription(), (g==null ? null:g.getOrganismName()));
    }

    public static TestObject retrieve(Transaction t, TestObject testObject) {
        return t.getTestObject(testObject.getDescription());
    }

    public static TestType retrieve(Transaction t, TestType testType) {
        return t.getTestType(testType);
    }

    public static Attribute retrieve(Transaction t, Attribute attribute) {
        return t.getAttribute(attribute.getName(), attribute.getAttributeGroup().getGroupName());
    }

    public static AttributeNominalValue retrieve(Transaction t, AttributeNominalValue attributeNominalValue) {
        return t.getAttributeNominalValue(retrieve(t, attributeNominalValue.getAttribute()), attributeNominalValue.getValue());
    }

    public static AttributeGroup retrieve(Transaction t, AttributeGroup attributeGroup) {
        return t.getAttributeGroup(attributeGroup.getGroupName());
    }
    
    public static Event retrieve(Transaction t, Event event) {
        return t.getEvent(event.getName());
    }
    
    public static EventNominalValue retrieve(Transaction t, EventNominalValue eventNominalValue) {
        return t.getEventNominalValue(retrieve(t, eventNominalValue.getEvent()), eventNominalValue.getValue());
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
        return t.getDrugGeneric(o.getGenericId());
    }

    public static DrugCommercial retrieve(Transaction t, DrugCommercial o) {
        return t.getDrugCommercial(o.getName());
    }

    public static OpenReadingFrame retrieve(Transaction t,
            OpenReadingFrame openReadingFrame) {
        return t.getOpenReadingFrame(retrieve(t,openReadingFrame.getGenome()), openReadingFrame.getName());
    }

    public static Genome retrieve(Transaction t, Genome genome) {
        return t.getGenome(genome.getOrganismName());
    }
}
