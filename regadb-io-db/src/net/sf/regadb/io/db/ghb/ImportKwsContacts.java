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
		
		ImportKwsContacts pkc = new ImportKwsContacts();
		pkc.run(user.getValue(), pass.getValue(), dataset.getValue(), new File(file.getValue()));
	}
	
	public void run(String user, String pass, String datasetDescription, File contactFile) throws BiffException, IOException{
        Login login;
        DateFormat df = new SimpleDateFormat("M/d/yy H:m");
		try {
			login = Login.authenticate(user, pass);
			ObjectStore os = new DbObjectStore(login);
			
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
				if(p != null){
					Date d = df.parse(datum);
					TestResult tr = new TestResult(ctype.equals("consultatie") ? consultation : hospitalisation);
					tr.setTestDate(d);
					tr.setValue(d.getTime()+"");
					
					if(!duplicateTestResult(p, tr))
						p.addTestResult(tr);
				}
				else{
					System.err.println("Invalid patient id: "+ eadnr);
				}
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
