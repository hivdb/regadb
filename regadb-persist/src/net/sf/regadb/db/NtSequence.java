package net.sf.regadb.db;


import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * NtSequence generated by hbm2java
 */
public class NtSequence implements java.io.Serializable {

    private Integer ntSequenceIi;

    private int version;

    private ViralIsolate viralIsolate;

    private String label;

    private Date sequenceDate;

    private String nucleotides;
    
    private boolean aligned;

	private Set<AaSequence> aaSequences = new HashSet<AaSequence>(0);

    private Set<TestResult> testResults = new HashSet<TestResult>(0);

    public NtSequence() {
    }

    public NtSequence(ViralIsolate viralIsolate) {
        this.viralIsolate = viralIsolate;
    }

    public NtSequence(ViralIsolate viralIsolate, String label,
            Date sequenceDate, String nucleotides, boolean aligned,
            Set<AaSequence> aaSequences, Set<TestResult> testResults) {
        this.viralIsolate = viralIsolate;
        this.label = label;
        this.sequenceDate = sequenceDate;
        this.nucleotides = nucleotides;
        this.aligned = aligned;
        this.aaSequences = aaSequences;
        this.testResults = testResults;
    }

    public Integer getNtSequenceIi() {
        return this.ntSequenceIi;
    }

    public void setNtSequenceIi(Integer ntSequenceIi) {
        this.ntSequenceIi = ntSequenceIi;
    }

    public int getVersion() {
        return this.version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public ViralIsolate getViralIsolate() {
        return this.viralIsolate;
    }

    public void setViralIsolate(ViralIsolate viralIsolate) {
        this.viralIsolate = viralIsolate;
    }

    public String getLabel() {
        return this.label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Date getSequenceDate() {
        return this.sequenceDate;
    }

    public void setSequenceDate(Date sequenceDate) {
        this.sequenceDate = sequenceDate;
    }

    public String getNucleotides() {
        return this.nucleotides;
    }

    public void setNucleotides(String nucleotides) {
        this.nucleotides = nucleotides;
    }
    
    public boolean isAligned() {
		return aligned;
	}

	public void setAligned(boolean aligned) {
		this.aligned = aligned;
	}

    public Set<AaSequence> getAaSequences() {
        return this.aaSequences;
    }

    public void setAaSequences(Set<AaSequence> aaSequences) {
        this.aaSequences = aaSequences;
    }

    public Set<TestResult> getTestResults() {
        return this.testResults;
    }

    public void setTestResults(Set<TestResult> testResults) {
        this.testResults = testResults;
    }

}
