package net.sf.regadb.io.db.ghb;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import net.sf.regadb.csv.Table;
import net.sf.regadb.db.Dataset;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestNominalValue;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.ValueTypes;
import net.sf.regadb.db.login.DisabledUserException;
import net.sf.regadb.db.login.WrongPasswordException;
import net.sf.regadb.db.login.WrongUidException;
import net.sf.regadb.db.session.Login;
import net.sf.regadb.io.db.util.Utils;
import net.sf.regadb.util.args.Arguments;
import net.sf.regadb.util.args.PositionalArgument;
import net.sf.regadb.util.args.ValueArgument;
import net.sf.regadb.util.settings.RegaDBSettings;

public class ImportAdherence {

	private Login login;
	private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private List<Dataset> datasets = new ArrayList<Dataset>();
	
	public ImportAdherence(Login login, Collection<String> datasetNames) throws WrongUidException, WrongPasswordException, DisabledUserException{
		this.login = login;
		
		Transaction t = login.createTransaction();
		for(String name : datasetNames){
			Dataset ds = t.getDataset(name);
			datasets.add(ds);
		}
		t.commit();
	}

	public void run(File input) throws FileNotFoundException, UnsupportedEncodingException{
		File logFile = new File(
				RegaDBSettings.getInstance().getInstituteConfig().getLogDir().getAbsolutePath()
				+ File.separatorChar + input.getName() +".log");
		PrintStream log = new PrintStream(logFile);
		
		try{
			Table table = Table.readTable(input.getAbsolutePath(), ';');
			
			int cEad = table.findColumn("EAD");
			int cTestDate = table.findColumn("TestDate");
			int cVas = table.findColumn("AdherenceVAS");
			int cMissed = table.findColumn("AdherenceMissed");
			int cDrughol = table.findColumn("AdherenceDrughol");
			
			Transaction t = login.createTransaction();
	
			Test tDrughol = t.getTest("Adherence - SHCS-AQ (Drug Holiday)");
			if(tDrughol == null) throw new Exception("missing test: Adherence - SHCS-AQ (Drug Holiday)");
			
			Test tMissed = t.getTest("Adherence - SHCS-AQ (Missed)");
			if(tMissed == null) throw new Exception("missing test: Adherence - SHCS-AQ (Missed)");
			
			Test tVas = t.getTest("Adherence - VAS");
			if(tVas == null) throw new Exception("Adherence - VAS");
			
			for(int i=1; i<table.numRows(); ++i){
				String ead = table.valueAt(cEad, i);
				
				if(ead == null)
					continue;
				ead = ead.trim();
				if(ead.length() == 0)
					continue;
				
				Patient p = null;
				for(Dataset ds : datasets){
					if((p = t.getPatient(ds, ead)) != null)
						break;
				}
				
				if(p != null){
					Date testDate = getDate(table.valueAt(cTestDate, i));
					if(testDate != null){
						String value = table.valueAt(cDrughol,i);
						TestResult tr;
						if((tr = handleTest(p, tDrughol, testDate, value)) == null)
							log.println("invalid value: '"+ value +"'");
						else if(Utils.getDuplicateTestResult(p, tr) == null)
							p.addTestResult(tr);
						
						value = table.valueAt(cMissed,i);
						if((tr = handleTest(p, tMissed, testDate, value)) == null)
							log.println("invalid value: '"+ value +"'");
						else if(Utils.getDuplicateTestResult(p, tr) == null)
							p.addTestResult(tr);
						
						value = table.valueAt(cVas,i);
						if((tr = handleTest(p, tVas, testDate, value)) == null)
							log.println("invalid value: '"+ value +"'");
						else if(Utils.getDuplicateTestResult(p, tr) == null)
							p.addTestResult(tr);
						
						t.save(p);
					} else {
						log.println("invalid date: '"+ table.valueAt(cTestDate, i) +"'");
					}
				} else {
					log.println("patient does not exist: '"+ ead +"'");
				}
			}
			
			t.commit();
		}catch(Exception e){
			e.printStackTrace(log);
		}
		
		log.close();
	}
	
	private TestResult handleTest(Patient p, Test t, Date d, String v){
		if(v == null)
			return null;
		
		v = v.trim();
		if(v.length() == 0)
			return null;
		
		TestResult tr = null;
		
		if(ValueTypes.getValueType(t.getTestType().getValueType())
				== ValueTypes.NOMINAL_VALUE){
			v = v.toLowerCase();
			for(TestNominalValue tnv : t.getTestType().getTestNominalValues()){
				if(v.equals(tnv.getValue().toLowerCase())){
					tr = new TestResult(t);
					tr.setTestNominalValue(tnv);
					break;
				}
			}
		}else{
			tr = new TestResult(t);
			tr.setValue(v);
		}
		
		if(tr != null){
			tr.setTestDate(d);
		}
		
		return tr;
	}
	
	private Date getDate(String date){
		if(date != null && date.length() > 0){
			try {
				return dateFormat.parse(date);
			} catch (ParseException e){}
		}
		return null;
	}
	
	public static void main(String[] args) throws WrongUidException, WrongPasswordException, DisabledUserException,
													FileNotFoundException, UnsupportedEncodingException{
		Arguments as = new Arguments();
		PositionalArgument user = as.addPositionalArgument("user", true);
		PositionalArgument pass = as.addPositionalArgument("pass", true);
		PositionalArgument datasets = as.addPositionalArgument("datasets", true);
		PositionalArgument kwsFile = as.addPositionalArgument("adherence-file", true);
		ValueArgument confDir = as.addValueArgument("c", "configuration-dir", false);
		
		if(!as.handle(args))
			return;
		
		if(confDir.isSet())
			RegaDBSettings.createInstance(confDir.getValue());
		else
			RegaDBSettings.createInstance();
		
		Login login = Login.authenticate(user.getValue(), pass.getValue());
		
		ImportAdherence ia = new ImportAdherence(login,
				Arrays.asList(datasets.getValue().split(",")));
		ia.run(new File(kwsFile.getValue()));
		
		login.closeSession();
	}
}
