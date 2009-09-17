package net.sf.regadb.system.cron.jobs;

import java.io.File;
import java.util.Date;

import net.sf.regadb.io.db.ghb.lis.AutoImport;
import net.sf.regadb.util.date.DateUtils;

import org.quartz.JobExecutionException;

public class GhbLisImport extends ParameterizedJob {

	public void execute() throws JobExecutionException {
		System.err.println(DateUtils.format(new Date())+" started LIS import.");
		try {
			AutoImport ai = new AutoImport(
					getParam("username"),
					getParam("password"),
					new File(getParam("mapping-file")),
					getParam("dataset"));
			ai.run(new File(getParam("export-file")));
			System.err.println(DateUtils.format(new Date())+" finished LIS import.");
		} catch (Exception e){
			System.err.println("critical error during LIS import: "+ e.getLocalizedMessage());
			e.printStackTrace();
		}
	}
}
