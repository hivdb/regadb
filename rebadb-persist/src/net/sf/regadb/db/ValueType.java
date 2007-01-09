package net.sf.regadb.db;

// Generated 9/01/2007 13:30:52 by Hibernate Tools 3.2.0.beta8

/**
 * ValueType generated by hbm2java
 */
public class ValueType implements java.io.Serializable {

    // Fields    

    private Integer valueTypeIi;

    private Integer version;

    private String description;

    private Double min;

    private Double max;

    private Boolean multiple;

    // Constructors

    /** default constructor */
    public ValueType() {
    }

    /** minimal constructor */
    public ValueType(String description) {
        this.description = description;
    }

    /** full constructor */
    public ValueType(String description, Double min, Double max,
            Boolean multiple) {
        this.description = description;
        this.min = min;
        this.max = max;
        this.multiple = multiple;
    }

    // Property accessors
    public Integer getValueTypeIi() {
        return this.valueTypeIi;
    }

    public void setValueTypeIi(Integer valueTypeIi) {
        this.valueTypeIi = valueTypeIi;
    }

    public Integer getVersion() {
        return this.version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getMin() {
        return this.min;
    }

    public void setMin(Double min) {
        this.min = min;
    }

    public Double getMax() {
        return this.max;
    }

    public void setMax(Double max) {
        this.max = max;
    }

    public Boolean getMultiple() {
        return this.multiple;
    }

    public void setMultiple(Boolean multiple) {
        this.multiple = multiple;
    }

}
