package net.sf.regadb.io.exportCsv;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import net.sf.regadb.db.Attribute;
import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.PatientAttributeValue;
import net.sf.regadb.db.PatientEventValue;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.Therapy;
import net.sf.regadb.db.TherapyCommercial;
import net.sf.regadb.db.TherapyGeneric;
import net.sf.regadb.db.ViralIsolate;

public class FullCsvExport {
	public FullCsvExport() {
	}
	
	public void export(List<Patient> patients, List<Attribute> attributes, File zipFile) throws IOException {
		List<File> files = new ArrayList<File>();
		List<String> fileNames = new ArrayList<String>();
		
		File patientFile = File.createTempFile("patients", "csv");
		files.add(patientFile);
		fileNames.add("patients.csv");
		FileWriter patientFileWriter = new FileWriter(patientFile);
		File eventFile = File.createTempFile("events", "csv");
		files.add(eventFile);
		fileNames.add("events.csv");
		FileWriter eventFileWriter = new FileWriter(eventFile);
		File testFile = File.createTempFile("tests", "csv");
		files.add(testFile);
		fileNames.add("tests.csv");
		FileWriter testFileWriter = new FileWriter(testFile);
		File therapyFile = File.createTempFile("therapies", "csv");
		fileNames.add("therapies.csv");
		files.add(therapyFile);
		FileWriter therapyFileWriter = new FileWriter(therapyFile);
		File viralIsolateFile = File.createTempFile("viral_isolates", "csv");
		files.add(viralIsolateFile);
		fileNames.add("viral_isolates.csv");
		FileWriter viralIsolateFileWriter = new FileWriter(viralIsolateFile);
		
		Collections.sort(attributes, new Comparator<Attribute>(){
			public int compare(Attribute a0, Attribute a1) {
				return a0.getName().compareTo(a1.getName());
			}
		});
		
		int maxNumberSeqs = 0;
		
		for(Patient p : patients) {
			for(ViralIsolate vi : p.getViralIsolates()) {
				maxNumberSeqs = Math.max(maxNumberSeqs, vi.getNtSequences().size());
			}
		}
		
		patientHeader(patientFileWriter, attributes);
		eventHeader(eventFileWriter);
		testHeader(testFileWriter);
		therapyHeader(therapyFileWriter);
		viralIsolateHeader(viralIsolateFileWriter, maxNumberSeqs);
		
		for(Patient p : patients) {
			patientRow(p, patientFileWriter, attributes);
			for(PatientEventValue pev : p.getPatientEventValues()) {
				eventRow(p, pev, eventFileWriter);
			}
			for(TestResult tr : p.getTestResults()) {
				testRow(p, tr, testFileWriter);
			}
			for(Therapy t : p.getTherapies()) {
				therapyRow(p, t, therapyFileWriter);
			}
			for(ViralIsolate vi : p.getViralIsolates()) {
				viralIsolateRow(p, vi, viralIsolateFileWriter, maxNumberSeqs);
			}
		}
		
		patientFileWriter.close();
		eventFileWriter.close();
		testFileWriter.close();
		therapyFileWriter.close();
		viralIsolateFileWriter.close();
		
		// Create a buffer for reading the files
	    byte[] buf = new byte[1024];
	    
	    try {
	        // Create the ZIP file
	        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile));
	    
	        // Compress the files
	        for (int i=0; i<files.size(); i++) {
	            FileInputStream in = new FileInputStream(files.get(i));
	    
	            // Add ZIP entry to output stream.
	            out.putNextEntry(new ZipEntry(fileNames.get(i)));
	    
	            // Transfer bytes from the file to the ZIP file
	            int len;
	            while ((len = in.read(buf)) > 0) {
	                out.write(buf, 0, len);
	            }
	    
	            // Complete the entry
	            out.closeEntry();
	            in.close();
	        }
	        out.close();
	    } catch (IOException ioe) {
	    	ioe.printStackTrace();
	    }
	    
	    for(File f : files) {
	    	f.delete();
	    }
	}
	
	private void therapyRow(Patient p, Therapy t, FileWriter fw) throws IOException {
		StringBuilder row = new StringBuilder();
		
		formatField(row, p.getPatientId());
		formatField(row, t.getStartDate());
		formatField(row, t.getStopDate());
		formatField(row, t.getTherapyMotivation()==null?"":t.getTherapyMotivation().getValue());
		
		String haart = "";
		for(TherapyCommercial tc : t.getTherapyCommercials()) {
			for(DrugGeneric dg : tc.getId().getDrugCommercial().getDrugGenerics()) {
				haart += dg.getGenericId() + " ";
			}
		}
		for(TherapyGeneric tg : t.getTherapyGenerics()) {
			haart += tg.getId().getDrugGeneric().getGenericId() + " ";
		}
		
		formatField(row, haart, false);
		
		fw.append(row.toString());
	}
	
	private void viralIsolateHeader(FileWriter fw, int maxNumberSeqs) throws IOException {
		StringBuilder header = new StringBuilder();
		
		formatField(header, "patient_id");
		formatField(header, "sample_date");
		formatField(header, "sample_id");
		for(int i = 0; i<maxNumberSeqs; i++) {
			formatField(header, "sequence_"+(i+1), i!=maxNumberSeqs-1);
		}
		
		fw.append(header.toString());
	}
	
	private void viralIsolateRow(Patient p, ViralIsolate vi, FileWriter fw, int maxNumberSeqs) throws IOException {
		StringBuilder row = new StringBuilder();
		
		formatField(row, p.getPatientId());
		formatField(row, vi.getSampleDate());
		formatField(row, vi.getSampleId());
		
		List<NtSequence> ntseqs = new ArrayList<NtSequence>(vi.getNtSequences());
		
		for(int i = 0; i<maxNumberSeqs; i++) {
			if(i<ntseqs.size())
				formatField(row, ntseqs.get(i).getNucleotides(), i!=maxNumberSeqs-1);
			else 
				formatField(row, "", i!=maxNumberSeqs-1);
		}
		
		fw.append(row.toString());
	}
	
	private void therapyHeader(FileWriter fw) throws IOException {
		StringBuilder header = new StringBuilder();
		
		formatField(header, "patient_id");
		formatField(header, "start_date");
		formatField(header, "stop_date");
		formatField(header, "stop_reason");
		formatField(header, "HAART (generic format)", false);
		
		fw.append(header.toString());
	}
	
	private void testHeader(FileWriter fw) throws IOException {
		StringBuilder header = new StringBuilder();
		
		formatField(header, "patient_id");
		formatField(header, "test");
		formatField(header, "test_type");
		formatField(header, "date");
		formatField(header, "sample_id");
		formatField(header, "value", false);
		
		fw.append(header.toString());
	}
	
	private void testRow(Patient p, TestResult tr, FileWriter fw) throws IOException {
		StringBuilder row = new StringBuilder();
		
		formatField(row, p.getPatientId());
		formatField(row, tr.getTest().getDescription());
		formatField(row, tr.getTest().getTestType().getDescription());
		formatField(row, tr.getTestDate());
		formatField(row, tr.getSampleId());
		formatField(row, tr.getValue()==null?tr.getTestNominalValue().getValue():tr.getValue(), false);
		
		fw.append(row.toString());
	}

	private void eventHeader(FileWriter fw) throws IOException {
		StringBuilder header = new StringBuilder();
		
		formatField(header, "patient_id");
		formatField(header, "event");
		formatField(header, "start_date");
		formatField(header, "end_date");
		formatField(header, "value", false);
		
		fw.append(header.toString());
	}
	
	private void eventRow(Patient p, PatientEventValue pev, FileWriter fw) throws IOException {
		StringBuilder row = new StringBuilder();
		
		formatField(row, p.getPatientId());
		formatField(row, pev.getEvent().getName());
		formatField(row, pev.getStartDate());
		formatField(row, pev.getEndDate());
		formatField(row, pev.getValue()==null?pev.getEventNominalValue().getValue():pev.getValue(), false);
		
		fw.append(row.toString());
	}

	private void patientHeader(FileWriter fw, List<Attribute> sortedAttributes) throws IOException {
		StringBuilder header = new StringBuilder();
		
		formatField(header, "patient_id");
		formatField(header, "first_name");
		formatField(header, "last_name");
		formatField(header, "birth_date");
		formatField(header, "death_date");
		
		for(int i = 0; i<sortedAttributes.size(); i++) {
			formatField(header, sortedAttributes.get(i).getName(), i!=sortedAttributes.size()-1);
		}
		
		fw.append(header.toString());
	}
	
	private void patientRow(Patient p, FileWriter fw, List<Attribute> sortedAttributes) throws IOException {
		StringBuilder row = new StringBuilder();
		
		formatField(row, p.getPatientId());
		formatField(row, p.getFirstName());
		formatField(row, p.getLastName());
		formatField(row, p.getBirthDate());
		formatField(row, p.getDeathDate());
		
		for(int i = 0; i<sortedAttributes.size(); i++) {
			boolean found = false;
			for(PatientAttributeValue pav : p.getPatientAttributeValues()) {
				if(pav.getAttribute().getName().equals(sortedAttributes.get(i).getName())) {
					found = true;
					formatField(row, pav.getValue()==null?pav.getAttributeNominalValue().getValue():pav.getValue(), i!=sortedAttributes.size()-1);
				}
			}
			if(!found)
				formatField(row, "", i!=sortedAttributes.size()-1);
		}
		
		fw.append(row.toString());
	}
	
	private void formatField(StringBuilder sb, String field) {
		formatField(sb, field, true);
	}
	private void formatField(StringBuilder sb, String field, boolean comma) {
		if(field == null)
			field = "";
		sb.append("\""+field+"\"");
		if(comma)
			sb.append(",");
		else
			sb.append("\n");
	}
	
	private void formatField(StringBuilder sb, Date field) {
		formatField(sb, field, true);
	}
	private void formatField(StringBuilder sb, Date field, boolean comma) {
		if(field == null)
			sb.append("\"\"");
		else 
			sb.append("\""+field.toString()+"\"");
		
		if(comma)
			sb.append(",");
		else
			sb.append("\n");
	}

}
