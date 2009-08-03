package net.sf.regadb.ui.form.query.querytool;

import java.util.HashMap;
import java.util.Map;

import net.sf.regadb.db.session.Login;

import com.pharmadm.custom.rega.queryeditor.QueryEditor;

import eu.webtoolkit.jwt.WString;

public class QueryToolThread {
	private static Object mutex_ = new Object();
	private QueryToolRunnable run_;
	
	private static Map<String, QueryToolThread> queryThreads = new HashMap<String, QueryToolThread>();
	
	private Thread thread_;
	private String fileName_;
	
	public QueryToolThread(final Login copiedLogin, QueryEditor editor)
	{
		fileName_ = init(copiedLogin);
		run_ = new QueryToolRunnable(copiedLogin, fileName_, editor);
		thread_ = new Thread(run_);
	}
		
    private String init(final Login copiedLogin)
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
    		
			return copiedLogin.getUid() + "_" + getFileName() + "_" + System.currentTimeMillis() + ".csv";
    	}
    }
    
	private String getFileName() {
		//TODO
		//????
		
		return WString.tr("file.query.querytool").getValue();
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
    
    public QueryToolRunnable getRun() {
    	return run_;
    }
    
    public static void removeQueryThread(String fileName)
    {
    	queryThreads.remove(fileName);
    }
}
