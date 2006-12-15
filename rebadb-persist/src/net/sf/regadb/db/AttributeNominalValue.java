package net.sf.regadb.db;

// Generated Dec 15, 2006 10:15:52 AM by Hibernate Tools 3.2.0.beta8

/**
 * AttributeNominalValue generated by hbm2java
 */
public class AttributeNominalValue implements java.io.Serializable {

    // Fields    

    private AttributeNominalValueId id;

    private Integer version;

    private Attribute attribute;

    private String value;

    // Constructors

    /** default constructor */
    public AttributeNominalValue() {
    }

    /** full constructor */
    public AttributeNominalValue(AttributeNominalValueId id,
            Attribute attribute, String value) {
        this.id = id;
        this.attribute = attribute;
        this.value = value;
    }

    // Property accessors
    public AttributeNominalValueId getId() {
        return this.id;
    }

    public void setId(AttributeNominalValueId id) {
        this.id = id;
    }

    public Integer getVersion() {
        return this.version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Attribute getAttribute() {
        return this.attribute;
    }

    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
