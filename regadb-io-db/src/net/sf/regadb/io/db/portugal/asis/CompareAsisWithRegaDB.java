package net.sf.regadb.io.db.portugal.asis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.regadb.db.Patient;
import net.sf.regadb.db.PatientAttributeValue;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.login.DisabledUserException;
import net.sf.regadb.db.login.WrongPasswordException;
import net.sf.regadb.db.login.WrongUidException;
import net.sf.regadb.db.session.Login;
import net.sf.regadb.util.settings.RegaDBSettings;

import org.apache.commons.io.FileUtils;

public class CompareAsisWithRegaDB {
	private Login login;
	private List<Patient> patients;

	private Set<String> codes = new HashSet<String>();

	List<String> asisPatientsIds = new ArrayList<String>();
	Map<String, String> samplePatient = new HashMap<String, String>();
	Map<String, Date> sampleDate = new HashMap<String, Date>();
	Map<String, String> sampleValue = new HashMap<String, String>();
	
	private File errorReportDir;
	
	public static Date dateUntill = null;
	
	static {
		try {
			dateUntill = new SimpleDateFormat("dd/MM/yyyy").parse("15/08/2008");
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public void run(String userName, String password, File asisExportDir, File errorReportDir)
			throws IOException, WrongUidException, WrongPasswordException,
			DisabledUserException {
		RegaDBSettings.createInstance();
		
		login = Login.authenticate(userName, password);

		Transaction t = login.createTransaction();
		patients = t.getPatients();
		
		this.errorReportDir = errorReportDir;
		
		for (File f : asisExportDir.listFiles()) {
			if (f.isFile())
				processAsisFile(f);
		}

		for (String c : codes) {
			writeToFile("test_codes.txt", c);
		}

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		
		Set<String> idDates = new HashSet<String>();

		System.err.println("patients.size()=" + patients.size());

		//headers
		writeToFile("patientIdProblems.csv", "asisPatientId"+";" + "regadbPatientIdDB" + ";"
				 +"regadb SampleId" + ";" + "regadb testDate");
		writeToFile("sampleIdCannotBeFoundInAsis.csv", " regadb sampleId" + ";" +
				 " regadb testDate" + ";" + "regadb patientId");
		writeToFile("regadbAsisValuesDiffer.csv", "regadb sampleId" + ";" + "regadb value" + ";" + " asis value");
		writeToFile("regadbAsisDatesDiffer.csv", "regadb sampleId" + ";" + "regadb test date" + ";" + "asis sample date");
		
		for (Patient p : patients) {
			String patientIdDB = getPatientId(p);

			for (TestResult tr : p.getTestResults()) {
				if (tr.getTestDate() == null) {
					writeToFile("noTestDate.txt", "test date null: " + tr.getSampleId() + " " + p.getPatientId());
				} else if (!tr.getTestDate().after(dateUntill) && isRegaDBViralLoad(tr)) {
					String patientId = samplePatient.get(tr.getSampleId());
					if (patientId == null) {
						if (tr.getSampleId() == null || tr.getSampleId().trim().equals("")) {
							if (!asisPatientsIds.contains(patientIdDB))
								continue;
						}
						writeToFile("sampleIdCannotBeFoundInAsis.csv", tr.getSampleId() + ";" +
								 tr.getTestDate() + ";" + p.getPatientId());
					} else if (!patientId.equals(patientIdDB)) {
						String asisId = getAsisId(p);
						if (asisId == null) {
							writeToFile("patientIdProblems.csv", patientId+";" + patientIdDB + ";"
									+tr.getSampleId() + ";" + tr.getTestDate());
						}
					} else {
						String [] tr_d = sdf.format(tr.getTestDate()).split("\\/");
						String [] asis_d = sdf.format(this.sampleDate.get(tr.getSampleId())).split("\\/");
						
						//only compare month and year
						if(!(tr_d[1]+"/"+tr_d[2]).equals(asis_d[1]+"/"+asis_d[2])) {
							idDates.add(tr.getSampleId() + ";" + sdf.format(tr.getTestDate()) + ";" + sdf.format(sampleDate.get(tr.getSampleId())));
						}
						
						//TODO check these rules
						
						//this is too strict, the former rule should take care of most important differences						
						//if(!sdf.format(tr.getTestDate()).equals(sdf.format(this.sampleDate.get(tr.getSampleId())))) {
						//	idDates.add(tr.getSampleId() + " " + sdf.format(tr.getTestDate()) + " " + sdf.format(this.sampleDate.get(tr.getSampleId())));
						//}
						
						if(this.sampleValue.get(tr.getSampleId())==null) {
							
						} else if(tr.getValue()==null) {
							writeToFile("regadbAsisValuesDiffer.csv", tr.getSampleId() + ";" + tr.getValue() + ";" + this.sampleValue.get(tr.getSampleId()));
						} else if(!tr.getValue().substring(1).equals(this.sampleValue.get(tr.getSampleId()))) {
							writeToFile("regadbAsisValuesDiffer.csv", tr.getSampleId() + ";" + tr.getValue() + ";" + this.sampleValue.get(tr.getSampleId()));
						}
					}
				}
			}
		}
		
		System.err.println("size=" + idDates.size());
		
		for(String d : idDates) {
			writeToFile("regadbAsisDatesDiffer.csv", d);
		}
	}

	private void writeToFile(String fileName, String message) {
		try {
			String file = errorReportDir.getAbsolutePath() + File.separatorChar + fileName;
			
			List<String> text;
			if (new File(file).exists())
				text = FileUtils.readLines(new File(file));
			else 
				text = new ArrayList<String>();
			
			text.add(message);
			
			Collections.sort(text);
			Collections.reverse(text);
			
			Set<String> dups = new HashSet<String>();
			FileWriter fw = new FileWriter(file);
			for (String t : text) {
				if(dups.add(t))
					fw.write(t + '\n');
			}
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void processAsisFile(File asisFile) throws IOException {
		String patientId;
		String sampleId;
		String sampleDate;
		String testCode;
		String testValue;

		String line;
		String[] words;
		BufferedReader input = new BufferedReader(new FileReader(asisFile));
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

		int sampleCounter = 0;

		while ((line = input.readLine()) != null) {
			words = line.split("\\|");
			patientId = words[0].trim();
			sampleId = words[1].trim();
			sampleDate = words[2].trim();
			testCode = words[3].trim();
			testValue = words[4].trim();

			asisPatientsIds.add(patientId);
			
			samplePatient.put(sampleId, patientId);
			try {
				this.sampleDate.put(sampleId, sdf.parse(sampleDate));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			if(isHiv1ViralLoadTest(testCode))
				this.sampleValue.put(sampleId, testValue);
			
			codes.add(testCode);

			// System.err.println(patientId + " " + sampleId + " " + testCode +
			// " " + testValue);
		}

	}

	public static void main(String[] args) throws IOException,
			WrongUidException, WrongPasswordException, DisabledUserException {
		CompareAsisWithRegaDB importAsis = new CompareAsisWithRegaDB();
		importAsis.run(args[0], args[1], new File(args[2]), new File(args[3]));
	}
	
	public static boolean isHiv1ViralLoadTest(String test) {
		if(test.equals("RTPCRHIV1")  || test.equals("bDNAHIV") || test.equals("0DNAHIV") || test.equals("PCRHIV")) {
			return true;
		} else {
			return false;
		}
	}
	
	public static boolean isRegaDBViralLoad(TestResult tr) {
		return tr.getTest().getDescription().contains("Viral Load");
	}
	
	public static String getPatientId(Patient p) {
		String patientIdDB = p.getPatientId();

		for (PatientAttributeValue pav : p.getPatientAttributeValues()) {
			if (pav.getAttribute().getName().equals("old_id")) {
				patientIdDB = pav.getValue();
			}
		}
		
		return patientIdDB;
	}
	
	public static String getAsisId(Patient p) {
		String patientIdDB = null;

		for (PatientAttributeValue pav : p.getPatientAttributeValues()) {
			if (pav.getAttribute().getName().equals("ASIS ID")) {
				patientIdDB = pav.getValue();
			}
		}
		
		return patientIdDB;
	}
}
