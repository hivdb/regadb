package net.sf.regadb.ui.form.query;

import java.io.File;
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
import net.sf.regadb.io.exportCsv.ExportToCsv;
import net.sf.regadb.util.settings.RegaDBSettings;

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
				os = new FileOutputStream(RegaDBSettings.getInstance().getPropertyValue("regadb.query.resultDir") + File.separatorChar + fileName_);
			}
        	catch (FileNotFoundException e1)
        	{
				e1.printStackTrace();
			}
        	
        	qdr_.setResult(fileName_);
        	
        	try
        	{
        		Query q = t.createQuery(qdr_.getQueryDefinition().getQuery());
        		
        		for(QueryDefinitionRunParameter qdrp : qdr_.getQueryDefinitionRunParameters())
            	{
            		q.setParameter(qdrp.getQueryDefinitionParameter().getName(), qdrp.getValue());
            	}
        		
        		List result = q.list();
        		
                ExportToCsv csvExport = new ExportToCsv();
                
        		for(Object o : result)
        		{
        			if(q.getReturnTypes().length == 1)
        			{
        				os.write((csvExport.getCsvLineSwitch(o)+"\n").getBytes());
        			}
        			else
        			{
        				Object[] array = (Object[])o;
        				
        				for(int i = 0; i < array.length - 1; i++)
        				{
        					os.write((csvExport.getCsvLineSwitch(array[i])+",").getBytes());
        				}

                        os.write((csvExport.getCsvLineSwitch(array[array.length - 1])+"\n").getBytes());
        			}
        		}
        		
        		qdr_.setStatus((QueryDefinitionRunStatus.Finished).getValue());
        		
        		qdr_.setEnddate(new Date(System.currentTimeMillis()));
        		
        		QueryThread.removeQueryThread(fileName_);
        	}
        	catch(Exception e)
        	{
        		try
        		{
					os.write((e.getMessage()).getBytes());
				}
        		catch (IOException e1)
        		{
					e1.printStackTrace();
				}
        		
        		qdr_.setStatus((QueryDefinitionRunStatus.Failed).getValue());
        		
        		qdr_.setEnddate(new Date(System.currentTimeMillis()));
        		
        		QueryThread.removeQueryThread(fileName_);
        	}
        	
        	t.commit();
        }
}
