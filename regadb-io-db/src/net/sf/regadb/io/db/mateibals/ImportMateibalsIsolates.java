package net.sf.regadb.io.db.mateibals;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.regadb.analysis.functions.FastaHelper;
import net.sf.regadb.analysis.functions.FastaRead;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.PatientAttributeValue;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.io.db.util.ConsoleLogger;
import net.sf.regadb.io.db.util.Utils;
import net.sf.regadb.io.util.IOUtils;
import net.sf.regadb.io.util.StandardObjects;
import net.sf.regadb.util.xls.ExcelTable;

public class ImportMateibalsIsolates {
	public static void main(String [] args) {
		File dir = new File(args[0]);
		
		ImportClinicalDb clinical = new ImportClinicalDb();
		Map<String, Patient> patients = null;
		try {
			patients = clinical.loadPatients(new File(dir.getAbsolutePath() + File.separatorChar + "cd4_vl.xls"));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		ImportMateibalsIsolates importIsolates = new ImportMateibalsIsolates(new File(dir.getAbsolutePath() + File.separatorChar + "database.xls"), new File(dir.getAbsolutePath() + File.separatorChar + "fasta"), clinical.getPatients(), clinical.getPatientNames());
		importIsolates.run(new File(dir.getAbsolutePath() + File.separatorChar + "patients.xml"), 
				new File(dir.getAbsolutePath() + File.separatorChar + "isolates.xml"));
	}
	
	private ExcelTable table = new ExcelTable("dd.MM.yyyy");
	private Map<String, Integer> colNameMappings = new HashMap<String, Integer>();
	private Map<String, String> sequences = new HashMap<String, String>();
	private Map<String, String> sequencesInfo = new HashMap<String, String>();
	private Map<String, Patient> patients;
	private List<Patient> externalPatients = new ArrayList<Patient>();
	private Map<String, Set<String>> patientNames;
	
	private SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
	
	public ImportMateibalsIsolates(File xlsFile, File fastaDir, Map<String, Patient> patients, Map<String, Set<String>> patientNames) {
		this.patients = patients;
		this.patientNames = patientNames;
		
		df.setLenient(false);
		
		try {
			table.loadFile(xlsFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for (int c = 0; c < table.columnCount(); c++) {
			String header = table.getCell(0, c).trim();
			if (header.equals("")) {
				System.err.println("Empty col name at column " + c);
				return;
			} else {
				colNameMappings.put(header, c);
			}
		}
		
		for (File d : fastaDir.listFiles()) {
			for (File fasta : d.listFiles()) {
				if (fasta.isFile()) {
					FastaRead fr = FastaHelper.readFastaFile(fasta, true);

					switch (fr.status_) {
			        	case Valid:
			            case ValidButFixed:
			            	String name = fasta.getName();
			            	name = name.substring(0, name.lastIndexOf('.'));
			            	name = fixIsolateName(name);
			            	
			            	if (sequences.put(name, fr.xna_) != null) {
			            		System.err.println("Value exists already for isolate: " + name);
			            	}
			                break;
			            case MultipleSequences:
			            case FileNotFound:
			            case Invalid:
			                System.err.println("invalid fasta " + fasta.getAbsolutePath());
			                continue;
					}
				}
			}
		}
	}
	
	public void run(File patientOutputFile, File isolateOutputFile) {
		for (int r = 1; r < table.rowCount(); r++) {
			Date drawnDate = 
				MateibalsUtils.parseDate(df, getValue(r, "Drawn date"), MateibalsUtils.createDate(df, "01.01.2000"), r + 1, "Drawn date");
			
			Date birthDate = 
				MateibalsUtils.parseDate(df, getValue(r, "Birth date"), MateibalsUtils.createDate(df, "01.01.1920"), r + 1, "Drawn date");
			
			String name = getValue(r, "Patient name");
			
			String isolateId = fixIsolateName(getValue(r, "Registration No"));
			
			Patient p = findPatientInClinicalDB(name, birthDate);
			
			if (p == null) {
				p = findExternalPatient(name, birthDate);
				if (p == null) {
					String bd = (birthDate !=null) ? df.format(birthDate) : "";
					System.err.println("cannot find patient :" + name.toLowerCase() + " " +  bd + " >" + sequencesInfo.get(fixIsolateName(getValue(r, "Registration No"))));
					
					p = new Patient();
					String id = name.replace(' ', '_');
					if (birthDate != null) {
						id += "_" + df.format(birthDate);
					}
					p.setPatientId(id);
					Utils.setBirthDate(p, birthDate);
					Utils.setPatientAttributeValue(p, MateibalsUtils.nameA, name);
					externalPatients.add(p);
				}
				
				String vl = getValue(r, "VL");
				if (vl.contains("(") && vl.contains(")")) {
					vl = vl.substring(vl.indexOf('(') + 1, vl.indexOf(')'));
					if (vl.startsWith("~"))
						vl = vl.substring(1);
				}
				vl = MateibalsUtils.parseViralLoad(r, vl);
				if (vl != null) {
					MateibalsUtils.addTestResult(p, StandardObjects.getGenericHiv1ViralLoadTest(), vl, drawnDate);
				}
				
				try {
					String cd4 = getValue(r, "CD4");
					if (!cd4.equals("")) {
						Integer.parseInt(cd4);
						MateibalsUtils.addTestResult(p, StandardObjects.getGenericCD4Test(), cd4+"", drawnDate);
					}
				} catch(Exception e) {
					System.err.println("Invalid CD4 row=" + r + " value=" + getValue(r, "CD4"));
				}
			}
			
			MateibalsUtils.handleANV(p, MateibalsUtils.countyA, getValue(r, "County"));
			MateibalsUtils.handleANV(p, MateibalsUtils.residenceA, getValue(r, "Residence"));
			
			String seqInfo = sequencesInfo.get(isolateId);
			if (seqInfo == null)
				seqInfo = "";
			else 
				seqInfo = "_" + seqInfo;
			
			String sequence = sequences.get(isolateId);
			
			ViralIsolate vi = null;
			if (sequence != null) {
				vi = addViralIsolate(p, isolateId + seqInfo, sequence, drawnDate);
			} else {
				System.err.println("no seq:" + getValue(r, "Registration No"));
			}
			
			String drawnDateColor = table.getCellHexColor(r, colNameMappings.get("Drawn date"));
			if (drawnDateColor != null && drawnDateColor.equals("CCCC:FFFF:FFFF") && vi != null) {
				addViralIsolateTest(p, vi, MateibalsUtils.viCommentT, "Sample date could be earlier");
			}
			
			String setNo = getValue(r, "Set no");
			if (!setNo.equals("") && vi != null) {
				addViralIsolateTest(p, vi, MateibalsUtils.viSetNoT, setNo);
			}
			
			String epid = getValue(r, "Epidem info");
			if (!epid.equals("") && vi != null) {
				addViralIsolateTest(p, vi, MateibalsUtils.viEpidT, epid);
			}
		}
		
		for (Patient p : externalPatients) {
			patients.put(p.getPatientId(), p);
		}

        IOUtils.exportPatientsXML(patients.values(), patientOutputFile.getAbsolutePath(), ConsoleLogger.getInstance());
        IOUtils.exportNTXMLFromPatients(patients.values(), isolateOutputFile.getAbsolutePath(), ConsoleLogger.getInstance());
	}
	
	private static void addViralIsolateTest(Patient p, ViralIsolate vi, Test test, String value) {
		if (!value.equals("")) {
			TestResult tr = new TestResult();
			tr.setTest(test);
			tr.setData(value.getBytes());
			tr.setViralIsolate(vi);
			
			tr.setPatient(vi.getPatient());
			vi.getTestResults().add(tr);
		}
	}
	
	private static ViralIsolate addViralIsolate(Patient p, String id, String sequence, Date date) {
		id = id.trim();
		sequence = sequence.trim();
		
        ViralIsolate vi = p.createViralIsolate();
        vi.setSampleDate(date);
        vi.setSampleId(id);
        
        NtSequence ntseq = new NtSequence();
        ntseq.setLabel("Sequence 1");
        ntseq.setNucleotides(Utils.clearNucleotides(sequence));
        
        vi.getNtSequences().add(ntseq);
        
        return vi;
	}

	public Patient findExternalPatient(String name, Date birthDate) {
		for (Patient p : externalPatients) {
			if ((birthDate != null && p.getBirthDate() != null && df.format(p.getBirthDate()).equals(df.format(birthDate)))
					|| (birthDate == null && p.getBirthDate() == null)) {
				for (PatientAttributeValue pav : p.getPatientAttributeValues()) {
					if  (pav.getAttribute().getName().equals(MateibalsUtils.nameA.getName())) {
						if (pav.getValue().toLowerCase().equals(name.toLowerCase())) 
							return p;
					}
				}
			}
		}
		
		return null;
	}
	
	public Patient findPatientInClinicalDB(String name, Date birthDate) {
		for (Map.Entry<String, Patient> e : patients.entrySet()) {
			Patient p = e.getValue();
			if (birthDate != null && df.format(p.getBirthDate()).equals(df.format(birthDate))) {
				Set<String> names = this.patientNames.get(p.getPatientId());
				for (String n : names) {
					if (name.toLowerCase().equals(n.toLowerCase())) {
						return p;
					}
				}
			}
		}
		
		return null;
	}
	
	private String getValue(int r, String string) {
		return MateibalsUtils.getValue(table, r, string);
	}
	
	public String fixIsolateName(String isolateName) {
		StringBuilder buffer = new StringBuilder();
		
		StringBuilder infoBuffer = new StringBuilder();
		
		for (int i = 0; i < isolateName.length(); i++) {
			char c = isolateName.charAt(i);
			if (Character.isDigit(c) || c == '_') 
				buffer.append(c);
			else 
				infoBuffer.append(c);
		}
		
		if(infoBuffer.toString().trim().length() > 0)
			sequencesInfo.put(buffer.toString(), infoBuffer.toString().trim());
		
		return buffer.toString();
	}
}
