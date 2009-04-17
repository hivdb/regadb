package net.sf.regadb.io.db.util.mapping;

import java.util.Map;

import net.sf.regadb.db.Attribute;
import net.sf.regadb.db.AttributeNominalValue;
import net.sf.regadb.db.PatientAttributeValue;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestNominalValue;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.TestType;
import net.sf.regadb.db.ValueTypes;
import net.sf.regadb.util.mapper.AttributeMapping;
import net.sf.regadb.util.mapper.TestMapping;
import net.sf.regadb.util.mapper.XmlMapper;

public class ObjectMapper{
    @SuppressWarnings("serial")
    public static class MappingException extends Exception{
        public MappingException(){
            super();
        }
        public MappingException(String msg){
            super(msg);
        }
    }
    @SuppressWarnings("serial")
    public static class MappingDoesNotExistException extends MappingException{
    }
    @SuppressWarnings("serial")
    public static class ObjectDoesNotExistException extends MappingException{
        public ObjectDoesNotExistException(String obj){
            super("object: '"+ obj +'\'');
        }
    }
    @SuppressWarnings("serial")
    public static class InvalidValueException extends MappingException{
        public InvalidValueException(String obj){
            super("value: '"+ obj +'\'');
        }
    }
    
    private boolean createTests = false;
    private boolean createTestNominalValues = false;
    private boolean createAttributeNominalValues = false;
    
    private ObjectStore objectStore;
    private XmlMapper mapper;
    
    
    public ObjectMapper(ObjectStore objectStore, XmlMapper mapper){
        setObjectStore(objectStore);
        setMapper(mapper);
    }

    public XmlMapper getMapper(){
        return mapper;
    }
    public void setMapper(XmlMapper mapper){
        this.mapper = mapper;
    }
    
    public Test getTest(Map<String,String> variables) throws MappingException{
        return getTest(mapper.getTest(variables));
    }
    
    public TestResult getTestResult(Map<String,String> variables) throws MappingException{
        return getTestResult(mapper.getTest(variables), variables);
    }
    
    public Attribute getAttribute(Map<String,String> variables){
        return getAttribute(mapper.getAttribute(variables));
    }
    
    public PatientAttributeValue getAttributeValue(Map<String,String> variables) throws MappingException{
        AttributeMapping am = mapper.getAttribute(variables);
        if(am != null)
            return getAttributeValue(am, variables);
        return null;
    }
    
    public Test getTest(TestMapping m) throws MappingException{
        if(m == null)
            throw new MappingDoesNotExistException();
        
        TestType tt = getObjectStore().getTestType(m.getTestTypeDescription(), m.getOrganismName());
        if(tt == null)
            throw new ObjectDoesNotExistException(m.getTestTypeDescription());
        
        Test t = getObjectStore().getTest(m.getTestDescription(), m.getTestTypeDescription(), m.getOrganismName());
        if(t == null){
            if(createTests()){
                t = new Test(tt, m.getTestDescription());
            }
            else
                throw new ObjectDoesNotExistException(m.getTestDescription());
        }
            
        return t;
    }
    
    public TestResult getTestResult(TestMapping tm, Map<String,String> variables) throws MappingException{
        Test t = getTest(tm);
        
        TestResult tr = new TestResult();
        tr.setTest(t);
        
        String value = tm.getValue(variables);
        
        if(ValueTypes.isNominal(t.getTestType().getValueType())){
            TestNominalValue tnv = getObjectStore().getTestNominalValue(t.getTestType(), value);
            
            if(tnv == null){
                if(createTestNominalValues()){
                    tnv = new TestNominalValue(t.getTestType(),value);
                    t.getTestType().getTestNominalValues().add(tnv);
                }
                else
                    throw new InvalidValueException(value);
            }
            
            tr.setTestNominalValue(tnv);
        }
        else{
            if(ValueTypes.isValidValue(t.getTestType().getValueType(), value))
                tr.setValue(value);
            else
                throw new InvalidValueException(value);
        }
        
        return tr;
    }
    
    public Attribute getAttribute(AttributeMapping m){
        return getObjectStore().getAttribute(m.getName(), m.getGroup());
    }
    
    public PatientAttributeValue getAttributeValue(AttributeMapping am, Map<String,String> variables) throws MappingException{
        Attribute a = getAttribute(am);
        
        if(a == null)
            throw new ObjectDoesNotExistException(am.getName());
        
        PatientAttributeValue pav = new PatientAttributeValue();
        pav.setAttribute(a);
        
        String value = am.getValue(variables);
        
        if(ValueTypes.isNominal(a.getValueType())){
            AttributeNominalValue anv = getObjectStore().getAttributeNominalValue(a, value);
            if(anv == null){
                if(createAttributeNominalValues()){
                    anv = new AttributeNominalValue(a, value);
                    a.getAttributeNominalValues().add(anv);
                }
                else
                    throw new InvalidValueException(value);
            }
            
            pav.setAttributeNominalValue(anv);
        }
        else{
            if(ValueTypes.isValidValue(a.getValueType(), value)){
                pav.setValue(value);
            }
            else
                throw new InvalidValueException(value);
        }
        return pav;
    }
    
    public boolean createTests(){
        return createTests;
    }
    public void setCreateTests(boolean on){
        createTests = on;
    }
    public boolean createTestNominalValues(){
        return createTestNominalValues;
    }
    public void setCreateTestNominalValues(boolean on){
        createTestNominalValues = on;
    }
    public boolean createAttributeNominalValues(){
        return createAttributeNominalValues;
    }
    public void setCreateAttributeNominalValues(boolean on){
        createAttributeNominalValues = on;
    }
    
    protected ObjectStore getObjectStore(){
        return objectStore;
    }
    protected void setObjectStore(ObjectStore objectStore){
        this.objectStore = objectStore;
    }
    
}
