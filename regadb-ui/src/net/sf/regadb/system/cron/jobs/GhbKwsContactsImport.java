package net.sf.regadb.system.cron.jobs;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import jxl.read.biff.BiffException;
import net.sf.regadb.db.login.DisabledUserException;
import net.sf.regadb.db.login.WrongPasswordException;
import net.sf.regadb.db.login.WrongUidException;
import net.sf.regadb.io.db.ghb.ImportKwsContacts;
import net.sf.regadb.util.date.DateUtils;

import org.quartz.JobExecutionException;

public class GhbKwsContactsImport extends ParameterizedJob{

	@Override
	public void execute() throws JobExecutionException {
		System.err.println(DateUtils.format(new Date())+" started KWS contact import cron.");
		try {
			File newKwsContactDir = new File(getParam("newKwsContactDir"));
			File oldKwsContactDir = new File(getParam("oldKwsContactDir"));
			
			for(File f : newKwsContactDir.listFiles()){
				if(f.isFile() && !f.getName().toLowerCase().endsWith(".log")){

					ImportKwsContacts ikc = new ImportKwsContacts(getParam("username"),
							getParam("password"),
							getParam("dataset"));
					ikc.run(f);
					ikc.close();
					
					File f2 = new File(oldKwsContactDir.getAbsolutePath() + File.separatorChar + f.getName());
					if (!f.renameTo(f2))
						System.err.println(DateUtils.format(new Date())+" KWS contact import cron failed to rename the input file.");
				}
			}
		} catch (BiffException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (WrongUidException e) {
			e.printStackTrace();
		} catch (WrongPasswordException e) {
			e.printStackTrace();
		} catch (DisabledUserException e) {
			e.printStackTrace();
		}
		System.err.println(DateUtils.format(new Date())+" ended KWS contact import cron.");
	}
}
