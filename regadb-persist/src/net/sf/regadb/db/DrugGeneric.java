package net.sf.regadb.db;


import java.util.HashSet;
import java.util.Set;

/**
 * DrugGeneric generated by hbm2java
 */
public class DrugGeneric implements java.io.Serializable {

    // Fields    

    private Integer genericIi;

    private int version;

    private DrugClass drugClass;

    private String genericId;

    private String genericName;

    private Integer resistanceTableOrder;

    private String atcCode;

    private Set<DrugCommercial> drugCommercials = new HashSet<DrugCommercial>(0);

    // Constructors

    /** default constructor */
    public DrugGeneric() {
    }

    /** minimal constructor */
    public DrugGeneric(DrugClass drugClass, String genericId, String genericName) {
        this.drugClass = drugClass;
        this.genericId = genericId;
        this.genericName = genericName;
    }

    /** full constructor */
    public DrugGeneric(DrugClass drugClass, String genericId,
            String genericName, Integer resistanceTableOrder, String atcCode,
            Set<DrugCommercial> drugCommercials) {
        this.drugClass = drugClass;
        this.genericId = genericId;
        this.genericName = genericName;
        this.resistanceTableOrder = resistanceTableOrder;
        this.atcCode = atcCode;
        this.drugCommercials = drugCommercials;
    }

    // Property accessors
    public Integer getGenericIi() {
        return this.genericIi;
    }

    public void setGenericIi(Integer genericIi) {
        this.genericIi = genericIi;
    }

    public int getVersion() {
        return this.version;
    }

    public void setVersion(int version) {
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

    public Integer getResistanceTableOrder() {
        return this.resistanceTableOrder;
    }

    public void setResistanceTableOrder(Integer resistanceTableOrder) {
        this.resistanceTableOrder = resistanceTableOrder;
    }

    public String getAtcCode() {
        return this.atcCode;
    }

    public void setAtcCode(String atcCode) {
        this.atcCode = atcCode;
    }

    public Set<DrugCommercial> getDrugCommercials() {
        return this.drugCommercials;
    }

    public void setDrugCommercials(Set<DrugCommercial> drugCommercials) {
        this.drugCommercials = drugCommercials;
    }

}
