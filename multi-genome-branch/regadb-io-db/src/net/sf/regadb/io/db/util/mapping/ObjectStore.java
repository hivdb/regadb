package net.sf.regadb.io.db.util.mapping;

import net.sf.regadb.db.Attribute;
import net.sf.regadb.db.AttributeNominalValue;
import net.sf.regadb.db.Dataset;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestNominalValue;
import net.sf.regadb.db.TestType;

public abstract class ObjectStore {
    public abstract TestType getTestType(String description, String organismName);
    
    public abstract Test getTest(String description, String testTypeDescription, String organismName);
    public Test createTest(TestType testType, String description){
    	Test t = new Test(testType, description);
        return t;
    }
    
    public abstract TestNominalValue getTestNominalValue(TestType testType, String value);
    public TestNominalValue createTestNominalValue(TestType testType, String value){
    	TestNominalValue tnv = new TestNominalValue(testType, value);
        testType.getTestNominalValues().add(tnv);
        return tnv;
    }
    
    public abstract Attribute getAttribute(String name, String group);
    
    public abstract AttributeNominalValue getAttributeNominalValue(Attribute attribute, String value);
    public AttributeNominalValue createAttributeNominalValue(Attribute attribute, String value){
    	AttributeNominalValue anv = new AttributeNominalValue(attribute, value);
        attribute.getAttributeNominalValues().add(anv);
        return anv;
    }
    
    public abstract Dataset getDataset(String description);
    
    public abstract Patient getPatient(Dataset dataset, String id);
    public abstract Patient createPatient(Dataset dataset, String id);
    
    public void commit(){
    	
    }
}

