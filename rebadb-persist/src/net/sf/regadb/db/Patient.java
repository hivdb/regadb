/*
 * Created on Dec 15, 2006
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package net.sf.regadb.db;

import java.util.Date;
import java.util.Set;

public class Patient {

    private PatientImpl patient;
    private Privileges privileges;
    
    PatientImpl getPatient() {
        return patient;
    }

    public Patient(PatientImpl patient, int privileges) {
        this.patient = patient;
        this.privileges = Privileges.getPrivilege(privileges);
    }

    // Property accessors
    public int getPatientIi() {
        return patient.getPatientIi();
    }

    public Set<Dataset> getDatasets() {
        return patient.getDatasets();
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

    public String getLastName() {
        if (privileges.getValue() >= Privileges.READONLY.getValue())
            return patient.getLastName();
        else
            return null;
    }

    public void setLastName(String lastName) {
        patient.setLastName(lastName);
    }

    public String getFirstName() {
        if (privileges.getValue() >= Privileges.READONLY.getValue())
            return patient.getFirstName();
        else
            return null;
    }

    public void setFirstName(String firstName) {
        patient.setFirstName(firstName);
    }

    public Date getBirthDate() {
        return patient.getBirthDate();
    }

    public void setBirthDate(Date birthDate) {
        patient.setBirthDate(birthDate);
    }

    public Date getDeathDate() {
        return patient.getDeathDate();
    }

    public void setDeathDate(Date deathDate) {
        patient.setDeathDate(deathDate);
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
        this.setPatientAttributeValues(patientAttributeValues);
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
            = new PatientAttributeValue
                (new PatientAttributeValueId(patient, attribute));

        getPatientAttributeValues().add(result);
        
        return result;
    }
    
    public PatientAttributeValue getAttributeValue(Attribute attribute)
    {
        for(PatientAttributeValue pav : getPatientAttributeValues())
        {
            if(pav.getId().getAttribute().getAttributeIi().equals(attribute.getAttributeIi()))
            {
                return pav;
            }
        }
        
        return null;
    }
    
    public Therapy createTherapy(Date startDate) {
        Therapy result = new Therapy(patient, startDate);
        getTherapies().add(result);
        return result;
    }
    
    public ViralIsolate createViralIsolate() {
        ViralIsolate result = new ViralIsolate(patient);
        getViralIsolates().add(result);
        return result;
    }
    
    public TestResult createTestResult(Test test) {
        TestResult result = new TestResult(test, patient);
        getTestResults().add(result);
        return result;
    }
}
