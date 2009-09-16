package net.sf.regadb.system.cron.jobs;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public abstract class ParameterizedJob implements Job{
	private JobExecutionContext context;

	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		context = arg0;
		execute();
	}
	
	public abstract void execute() throws JobExecutionException; 

	public JobExecutionContext getJobExecutionContext(){
		return context;
	}
	
	public String getParam(String name){
		try{
			return context.getJobDetail().getJobDataMap().getString(name);
		}
		catch(Exception e){
			return null;
		}
	}
}
