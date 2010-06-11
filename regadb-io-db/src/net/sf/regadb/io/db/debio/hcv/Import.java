package net.sf.regadb.io.db.debio.hcv;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.read.biff.WorkbookParser;
import net.sf.regadb.db.Attribute;
import net.sf.regadb.db.AttributeGroup;
import net.sf.regadb.db.AttributeNominalValue;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestNominalValue;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.TestType;
import net.sf.regadb.db.ValueType;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.db.meta.Equals;
import net.sf.regadb.io.db.util.ConsoleLogger;
import net.sf.regadb.io.db.util.Utils;
import net.sf.regadb.io.util.IOUtils;
import net.sf.regadb.io.util.StandardObjects;
import net.sf.regadb.util.args.Arguments;
import net.sf.regadb.util.args.PositionalArgument;

public class Import {
	private Map<String, Patient> patients = new HashMap<String,Patient>();
	
	private Test barcTest, reqTest, visitTest, boxnumberTest, viralresponseTest, remarksTest;
	private AttributeGroup debioGroup = new AttributeGroup("Debio");
	private Attribute treatmentAttribute, treatmentArmAttribute, screeningNumberAttribute, randomizationNumberAttribute;
	
	public Import(){
		barcTest = createViralIsolateTest("BARC", StandardObjects.getStringValueType());
		reqTest = createViralIsolateTest("REQ", StandardObjects.getStringValueType());
		visitTest = createViralIsolateTest("Visit", new String[]{"baseline", "day 15", "day 29", "week 8"});
		boxnumberTest = createViralIsolateTest("Box number", StandardObjects.getNumberValueType());
		viralresponseTest = createViralIsolateTest("Viral response", StandardObjects.getStringValueType());
		remarksTest = createViralIsolateTest("Remarks", StandardObjects.getStringValueType());
		
		treatmentAttribute = createAttribute("Treatment", new String[]{
				"tritherapy 400mg", "Mono therapy 400mg", "double therapy 400mg", "tritherapy 800mg", "tritherapy 400mg (loading dose)"});
		treatmentArmAttribute = createAttribute("Treatment arm", new String[]{"A","B","C","D","E"});
		screeningNumberAttribute = createAttribute("Screening number", StandardObjects.getNumberValueType());
		randomizationNumberAttribute = createAttribute("Randomization number", StandardObjects.getNumberValueType());
	}
	
	public void run(File input, File output){
		try {
			Workbook book = WorkbookParser.getWorkbook(input);
			Sheet sheet = book.getSheet(0);
			
			Map<String,Integer> header = new HashMap<String,Integer>();
			for(int j=0; j<sheet.getColumns(); ++j){
				header.put(sheet.getCell(j,0).getContents(),j);
			}
			
			for(int i=1; i<sheet.getRows(); ++i){
				String patientId = sheet.getCell(header.get("Patient ID"), i).getContents().trim();
				String sampleId = sheet.getCell(header.get("Sample Number"), i).getContents().trim();
				String barc = sheet.getCell(header.get("BARC"), i).getContents().trim();
				String req = sheet.getCell(header.get("REQ"), i).getContents().trim();
				Date sampleDate = parseDate(sheet.getCell(header.get("Sampling date"), i).getContents().trim());
				String visit = sheet.getCell(header.get("Visit"), i).getContents().trim();
				String viralLoad = sheet.getCell(header.get("Viral Load"), i).getContents().trim();
				String viralLoadLog = sheet.getCell(header.get("log viral load"), i).getContents().trim();
				String boxNumber = sheet.getCell(header.get("Box number"), i).getContents().trim();
				String viralResponse = sheet.getCell(header.get("Viral response"), i).getContents().trim();
				String remarks = sheet.getCell(header.get("remarks"), i).getContents().trim();
				String nucleotides = sheet.getCell(header.get("Nucleotide sequence"), i).getContents().trim();
				
//				if(nucleotides.length() == 0)
//					continue;
				
				Patient p = patients.get(patientId);
				if(p == null){
					p = new Patient();
					p.setPatientId(patientId);
					patients.put(patientId, p);
					
					Date birthdate = parseDate(sheet.getCell(header.get("Date of birth"), i).getContents().trim());
					String treatmentArm = sheet.getCell(header.get("Treatment Arm"), i).getContents().trim();
					String treatment = sheet.getCell(header.get("Treatment"), i).getContents().trim();
					String screeningNumber = sheet.getCell(header.get("Screening number"), i).getContents().trim();
					String randomizationNumber = sheet.getCell(header.get("Randomization number"), i).getContents().trim();

					Utils.setBirthDate(p, birthdate);
					Utils.setPatientAttributeValue(p, treatmentArmAttribute, treatmentArm);
					Utils.setPatientAttributeValue(p, treatmentAttribute, treatment);
					Utils.setPatientAttributeValue(p, screeningNumberAttribute, screeningNumber);
					Utils.setPatientAttributeValue(p, randomizationNumberAttribute, randomizationNumber);
				}
				
				ViralIsolate vi = p.createViralIsolate();
				vi.setSampleId(sampleId);
				vi.setSampleDate(sampleDate);
				
				NtSequence nt = new NtSequence(vi);
				nt.setNucleotides(nucleotides.replaceAll("\\?", ""));
				nt.setLabel("Sequence 1");
				vi.getNtSequences().add(nt);
				
				createTestResult(vi, barcTest, sampleDate, sampleId, barc);
				createTestResult(vi, reqTest, sampleDate, sampleId, req);
				createTestResult(vi, visitTest, sampleDate, sampleId, visit);
				createTestResult(vi, boxnumberTest, sampleDate, sampleId, boxNumber);
				createTestResult(vi, viralresponseTest, sampleDate, sampleId, viralResponse);
				createTestResult(vi, remarksTest, sampleDate, sampleId, remarks);
				
				createViralLoadTestResult(p, StandardObjects.getGenericHCVViralLoadTest(), sampleDate, sampleId, viralLoad);
				createViralLoadTestResult(p, StandardObjects.getGenericHCVViralLoadLog10Test(), sampleDate, sampleId, viralLoadLog);
			}
			
			String patientsXml = output.getAbsolutePath() + File.separatorChar +"patients.xml";
			String viXml = output.getAbsolutePath() + File.separatorChar +"viral-isolates.xml";
			System.out.println("writing "+ patientsXml);
			IOUtils.exportPatientsXML(patients.values(), patientsXml, ConsoleLogger.getInstance());
			System.out.println("writing "+ viXml);
			IOUtils.exportNTXMLFromPatients(patients.values(), viXml, ConsoleLogger.getInstance());
			System.out.println("done");
		} catch (BiffException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private TestResult createTestResult(ViralIsolate vi, Test test, Date date, String sampleId, String value){
		if(value.length() == 0)
			return null;
		
		TestResult tr = new TestResult(test);
		tr.setPatient(vi.getPatient());
		vi.getTestResults().add(tr);
		tr.setViralIsolate(vi);
		
		tr.setTestDate(date);
		tr.setSampleId(sampleId);
		
		if(Equals.isSameValueType(test.getTestType().getValueType(),StandardObjects.getNominalValueType()))
			tr.setTestNominalValue(Utils.getNominalValue(test.getTestType(), value));
		else
			tr.setValue(value);
		
		return tr;
	}
	
	private TestResult createViralLoadTestResult(Patient p, Test test, Date date, String sampleId, String value){
		if(value.length() == 0)
			return null;

		value = value.replaceAll(" ", "").replaceAll("^([0-9]+)", "=$1");
		
		TestResult tr = p.createTestResult(test);
		tr.setSampleId(sampleId);
		tr.setTestDate(date);
		tr.setValue(value);
		
		return tr;
	}
	
	
	private Test createViralIsolateTest(String description, ValueType type){
		return createViralIsolateTest(description, type, null);
	}
	private Test createViralIsolateTest(String description, String values[]){
		return createViralIsolateTest(description, StandardObjects.getNominalValueType(), values);
	}
	private Test createViralIsolateTest(String description, ValueType type, String values[]){
		TestType tt = new TestType(StandardObjects.getViralIsolateAnalysisTestObject(), description);
		tt.setValueType(type);
		
		if(values != null){
			for(String value : values){
				TestNominalValue tnv = new TestNominalValue(tt, value);
				tt.getTestNominalValues().add(tnv);
			}
		}
		
		return new Test(tt, description);
	}
	
	private Attribute createAttribute(String description, ValueType type){
		return createAttribute(description, type, null);
	}
	private Attribute createAttribute(String description, String values[]){
		return createAttribute(description, StandardObjects.getNominalValueType(), values);
	}
	private Attribute createAttribute(String description, ValueType type, String values[]){
		Attribute a = new Attribute(description);
		a.setAttributeGroup(debioGroup);
		a.setValueType(type);
		
		if(values != null){
			for(String value : values){
				AttributeNominalValue anv = new AttributeNominalValue(a, value);
				a.getAttributeNominalValues().add(anv);
			}
		}
		
		return a;
	}
	
	private SimpleDateFormat df1 = new SimpleDateFormat("MM.dd.yyyy");
	private SimpleDateFormat df2 = new SimpleDateFormat("MM dd yyyy");
	private Date parseDate(String s){
		try {
			return df1.parse(s);
		} catch (ParseException e) {
			try {
				return df2.parse(s);
			} catch (ParseException e1) {
				System.err.println("unparseable date: '"+ s +"'");
				return null;
			}
		}
	}
	
	public static void main(String args[]){
		Arguments as = new Arguments();
		PositionalArgument input = as.addPositionalArgument("input-file.xls", true);
		PositionalArgument output = as.addPositionalArgument("output-dir", true);
		
		if(!as.handle(args))
			return;
		
		Import imp = new Import();
		imp.run(new File(input.getValue()), new File(output.getValue()));
	}
}
