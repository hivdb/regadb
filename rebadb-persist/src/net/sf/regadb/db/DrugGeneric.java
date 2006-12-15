package net.sf.regadb.db;

// Generated Dec 15, 2006 10:15:52 AM by Hibernate Tools 3.2.0.beta8

import java.util.HashSet;
import java.util.Set;

/**
 * DrugGeneric generated by hbm2java
 */
public class DrugGeneric implements java.io.Serializable {

    // Fields    

    private int genericIi;

    private Integer version;

    private DrugClass drugClass;

    private String genericId;

    private String genericName;

    private Set<TherapyGeneric> therapyGenerics = new HashSet<TherapyGeneric>(0);

    private Set<TestResult> testResults = new HashSet<TestResult>(0);

    private Set<DrugCommercial> drugCommercials = new HashSet<DrugCommercial>(0);

    // Constructors

    /** default constructor */
    public DrugGeneric() {
    }

    /** minimal constructor */
    public DrugGeneric(int genericIi, DrugClass drugClass, String genericId,
            String genericName) {
        this.genericIi = genericIi;
        this.drugClass = drugClass;
        this.genericId = genericId;
        this.genericName = genericName;
    }

    /** full constructor */
    public DrugGeneric(int genericIi, DrugClass drugClass, String genericId,
            String genericName, Set<TherapyGeneric> therapyGenerics,
            Set<TestResult> testResults, Set<DrugCommercial> drugCommercials) {
        this.genericIi = genericIi;
        this.drugClass = drugClass;
        this.genericId = genericId;
        this.genericName = genericName;
        this.therapyGenerics = therapyGenerics;
        this.testResults = testResults;
        this.drugCommercials = drugCommercials;
    }

    // Property accessors
    public int getGenericIi() {
        return this.genericIi;
    }

    public void setGenericIi(int genericIi) {
        this.genericIi = genericIi;
    }

    public Integer getVersion() {
        return this.version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public DrugClass getDrugClass() {
        return this.drugClass;
    }

    public void setDrugClass(DrugClass drugClass) {
        this.drugClass = drugClass;
    }

    public String getGenericId() {
        return this.genericId;
    }

    public void setGenericId(String genericId) {
        this.genericId = genericId;
    }

    public String getGenericName() {
        return this.genericName;
    }

    public void setGenericName(String genericName) {
        this.genericName = genericName;
    }

    public Set<TherapyGeneric> getTherapyGenerics() {
        return this.therapyGenerics;
    }

    public void setTherapyGenerics(Set<TherapyGeneric> therapyGenerics) {
        this.therapyGenerics = therapyGenerics;
    }

    public Set<TestResult> getTestResults() {
        return this.testResults;
    }

    public void setTestResults(Set<TestResult> testResults) {
        this.testResults = testResults;
    }

    public Set<DrugCommercial> getDrugCommercials() {
        return this.drugCommercials;
    }

    public void setDrugCommercials(Set<DrugCommercial> drugCommercials) {
        this.drugCommercials = drugCommercials;
    }

}
