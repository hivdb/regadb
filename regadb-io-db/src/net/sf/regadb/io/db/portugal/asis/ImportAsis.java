package net.sf.regadb.io.db.portugal.asis;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import net.sf.regadb.db.Patient;
import net.sf.regadb.db.TestResult;
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
		
		Map<Patient, TestResult> asisViralLoads = new HashMap<Patient, TestResult>();
		Map<String, Patient> patients = getPatients(t);

		HashSet<String> sampleIdNull = new HashSet<String>();
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
			
			Patient p = patients.get(patientId);
			if (p != null) { 
				//check if the pt has test results (only viral load!) which have sample id == null
				for (TestResult tr : getViralLoads(p)) {
					if (tr.getSampleId() == null || tr.getSampleId().trim().equals("")) {
						System.err.println(p.getPatientId() + "," + tr.getTestDate());
					}
				}
				
				//if sample id already in regadb -> skip
				boolean alreadyIn = false;
				for (TestResult tr : getViralLoads(p)) {
					if (tr.getSampleId() != null && tr.getSampleId().trim().equals(sampleId))
						alreadyIn=true;
				}
				if (alreadyIn)
					continue;
				
				final int dayInMS = 1000 * 60 * 60 * 24;
				//check if the patient has viral loads on the same date (MM/yyyy)
				for (TestResult tr : getViralLoads(p)) {
						if (Math.abs(tr.getTestDate().getTime() - sampleDate.getTime()) < (dayInMS*7)) {
							System.err.println("~same date:"+
									patientId+","+tr.getSampleId()+","+sdf.format(tr.getTestDate())
									+","+sampleId+","+sdf.format(sampleDate));							
						}
				}
			}
		}
		
		System.err.println("sampleIdNull" + sampleIdNull.size());
	}
	
	public static int getMonth(Date d) {
		return Integer.parseInt(new SimpleDateFormat("MM").format(d));
	}
	
	public static int getYear(Date d) {
		return Integer.parseInt(new SimpleDateFormat("yyyy").format(d));
	}
	
	public static List<TestResult> getViralLoads(Patient p) {
		List<TestResult> vl = new ArrayList<TestResult>();
		
		for (TestResult tr : p.getTestResults())
			if (CompareAsisWithRegaDB.isRegaDBViralLoad(tr))
				vl.add(tr);
		
		return vl;
	}
	
	public static Map<String, Patient> getPatients(Transaction t) {
		Map<String, Patient> patients = new HashMap<String, Patient>();

		for(Patient p : t.getPatients()) {
			patients.put(CompareAsisWithRegaDB.getPatientId(p), p);
		}
		
		return patients;
	}
}
