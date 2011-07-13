package net.sf.regadb.system.cron.jobs;

import java.io.File;
import java.util.Date;

import net.sf.regadb.io.db.ghb.lis.AutoImport;
import net.sf.regadb.util.date.DateUtils;

import org.quartz.JobExecutionException;

public class GhbLisImport extends ParameterizedJob {

/*
    <job name="ghb-lis" expression="0 0 2 ? * 3" class="net.sf.regadb.system.cron.jobs.GhbLisImport">
		<param name="dataset" value="" />
		<param name="username" value="" />
		<param name="password" value="" />
		<param name="mapping-file" value="" />
		<param name="newLisDir" value="" />
    	<param name="oldLisDir" value="" />
    </job>
*/
	
	public void execute() throws JobExecutionException {
		System.err.println(DateUtils.format(new Date())+" started LIS import.");
		try {
			File newLisExportDir = new File(getParam("newLisDir"));
			File oldLisExportDir = new File(getParam("oldLisDir"));
			
			AutoImport ai = new AutoImport(
					getParam("username"),
					getParam("password"),
					new File(getParam("mapping-file")),
					getParam("dataset"));
			
			for(File f : newLisExportDir.listFiles()){
				if(f.isFile()){
					ai.run(f);
					System.out.println(DateUtils.format(new Date())+" finished LIS import: "+ f.getAbsolutePath());
					
					File f2 = new File(oldLisExportDir.getAbsolutePath() + File.separatorChar + f.getName());
					f.renameTo(f2);
				}
			}
		} catch (Exception e){
			System.err.println("critical error during LIS import: "+ e.getLocalizedMessage());
			e.printStackTrace();
		}
	}
}
