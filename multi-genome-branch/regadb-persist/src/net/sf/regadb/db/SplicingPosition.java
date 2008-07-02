package net.sf.regadb.db;


/**
 * SplicingPosition generated by hbm2java
 */
public class SplicingPosition implements java.io.Serializable {

    // Fields    

    private Integer splicingPositionIi;

    private int version;

    private Protein protein;

    private int position;

    // Constructors

    /** default constructor */
    public SplicingPosition() {
    }

    /** full constructor */
    public SplicingPosition(Protein protein, int position) {
        this.protein = protein;
        this.position = position;
    }

    // Property accessors
    public Integer getSplicingPositionIi() {
        return this.splicingPositionIi;
    }

    public void setSplicingPositionIi(Integer splicingPositionIi) {
        this.splicingPositionIi = splicingPositionIi;
    }

    public int getVersion() {
        return this.version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Protein getProtein() {
        return this.protein;
    }

    public void setProtein(Protein protein) {
        this.protein = protein;
    }

    public int getPosition() {
        return this.position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

}
