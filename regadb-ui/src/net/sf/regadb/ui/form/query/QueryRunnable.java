package net.sf.regadb.ui.form.query;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

import net.sf.regadb.db.QueryDefinitionRun;
import net.sf.regadb.db.QueryDefinitionRunParameter;
import net.sf.regadb.db.QueryDefinitionRunStatus;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.session.Login;
import net.sf.regadb.ui.settings.Settings;

import org.hibernate.Query;

public class QueryRunnable implements Runnable
{
	private String fileName_;
	private Login copiedLogin_;
	private QueryDefinitionRun qdr_;
	
	public QueryRunnable(final Login copiedLogin, final QueryDefinitionRun qdr, String fileName)
	{
		fileName_ = fileName;
		copiedLogin_ = copiedLogin;
		qdr_ = qdr;
	}
	
	public void run() 
        {
			OutputStream os = null;
			
        	Transaction t = copiedLogin_.createTransaction();
        	
        	try
        	{
				os = new FileOutputStream(Settings.getQueryResultDir() + fileName_);
			}
        	catch (FileNotFoundException e1)
        	{
				e1.printStackTrace();
			}
        	
        	try
        	{
        		Query q = t.createQuery(qdr_.getQueryDefinition().getQuery());
        		
        		for(QueryDefinitionRunParameter qdrp : qdr_.getQueryDefinitionRunParameters())
            	{
            		q.setParameter(qdrp.getQueryDefinitionParameter().getName(), qdrp.getValue());
            	}
        		
        		List result = q.list();
        		
        		for(Object o : result)
        		{
        			if(q.getReturnTypes().length == 1)
        			{
        				os.write((o.toString()).getBytes());
        			}
        			else
        			{
        				Object[] array = (Object[])o;
        				
        				for(int i = 0; i < array.length - 1; i++)
        				{
        					os.write((array[i].toString()).getBytes());
        					
        					os.write((",").getBytes());
        				}
        				
        				os.write((array[array.length - 1].toString()).getBytes());
        			}
        			
        			os.write(("\n").getBytes());
        		}
        		
        		qdr_.setStatus((QueryDefinitionRunStatus.Finished).getValue());
        		
        		qdr_.setResult(fileName_);
        		
        		qdr_.setEnddate(new Date(System.currentTimeMillis()));
        	}
        	catch(Exception e)
        	{
        		try
        		{
					os.write(("Query Failed").getBytes());
				}
        		catch (IOException e1)
        		{
					e1.printStackTrace();
				}
        		
        		qdr_.setStatus((QueryDefinitionRunStatus.Failed).getValue());
        		
        		qdr_.setEnddate(new Date(System.currentTimeMillis()));
        	}
        	
        	t.commit();
        }
}
