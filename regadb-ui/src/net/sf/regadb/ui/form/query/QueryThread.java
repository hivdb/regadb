package net.sf.regadb.ui.form.query;

import net.sf.regadb.db.QueryDefinitionRun;
import net.sf.regadb.db.session.Login;

public class QueryThread
{
	private static Object mutex_ = new Object();
	
	private Thread thread_;
	private String fileName_;
	
	public QueryThread(final Login copiedLogin, final QueryDefinitionRun qdr)
	{
		fileName_ = init(copiedLogin, qdr);
		thread_ = new Thread(new QueryRunnable(copiedLogin, qdr, fileName_));
	}
		
    private String init(final Login copiedLogin, final QueryDefinitionRun qdr)
    {
    	synchronized(mutex_)
    	{
    		try 
    		{
				Thread.sleep(2);
    		}
    		catch (InterruptedException e) 
    		{
				e.printStackTrace();
			}
    		
			return copiedLogin.getUid() + "_" + qdr.getQueryDefinition().getName() + "_" + qdr.getName() + "_" + System.currentTimeMillis() + ".csv";
    	}
    }
	
    public void startQueryThread()
	{
		thread_.start();
	}
    
    public void stopQueryThread()
    {
    	if(thread_.isAlive())
    	{
    		thread_.stop();
    	}
    }

	public String getFileName() 
	{
		return fileName_;
	}
}
