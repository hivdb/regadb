package net.sf.regadb.io.db.portugal.hiv2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import jxl.read.biff.BiffException;
import net.sf.regadb.csv.Table;
import net.sf.regadb.db.Attribute;
import net.sf.regadb.db.AttributeGroup;
import net.sf.regadb.db.AttributeNominalValue;
import net.sf.regadb.db.Event;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.PatientAttributeValue;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.ValueType;
import net.sf.regadb.io.db.util.ConsoleLogger;
import net.sf.regadb.io.db.util.NominalAttribute;
import net.sf.regadb.io.db.util.Utils;
import net.sf.regadb.io.util.IOUtils;
import net.sf.regadb.io.util.StandardObjects;

public class ImportEgazMonizHiv2 {
	Map<String, Patient> patientMap = new HashMap<String, Patient>();
	
    private NominalAttribute countryOfOriginA;
    private NominalAttribute geographicOriginA;
    
	private AttributeGroup regadb = new AttributeGroup("RegaDB");
	private AttributeGroup pt_group = new AttributeGroup("PT");
	
	private List<Attribute> regadbAttributes;
	private List<Event> regadbEvents;
	
	Attribute clinicalFileNumberA;
	
	private Table instituteTable;
	
	SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yy");
	
	private Map<String, String> institutesMapping = new HashMap<String, String>();
	
	private String mappingPath;
    
	public static void main(String [] args) throws BiffException, IOException {
		ImportEgazMonizHiv2 imp = new ImportEgazMonizHiv2(args[0]);
		imp.run(new File(args[1]));
	}
	
	public ImportEgazMonizHiv2(String mappingPath) {
		this.mappingPath = mappingPath;
		regadbAttributes = Utils.prepareRegaDBAttributes();
		regadbEvents = Utils.prepareRegaDBEvents();
		clinicalFileNumberA = Utils.selectAttribute("Clinical File Number", regadbAttributes);
		
		try {
			Table institutesMappingT = Table.readTable(mappingPath + File.separatorChar + "institutes.mapping");
			for(int i = 1; i<institutesMappingT.numRows(); i++) {
				institutesMapping.put(institutesMappingT.valueAt(0, i), institutesMappingT.valueAt(1, i));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	public void run(File dir) throws BiffException, IOException {
		parsePatientInfo(new File(dir.getAbsolutePath()+File.separatorChar+"access"));
		parseAnalyses(new File(dir.getAbsolutePath()+File.separatorChar+"access"));
		ImportEgazMonizHiv2Sequences seqs = new ImportEgazMonizHiv2Sequences();
		seqs.run(this, new File(dir.getAbsolutePath()+File.separatorChar+"seqs"));
		
        IOUtils.exportPatientsXML(patientMap.values(), dir.getAbsolutePath()+File.separatorChar+"xml"+File.separatorChar+"patients.xml", ConsoleLogger.getInstance());
        IOUtils.exportNTXMLFromPatients(patientMap.values(), dir.getAbsolutePath()+File.separatorChar+"xml"+File.separatorChar+"viralIsolates.xml", ConsoleLogger.getInstance());
	}
	
	public void parsePatientInfo(File dir) throws FileNotFoundException, UnsupportedEncodingException {
		Table patientInfo = Table.readTable( dir.getAbsolutePath()+File.separatorChar+"patient_info.csv");
		int PatientNrID = patientInfo.findColumn("NDoente");
		int ProcessNrID = patientInfo.findColumn("NProcesso");
		int NameID = patientInfo.findColumn("Nome");
		int InitialsID = patientInfo.findColumn("Ident");
		int GenderID = patientInfo.findColumn("Sexo");
		int BirthDateID = patientInfo.findColumn("Data_Nasc");
		int GeographicOriginID = patientInfo.findColumn("OrigemGeog");
		int InstituteID = patientInfo.findColumn("Instituicao");
		int HIV1CoinfectionID = patientInfo.findColumn("CoinfHIV1");
		int CommentsID = patientInfo.findColumn("Comentarios");
		
		this.geographicOriginA = new NominalAttribute("Geographic origin", Table.readTable(mappingPath+File.separatorChar+"geographic_origin.mapping"), regadb, Utils.selectAttribute("Geographic origin", regadbAttributes));
		this.countryOfOriginA = new NominalAttribute("Country of origin", Table.readTable(mappingPath+File.separatorChar+"country_of_origin.mapping"), regadb, Utils.selectAttribute("Country of origin", regadbAttributes));
		
		NominalAttribute genderA = new NominalAttribute("Gender", GenderID, new String[] { "M", "F" },
                new String[] { "male", "female" } );
    	genderA.attribute.setAttributeGroup(regadb);
    	
    	NominalAttribute coinfectionA = new NominalAttribute("HIV-1 Coinfection", 0, new String[] { "0", "1" },
                new String[] { "Negative", "Positive" } );
    	coinfectionA.attribute.setAttributeGroup(pt_group);
    	
    	Attribute commentA = new Attribute();
    	commentA.setValueType(new ValueType("string"));
    	commentA.setName("Comment");
    	commentA.setAttributeGroup(pt_group);
    	
    	Attribute initialsA = new Attribute();
    	initialsA.setValueType(new ValueType("string"));
    	initialsA.setName("Initials");
    	initialsA.setAttributeGroup(pt_group);
    	
    	Attribute instituteA = new Attribute();
    	instituteA.setValueType(new ValueType("nominal value"));
    	instituteA.setName("Institute");
    	instituteA.setAttributeGroup(pt_group);
		
    	HashSet<String> s = new HashSet<String>();
    	
		for(int i = 1 ; i<patientInfo.numRows(); i++) {
			String patientNr = patientInfo.valueAt(PatientNrID, i);
			String name = patientInfo.valueAt(NameID, i);
			String gender = patientInfo.valueAt(GenderID, i).trim();
			String birthDate = patientInfo.valueAt(BirthDateID, i).trim();
			String origin = patientInfo.valueAt(GeographicOriginID, i).trim();
			String institute = patientInfo.valueAt(InstituteID, i).trim();
			String coinfection = patientInfo.valueAt(HIV1CoinfectionID, i).trim();
			String comments = patientInfo.valueAt(CommentsID, i).trim();
			String initials = patientInfo.valueAt(InitialsID, i).trim();
			String processNr = patientInfo.valueAt(ProcessNrID, i).trim();
			
			if(!patientNr.equals("") && patientMap.get(patientNr)==null) {
				Patient p = new Patient();
				patientMap.put(patientNr, p);
				
				p.setPatientId(patientNr);
				
				//patient names
//				String[] nameParts = name.split(" ");
//				String firstName = "";
//				String lastName = "";
//				for(int n = 0; n<nameParts.length; n++) {
//					if(n==0) {
//						firstName = nameParts[n];
//					} else {
//						lastName += " " +nameParts[n];
//					}
//				}
//				p.setFirstName(firstName);
//				p.setLastName(lastName);
				//patient names
				
				if(!gender.equals("")) {
            		AttributeNominalValue vv = genderA.nominalValueMap.get(gender.toUpperCase());
                    if (vv != null) {
                        PatientAttributeValue v = p.createPatientAttributeValue(genderA.attribute);
                        v.setAttributeNominalValue(vv);
                    } else {
                        ConsoleLogger.getInstance().logWarning("Unsupported attribute value (gender): "+gender);
                    }
				}
				if(!birthDate.equals("")) {
					try {
						Date d = dateFormatter.parse(birthDate);
						Utils.setBirthDate(p, d);
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
				if(!origin.equals("")) {
					Utils.addCountryOrGeographicOrigin(countryOfOriginA, geographicOriginA, origin.toUpperCase(), p);
				}
				if(!institute.equals("")) {
					if(!findInstitute(institute)) {
						String instituteM = institutesMapping.get(institute);
						if(instituteM==null || instituteM.trim().equals("")) {
							s.add("Cannot map/find institute " + institute + " | " + instituteM);
							institute = null;
						} else {
							institute = instituteM;
						}
					}
					if(institute!=null) {
						AttributeNominalValue anv = Utils.getNominalValue(instituteA, institute);
						if(anv==null) {
							instituteA.getAttributeNominalValues().add(new AttributeNominalValue(instituteA, institute));
						}
						anv = Utils.getNominalValue(instituteA, institute);
						p.createPatientAttributeValue(instituteA).setAttributeNominalValue(anv);
					}
				}
				if(!coinfection.equals("")) {
            		AttributeNominalValue vv = coinfectionA.nominalValueMap.get(coinfection.toUpperCase());
                    if (vv != null) {
                        PatientAttributeValue v = p.createPatientAttributeValue(coinfectionA.attribute);
                        v.setAttributeNominalValue(vv);
                    } else {
                        ConsoleLogger.getInstance().logWarning("Unsupported attribute value (coinfection): "+coinfection);
                    }
				}
				if(!comments.equals("")) {
					p.createPatientAttributeValue(commentA).setValue(comments);
				}
//				if(!initials.equals("")) {
//					p.createPatientAttributeValue(initialsA).setValue(initials);
//				} 
				if(!processNr.equals("") && !processNr.equals("0")) {
					p.createPatientAttributeValue(clinicalFileNumberA).setValue(processNr);
				} else {
					//System.err.println("ERR: No processNr for PatientNr=" + patientNr);
				}
			} else {
				System.err.println("ERR: PatientNr=" + patientNr);
			}
		}
		
		System.err.println("set:");
		for(String ss : s) {
			System.err.println(ss+",");
		}
		System.err.println("set:");
	}
	
	public void parseAnalyses(File dir) throws FileNotFoundException, UnsupportedEncodingException {
		Table patientAnalyses = Table.readTable( dir.getAbsolutePath()+File.separatorChar+"patient_analyses.csv");
	
		int PatientNrID = patientAnalyses.findColumn("NDoente");
		int SampleNrID = patientAnalyses.findColumn("NPedido");
		int SampleDateID = patientAnalyses.findColumn("Data");
		int VLID = patientAnalyses.findColumn("CargaViral");
		int VLLog10ID = patientAnalyses.findColumn("Log10");
		int CommentsID = patientAnalyses.findColumn("Comentários");
		int CD4ID = patientAnalyses.findColumn("CD4");
		
		for(int i=1; i<patientAnalyses.numRows(); i++) {
			String patientId = patientAnalyses.valueAt(PatientNrID, i).trim();
			String sampleId = patientAnalyses.valueAt(SampleNrID, i).trim();
			String sampleDate = patientAnalyses.valueAt(SampleDateID, i).trim();
			String vl = patientAnalyses.valueAt(VLID, i).trim();
			String vl_log10 = patientAnalyses.valueAt(VLLog10ID, i).trim();
			//ignore commments
			String comments = patientAnalyses.valueAt(CommentsID, i).trim();
			String cd4Count = patientAnalyses.valueAt(CD4ID, i).trim();
			
			Patient p = this.patientMap.get(patientId);
			if (p != null) {
				Date d = null;
				try {
					d = dateFormatter.parse(sampleDate);
				} catch (ParseException e) {
				}
				if (d != null) {
					if (!cd4Count.equals("") && !cd4Count.equals("0")) {
						TestResult t = p.createTestResult(StandardObjects.getGenericCD4Test());
						t.setValue(cd4Count);
						t.setTestDate(d);
						t.setSampleId(sampleId);
					}
                    if (!vl.equals("") && !vl.equals("0")) {
                        TestResult t = p.createTestResult(StandardObjects.getGenericTest(StandardObjects.getViralLoadDescription(), StandardObjects.getHiv2AGenome()));
                        t.setValue("="+ vl);
                        t.setTestDate(d);
                        t.setSampleId(sampleId);
                    }
                    if (!vl_log10.equals("") && !vl_log10.equals("0")) {
                        TestResult t = p.createTestResult(StandardObjects.getGenericTest(StandardObjects.getViralLoadLog10Description(), StandardObjects.getHiv2AGenome()));
                        t.setValue("="+ vl_log10);
                        t.setTestDate(d);
                        t.setSampleId(sampleId);
                    }

				} else {
					System.err.println("ERR: cannot parse sampleDate="
							+ sampleDate + " sampleID=" + sampleId);
				}
			} else {
				System.err
						.println("ERR: no Patient for PatientID=" + patientId);
			}
		}
	}
	
	private boolean findInstitute(String institute) {
		if(instituteTable==null) {
			try {
				instituteTable = Table.readTable(mappingPath+File.separatorChar+"institutes.list");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		for(int i = 1; i<instituteTable.numRows(); i++) {
			if(instituteTable.valueAt(0, i).trim().equals(institute)) {
				return true;
			}
		}
		return false;
	}
}
