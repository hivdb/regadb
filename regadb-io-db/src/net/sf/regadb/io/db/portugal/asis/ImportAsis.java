package net.sf.regadb.io.db.portugal.asis;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import net.sf.regadb.db.Patient;
import net.sf.regadb.db.PatientAttributeValue;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.login.DisabledUserException;
import net.sf.regadb.db.login.WrongPasswordException;
import net.sf.regadb.db.login.WrongUidException;
import net.sf.regadb.db.session.Login;
import net.sf.regadb.io.util.StandardObjects;
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
		
		Map<Patient, TreeSet<TestResult>> patientAsisTestResults = new HashMap<Patient, TreeSet<TestResult>>();
		Map<String, Patient> patients = getPatients(t);
		Map<String, Patient> asisPatients = getAsisPatientId(t);
		
		Test viralLoadTest = t.getTest(
				StandardObjects.getGenericHiv1ViralLoadTest().getDescription(),
				StandardObjects.getHiv1ViralLoadTestType().getDescription(),
				StandardObjects.getHiv1Genome().getOrganismName());

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
			if (p == null) {
				p = asisPatients.get(patientId);
			}
			
			if (p != null) { 
				//check if the pt has test results (only viral load!) which have sample id == null
				for (TestResult tr : getViralLoads(p)) {
					if (tr.getSampleId() == null || tr.getSampleId().trim().equals("")) {
						sampleIdNull.add("test result with sample id null," + p.getPatientId() + "," + tr.getTestDate());
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
				
				if(testValue.equals("500000"))
					testValue = '>'+ testValue;
				else if(testValue.equals("50") || testValue.equals("40"))
					testValue = '<'+ testValue;
				else if(testValue.equals("999999"))
					testValue = ">10000000";
				else{
					long l = 0;
					try{
						l = Long.parseLong(testValue);
					}catch(Exception e){
						e.printStackTrace();
					}
					
					if(l < 40 || l > 500000)
						System.err.println("suspicious value: "+ patientId +","+ sampleId +","+ testValue);
					
					testValue = "="+ testValue;
				}
				
				TestResult asisTestResult = new TestResult(viralLoadTest);
				asisTestResult.setSampleId(sampleId);
				asisTestResult.setTestDate(sampleDate);
				asisTestResult.setValue(testValue);
				
				TreeSet<TestResult> asisTestResults = patientAsisTestResults.get(patientId);
				if(asisTestResults == null){
					asisTestResults = new TreeSet<TestResult>();
					patientAsisTestResults.put(p, asisTestResults);
				}
				asisTestResults.add(asisTestResult);
			}
		}
		
		for (String s : sampleIdNull) {
			System.err.println(s);
		}
		
		System.err.println(patientAsisTestResults.size());
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
	
	public static Map<String, Patient> getAsisPatientId(Transaction t) {
		Map<String, Patient> patients = new HashMap<String, Patient>();
		
		for(Patient p : t.getPatients()) {
			for (PatientAttributeValue pav : p.getPatientAttributeValues()) {
				if (pav.getAttribute().getName().equals("ASIS ID")) {
					patients.put(pav.getValue(), p);
				}
			}
		}
		
		return patients;
	}
}
