package net.sf.regadb.io.db.fiocruz.htlv;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import net.sf.regadb.csv.Table;
import net.sf.regadb.db.Attribute;
import net.sf.regadb.db.AttributeGroup;
import net.sf.regadb.db.AttributeNominalValue;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestNominalValue;
import net.sf.regadb.db.TestObject;
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
	
	public static void main(String [] args) throws FileNotFoundException, UnsupportedEncodingException {
		Table t = Table.readTable("/home/pieter/projects/htlv/htlv.csv");
		Map<String, Integer> map = getColumnMap(t);
		
		String mappingBasePath = 
			"/home/pieter/projects/regadb/regadb-io-db/src/net/sf/regadb/io/db/fiocruz/htlv/mappings";
		RegaDBSettings.createInstance();
		List<Attribute> regadbAttributes = Utils.prepareRegaDBAttributes();
		AttributeGroup demographics = new AttributeGroup("Demographics");
		AttributeGroup personal = new AttributeGroup("Personal");
		
		Table countryMappingTable = 
			Utils.readTable(mappingBasePath + File.separatorChar + "countryOfOrigin.mapping");
		NominalAttribute originA = new NominalAttribute("Country of origin", countryMappingTable, demographics, Utils.selectAttribute("Country of origin", regadbAttributes));
		NominalAttribute genderA = new NominalAttribute("Gender", null, personal, Utils.selectAttribute("Gender", regadbAttributes));
		Attribute ageA = new Attribute(StandardObjects.getNumberValueType(),personal,"Age",new TreeSet<AttributeNominalValue>());
		Attribute clinicalStatusA = new Attribute(StandardObjects.getNominalValueType(),personal,"Clinical Status", new HashSet<AttributeNominalValue>());
		
		HashMap<String, Patient> patients = new HashMap<String, Patient>();
		
		for (int i = 1; i < t.numRows(); i++) {
			Patient p = new Patient();
			
			String accession = t.valueAt(map.get("a_number"), i);
			String sequence = t.valueAt(map.get("sequence"), i);
			String size = t.valueAt(map.get("size"), i);
			if (isNotNull(size)) {
				int sizeI = Integer.parseInt(size.split(" ")[0]);
				if (sizeI != sequence.length()) 
					System.err.println("size:" + accession);
			}
			
			ViralIsolate vi = addViralIsolate(p, accession, sequence);
			
			p.setPatientId(vi.getSampleId());
			patients.put(p.getPatientId(), p);
			
			String region = t.valueAt(map.get("genomic_region"), i);
			addViralIsolateTest(p, vi, "Region", region);
			String status = t.valueAt(map.get("statuss"), i);
			addViralIsolateTest(p, vi, "Status", status);
			String isolated = t.valueAt(map.get("isolated"), i);
			addViralIsolateTest(p, vi, "Isolated", isolated);
			
			String gender = t.valueAt(map.get("genre"), i);
			if (isNotNull(gender))
				Utils.handlePatientAttributeValue(genderA, gender, p);
			
			String age = t.valueAt(map.get("age"), i);
			if (isNotNull(age))
				 p.createPatientAttributeValue(ageA).setValue(Integer.parseInt(age.trim())+"");
			
			String ethnicity = t.valueAt(map.get("ethnic"), i);
				if(isNotNull(ethnicity)) {
					System.err.println(ethnicity);
				}
			
			String country = t.valueAt(map.get("country"), i);
			if (isNotNull(country))
				Utils.handlePatientAttributeValue(originA, country, p);
			
			String clinicalStatus = t.valueAt(map.get("clinical_status"), i);
			if (isNotNull(clinicalStatus))
				handleClinicalStatus(p, clinicalStatusA, clinicalStatus);
			
			String proviralLoad = t.valueAt(map.get("proviral_load"), i);
			
			String cd4 = t.valueAt(map.get("cd4_count"), i);
			handleNumericTest(StandardObjects.getGenericCD4Test(), cd4, p);

			String cd8 = t.valueAt(map.get("cd8_count"), i);
			handleNumericTest(StandardObjects.getGenericCD8Test(), cd8, p);
			
			String contact = t.valueAt(map.get("contact"), i);
			addViralIsolateTest(p, vi, "Contact information", contact);
			String article = t.valueAt(map.get("article"), i);
			addViralIsolateTest(p, vi, "Article", article);
			String authors = t.valueAt(map.get("authors"), i);
			addViralIsolateTest(p, vi, "Authors", authors);
			String journal = t.valueAt(map.get("journal"), i);
			addViralIsolateTest(p, vi, "Journal", journal);
		}		
		
        IOUtils.exportPatientsXML(patients.values(), "/home/pieter/projects/htlv/htlv.xml", ConsoleLogger.getInstance());
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
		Test t = viTestMap.get(name);
		if (t == null) {
			TestType type = 
				new TestType(StandardObjects.getStringValueType(), null, new TestObject("Viral Isolate analysis"), name, new TreeSet<TestNominalValue>());
			Test test = new Test(type, value);
			
			viTestMap.put(name, test);
			TestResult tr = new TestResult(test);
			tr.setValue(value);
			tr.setViralIsolate(vi);
			p.getTestResults().add(tr);
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
