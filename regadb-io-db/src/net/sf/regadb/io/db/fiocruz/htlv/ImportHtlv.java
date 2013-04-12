package net.sf.regadb.io.db.fiocruz.htlv;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.io.FileUtils;

import net.sf.regadb.csv.Table;
import net.sf.regadb.db.Attribute;
import net.sf.regadb.db.AttributeGroup;
import net.sf.regadb.db.AttributeNominalValue;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.PatientAttributeValue;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.TestType;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.io.db.util.ConsoleLogger;
import net.sf.regadb.io.db.util.NominalAttribute;
import net.sf.regadb.io.db.util.Utils;
import net.sf.regadb.io.util.IOUtils;
import net.sf.regadb.io.util.StandardObjects;
import net.sf.regadb.util.settings.RegaDBSettings;

public class ImportHtlv {
	private static int numberViralIsolates = 0;
	private static Map<String, Test> viTestMap = new HashMap<String, Test>();
	
	public static void main(String [] args) throws IOException {
		if (args.length < 4) {
			System.err.println("Usage: regadb-import-fiocruz-htlv file.csv mappingPath output.xml output.csv");
			System.exit(0);
		}
		
		Table t = Table.readTable(args[0]);
		Map<String, Integer> map = getColumnMap(t);
		
		String mappingBasePath = args[1];
		RegaDBSettings.createInstance();
		List<Attribute> regadbAttributes = Utils.prepareRegaDBAttributes();
		AttributeGroup demographics = new AttributeGroup("Demographics");
		AttributeGroup personal = new AttributeGroup("Personal");
		
		Table countryMappingTable = 
			Utils.readTable(mappingBasePath + File.separatorChar + "countryOfOrigin.mapping");
		Table geographicOriginMappingTable = 
			Table.readTable(mappingBasePath+File.separatorChar+"geographicOrigin.mapping");
		Table ethnicityMappingTable = 
			Table.readTable(mappingBasePath+File.separatorChar+"ethnicity.mapping");
		NominalAttribute originA = 
			new NominalAttribute("Country of origin", countryMappingTable, demographics, Utils.selectAttribute("Country of origin", regadbAttributes));
		NominalAttribute geographicOriginA = 
			new NominalAttribute("Geographic origin", geographicOriginMappingTable, demographics, Utils.selectAttribute("Geographic origin", regadbAttributes));
		NominalAttribute genderA = new NominalAttribute("Gender", null, personal, Utils.selectAttribute("Gender", regadbAttributes));
		NominalAttribute ethnicityA = new NominalAttribute("Ethnicity", ethnicityMappingTable, personal, Utils.selectAttribute("Ethnicity", regadbAttributes));
		Attribute ageA = new Attribute(StandardObjects.getNumberValueType(),personal,"Age", null, new TreeSet<AttributeNominalValue>());
		Attribute regionA = new Attribute(StandardObjects.getStringValueType(),demographics,"Region", null ,new TreeSet<AttributeNominalValue>());
		Attribute clinicalStatusA = new Attribute(StandardObjects.getNominalValueType(),personal,"Clinical Status", null , new HashSet<AttributeNominalValue>());
		
		HashMap<String, Patient> patients = new HashMap<String, Patient>();
		
		Set<String> accessions = new HashSet<String>();
		
		for (int i = 1; i < t.numRows(); i++) {
			int row = i-1;
			
			Patient p = new Patient();
			
			String accession = t.valueAt(map.get("a_number"), i);
			if (!accessions.add(accession.trim().toLowerCase()))
				throw new RuntimeException("Accession nr is not unique: " + accession);
			writeValue(row, "Accession Number", accession, false);
			writeValue(row, "seq 1: fasta id", accession, false);
			writeValue(row, "UserAccount", "admin", false);
			String sequence = t.valueAt(map.get("sequence"), i);
			writeValue(row, "sequence", sequence, false);
			String size = t.valueAt(map.get("size"), i);
			if (isNotNull(size)) {
				int sizeI = Integer.parseInt(size.split(" ")[0]);
				if (sizeI != sequence.length()) 
					System.err.println("size:" + accession);
			}
			
			ViralIsolate vi = addViralIsolate(p, accession, sequence);
			
			p.setPatientId(i+"");
			patients.put(p.getPatientId(), p);
			
			String region = t.valueAt(map.get("genomic_region"), i);
			addViralIsolateTest(p, vi, "Isolate Region", region);
			String status = t.valueAt(map.get("statuss"), i);
			writeValue(row, "Status", status, true);
			addViralIsolateTest(p, vi, "Isolate Status", status);
			String isolated = t.valueAt(map.get("isolated"), i);
			writeValue(row, "Isolated ID 1", isolated, false);
			addViralIsolateTest(p, vi, "Isolate Isolated", isolated);
			
			String gender = t.valueAt(map.get("genre"), i);
			if (isNotNull(gender)) {
				Utils.handlePatientAttributeValue(genderA, gender, p);
				writeValue(row, "Gender", gender, true);
			}
			
			String age = t.valueAt(map.get("age"), i);
			if (isNotNull(age)) {
				try {
					p.createPatientAttributeValue(ageA).setValue(Integer.parseInt(age.trim())+"");
					writeValue(row, "Age", "="+age, false);
				} catch (Exception e) {
					System.err.println("error in accession:" + accession + " with value " + age);
				}
			}
			
			String ethnicity = t.valueAt(map.get("ethnic"), i);
			if(isNotNull(ethnicity)) {
				Utils.handlePatientAttributeValue(ethnicityA, ethnicity, p);
				writeValue(row, "Ethnicity", ethnicity, true);
			}
			
			String country = t.valueAt(map.get("geographic_region"), i);
			if (isNotNull(country)) {
		        AttributeNominalValue cnv = Utils.getANV(originA, country);
		        AttributeNominalValue gnv = Utils.getANV(geographicOriginA, country);
		         
		         if (cnv == null)
		        	 cnv = originA.nominalValueMap.get(country);
		         if (gnv == null)
		        	 gnv = geographicOriginA.nominalValueMap.get(country);
		         
		         if(cnv!=null) {
		             PatientAttributeValue v = p.createPatientAttributeValue(originA.attribute);
		             v.setAttributeNominalValue(cnv);
		             writeValue(row, "Country of origin", cnv.getValue(), true);
		         }
		         if(gnv!=null) {
		             PatientAttributeValue v = p.createPatientAttributeValue(geographicOriginA.attribute);
		             v.setAttributeNominalValue(gnv);
		             writeValue(row, "Geographic origin", gnv.getValue(), true);
		         }
			}
			
			String regionInCountry = t.valueAt(map.get("region"), i);
			if(isNotNull(regionInCountry)) {
				p.createPatientAttributeValue(regionA).setValue(regionInCountry.trim());
				writeValue(row, "Region", regionInCountry, true);
			}
			
			String clinicalStatus = t.valueAt(map.get("clinical_status"), i);
			if (isNotNull(clinicalStatus)) {
				handleClinicalStatus(p, clinicalStatusA, clinicalStatus);
				writeValue(row, "Clinical Status", clinicalStatus.trim(), true);
			}
			
			String proviralLoad = t.valueAt(map.get("proviral_load"), i);
			
			String cd4 = t.valueAt(map.get("cd4_count"), i);
			handleNumericTest(StandardObjects.getGenericCD4Test(), cd4, p);
			writeValue(row, "CD4 count", cd4, false);

			String cd8 = t.valueAt(map.get("cd8_count"), i);
			handleNumericTest(StandardObjects.getGenericCD8Test(), cd8, p);
			writeValue(row, "CD8 count", cd8, false);
			
			String contact = t.valueAt(map.get("contact"), i);
			writeValue(row, "Contact", contact, false);
			addViralIsolateTest(p, vi, "Contact information", contact);
			String article = t.valueAt(map.get("article"), i);
			writeValue(row, "Article", article, false);
			addViralIsolateTest(p, vi, "Article", article);
			String authors = t.valueAt(map.get("authors"), i);
			writeValue(row, "Authors", authors, false);
			addViralIsolateTest(p, vi, "Authors", authors);
			String journal = t.valueAt(map.get("journal"), i);
			writeValue(row, "Journal", journal, false);
			addViralIsolateTest(p, vi, "Journal", journal);
			
			String sampleYear = t.valueAt(map.get("sampling_date"), i);
			if (sampleYear.equals("NULL"))
				sampleYear="1800";
			writeValue(row, "Sample Date", sampleYear, false);
		}		
		
        IOUtils.exportPatientsXML(patients.values(), args[2], ConsoleLogger.getInstance());
        
        StringBuffer fasta = new StringBuffer();
        
        StringBuffer sb = new StringBuffer();
        
        for (int i = 0; i < headers.size(); i++) {
    		sb.append("\"" + headers.get(i) + "\"");
    		
    		if (i != headers.size() - 1)
    			sb.append(",");
        }
        sb.append("\n");
        
        for (Map<String, String> m : data) {
        	for (int i = 0; i < headers.size(); i++) {
        		String v = m.get(headers.get(i));
        		if (v == null || v.trim().toLowerCase().equals("null"))
        			v = "";
        		v = v.trim();
        		
        		sb.append("\"" + v + "\"");
        		
        		if (i != headers.size() - 1)
        			sb.append(",");
        		
        		if (m.get("Accession Number").trim().equals(""))
        		System.out.println("Accession Number"+m.get("Accession Number").trim());
        		
        		if (headers.get(i).equals("sequence")) {
        			fasta.append(">" + m.get("Accession Number").trim() + "\n");
        			fasta.append(v + "\n");
        		}
        	}
        	sb.append("\n");
        }
        FileUtils.writeStringToFile(new File(args[3]), sb.toString());
        FileUtils.writeStringToFile(new File(args[3] + ".fasta"), fasta.toString());
        
        File anvDir = new File(args[4]);
        for (String a : anvFiles.keySet()) {
        	Set<String> dups = new HashSet<String>();
        	
        	StringBuffer anv_sb = new StringBuffer();
        	Set<String> values = anvFiles.get(a);
        	for (String v : values) {
        		if (dups.add(v.trim().toLowerCase())) {
        			anv_sb.append(v.trim()+"\n");
        		}
        	}
        	FileUtils.writeStringToFile(new File(args[4] + "/" + a + ".csv"), anv_sb.toString());
        }
	}
	
	private static List<String> headers = new ArrayList<String>();
	private static List<Map<String, String>> data = new ArrayList<Map<String, String>>();
	private static Map<String, Set<String>> anvFiles = new HashMap<String, Set<String>>(); 
	
	private static void writeValue(int row, String key, String value, boolean anvs) {
		Map<String, String> rowData;
		if (row == data.size() ) {
			rowData = new HashMap<String, String>();
			data.add(rowData);
		} else {
			rowData = data.get(row);
		}
		
		rowData.put(key, value);
		
		if (!headers.contains(key))
			headers.add(key);
		
		Set<String> anvFile = anvFiles.get(key);
		if (anvFile == null && anvs) {
			anvFile = new HashSet<String>();
			anvFiles.put(key, anvFile);
		}
		
		if (anvFile != null)
			anvFile.add(value.trim());
	}
	
	private static ViralIsolate addViralIsolate(Patient p, String id, String sequence) {
		id = id.trim();
		sequence = sequence.trim();
		
		if (!isNotNull(id)) {
			id = "sample" + numberViralIsolates;
			numberViralIsolates++;
		}
		
        ViralIsolate vi = p.createViralIsolate();
        vi.setSampleDate(null);
        vi.setSampleId(id);
        
        NtSequence ntseq = new NtSequence();
        ntseq.setLabel("Sequence 1");
        ntseq.setNucleotides(Utils.clearNucleotides(sequence));
        
        vi.getNtSequences().add(ntseq);
        
        return vi;
	}
	
	private static void handleNumericTest(Test test, String value, Patient p) {
		if (!isNotNull(value))
			return;
		
	    try {
            Double.parseDouble(value);
        } catch(NumberFormatException nfe) {
            nfe.printStackTrace();
        }
        TestResult tr = p.createTestResult(test);
        tr.setValue(value);
	}
	
	private static void handleClinicalStatus(Patient p, Attribute status, String value) {
		value = value.trim();
		
		AttributeNominalValue selectedAnv = null;
		
		for (AttributeNominalValue anv : status.getAttributeNominalValues()) {
			if (anv.getValue().equals(value)) 
				selectedAnv = anv;
		}
		
		if (selectedAnv == null) {
			selectedAnv = new AttributeNominalValue(status, value);
			status.getAttributeNominalValues().add(selectedAnv);
		}
		p.createPatientAttributeValue(status).setAttributeNominalValue(selectedAnv);
	}
	
	private static boolean isNotNull(String s) {
		return !s.trim().equals("NULL") && !s.trim().equals("");
	}
	
	private static void addViralIsolateTest(Patient p, ViralIsolate vi, String name, String value) {
		if (isNotNull(value)) {
			Test t = viTestMap.get(name);
			if (t == null) {
				TestType type = 
					new TestType(StandardObjects.getViralIsolateAnalysisTestObject(), name);
				type.setValueType(StandardObjects.getStringValueType());
				Test test = new Test(type, name);
				
				viTestMap.put(name, test);
				t = test;
			}
			TestResult tr = new TestResult();
			tr.setTest(t);
			tr.setData(value.getBytes());
			tr.setViralIsolate(vi);
			
			tr.setPatient(vi.getPatient());
			vi.getTestResults().add(tr);
		}
	}
	
	private static Map<String, Integer> getColumnMap(Table t) {
		Map<String, Integer> map = new HashMap<String, Integer>();
		for (int i = 0; i < t.numColumns(); i++) {
			map.put(t.valueAt(i, 0).toLowerCase(), i);
		}
		return map;
	}
}
