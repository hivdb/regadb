package net.sf.regadb.db;

import java.util.Date;

public class AnalysisRun 
{
    private SettingsUser user;
    private Analysis analysis;
    private Date startTime;
    private Date endTime;
    
    public AnalysisRun(SettingsUser user, Analysis analysis, Date startTime, Date endTime) {
        this.user = user;
        this.analysis = analysis;
        this.startTime = startTime;
        this.endTime = endTime;
    }
    
    public Analysis getAnalysis() {
        return analysis;
    }
    public void setAnalysis(Analysis analysis) {
        this.analysis = analysis;
    }
    public Date getEndTime() {
        return endTime;
    }
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
    public Date getStartTime() {
        return startTime;
    }
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }
    public SettingsUser getUser() {
        return user;
    }
    public void setUser(SettingsUser user) {
        this.user = user;
    }
}
