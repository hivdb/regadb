package net.sf.regadb.io.db.mateibals;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Test;
import net.sf.regadb.io.db.util.Utils;
import net.sf.regadb.io.util.StandardObjects;
import net.sf.regadb.util.xls.ExcelTable;

public class ImportClinicalDb {
	private SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
	
	private ExcelTable table = new ExcelTable("dd.MM.yyyy");
	private Set<Test> viralLoads = new HashSet<Test>();
	
	public static void main(String [] args) throws IOException, ParseException {
		ImportClinicalDb clinical = new ImportClinicalDb();
		clinical.getPatients(new File("/home/pieter/projects/mybiodata/mateibals/mail_mona/cd4_vl.xls"));
	}
	
	public ImportClinicalDb() {
		viralLoads.add(new Test(StandardObjects.getHiv1ViralLoadTestType(), "Amplicor Monitor 1.5"));
		viralLoads.add(new Test(StandardObjects.getHiv1ViralLoadTestType(), "Cobas TaqMan"));
		viralLoads.add(new Test(StandardObjects.getHiv1ViralLoadTestType(), "M2000"));
		viralLoads.add(new Test(StandardObjects.getHiv1ViralLoadTestType(), "RT-PCR LCx"));
	}
	
	public Map<String, Patient> getPatients(File xlsFile) throws IOException, ParseException {
		Map<String, Patient> patients = new HashMap<String, Patient>();
		
		table.loadFile(xlsFile);
		
		Set<String> patientRep = new HashSet<String>();
		
		for (int r = 1; r < table.rowCount(); r++) {
			String cnp = getValue(r, "CNP");
			
			Patient patient = patients.get(cnp);
			if (patient == null) {
				patient = new Patient();
	            patient.setPatientId(cnp);
	            patients.put(cnp, patient);
	        
	            Date birthDate = getBirthDate(cnp);
	            Utils.setBirthDate(patient, birthDate);
	            Utils.setLastName(patient, getValue(r, "Nume"));
	            Utils.setFirstName(patient, getValue(r, "Prenume"));
				int sex = Integer.parseInt(cnp.charAt(0) + "");
				if (sex == 1 || sex == 5 || sex == 7) {
					Utils.setPatientAttributeValue(patient, StandardObjects.getGenderAttribute(), "male");
				} else if (sex == 2 || sex == 6 || sex == 8) {
					Utils.setPatientAttributeValue(patient, StandardObjects.getGenderAttribute(), "female");
				} else {
					System.err.println("Wrong gender: " + sex + " patient=" + cnp);
				}
				
				String rep = df.format(birthDate)+ " " + getValue(r, "Nume") + " " + getValue(r, "Prenume");
				if(!patientRep.add(rep)) {
					System.err.println("Contains patient already: " + cnp + " " + rep);
				}
			}
			
			String analysis = getValue(r, "DenumireAnaliza");
			String date = getValue(r, "DataSet");
			if (df.parse(date) == null) {
				System.err.println("Test date null at row " + r);
			}
			String value = getValue(r, "Rezultat");
			
			if (analysis.equals("CD3/CD4/CD8")) {
				try {
					Integer.parseInt(value);
				} catch(Exception e) {
					System.err.println(value);
				}
			} else if (analysis.toLowerCase().startsWith("hiv")) {
				Test t = getVLTest(analysis);
				t.getDescription();
				
				value = value.toLowerCase().trim();
				
				value = value.replace("copii/ml", "");
				value = value.replace("copi/ml", "");
				value = value.replace("c/ml", "");
				value = value.replace("ui/ml", "");
				value = value.replace("/ml", "");
				value = value.replace("cml", "");
				
				if (value.equals("nedetectabil")) {
					value = "-1";
				} else {
					value = value.replace("nedetectabil", "");
				}
				
				if (value.contains("(") && value.contains(")")) {
					value = value.substring(value.indexOf('(') + 1, value.indexOf(')'));
				}
				
				MateibalsUtils.parseViralLoad(r, value.trim());
			} else {
				System.err.println("Ignoring test: " + analysis);
			}
		}
		
		return null;
	}
	
	private String getValue(int r, String string) {
		return MateibalsUtils.getValue(table, r, string);
	}

	public Test getVLTest(String analysis) {
		for (Test t : viralLoads) {
			if (analysis.contains(t.getDescription())) {
				return t;
			}
		}
		
		return null;
	}
	
	public Date getBirthDate(String cnp) {
		String birthDate = cnp.substring(1, 1 + 6);
		
		int year = Integer.parseInt(birthDate.charAt(0) + "" + birthDate.charAt(1));
		int month = Integer.parseInt(birthDate.charAt(2) + "" + birthDate.charAt(3));
		int day = Integer.parseInt(birthDate.charAt(4) + "" + birthDate.charAt(5));
		if (year <= 10) {
			year = 2000 + year;
		} else {
			year = 1900 + year;
		}
		
		return MateibalsUtils.createDate(df, day + "." + month + "." + year);
	}
}
