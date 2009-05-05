package net.sf.regadb.io.db.util.mapping;

import net.sf.regadb.db.Attribute;
import net.sf.regadb.db.AttributeNominalValue;
import net.sf.regadb.db.Dataset;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestNominalValue;
import net.sf.regadb.db.TestType;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.session.Login;

public class DbObjectStore implements ObjectStore{
    private Login login;
    private Transaction transaction;
    
    public DbObjectStore(Login login){
        setLogin(login);
        createTransaction();
    }

    private Login getLogin(){
        return login;
    }
    private void setLogin(Login login){
        this.login = login;
    }
    
    private void createTransaction(){
        setTransaction(getLogin().createTransaction());
    }

    public Transaction getTransaction(){
        return transaction;
    }
    private void setTransaction(Transaction transaction){
        this.transaction = transaction;
    }

    public AttributeNominalValue createAttributeNominalValue(
            Attribute attribute, String value) {
        AttributeNominalValue anv = new AttributeNominalValue(attribute, value);
        attribute.getAttributeNominalValues().add(anv);
        return anv;
    }

    public Test createTest(TestType testType, String description) {
        Test t = new Test(testType, description);
        return t;
    }

    public TestNominalValue createTestNominalValue(TestType testType,
            String value) {
        TestNominalValue tnv = new TestNominalValue(testType, value);
        testType.getTestNominalValues().add(tnv);
        return tnv;
    }

    public Attribute getAttribute(String name, String group) {
        return getTransaction().getAttribute(name, group);
    }

    public AttributeNominalValue getAttributeNominalValue(Attribute attribute,
            String value) {
        
        return getTransaction().getAttributeNominalValue(attribute, value);
    }

    public Test getTest(String description, String testTypeDescription,
            String organismName) {
        return getTransaction().getTest(description, testTypeDescription, organismName);
    }

    public TestNominalValue getTestNominalValue(TestType testType, String value) {
        return getTransaction().getTestNominalValue(testType, value);
    }

    public TestType getTestType(String description, String organismName) {
        return getTransaction().getTestType(description, organismName);
    }

    public Patient getPatient(Dataset dataset, String id){
        return getTransaction().getPatient(dataset, id);
    }
    
    public Patient createPatient(Dataset dataset, String id){
        Patient p = new Patient();
        p.setPatientId(id);
        p.setSourceDataset(dataset, getTransaction());
        getTransaction().save(p);
        return p;
    }
    
    public Dataset getDataset(String description){
        return getTransaction().getDataset(description);
    }

    public void commit() {
        getTransaction().commit();
        getTransaction().clearCache();
        createTransaction();
    }
}
