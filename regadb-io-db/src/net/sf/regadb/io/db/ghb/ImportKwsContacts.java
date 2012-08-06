package net.sf.regadb.io.db.ghb;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import jxl.read.biff.BiffException;
import net.sf.regadb.csv.Table;
import net.sf.regadb.db.Dataset;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.login.DisabledUserException;
import net.sf.regadb.db.login.WrongPasswordException;
import net.sf.regadb.db.login.WrongUidException;
import net.sf.regadb.db.session.Login;
import net.sf.regadb.io.db.util.mapping.DbObjectStore;
import net.sf.regadb.io.db.util.mapping.ObjectStore;
import net.sf.regadb.util.args.Arguments;
import net.sf.regadb.util.args.PositionalArgument;
import net.sf.regadb.util.args.ValueArgument;
import net.sf.regadb.util.date.DateUtils;
import net.sf.regadb.util.settings.RegaDBSettings;

public class ImportKwsContacts {

	public static void main(String args[]) throws BiffException, IOException{
		Arguments as = new Arguments();
    	ValueArgument conf			= as.addValueArgument("conf-dir", "configuration directory", false);
    	ValueArgument dateFormat	= as.addValueArgument("date-format", "java date format", false);
    	dateFormat.setValue("M-d-yyyy H:m:s");
    	
    	PositionalArgument user		= as.addPositionalArgument("regadb user", true);
    	PositionalArgument pass		= as.addPositionalArgument("regadb password", true);
    	PositionalArgument dataset	= as.addPositionalArgument("regadb dataset", true);
		PositionalArgument file = as.addPositionalArgument("contacten.csv (;)", true);
		
		if(!as.handle(args))
			return;
		
		if(conf.isSet())
			RegaDBSettings.createInstance(conf.getValue());
		else
			RegaDBSettings.createInstance();
		
		try {
			ImportKwsContacts pkc = new ImportKwsContacts(user.getValue(), pass.getValue(), dataset.getValue());
			pkc.run(new File(file.getValue()),dateFormat.getValue());
			pkc.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private ObjectStore os;
	private String datasetDescription;
	
	public ImportKwsContacts(String user, String pass, String datasetDescription) throws WrongUidException, WrongPasswordException, DisabledUserException{
		Login login = Login.authenticate(user, pass);
		os = new DbObjectStore(login);
		
		this.datasetDescription = datasetDescription;
	}
	
	public ImportKwsContacts(ObjectStore os, String datasetDescription){
		this.os = os;
		this.datasetDescription = datasetDescription;
	}
	
	public void run(File contactFile) throws BiffException, IOException{
		run(contactFile, "M-d-yyyy H:m:s");
	}
	
	public void run(File contactFile, String dateFormat) throws BiffException, IOException{
		File logFile = new File(
				RegaDBSettings.getInstance().getInstituteConfig().getLogDir().getAbsolutePath()
				+ File.separatorChar + contactFile.getName() +".log");
		PrintStream log = new PrintStream(logFile);
		
        DateFormat df = new SimpleDateFormat(dateFormat);
		try {
			Dataset dataset = os.getDataset(datasetDescription);
			
			Test consultation = os.getTest("Consultation", "Contact", null);
			Test hospitalisation = os.getTest("Hospitalisation", "Contact", null);
			if(consultation == null || hospitalisation == null)
				throw new Exception("'Test' objects don't exist, create them first.");
			
			Table table = Table.readTable(contactFile.getAbsolutePath(),';');
			int cEadnr = table.findColumn("eadnr");
			int cDatum = table.findColumn("datum");
			int cCtype = table.findColumn("ctype");
			
			long maxYearDifference = 50l * (365l * 24l * 60l * 60l * 1000l);
			
			for(int i = 1; i < table.numRows(); ++i){
				String eadnr = table.valueAt(cEadnr, i);
				String datum = table.valueAt(cDatum, i);
				String ctype = table.valueAt(cCtype, i);
				
				int pos = eadnr.indexOf(',');
				if(pos > -1)
					eadnr = eadnr.substring(0, pos);
				
				Patient p = os.getPatient(dataset, eadnr);
				if(p == null){
					log.println("Patient does not exist: '"+ eadnr +"'");
				}else{
					Date d = df.parse(datum);
					Date now = new Date();
					if(now.getTime() - d.getTime() > maxYearDifference)
						log.println("Wrong date: "+ datum +" parsed as "+ DateUtils.format(d));
					else{
						TestResult tr = new TestResult(ctype.equals("consultatie") ? consultation : hospitalisation);
						tr.setTestDate(d);
						tr.setValue(d.getTime()+"");
						
						if(!duplicateTestResult(p, tr))
							p.addTestResult(tr);
					}
				}
			}
			os.commit();
			
		} catch (Exception e) {
			e.printStackTrace(log);
		}
		
		log.close();
	}
	
	private boolean duplicateTestResult(Patient p, TestResult result) {
        for(TestResult tr : p.getTestResults()) {
            if(tr.getTest().getDescription().equals(result.getTest().getDescription()) &&
                    DateUtils.equals(tr.getTestDate(),result.getTestDate())) {
            	return true;
            }
        }
        return false;
    }
	
	public void close(){
		os.close();
	}
}
