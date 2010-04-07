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
import net.sf.regadb.util.mapper.matcher.Matcher.MatcherException;

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
        public String msg;
        
        public MappingDoesNotExistException(Map<String, String> variables){
            super();
            StringBuilder sb = new StringBuilder("No mapping available: ");
            for(Map.Entry<String, String> me : variables.entrySet()){
                sb.append(me.getKey());
                sb.append("='");
                sb.append(me.getValue());
                sb.append("', ");
            }
            msg = sb.toString();
        }
        public String getMessage(){
            return msg;
        }
    }
    @SuppressWarnings("serial")
    public static class ObjectDoesNotExistException extends MappingException{
        public ObjectDoesNotExistException(String msg){
            super("Mapping to a non-existing object: "+ msg);
        }
    }
    @SuppressWarnings("serial")
    public static class InvalidValueException extends MappingException{
        public InvalidValueException(String msg){
            super("Invalid value: "+ msg);
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
    
    public TestMapping getTestMapping(Map<String,String> variables) throws MappingDoesNotExistException, MatcherException{
        TestMapping tm = mapper.getTest(variables);
        if(tm == null)
            throw new MappingDoesNotExistException(variables);
        return tm;
    }
    
    public Test getTest(Map<String,String> variables) throws MappingException, MatcherException{
        return getTest(getTestMapping(variables));
    }
    
    public TestResult getTestResult(Map<String,String> variables) throws MappingException, MatcherException{
        return getTestResult(getTestMapping(variables), variables);
    }
    
    public AttributeMapping getAttributeMapping(Map<String,String> variables) throws MappingDoesNotExistException{
        AttributeMapping am = mapper.getAttribute(variables);
        if(am == null)
            throw new MappingDoesNotExistException(variables);
        return am;
    }
    public Attribute getAttribute(Map<String,String> variables) throws MappingDoesNotExistException{
        return getAttribute(getAttributeMapping(variables));
    }
    
    public PatientAttributeValue getAttributeValue(Map<String,String> variables) throws MappingException, MatcherException{
        return getAttributeValue(getAttributeMapping(variables), variables);
    }
    
    public Test getTest(TestMapping m) throws MappingException{
        TestType tt = getObjectStore().getTestType(m.getTestTypeDescription(), m.getOrganismName());
        if(tt == null)
            throw new ObjectDoesNotExistException("test type: '"+ m.getTestTypeDescription() +'\'');
        
        Test t = getObjectStore().getTest(m.getTestDescription(), m.getTestTypeDescription(), m.getOrganismName());
        if(t == null){
            if(createTests()){
                t = new Test(tt, m.getTestDescription());
            }
            else
                throw new ObjectDoesNotExistException("test: '"+ m.getTestDescription() +'\'');
        }
            
        return t;
    }
    
    public TestResult getTestResult(TestMapping tm, Map<String,String> variables) throws MappingException, MatcherException{
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
                    throw new InvalidValueException("test '"+ t.getDescription() +"' nominal value: '"+ value +'\'');
            }
            
            tr.setTestNominalValue(tnv);
        }
        else{
            if(ValueTypes.isValidValue(t.getTestType().getValueType(), value))
                tr.setValue(value);
            else
                throw new InvalidValueException("test '"+ t.getDescription() +"' value: '"+ value +'\'');
        }
        
        return tr;
    }
    
    public Attribute getAttribute(AttributeMapping m){
        return getObjectStore().getAttribute(m.getName(), m.getGroup());
    }
    
    public PatientAttributeValue getAttributeValue(AttributeMapping am, Map<String,String> variables) throws MappingException, MatcherException{
        Attribute a = getAttribute(am);
        
        if(a == null)
            throw new ObjectDoesNotExistException("attribute: '"+ am.getName() +'\'');
        
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
                    throw new InvalidValueException("attribute '"+ a.getName() +"' nominal value: '"+ value +'\'');
            }
            
            pav.setAttributeNominalValue(anv);
        }
        else{
            if(ValueTypes.isValidValue(a.getValueType(), value)){
                pav.setValue(value);
            }
            else
                throw new InvalidValueException("attribute '"+ a.getName() +"' value: '"+ value +'\'');
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
