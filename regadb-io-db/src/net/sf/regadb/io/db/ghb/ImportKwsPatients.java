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
import net.sf.regadb.db.Attribute;
import net.sf.regadb.db.Dataset;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.login.DisabledUserException;
import net.sf.regadb.db.login.WrongPasswordException;
import net.sf.regadb.db.login.WrongUidException;
import net.sf.regadb.db.session.Login;
import net.sf.regadb.io.db.util.Utils;
import net.sf.regadb.io.util.StandardObjects;
import net.sf.regadb.util.args.Arguments;
import net.sf.regadb.util.args.PositionalArgument;
import net.sf.regadb.util.args.ValueArgument;
import net.sf.regadb.util.settings.RegaDBSettings;

public class ImportKwsPatients {
	
	private Login login;
	private DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
	private List<Dataset> datasets = new ArrayList<Dataset>();
	
	public ImportKwsPatients(Login login, Collection<String> datasetNames) throws WrongUidException, WrongPasswordException, DisabledUserException{
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
			int cEmd = table.findColumn("EMD");
			int cBirthDate = table.findColumn("DATE_OF_BIRTH");
			int cDeathDate = table.findColumn("DATE_OF_DEATH");
			int cDiagnoseDate = table.findColumn("DATE_OF_DIAGNOSIS");
			int cPatcode = table.findColumn("PATCODE");
			
			Transaction t = login.createTransaction();
	
			Attribute aEmd = t.getAttribute("EMD Number",
					StandardObjects.getClinicalAttributeGroup().getGroupName());
			Attribute aPatcode = t.getAttribute("PatCode",
					StandardObjects.getClinicalAttributeGroup().getGroupName());
			Attribute aDiagnoseDate = t.getAttribute("HIV Diagnosis date",
					StandardObjects.getClinicalAttributeGroup().getGroupName());
			Attribute aBirthDate = t.getAttribute(
					StandardObjects.getBirthDateAttribute().getName(),
					StandardObjects.getBirthDateAttribute().getAttributeGroup().getGroupName());
			Attribute aDeathDate = t.getAttribute(
					StandardObjects.getDeathDateAttribute().getName(),
					StandardObjects.getDeathDateAttribute().getAttributeGroup().getGroupName());
			
			
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
				
				if(p == null){
					log.println("new patient: "+ ead);
					p = new Patient();
					p.setPatientId(ead);
					
					for(Dataset ds : datasets){
						p.addDataset(ds);
					}
				
					String emd = table.valueAt(cEmd, i);
					Date birthDate = getDate(table.valueAt(cBirthDate, i));
					Date deathDate = getDate(table.valueAt(cDeathDate, i));
					Date diagnoseDate = getDate(table.valueAt(cDiagnoseDate, i));
					String patcode = table.valueAt(cPatcode, i);
					
					handleAttribute(p, aBirthDate, birthDate);
					handleAttribute(p, aDeathDate, deathDate);
					handleAttribute(p, aEmd, emd);
					handleAttribute(p, aDiagnoseDate, diagnoseDate);
					handleAttribute(p, aPatcode, patcode);
					
					t.save(p);
				}
			}
			
			t.commit();
		}catch(Exception e){
			e.printStackTrace(log);
		}
		
		log.close();
	}
	
	private void handleAttribute(Patient p, Attribute a, Date d){
		if(d == null)
			return;
		handleAttribute(p, a, d.getTime() +"");
	}
	
	private void handleAttribute(Patient p, Attribute a, String v){
		if(v == null)
			return;
		
		v = v.trim();
		if(v.length() == 0)
			return;
		
		Utils.setPatientAttributeValue(p, a, v);
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
		PositionalArgument kwsFile = as.addPositionalArgument("kws-file", true);
		ValueArgument confDir = as.addValueArgument("c", "configuration-dir", false);
		
		if(!as.handle(args))
			return;
		
		if(confDir.isSet())
			RegaDBSettings.createInstance(confDir.getValue());
		else
			RegaDBSettings.createInstance();
		
		Login login = Login.authenticate(user.getValue(), pass.getValue());
		
		ImportKwsPatients ikp = new ImportKwsPatients(login,
				Arrays.asList(datasets.getValue().split(",")));
		ikp.run(new File(kwsFile.getValue()));
		
		login.closeSession();
	}
}
