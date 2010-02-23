package net.sf.regadb.io.db.portugal.asis;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sf.regadb.db.Patient;
import net.sf.regadb.db.PatientAttributeValue;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.login.DisabledUserException;
import net.sf.regadb.db.login.WrongPasswordException;
import net.sf.regadb.db.login.WrongUidException;
import net.sf.regadb.db.session.Login;
import net.sf.regadb.util.settings.RegaDBSettings;

public class ImportAsis {
	private static SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
	
	public static void main(String [] args) throws IOException, ParseException, WrongUidException, WrongPasswordException, DisabledUserException {
		RegaDBSettings.createInstance();

		Transaction t = Login.authenticate(args[0], args[1]).createTransaction();
		
		String patientId;
		String sampleId;
		Date sampleDate;
		String testCode;
		String testValue;

		String line;
		BufferedReader input = new BufferedReader(new FileReader(args[2]));
		
		Map<String, Patient> patients = getPatients(t);
		
		Set<String> patientsNotFound = new HashSet<String>();
		while ((line = input.readLine()) != null) {
			String [] words = line.split("\\|");
			patientId = words[0].trim();
			sampleId = words[1].trim();
			sampleDate = sdf.parse(words[2].trim());
			testCode = words[3].trim();
			testValue = words[4].trim();
			
			if (!CompareAsisWithRegaDB.isHiv1ViralLoadTest(testCode) || 
					sampleDate.after(CompareAsisWithRegaDB.dateUntill))
				continue;
			
			if (patients.get(patientId) == null) { 
				patientsNotFound.add(patientId);
			}
		}
		
		System.err.println("patient not found " + patientsNotFound.size());
	}
	
	public static Map<String, Patient> getPatients(Transaction t) {
		Map<String, Patient> patients = new HashMap<String, Patient>();

		for(Patient p : t.getPatients()) {
			patients.put(CompareAsisWithRegaDB.getPatientId(p), p);
		}
		
		return patients;
	}
}
