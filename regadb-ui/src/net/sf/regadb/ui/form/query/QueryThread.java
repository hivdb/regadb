package net.sf.regadb.ui.form.query;

import java.util.HashMap;
import java.util.Map;

import net.sf.regadb.db.QueryDefinitionRun;
import net.sf.regadb.db.session.Login;

public class QueryThread
{
	private static Object mutex_ = new Object();
	
	private static Map<String, QueryThread> queryThreads = new HashMap<String, QueryThread>();
	
	private Thread thread_;
	private String fileName_;
	private Login copiedLogin_;
	
	public QueryThread(Login copiedLogin, final QueryDefinitionRun qdr, Map<String, Object> paramObjects)
	{
		copiedLogin_ = copiedLogin;
		fileName_ = init(qdr);
		thread_ = new Thread(new QueryRunnable(copiedLogin, qdr, fileName_, paramObjects));
	}
		
    private String init(final QueryDefinitionRun qdr)
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
    		
			return copiedLogin_.getUid() + "_" + qdr.getQueryDefinition().getName() + "_" + qdr.getName() + "_" + System.currentTimeMillis() + ".csv";
    	}
    }
	
    public void startQueryThread()
	{
		thread_.start();
		
		queryThreads.put(fileName_, this);
	}
    
	public static void stopQueryThread(String fileName)
    {
    	Thread thread = queryThreads.get(fileName).thread_;
    	
    	if(!(thread.isInterrupted()))
    	{
    		thread.interrupt();
    	}
    	
    	removeQueryThread(fileName);
    }
    
    public static void removeQueryThread(String fileName)
    {
    	QueryThread qt = queryThreads.get(fileName);
    	if(qt != null)
    		qt.copiedLogin_.closeSession();
    	
    	queryThreads.remove(fileName);
    }
}
