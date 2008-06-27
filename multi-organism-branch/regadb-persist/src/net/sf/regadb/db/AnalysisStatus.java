package net.sf.regadb.db;

public class AnalysisStatus 
{
    public final static AnalysisStatus STARTING = new AnalysisStatus("STARTING");
    public final static AnalysisStatus RUNNING = new AnalysisStatus("RUNNING");
    public final static AnalysisStatus FINISHED = new AnalysisStatus("FINISHED");
    public final static AnalysisStatus ERROR = new AnalysisStatus("ERROR");
    public final static AnalysisStatus PERSIST_ERROR = new AnalysisStatus("PERSIST_ERROR");
    
    private String description;

    public AnalysisStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
