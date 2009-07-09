package net.sf.regadb.service;

import java.util.ArrayList;
import java.util.List;

import net.sf.regadb.db.session.Login;

public class AnalysisPool 
{
    private static AnalysisPool instance_ = null;
    
    private List<AnalysisThread> threadList_ = new ArrayList<AnalysisThread>();
    
    private AnalysisPool()
    {
        /*TimerTask keepThreadListClean = new TimerTask()
        {
            @Override
            public void run() 
            {
                cleanThreadList();
            }
        };
        
        Timer timer = new Timer();
        timer.schedule(keepThreadListClean, 60*5*1000, 60*2*1000);*/
    }
    
    private synchronized void cleanThreadList()
    {
        List<AnalysisThread> toBeRemoved = new ArrayList<AnalysisThread>();
        long currentTime = System.currentTimeMillis();
        long endTimePlusLogDelay;
        for(AnalysisThread t : threadList_)
        {
            if(t.getAnalysis().getEndTime()!=null && t.getAnalysis().removeFromLogging()!=null && !t.isAlive())
            {
                endTimePlusLogDelay = t.getAnalysis().getEndTime().getTime() + t.getAnalysis().removeFromLogging();
                if(currentTime>=endTimePlusLogDelay)
                {
                    toBeRemoved.add(t);
                }
            }
        }
        
        threadList_.removeAll(toBeRemoved);
    }
    
    public synchronized void launchAnalysis(final IAnalysis analysis, Login login)
    {
        AnalysisThread t = new AnalysisThread(analysis, login);
        t.start();
        threadList_.add(t);
    }
    
    //The items returned by this list might be cleaned up by a job thread
    //Be aware of this when using them
    public synchronized List<IAnalysis> getAnalyses()
    {
        List<IAnalysis> analysesList = new ArrayList<IAnalysis>();
        
        for(AnalysisThread at : threadList_)
        {
            analysesList.add(at.getAnalysis());
        }
        
        return analysesList;
    }
    
    public static AnalysisPool getInstance()
    {
        if(instance_==null)
            instance_ = new AnalysisPool();
        return instance_;
    }
}
