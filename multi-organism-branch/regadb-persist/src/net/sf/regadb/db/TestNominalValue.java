package net.sf.regadb.db;


/**
 * TestNominalValue generated by hbm2java
 */
public class TestNominalValue implements java.io.Serializable {

    // Fields    

    private Integer nominalValueIi;

    private int version;

    private TestType testType;

    private String value;

    // Constructors

    /** default constructor */
    public TestNominalValue() {
    }

    /** full constructor */
    public TestNominalValue(TestType testType, String value) {
        this.testType = testType;
        this.value = value;
    }

    // Property accessors
    public Integer getNominalValueIi() {
        return this.nominalValueIi;
    }

    public void setNominalValueIi(Integer nominalValueIi) {
        this.nominalValueIi = nominalValueIi;
    }

    public int getVersion() {
        return this.version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public TestType getTestType() {
        return this.testType;
    }

    public void setTestType(TestType testType) {
        this.testType = testType;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
