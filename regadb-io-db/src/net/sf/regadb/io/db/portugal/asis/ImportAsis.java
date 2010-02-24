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
import java.util.Set;
import java.util.TreeSet;

import net.sf.regadb.db.Patient;
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
	public static class TestResultComparator implements Comparator<TestResult>{
		@Override
		public int compare(TestResult o1, TestResult o2) {
			return o1.getTestDate().compareTo(o2.getTestDate());
		}
	}
	
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
		
		Map<String, TreeSet<TestResult>> patientAsisTestResults = new HashMap<String, TreeSet<TestResult>>();
		Map<String, Patient> patients = getPatients(t);
		
		Test viralLoadTest = t.getTest(
				StandardObjects.getGenericHiv1ViralLoadTest().getDescription(),
				StandardObjects.getHiv1ViralLoadTestType().getDescription(),
				StandardObjects.getHiv1Genome().getOrganismName());
		
		TestResultComparator testResultComparator = new TestResultComparator();

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
					if (tr.getSampleId() == null) {
						sampleIdNull.add(p.getPatientId() + "," + tr.getTestDate());
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
				
				//check if the patient has viral loads on the same date (MM/yyyy)
				for (TestResult tr : getViralLoads(p)) {
					if (getMonth(tr.getTestDate()) == getMonth(sampleDate) && 
							getYear(tr.getTestDate()) == getYear(sampleDate)) {
						if (Math.abs(tr.getTestDate().getTime() - sampleDate.getTime()) < 604800000L) {
							System.err.println("~same date:"+
									patientId+","+tr.getSampleId()+","+sdf.format(tr.getTestDate())
									+","+sampleId+","+sdf.format(sampleDate));
						}
					}
				}
				
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
					}
					
					if(l < 40 || l > 999999)
						System.err.println("suspicious value: "+ patientId +","+ sampleId +","+ testValue);
					
					testValue = "="+ testValue;
				}
				
				TestResult asisTestResult = new TestResult(viralLoadTest);
				asisTestResult.setSampleId(sampleId);
				asisTestResult.setTestDate(sampleDate);
				asisTestResult.setValue(testValue);
				
				TreeSet<TestResult> asisTestResults = patientAsisTestResults.get(patientId);
				if(asisTestResults == null){
					asisTestResults = new TreeSet<TestResult>(testResultComparator);
					patientAsisTestResults.put(patientId, asisTestResults);
				}
				asisTestResults.add(asisTestResult);
			}
		}
		
		for(Map.Entry<String, TreeSet<TestResult>> me : patientAsisTestResults.entrySet()){
			Set<String> sampleIds = new HashSet<String>();
			TestResult lastTr = null;
			for(TestResult tr : me.getValue()){
				if(!sampleIds.add(tr.getSampleId()))
					System.err.println("duplicate sample id: "+ tr.getSampleId() +" patient: "+ me.getKey());
				
				if(lastTr != null){
					if(Math.abs(tr.getTestDate().getTime() - lastTr.getTestDate().getTime()) < 864000000L)
						System.err.println("dates less than 10 days appart: "+ lastTr.getTestDate() +" "+ tr.getTestDate() +" patient: "+ me.getKey());
				}
				lastTr = tr;
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
