package net.sf.regadb.system.cron.jobs;

import java.io.File;
import java.io.IOException;

import jxl.read.biff.BiffException;
import net.sf.regadb.db.login.DisabledUserException;
import net.sf.regadb.db.login.WrongPasswordException;
import net.sf.regadb.db.login.WrongUidException;
import net.sf.regadb.io.db.ghb.ImportKwsContacts;

import org.quartz.JobExecutionException;

public class GhbKwsContactsImport extends ParameterizedJob{

	@Override
	public void execute() throws JobExecutionException {
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
					f.renameTo(f2);
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
	}
}
