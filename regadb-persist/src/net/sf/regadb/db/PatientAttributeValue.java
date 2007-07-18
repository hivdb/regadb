package net.sf.regadb.db;

// Generated Jul 18, 2007 4:05:12 PM by Hibernate Tools 3.2.0.beta8

/**
 * PatientAttributeValue generated by hbm2java
 */
public class PatientAttributeValue implements java.io.Serializable {

    // Fields    

    private PatientAttributeValueId id;

    private int version;

    private AttributeNominalValue attributeNominalValue;

    private String value;

    // Constructors

    /** default constructor */
    public PatientAttributeValue() {
    }

    /** minimal constructor */
    public PatientAttributeValue(PatientAttributeValueId id) {
        this.id = id;
    }

    /** full constructor */
    public PatientAttributeValue(PatientAttributeValueId id,
            AttributeNominalValue attributeNominalValue, String value) {
        this.id = id;
        this.attributeNominalValue = attributeNominalValue;
        this.value = value;
    }

    // Property accessors
    public PatientAttributeValueId getId() {
        return this.id;
    }

    public void setId(PatientAttributeValueId id) {
        this.id = id;
    }

    public int getVersion() {
        return this.version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public AttributeNominalValue getAttributeNominalValue() {
        return this.attributeNominalValue;
    }

    public void setAttributeNominalValue(
            AttributeNominalValue attributeNominalValue) {
        this.attributeNominalValue = attributeNominalValue;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
