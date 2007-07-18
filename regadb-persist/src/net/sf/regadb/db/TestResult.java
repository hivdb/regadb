package net.sf.regadb.db;

// Generated Jul 18, 2007 4:05:12 PM by Hibernate Tools 3.2.0.beta8

import java.util.Date;

/**
 * TestResult generated by hbm2java
 */
public class TestResult implements java.io.Serializable {

    // Fields    

    private Integer testResultIi;

    private int version;

    private Test test;

    private DrugGeneric drugGeneric;

    private ViralIsolate viralIsolate;

    private TestNominalValue testNominalValue;

    private PatientImpl patient;

    private NtSequence ntSequence;

    private String value;

    private Date testDate;

    private String sampleId;

    private byte[] data;

    // Constructors

    /** default constructor */
    public TestResult() {
    }

    /** minimal constructor */
    public TestResult(Test test) {
        this.test = test;
    }

    /** full constructor */
    public TestResult(Test test, DrugGeneric drugGeneric,
            ViralIsolate viralIsolate, TestNominalValue testNominalValue,
            PatientImpl patient, NtSequence ntSequence, String value,
            Date testDate, String sampleId, byte[] data) {
        this.test = test;
        this.drugGeneric = drugGeneric;
        this.viralIsolate = viralIsolate;
        this.testNominalValue = testNominalValue;
        this.patient = patient;
        this.ntSequence = ntSequence;
        this.value = value;
        this.testDate = testDate;
        this.sampleId = sampleId;
        this.data = data;
    }

    // Property accessors
    public Integer getTestResultIi() {
        return this.testResultIi;
    }

    public void setTestResultIi(Integer testResultIi) {
        this.testResultIi = testResultIi;
    }

    public int getVersion() {
        return this.version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Test getTest() {
        return this.test;
    }

    public void setTest(Test test) {
        this.test = test;
    }

    public DrugGeneric getDrugGeneric() {
        return this.drugGeneric;
    }

    public void setDrugGeneric(DrugGeneric drugGeneric) {
        this.drugGeneric = drugGeneric;
    }

    public ViralIsolate getViralIsolate() {
        return this.viralIsolate;
    }

    public void setViralIsolate(ViralIsolate viralIsolate) {
        this.viralIsolate = viralIsolate;
    }

    public TestNominalValue getTestNominalValue() {
        return this.testNominalValue;
    }

    public void setTestNominalValue(TestNominalValue testNominalValue) {
        this.testNominalValue = testNominalValue;
    }

    public PatientImpl getPatient() {
        return this.patient;
    }

    public void setPatient(PatientImpl patient) {
        this.patient = patient;
    }

    public NtSequence getNtSequence() {
        return this.ntSequence;
    }

    public void setNtSequence(NtSequence ntSequence) {
        this.ntSequence = ntSequence;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Date getTestDate() {
        return this.testDate;
    }

    public void setTestDate(Date testDate) {
        this.testDate = testDate;
    }

    public String getSampleId() {
        return this.sampleId;
    }

    public void setSampleId(String sampleId) {
        this.sampleId = sampleId;
    }

    public byte[] getData() {
        return this.data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

}
