package net.sf.regadb.io.db.ucl.hiv2;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jxl.CellType;
import jxl.DateCell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import net.sf.regadb.analysis.functions.FastaHelper;
import net.sf.regadb.analysis.functions.FastaRead;
import net.sf.regadb.db.Attribute;
import net.sf.regadb.db.AttributeGroup;
import net.sf.regadb.db.AttributeNominalValue;
import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.Event;
import net.sf.regadb.db.Genome;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.PatientEventValue;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestNominalValue;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.TestType;
import net.sf.regadb.db.Therapy;
import net.sf.regadb.db.TherapyGeneric;
import net.sf.regadb.db.TherapyGenericId;
import net.sf.regadb.db.ValueType;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.io.db.util.ConsoleLogger;
import net.sf.regadb.io.db.util.NominalAttribute;
import net.sf.regadb.io.db.util.NominalEvent;
import net.sf.regadb.io.db.util.Utils;
import net.sf.regadb.io.util.IOUtils;
import net.sf.regadb.io.util.StandardObjects;
import net.sf.regadb.service.wts.BlastAnalysis;
import net.sf.regadb.service.wts.ServiceException;
import net.sf.regadb.util.frequency.Frequency;


public class ImportUCLHiv2DB {
	//TODO
	
	//should AIDS and AIDS date be an attribute or a test
	//makes sense to add it as a test, to keep track of the patients aids evolution
	
	//dropped out test/attribute?
	
	//death information
	
	
	
	private List<String> fieldsToIgnore = new ArrayList<String>();
	
	private static SimpleDateFormat fullSimpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
	
	private static SimpleDateFormat viDateFormat = new SimpleDateFormat("yyyyMMdd");
	
	private AttributeGroup ucl_group = new AttributeGroup("UCL");
	
	private List<Attribute> regadbAttributesList;
	
	private NominalAttribute ARL_A;
	private Attribute cliniciansA;
	private NominalAttribute ARC_A;
	private Attribute enrolmentDateA;
	private Attribute countryOfOriginA;
	private Attribute genderA;
	private Attribute transmissionRiskA;
	private Attribute transmissionOtherA;
	
	//mappings 
	private HashMap<String, String> arlMappings;
	private HashMap<String, String> arcMappings;
	private HashMap<String, String> countryMappings;
	private HashMap<String, String> transmissionMappings;
	private HashMap<String, String> drugGenericsMappings;
	
	private Test seroconversion;
	private HashMap<String, TestNominalValue> seroconversionTNVMappings;
	
	private List<Event> regadbEvents;
	private NominalEvent aidsDefiningIllnessA;
	
	private List<DrugGeneric> drugGenerics;
	
	private Map<String, Test> viralLoadTests = new HashMap<String, Test>();
	
	private Map<String, Patient> patientMap = new HashMap<String, Patient>();
	
	public ImportUCLHiv2DB(String mappingBasePath, String filesDir) throws IndexOutOfBoundsException, BiffException, IOException {
		fieldsToIgnore.add("Variable");
		fieldsToIgnore.add("GENERAL DATA");
		fieldsToIgnore.add("AIDS Stage");
		fieldsToIgnore.add("Opportunistic diseases");
		fieldsToIgnore.add("Death and Drop out");
		
		arlMappings  = Utils.translationFileToHashMap(Utils.readTable(mappingBasePath + File.separatorChar + "arl.mapping"));
		arcMappings  = Utils.translationFileToHashMap(Utils.readTable(mappingBasePath + File.separatorChar + "arc.mapping"));
		countryMappings  = Utils.translationFileToHashMap(Utils.readTable(mappingBasePath + File.separatorChar + "country.mapping"));
		transmissionMappings  = Utils.translationFileToHashMap(Utils.readTable(mappingBasePath + File.separatorChar + "transmission.mapping"));
		drugGenericsMappings  = Utils.translationFileToHashMap(Utils.readTable(mappingBasePath + File.separatorChar + "genericDrugs.mapping"));
		
		regadbAttributesList = Utils.prepareRegaDBAttributes();
		countryOfOriginA = Utils.selectAttribute("Country of origin", regadbAttributesList);
        genderA = Utils.selectAttribute("Gender", regadbAttributesList);
        transmissionRiskA = Utils.selectAttribute("Transmission group", regadbAttributesList);
	
        String[] arls = new String[] { "Luxembourg", "VUB" };
		ARL_A = new NominalAttribute("ARL", 0, arls, arls);
    	ARL_A.attribute.setAttributeGroup(ucl_group);
    	
        cliniciansA = new Attribute("Clinician");
        cliniciansA.setValueType(new ValueType("string"));
        cliniciansA.setAttributeGroup(ucl_group);
        
        String [] arcs = new String[] { "UZBrussel"};
		ARC_A = new NominalAttribute("ARC/Hospital", 0, arcs, arcs);
    	ARC_A.attribute.setAttributeGroup(ucl_group);
    	
    	enrolmentDateA = new Attribute("Enrollment date");
    	enrolmentDateA.setValueType(new ValueType("date"));
    	enrolmentDateA.setAttributeGroup(ucl_group);
    	
    	transmissionOtherA = new Attribute("Transmission (Other)");
    	transmissionOtherA.setValueType(new ValueType("string"));
    	transmissionOtherA.setAttributeGroup(ucl_group);
    	
    	TestType seroconversionTT = new TestType(StandardObjects.getPatientTestObject(), "Seroconversion type");
    	seroconversionTT.setValueType(StandardObjects.getNominalValueType());
    	seroconversion = new Test(seroconversionTT, "Seroconversion type (generic)");
    	seroconversionTNVMappings = new HashMap<String, TestNominalValue>();
    	seroconversionTNVMappings.put("1", new TestNominalValue(seroconversionTT, "midpoint between last neg. and first pos. HIV-2 test"));
    	seroconversionTT.getTestNominalValues().add(seroconversionTNVMappings.get("1"));
    	seroconversionTNVMappings.put("2", new TestNominalValue(seroconversionTT, "lab evidence of seroconversion"));
    	seroconversionTT.getTestNominalValues().add(seroconversionTNVMappings.get("2"));
    	seroconversionTNVMappings.put("3", new TestNominalValue(seroconversionTT, "seroconversion illness"));
    	seroconversionTT.getTestNominalValues().add(seroconversionTNVMappings.get("3"));
    	seroconversionTNVMappings.put("4", new TestNominalValue(seroconversionTT, "first pos HIV-2 test"));
    	seroconversionTT.getTestNominalValues().add(seroconversionTNVMappings.get("4"));
	
    	regadbEvents = Utils.prepareRegaDBEvents();
    	aidsDefiningIllnessA = new NominalEvent("Aids defining illness", Utils.readTable(mappingBasePath + File.separatorChar + "ade.mapping"), Utils.selectEvent("Aids defining illness", regadbEvents));
    	
    	drugGenerics = Utils.prepareRegaDrugGenerics();
    	
    	File srcPath = new File(filesDir);
    	for(File f : srcPath.listFiles()) {
    		if(f.getAbsolutePath().endsWith(".xls")) {
    			Patient p = new Patient();
    			run(f, srcPath, p);
    			patientMap.put(p.getPatientId(), p);
    		}
    	}
    	
		//print out xml files
		IOUtils.exportPatientsXML(patientMap, srcPath.getAbsolutePath()+File.separatorChar+"patients.xml", ConsoleLogger.getInstance());
        IOUtils.exportNTXMLFromPatients(patientMap, srcPath.getAbsolutePath()+File.separatorChar+"vi.xml", ConsoleLogger.getInstance());
	}
	
	public void run(File xlsFile, File srcPath, Patient p) throws IndexOutOfBoundsException, BiffException, IOException {
		System.out.println("Handling file: " + xlsFile.getName());
		Sheet generalSheet = Workbook.getWorkbook(xlsFile).getSheet(0);
		Sheet arvTestsSheet = Workbook.getWorkbook(xlsFile).getSheet(1);
		TestResult seroConversionTR = null;
		PatientEventValue adi = null;
		
		//general sheet
		for(int r = 0; r<generalSheet.getRows(); r++) {
			String name = getCell(generalSheet, r, 1);
			if(fieldsToIgnore.contains(name))
				continue;
			String value = getCell(generalSheet, r, 2);
			if(value.equals(""))
				continue;
			
			if(name.equals("ARL")) {
				createANV(p, ARL_A.attribute, value, arlMappings);
			} else if(name.equals("Clinician")) {
				p.createPatientAttributeValue(cliniciansA).setValue(value);
			} else if(name.equals("ARC / Hospital")) {
				createANV(p, ARC_A.attribute, value, arcMappings);
			} else if(name.equals("PATIENT")) {
				p.setPatientId(value);
			} else if(name.equals("BIRTH_D")) {
				p.setBirthDate(getDate(value));
			} else if(name.equals("ENROL_D")) {
				p.createPatientAttributeValue(enrolmentDateA).setValue(getDate(value).getTime()+"");
			} else if(name.equals("GENDER")) {
                if(value.toLowerCase().equals("f")) {
                    p.createPatientAttributeValue(genderA).setAttributeNominalValue(new AttributeNominalValue(genderA, "female"));
                } else if(value.toLowerCase().equals("m")) {
                    p.createPatientAttributeValue(genderA).setAttributeNominalValue(new AttributeNominalValue(genderA, "male"));
                } else {
                    System.err.println("Not a gender: " + value);
                }
			} else if(name.equals("MODE")) {
				this.createANV(p, transmissionRiskA, value, transmissionMappings);
			} else if(name.equals("MODE_OTH")) {
				p.createPatientAttributeValue(transmissionOtherA).setValue(value);
			} else if(name.equals("ORIGIN")) {
				this.createANV(p, countryOfOriginA, value, countryMappings);
			} else if(name.equals("SEROCO_D")) {
				seroConversionTR = p.createTestResult(seroconversion);
				seroConversionTR.setTestDate(getDate(value));
			} else if(name.equals("SEROCO_M")) {
				if(seroconversionTNVMappings.get(value)==null) {
					System.err.println("Unsupported seroconversion value: " + value);
				}
				seroConversionTR.setTestNominalValue(seroconversionTNVMappings.get(value));
			} else if(name.equals("DIS_ID")) {
				if(value.equals("none"))
					continue;
				adi = Utils.handlePatientEventValue(aidsDefiningIllnessA, value, null, null, p);
			} else if(name.equals("DIS_D")) {
				adi.setStartDate(getDate(value));
			}
			else {
				System.err.println("Field is not supported: " + name);
			}
		}
		
		Genome genome = null;
		//viral isolates
		String baseName = xlsFile.getName();
		baseName = baseName.substring(0, baseName.indexOf('_'));
		for(File f : srcPath.listFiles()) {
			if(f.getName().endsWith(baseName+".txt")) {
				String fileName = f.getName();
				String date = fileName.substring(0, fileName.indexOf(baseName));
				try {
					Date d = viDateFormat.parse(date);
		            FastaRead fr = FastaHelper.readFastaFile(f, true);

		            switch (fr.status_) {
		            case Valid:
		            case ValidButFixed:
		                break;
		            case MultipleSequences:
		            case FileNotFound:
		            case Invalid:
		                System.err.println("Invalid fasta: " + f.getName());
		                continue;
		            }
		            
                    ViralIsolate vi = p.createViralIsolate();
                    vi.setSampleDate(d);
                    vi.setSampleId(fr.fastaHeader_);
                    
                    NtSequence nts = new NtSequence(vi);
                    vi.getNtSequences().add(nts);
                    nts.setNucleotides(Utils.clearNucleotides(fr.xna_));
                    nts.setLabel("Sequence 1");
                    
		            BlastAnalysis blastAnalysis = new BlastAnalysis(nts);
		            try {
		                blastAnalysis.launch();
		            } catch (ServiceException e) {
		                e.printStackTrace();
		            }
		            genome = blastAnalysis.getGenome();
				} catch (ParseException e) {
					System.err.println("Cannot parse VI date: " + date);
				}
			}
		}
		
		//arv+measurement form
		//TODO stop reason mapping (is the nominal list sufficient?)
		boolean arv = false;
		boolean tests = false;
		for(int r = 0; r<arvTestsSheet.getRows(); r++) {
			if(getCell(arvTestsSheet, r, 0).equals("ARV")) {
				arv = true;
				continue;
			}
			
			if(getCell(arvTestsSheet, r, 0).equals("Date of visit")) {
				arv = false;
				tests = true;
				continue;
			}
			
			if(arv) {
				parseARV(getCell(arvTestsSheet, r, 0), getCell(arvTestsSheet, r, 1), getCell(arvTestsSheet, r, 2), p);
			}
			if(tests && !getCell(arvTestsSheet, r, 0).equals("")) {
				parseTests(getDate(getCell(arvTestsSheet, r, 0)), getCell(arvTestsSheet, r, 1), getCell(arvTestsSheet, r, 4), getCell(arvTestsSheet, r, 3), p, genome);
			}
		}
	}
	
	public void parseTests(Date d, String cd4, String vl, String vl_limit, Patient p, Genome genome) {
		//TODO
		//We check for the genome, however, if no sequence is provided which genome should we choose?
		
		if(!cd4.equals("")) {
	        TestResult t = p.createTestResult(StandardObjects.getGenericCD4Test());
	        try {
	            int cd4I = Integer.parseInt(cd4);
	        } catch(NumberFormatException nfe) {
	            throw new RuntimeException("Illegal CD4:" + cd4);
	        }
	        
	        t.setValue(cd4);
	        t.setTestDate(d);
		}
		
		if(!vl.equals("")) {
			Test vlTest = null;
			if(vl_limit.equals("")) {
				vlTest = StandardObjects.getGenericTest(StandardObjects.getViralLoadDescription(), genome);
			} else {
				TestType tt = StandardObjects.getGenericTest(StandardObjects.getViralLoadDescription(), genome).getTestType();
				vlTest = viralLoadTests.get(vl_limit);
				if(vlTest == null) {
					vlTest = new Test(tt, "Viral Load (limit="+vl_limit+")");
					viralLoadTests.put(vl_limit, vlTest);
				}
			}
			
			String changed_vl = null;
			if(!"><=".contains(vl.charAt(0)+"")) {
				changed_vl = "="+vl;
			} else {
				changed_vl = vl;
			}
			
			try {
				Integer.parseInt(vl.substring(1));
			} catch (NumberFormatException e) {
				System.err.println("Cannot parse viral load: " + vl);
			}
			
            TestResult t = p.createTestResult(StandardObjects.getGenericTest(StandardObjects.getViralLoadDescription(), genome));
            t.setValue(changed_vl);
            t.setTestDate(d);
		}
	}
	
	public void parseARV(String drugs, String startDate, String stopDate, Patient p) {
		if(drugs.equals(""))
			return;
		
		List<String> elements = new ArrayList<String>();
		StringBuilder buffer = new StringBuilder();
		String delims = "+-, ";
		for(int i = 0; i<drugs.length(); i++) {
			char c = drugs.charAt(i);
			if(delims.contains(c+"") && buffer.length()>0) {
				elements.add(buffer.toString().trim());
				buffer.delete(0, buffer.length());
			} else {
				buffer.append(c);
			}
		}
		if(buffer.length()>0)
			elements.add(buffer.toString().trim());
	
		//couple ritonavir boosts to the appropriate drug
		for(int i = 0; i<elements.size(); i++) {
			if(elements.get(i).equals("RTV/b")) {
				if(i-1>-1) {
					elements.set(i-1, elements.get(i-1)+"/r");
				}
			}
		}
		
		//remove the boosts as a drug
		Iterator<String> i = elements.iterator();
		String former = null;
		while(i.hasNext()) {
			String e = i.next();
			if(e.equals("RTV/b")) {
				i.remove();
			}
		}
		
		if(elements.size()>0) {
			Therapy t = p.createTherapy(getDate(startDate));
			if(!stopDate.equals(""))
				t.setStopDate(getDate(stopDate));
			
			for(String e : elements) {
				DrugGeneric dg = getGenericDrug(e);
				if(dg!=null) {
		    		TherapyGeneric tg = new TherapyGeneric(new TherapyGenericId(t, dg),
	                        1.0, 
	                        false,
	                        false, 
	                        (long)Frequency.DAYS.getSeconds());
		    		t.getTherapyGenerics().add(tg);
				}
			}	
		}
	}
	
	public DrugGeneric getGenericDrug(String drugName) {
		//TODO drugs annotated as boosted do not exist in boosted form?
		//or do we misinterpret the boosted notation
		
		for(DrugGeneric dg : drugGenerics) {
			if(dg.getGenericId().equals(drugName)) 
				return dg;
		}
		
		String drugNameM = drugGenericsMappings.get(drugName);
		
		for(DrugGeneric dg : drugGenerics) {
			if(dg.getGenericId().equals(drugNameM)) 
				return dg;
		}
		
		System.err.println("Drug generic cannot be mapped: " + drugName);
		
		return null;
	}
	
	public void createANV(Patient p, Attribute a, String value, HashMap<String, String> mappings) {
		for(AttributeNominalValue anv : a.getAttributeNominalValues()) {
			if(anv.getValue().equals(value)) {
				p.createPatientAttributeValue(a).setAttributeNominalValue(anv);
				return;
			}
		}
		
		value = mappings.get(value);
		
		for(AttributeNominalValue anv : a.getAttributeNominalValues()) {
			if(anv.getValue().equals(value)) {
				p.createPatientAttributeValue(a).setAttributeNominalValue(anv);
				return;
			}
		}
		
		System.err.println("ANV not supported: " + a.getName() + " - " + value);
	}
	
	public Date getDate(String value) {
		try {
			return fullSimpleDateFormat.parse(value);
		} catch (ParseException e) {
			System.err.println("Cannot parse date: " + value);
			return null;
		}
	}
	
	public String getCell(Sheet sheet, int row, int col) {
		if(sheet.getCell(col, row).getType() == CellType.DATE) {
			DateCell dc = ((DateCell)sheet.getCell(col, row));
			return fullSimpleDateFormat.format(dc.getDate());
		} else {
			return sheet.getCell(col, row).getContents().trim();
		}
	}
	
	public static void main(String [] args) {
		try {
			ImportUCLHiv2DB importUCL = new ImportUCLHiv2DB("/home/plibin0/myWorkspace-mob-lisbon/regadb-io-db/src/net/sf/regadb/io/db/ucl/hiv2/mappings", "/home/plibin0/import/ucl/");
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
		} catch (BiffException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
