package net.sf.regadb.service;


public class AnalysisThread extends Thread
{
    private IAnalysis analysis_;
    
    public AnalysisThread(final IAnalysis analysis)
    {
        super(new Runnable()
        {
            public void run() 
            {
                analysis.launch();
            }
        });
        analysis_ = analysis;
    }

    public IAnalysis getAnalysis() 
    {
        return analysis_;
    }
}
