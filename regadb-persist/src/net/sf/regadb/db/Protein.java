package net.sf.regadb.db;


/**
 * Protein generated by hbm2java
 */
public class Protein implements java.io.Serializable {

    // Fields    

    private Integer proteinIi;

    private int version;

    private String abbreviation;

    private String fullName;

    // Constructors

    /** default constructor */
    public Protein() {
    }

    /** minimal constructor */
    public Protein(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    /** full constructor */
    public Protein(String abbreviation, String fullName) {
        this.abbreviation = abbreviation;
        this.fullName = fullName;
    }

    // Property accessors
    public Integer getProteinIi() {
        return this.proteinIi;
    }

    public void setProteinIi(Integer proteinIi) {
        this.proteinIi = proteinIi;
    }

    public int getVersion() {
        return this.version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getAbbreviation() {
        return this.abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public String getFullName() {
        return this.fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

}
