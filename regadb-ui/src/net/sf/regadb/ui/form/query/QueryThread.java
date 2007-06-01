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

public class QueryThread extends Thread
{
	private static String fileName;
	
	private static OutputStream os;
	
	public QueryThread(final Login copiedLogin, final QueryDefinitionRun qdr)
	{
		super(new Runnable()
        {
            public void init()
            {
            	fileName = copiedLogin.getUid() + "_" + qdr.getQueryDefinition().getName() + "_" + qdr.getName() + "_" + System.currentTimeMillis() + ".csv";
            }
			
			public void run() 
            {
				init();
				
            	Transaction t = copiedLogin.createTransaction();
            	
            	try
            	{
					os = new FileOutputStream(Settings.getQueryResultDir() + fileName);
				}
            	catch (FileNotFoundException e1)
            	{
					e1.printStackTrace();
				}
            	
            	try
            	{
            		Query q = t.createQuery(qdr.getQueryDefinition().getQuery());
            		
            		for(QueryDefinitionRunParameter qdrp : qdr.getQueryDefinitionRunParameters())
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
            		
            		qdr.setStatus((QueryDefinitionRunStatus.Finished).getValue());
            		
            		qdr.setResult(fileName);
            		
            		qdr.setEnddate(new Date(System.currentTimeMillis()));
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
            		
            		qdr.setStatus((QueryDefinitionRunStatus.Failed).getValue());
            		
            		qdr.setEnddate(new Date(System.currentTimeMillis()));
            	}
            	
            	t.commit();
            }
        });
	}
}
