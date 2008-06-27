package net.sf.regadb.service;

import net.sf.regadb.db.session.Login;


public class AnalysisThread extends Thread
{
    private IAnalysis analysis_;
    public final static Object mutex_ = new Object();
    
    public AnalysisThread(final IAnalysis analysis, final Login login)
    {
        super(new Runnable()
        {
            public void run() 
            {
                analysis.launch(login);
            }
        });
        analysis_ = analysis;
    }

    public IAnalysis getAnalysis() 
    {
        return analysis_;
    }
}
