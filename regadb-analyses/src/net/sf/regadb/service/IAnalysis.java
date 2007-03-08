package net.sf.regadb.service;

import java.util.Date;

import net.sf.regadb.db.AnalysisStatus;
import net.sf.regadb.db.SettingsUser;

public interface IAnalysis 
{
    public SettingsUser getUser();
    public AnalysisStatus getStatus();
    public Date getStartTime();
    public Date getEndTime();
    //return null if it should not be removed from the loggin
    //return a Long represeting the time after the Analsyis should be removed from the logging
    public Long removeFromLogging();
    
    public void launch();
    public void pause();
    public void kill();
}
