package net.sf.regadb.service;

import net.sf.regadb.db.session.Login;
import net.sf.regadb.service.wts.ServiceException;


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
            	Login copiedLogin = login.copyLogin();
                try {
                    analysis.launch(copiedLogin);
                } catch (ServiceException e) {
                    e.printStackTrace();
                } finally {
                    copiedLogin.closeSession();
                }
            }
        });
        analysis_ = analysis;
    }

    public IAnalysis getAnalysis() 
    {
        return analysis_;
    }
}
