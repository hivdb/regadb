/*
 * Created on Dec 15, 2006
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package net.sf.regadb.db;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Patient implements Serializable {

    public static final String FIRST_NAME = "First name";
    public static final String LAST_NAME = "Last name";
    public static final String BIRTH_DATE = "Birth date";
    public static final String DEATH_DATE = "Death date";
    public static final String GROUP = "RegaDB";
    
    private PatientImpl patient;
    private Privileges privileges;
    
    public Patient()
    {
        patient = new PatientImpl();
        privileges = Privileges.READWRITE;
    }
    
    PatientImpl getPatient() {
        return patient;
    }

    public Patient(PatientImpl patient, int privileges) {
        this.patient = patient;
        this.privileges = Privileges.getPrivilege(privileges);
    }

    // Property accessors
    public Integer getPatientIi() {
        return patient.getPatientIi();
    }

    public Set<Dataset> getDatasets() {
        Set<PatientDataset> pds_set = patient.getPatientDatasets();
        Set<Dataset> ds = new HashSet<Dataset>();
        for(PatientDataset pds : pds_set)
        {
            ds.add(pds.getId().getDataset());
        }
        return ds;
    }
    
    public void setPatientIi(int patientIi) {
        patient.setPatientIi(patientIi);
    }

    public Integer getVersion() {
        return patient.getVersion();
    }

    public void setVersion(Integer version) {
        patient.setVersion(version);
    }

    public String getPatientId() {
        return patient.getPatientId();
    }

    public void setPatientId(String patientId) {
        patient.setPatientId(patientId);
    }
    
    private String getAttributeValue(String attribute){
        for(PatientAttributeValue pav : patient.getPatientAttributeValues())
            if(pav.getAttribute().getName().equals(attribute))
                return pav.getValue();
                
        return null;
    }
    private PatientAttributeValue setAttributeValue(Attribute attribute, String value){
        PatientAttributeValue av = null;
        for(PatientAttributeValue pav : patient.getPatientAttributeValues()){
            if(pav.getAttribute().getName().equals(attribute.getName())){
                av = pav;
                break;
            }
        }
        
        
        if(av == null){
            if(value != null){
                av = createPatientAttributeValue(attribute);
                av.setValue(value);
            }
        }
        else{
            if(value == null){
                patient.getPatientAttributeValues().remove(av);
            }
            else{
                av.setValue(value);
            }
        }
            
        return av;
    }
    private Date getDate(String timestamp){
        if(timestamp != null){
            try{
                return new Date(Long.parseLong(timestamp));
            }
            catch(NumberFormatException e){
                e.printStackTrace();
            }
        }
        return null;
    }


    public String getLastName() {
        if (privileges.getValue() >= Privileges.READONLY.getValue())
            return getAttributeValue(LAST_NAME);
        else
            return null;
    }

    public void setLastName(Transaction t, String lastName) {
        PatientAttributeValue pav = setAttributeValue(t.getAttribute(LAST_NAME, GROUP), lastName);
        if(pav != null && lastName == null)
            t.delete(pav);
    }

    public String getFirstName() {
        if (privileges.getValue() >= Privileges.READONLY.getValue())
            return getAttributeValue(FIRST_NAME);
        else
            return null;
    }

    public void setFirstName(Transaction t, String firstName) {
        PatientAttributeValue pav = setAttributeValue(t.getAttribute(FIRST_NAME, GROUP), firstName);
        if(pav != null && firstName == null)
            t.delete(pav);
    }

    public Date getBirthDate() {
        return getDate(getAttributeValue(BIRTH_DATE));
    }

    public void setBirthDate(Transaction t, Date birthDate) {
        PatientAttributeValue pav = setAttributeValue(t.getAttribute(BIRTH_DATE, GROUP), birthDate == null ? null : birthDate.getTime()+"");
        if(pav != null && birthDate == null)
            t.delete(pav);
    }

    public Date getDeathDate() {
        return getDate(getAttributeValue(DEATH_DATE));
    }

    public void setDeathDate(Transaction t, Date deathDate) {
        PatientAttributeValue pav = setAttributeValue(t.getAttribute(DEATH_DATE, GROUP), deathDate == null ? null : deathDate.getTime()+"");
        if(pav != null && deathDate == null)
            t.delete(pav);
    }

    public Set<TestResult> getTestResults() {
        return patient.getTestResults();
    }

    public void setTestResults(Set<TestResult> testResults) {
        patient.setTestResults(testResults);
    }

    public Set<PatientAttributeValue> getPatientAttributeValues() {
        return patient.getPatientAttributeValues();
    }

    public void setPatientAttributeValues(
            Set<PatientAttributeValue> patientAttributeValues) {
        patient.setPatientAttributeValues(patientAttributeValues);
    }

    public Set<PatientEventValue> getPatientEventValues() {
        return patient.getPatientEventValues();
    }    

    public void setPatientEventValues(
            Set<PatientEventValue> patientEventValues) {
        patient.setPatientEventValues(patientEventValues);
    }

    public Set<ViralIsolate> getViralIsolates() {
        return patient.getViralIsolates();
    }

    public void setViralIsolates(Set<ViralIsolate> viralIsolates) {
        patient.setViralIsolates(viralIsolates);
    }

    public Set<Therapy> getTherapies() {
        return patient.getTherapies();
    }

    public void setTherapies(Set<Therapy> therapies) {
        patient.setTherapies(therapies);
    }

    public Privileges getPrivileges() {
        return privileges;
    }

    public PatientAttributeValue createPatientAttributeValue(Attribute attribute) {
        PatientAttributeValue result
            = new PatientAttributeValue(attribute, patient);

        getPatientAttributeValues().add(result);
        
        return result;
    }

    public void addPatientAttributeValue(PatientAttributeValue attributeValue) {
        getPatientAttributeValues().add(attributeValue);
        attributeValue.setPatient(patient);
    }

    public PatientAttributeValue getAttributeValue(Attribute attribute)
    {
        for(PatientAttributeValue pav : getPatientAttributeValues())
        {
            if(pav.getAttribute().getAttributeIi().equals(attribute.getAttributeIi()))
            {
                return pav;
            }
        }
        
        return null;
    }
    
    public PatientEventValue createPatientEventValue(Event event) {
        PatientEventValue result
            = new PatientEventValue(patient, event);

        getPatientEventValues().add(result);
        
        return result;
    }

    public void addPatientEventValue(PatientEventValue eventValue) {
        getPatientEventValues().add(eventValue);
        eventValue.setPatient(patient);
    }
    
    public Therapy createTherapy(Date startDate){
        Therapy result = new Therapy(patient, startDate);
        getTherapies().add(result);
        return result;
    }

    public void addTherapy(Therapy therapy) {
        getTherapies().add(therapy);
        therapy.setPatient(patient);
    }
    
    public ViralIsolate createViralIsolate() {
        ViralIsolate result = new ViralIsolate(patient);
        getViralIsolates().add(result);
        return result;
    }

    public void addViralIsolate(ViralIsolate isolate) {
        getViralIsolates().add(isolate);
        isolate.setPatient(patient);
    }
    
    public TestResult createTestResult(Test test) {
        TestResult result = new TestResult(test);
        result.setPatient(patient);
        getTestResults().add(result);
        return result;
    }
    
    public PatientEventValue addPatientEvent(Event e) {
        PatientEventValue pev = new PatientEventValue(patient, e);
        return pev;
    }
    
    public void addTestResult(TestResult testResult) {
        getTestResults().add(testResult);
        testResult.setPatient(patient);
    }

    public void setSourceDataset(Dataset ds, Transaction t)
    {
        List<Dataset> dsl = t.getCurrentUsersDatasets(Privileges.READWRITE);
        
        for(Dataset dsinl : dsl)
        {
            if(dsinl.getDescription().equals(ds.getDescription()) && ds.getClosedDate()==null)
            {
                patient.getPatientDatasets().add(new PatientDataset(new PatientDatasetId(ds, getPatient())));
            }
        }
        
        // FIXME: throw exception if no access
    }

    public Dataset getSourceDataset() {
        for (PatientDataset d : patient.getPatientDatasets())
            if (d.getId().getDataset().getClosedDate() == null)
                return d.getId().getDataset();
        return null;
    }
    
    public void addDataset(Dataset ds){
        getPatient().getPatientDatasets().add(new PatientDataset(new PatientDatasetId(ds,getPatient())));
    }
}
