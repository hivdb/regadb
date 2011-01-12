package net.sf.regadb.system.cron;

import java.text.ParseException;
import java.util.Map;

import net.sf.regadb.util.settings.CronConfig;
import net.sf.regadb.util.settings.CronConfig.JobElement;
import net.sf.regadb.util.settings.RegaDBSettings;

import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

public class Cron {
	private Scheduler scheduler;
	
	public Cron(){
		SchedulerFactory schedFact = new StdSchedulerFactory();
		try {
			scheduler = schedFact.getScheduler();
			addJobs();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}
	
	public void start(){
		try {
			scheduler.start();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}
	
	public void stop(){
		try {
			scheduler.shutdown();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}
	
	public void addJob(JobElement j) throws ClassNotFoundException, ParseException, SchedulerException{
		JobDetail jobDetail = new JobDetail(j.getName(),null,Class.forName(j.getClassName()));
		CronTrigger ct = new CronTrigger(j.getName() +"-trigger",null,j.getExpression());
		for(Map.Entry<String, String> me : j.getParams().entrySet())
			jobDetail.getJobDataMap().put(me.getKey(), me.getValue());
		
		addJob(ct, jobDetail);
	}
	
	public void addJob(Trigger trigger, JobDetail jobDetail) throws SchedulerException{
		scheduler.scheduleJob(jobDetail, trigger);
	}
	
	private void addJobs(){
		CronConfig cc = RegaDBSettings.getInstance().getCronConfig();
		if(cc != null){
			for(JobElement j : cc.getJobs()){
				try {
					addJob(j);
				} catch (ClassNotFoundException e) {
					System.err.println("undefined class in cron job: "+ j.getClassName());
				} catch (ParseException e) {
					e.printStackTrace();
				} catch (SchedulerException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
