package net.sf.regadb.io.db.ghb;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
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
    	PositionalArgument user		= as.addPositionalArgument("regadb user", true);
    	PositionalArgument pass		= as.addPositionalArgument("regadb password", true);
    	PositionalArgument dataset	= as.addPositionalArgument("regadb dataset", true);
		PositionalArgument file = as.addPositionalArgument("contacten.xls", true);
		if(!as.handle(args))
			return;
		
		if(conf.isSet())
			RegaDBSettings.createInstance(conf.getValue());
		else
			RegaDBSettings.createInstance();
		
		try {
			ImportKwsContacts pkc = new ImportKwsContacts(user.getValue(), pass.getValue(), dataset.getValue());
			pkc.run(new File(file.getValue()));
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
        DateFormat df = new SimpleDateFormat("M/d/yy H:m");
		try {
			Dataset dataset = os.getDataset(datasetDescription);
			
			Test consultation = os.getTest("Consultation", "Contact", null);
			Test hospitalisation = os.getTest("Hospitalisation", "Contact", null);
			if(consultation == null || hospitalisation == null)
				throw new Exception("'Test' objects don't exist, create them first.");
			
	    	Workbook wb;
			wb = Workbook.getWorkbook(contactFile);
			Sheet sh = wb.getSheet(0);
			
			for(int i = 1; i < sh.getRows(); ++i){
				String eadnr = sh.getCell(0, i).getContents();
				String datum = sh.getCell(1, i).getContents();
				String ctype = sh.getCell(2, i).getContents();
				
				Patient p = os.getPatient(dataset, eadnr);
				if(p == null)
					p = os.createPatient(dataset, eadnr);
				
				Date d = df.parse(datum);
				TestResult tr = new TestResult(ctype.equals("consultatie") ? consultation : hospitalisation);
				tr.setTestDate(d);
				tr.setValue(d.getTime()+"");
				
				if(!duplicateTestResult(p, tr))
					p.addTestResult(tr);
			}
			os.commit();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
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
}
