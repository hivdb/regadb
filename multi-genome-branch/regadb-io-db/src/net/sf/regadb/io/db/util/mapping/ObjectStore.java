package net.sf.regadb.io.db.util.mapping;

import net.sf.regadb.db.Attribute;
import net.sf.regadb.db.AttributeNominalValue;
import net.sf.regadb.db.Dataset;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestNominalValue;
import net.sf.regadb.db.TestType;

public interface ObjectStore {
    public TestType getTestType(String description, String organismName);
    
    public Test getTest(String description, String testTypeDescription, String organismName);
    public Test createTest(TestType testType, String description);
    
    public TestNominalValue getTestNominalValue(TestType testType, String value);
    public TestNominalValue createTestNominalValue(TestType testType, String value);
    
    public Attribute getAttribute(String name, String group);
    
    public AttributeNominalValue getAttributeNominalValue(Attribute attribute, String value);
    public AttributeNominalValue createAttributeNominalValue(Attribute attribute, String value);
    
    public Dataset getDataset(String description);
    
    public Patient getPatient(Dataset dataset, String id);
    public Patient createPatient(Dataset dataset, String id);
    
    public void commit();
}

