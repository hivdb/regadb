package net.sf.regadb.system.cron.jobs;

import java.io.File;
import java.util.Arrays;
import java.util.Date;

import net.sf.regadb.db.session.Login;
import net.sf.regadb.io.db.ghb.ImportKwsPatients;
import net.sf.regadb.util.date.DateUtils;

import org.quartz.JobExecutionException;

/*
<job name="ghb-kws-patients" expression="0 0 1 ? * 3" class="net.sf.regadb.system.cron.jobs.KwsPatientsImport">
	<param name="datasets" value="" />
	<param name="username" value="" />
	<param name="password" value="" />
	<param name="newKwsDir" value="" />
	<param name="oldKwsDir" value="" />
</job>
*/

public class GhbKwsPatientsImport extends ParameterizedJob {
	
	public void execute() throws JobExecutionException {
		System.err.println(DateUtils.format(new Date())+" started KWS patients import cron.");
		try {
			File newKwsExportDir = new File(getParam("newKwsDir"));
			File oldKwsExportDir = new File(getParam("oldKwsDir"));
			
			Login login = Login.authenticate(getParam("username"),getParam("password"));

			ImportKwsPatients ikp = new ImportKwsPatients(
					login,
					Arrays.asList(getParam("datasets").split(",")));
			
			for(File f : newKwsExportDir.listFiles()){
				if(f.isFile() && !f.getName().toLowerCase().endsWith(".log")){
					ikp.run(f);
					
					File f2 = new File(oldKwsExportDir.getAbsolutePath() + File.separatorChar + f.getName());
					if (!f.renameTo(f2))
						System.err.println(DateUtils.format(new Date())+" KWS patients import cron failed to rename the input file.");
				}
			}
			
			login.closeSession();
		} catch (Exception e){
			System.err.println("critical error during KWS patients import cron: "+ e.getLocalizedMessage());
			e.printStackTrace();
		}
		System.err.println(DateUtils.format(new Date())+" ended KWS patients import cron.");
	}
}
