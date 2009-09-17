package net.sf.regadb.system.cron.jobs;

import java.util.Date;

import net.sf.regadb.util.date.DateUtils;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class TestJob implements Job{
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		System.err.println("cron: "
				+ DateUtils.format(new Date())+" "
				+ arg0.getJobDetail().getName());
		print(arg0.getJobDetail().getJobDataMap());
	}
	
	private void print(JobDataMap map){
		for(String key : map.getKeys()){
			System.err.println("'"+ key +"' -> '"+ map.get(key) +"'");
		}
	}
}
