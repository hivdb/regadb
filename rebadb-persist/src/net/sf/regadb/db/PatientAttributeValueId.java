package net.sf.regadb.db;

// Generated 9/01/2007 13:30:52 by Hibernate Tools 3.2.0.beta8

/**
 * PatientAttributeValueId generated by hbm2java
 */
public class PatientAttributeValueId implements java.io.Serializable {

    // Fields    

    private int patientIi;

    private int attributeIi;

    // Constructors

    /** default constructor */
    public PatientAttributeValueId() {
    }

    /** full constructor */
    public PatientAttributeValueId(int patientIi, int attributeIi) {
        this.patientIi = patientIi;
        this.attributeIi = attributeIi;
    }

    // Property accessors
    public int getPatientIi() {
        return this.patientIi;
    }

    public void setPatientIi(int patientIi) {
        this.patientIi = patientIi;
    }

    public int getAttributeIi() {
        return this.attributeIi;
    }

    public void setAttributeIi(int attributeIi) {
        this.attributeIi = attributeIi;
    }

}
