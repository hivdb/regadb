package net.sf.regadb.workflow.analysis;

public class AnalysisOutput {
    private String name;
    private Analysis analysis;
    
    public AnalysisOutput(String name, Analysis analysis) {
        this.name = name;
        this.analysis = analysis;
    }
    
    public Analysis getAnalysis() {
        return analysis;
    }
    public void setAnalysis(Analysis analysis) {
        this.analysis = analysis;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
