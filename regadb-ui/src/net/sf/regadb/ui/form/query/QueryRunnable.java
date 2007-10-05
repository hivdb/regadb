package net.sf.regadb.ui.form.query;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
    private Map<String, Object> paramObjects_;
	
	public QueryRunnable(final Login copiedLogin, final QueryDefinitionRun qdr, String fileName, Map<String, Object> paramObjects)
	{
		fileName_ = fileName;
		copiedLogin_ = copiedLogin;
		qdr_ = qdr;
        paramObjects_ = paramObjects;
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
            		q.setParameter(qdrp.getQueryDefinitionParameter().getName(), paramObjects_.get(qdrp.getQueryDefinitionParameter().getName()));
            	}
        		
        		List result = q.list();
        		
                ExportToCsv csvExport = new ExportToCsv();
                
                if(result.size()>0) {
                    if(q.getReturnTypes().length == 1)
                    {
                        os.write((getCsvHeaderSwitchNoComma(result.get(0), csvExport)+"\n").getBytes());
                    }
                    else
                    {
                        Object[] array = (Object[])result.get(0);
                        
                        for(int i = 0; i < array.length - 1; i++)
                        {
                            os.write((getCsvHeaderSwitchNoComma(array[i], csvExport)+",").getBytes());
                        }

                        os.write((getCsvHeaderSwitchNoComma(array[array.length - 1], csvExport)+"\n").getBytes());
                    }
                }
                
        		for(Object o : result)
        		{
        			if(q.getReturnTypes().length == 1)
        			{
        				os.write((getCsvLineSwitchNoComma(o, csvExport)+"\n").getBytes());
        			}
        			else
        			{
        				Object[] array = (Object[])o;
        				
        				for(int i = 0; i < array.length - 1; i++)
        				{
        					os.write((getCsvLineSwitchNoComma(array[i], csvExport)+",").getBytes());
        				}

                        os.write((getCsvLineSwitchNoComma(array[array.length - 1], csvExport)+"\n").getBytes());
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
    
    public String getCsvLineSwitchNoComma(Object o, ExportToCsv csvExport) {
        String temp = csvExport.getCsvLineSwitch(o);
        if(temp==null)
            return temp;
        temp = temp.substring(0, temp.length()-1);
        return temp;
    }
    
    public String getCsvHeaderSwitchNoComma(Object o, ExportToCsv csvExport) {
        String temp = csvExport.getCsvHeaderSwitch(o);
        if(temp==null)
            return temp;
        temp = temp.substring(0, temp.length()-1);
        return temp;
    }
}
