package net.sf.regadb.db;

// Generated 18/04/2007 12:36:46 by Hibernate Tools 3.2.0.beta8

import java.util.HashSet;
import java.util.Set;

/**
 * TestType generated by hbm2java
 */
public class TestType implements java.io.Serializable {

    // Fields    

    private Integer testTypeIi;

    private Integer version;

    private ValueType valueType;

    private TestObject testObject;

    private String description;

    private Set<TestNominalValue> testNominalValues = new HashSet<TestNominalValue>(
            0);

    // Constructors

    /** default constructor */
    public TestType() {
    }

    /** minimal constructor */
    public TestType(TestObject testObject, String description) {
        this.testObject = testObject;
        this.description = description;
    }

    /** full constructor */
    public TestType(ValueType valueType, TestObject testObject,
            String description, Set<TestNominalValue> testNominalValues) {
        this.valueType = valueType;
        this.testObject = testObject;
        this.description = description;
        this.testNominalValues = testNominalValues;
    }

    // Property accessors
    public Integer getTestTypeIi() {
        return this.testTypeIi;
    }

    public void setTestTypeIi(Integer testTypeIi) {
        this.testTypeIi = testTypeIi;
    }

    public Integer getVersion() {
        return this.version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public ValueType getValueType() {
        return this.valueType;
    }

    public void setValueType(ValueType valueType) {
        this.valueType = valueType;
    }

    public TestObject getTestObject() {
        return this.testObject;
    }

    public void setTestObject(TestObject testObject) {
        this.testObject = testObject;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<TestNominalValue> getTestNominalValues() {
        return this.testNominalValues;
    }

    public void setTestNominalValues(Set<TestNominalValue> testNominalValues) {
        this.testNominalValues = testNominalValues;
    }

}
