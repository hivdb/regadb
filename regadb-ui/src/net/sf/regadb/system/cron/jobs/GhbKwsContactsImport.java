package net.sf.regadb.system.cron.jobs;

import java.io.File;
import java.io.IOException;

import jxl.read.biff.BiffException;

import net.sf.regadb.io.db.ghb.ImportKwsContacts;

import org.quartz.JobExecutionException;

public class GhbKwsContactsImport extends ParameterizedJob{

	@Override
	public void execute() throws JobExecutionException {
		ImportKwsContacts ikc = new ImportKwsContacts();
		try {
			ikc.run(getParam("username"),
					getParam("password"),
					getParam("dataset"),
					new File(getParam("contact-file")));
		} catch (BiffException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
