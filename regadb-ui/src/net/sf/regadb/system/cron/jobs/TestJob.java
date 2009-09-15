package net.sf.regadb.system.cron.jobs;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class TestJob implements Job{
	private DateFormat df = new SimpleDateFormat("yyyy-MM-dd H:m");
	
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		System.err.println("cron: "+ df.format(new Date()) +" "+ this.getClass().getCanonicalName());
	}
}
