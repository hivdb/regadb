package net.sf.regadb.io.db.util.mapping;

import java.util.Collection;

import net.sf.regadb.db.Attribute;
import net.sf.regadb.db.AttributeGroup;
import net.sf.regadb.db.AttributeNominalValue;
import net.sf.regadb.db.Dataset;
import net.sf.regadb.db.Genome;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestNominalValue;
import net.sf.regadb.db.TestObject;
import net.sf.regadb.db.TestType;
import net.sf.regadb.db.ValueType;

public abstract class ObjectStore {
    public abstract TestType getTestType(String description, String organismName);
    public TestType getTestType(TestType tt){
    	return getTestType(tt.getDescription(),tt.getGenome() == null ? null : tt.getGenome().getOrganismName());
    }
    public TestType createTestType(String description, TestObject testObject, Genome genome, ValueType valueType){
    	TestType tt = new TestType();
    	tt.setValueType(valueType);
    	tt.setDescription(description);
    	tt.setGenome(genome);
    	tt.setTestObject(testObject);
    	return tt;
    }
    
    public abstract Test getTest(String description, String testTypeDescription, String organismName);
    public Test getTest(Test t){
    	return getTest(t.getDescription(), t.getTestType().getDescription(),
    			t.getTestType().getGenome() == null ? null : t.getTestType().getGenome().getOrganismName());
    }
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
    
    public abstract AttributeGroup getAttributeGroup(String name);
    public abstract AttributeGroup createAttributeGroup(String name);
    
    public abstract Attribute createAttribute(AttributeGroup attributeGroup, ValueType valueType, String name);
    
    public abstract ValueType getValueType(String description);
    
    public abstract TestObject getTestObject(String description);
    
    public abstract Genome getGenome(String organismName);
    
    public abstract Collection<Patient> getPatients();
    
    public abstract void close();
    
    public void commit(){
    	
    }
}

