package net.sf.regadb.io.db.ghb;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import net.sf.regadb.csv.Table;
import net.sf.regadb.db.Attribute;
import net.sf.regadb.db.Dataset;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.PatientAttributeValue;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.login.DisabledUserException;
import net.sf.regadb.db.login.WrongPasswordException;
import net.sf.regadb.db.login.WrongUidException;
import net.sf.regadb.db.session.Login;
import net.sf.regadb.util.args.Arguments;
import net.sf.regadb.util.args.PositionalArgument;
import net.sf.regadb.util.args.ValueArgument;
import net.sf.regadb.util.settings.RegaDBSettings;

public class ImportPatCodes {
	private Login login;

	public static void main(String args[]) throws WrongUidException, WrongPasswordException, DisabledUserException, IOException{
		Arguments as = new Arguments();
		PositionalArgument user = as.addPositionalArgument("user", true);
		PositionalArgument pass = as.addPositionalArgument("pass", true);
		PositionalArgument file = as.addPositionalArgument("wiv-codes-file", true);
		ValueArgument conf = as.addValueArgument("conf-dir", "configuration directory", false);
		
		if(!as.handle(args))
			return;
		
		if(conf.isSet())
			RegaDBSettings.createInstance(conf.getValue());
		else
			RegaDBSettings.createInstance();
		
		ImportPatCodes ipc = new ImportPatCodes(user.getValue(), pass.getValue());
		ipc.run(new File(file.getValue()));
		ipc.close();
	}
	
	public ImportPatCodes(String user, String pass) throws WrongUidException, WrongPasswordException, DisabledUserException{
		login = Login.authenticate(user, pass);
	}
	
	public void run(File file) throws IOException{
		BufferedReader in = new BufferedReader(new FileReader(file));
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		
		String line = in.readLine();
		ArrayList<String> fields = Table.splitHandleQuotes(line, ',', '"', '\\');
		int iPat = fields.indexOf("WIV-code");
		int iEad = fields.indexOf("eadnr");
		int iEmd = fields.indexOf("emdnr");
		int iBirth = fields.indexOf("gebDatum");

		while((line = in.readLine()) != null){
			fields = Table.splitHandleQuotes(line, ',', '"', '\\');
			
			String sEad = fields.get(iEad).trim();
			
			Transaction t = login.createTransaction();
			Dataset ds = t.getDataset("KUL");
			Patient p = t.getPatient(ds,sEad);
			
			if(p != null){
				String sEmd = fields.get(iEmd).trim();
				String sPat = fields.get(iPat).trim();
				String sBirth = fields.get(iBirth).trim();

				Attribute aBirth = t.getAttribute("Birth date", "Personal");
				Attribute aEmd = t.getAttribute("EMD Number", "Clinical");
				Attribute aPat = t.getAttribute("PatCode","Clinical");
			
				try {
					Date dBirth = df.parse(sBirth);
					setAttributeValue(p, aBirth, dBirth.getTime() +"");
				} catch (ParseException e) {
					System.err.println("invalid date: '"+ sBirth +"'");
				}
				
				setAttributeValue(p, aEmd, sEmd);
				setAttributeValue(p, aPat, sPat);
			}
			else{
				System.err.println("invalid eadnr: '"+ sEad +"'");
			}
			
			t.commit();
		}
		
		in.close();
	}
	
	private void setAttributeValue(Patient p, Attribute a, String value){
		for(PatientAttributeValue pav : p.getPatientAttributeValues()){
			if(pav.getAttribute().getAttributeIi() == a.getAttributeIi()){
				if(!pav.getValue().equals(value)){
					System.err.println("patient "+ p.getPatientId() +" "+ a.getName() +" '"+ pav.getValue() +"' -> '"+ value +"'");
					pav.setValue(value);
				}
				return;
			}
		}
		p.createPatientAttributeValue(a).setValue(value);
	}
	
	public void close(){
		login.closeSession();
	}
}
