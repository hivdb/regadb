package net.sf.regadb.db;


/**
 * TestObject generated by hbm2java
 */
public class TestObject implements java.io.Serializable {

    // Fields    

    private Integer testObjectIi;

    private int version;

    private String description;

    private Integer testObjectId;

    // Constructors

    /** default constructor */
    public TestObject() {
    }

    /** minimal constructor */
    public TestObject(String description) {
        this.description = description;
    }

    /** full constructor */
    public TestObject(String description, Integer testObjectId) {
        this.description = description;
        this.testObjectId = testObjectId;
    }

    // Property accessors
    public Integer getTestObjectIi() {
        return this.testObjectIi;
    }

    public void setTestObjectIi(Integer testObjectIi) {
        this.testObjectIi = testObjectIi;
    }

    public int getVersion() {
        return this.version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getTestObjectId() {
        return this.testObjectId;
    }

    public void setTestObjectId(Integer testObjectId) {
        this.testObjectId = testObjectId;
    }

}
