package net.sf.regadb.io.db.uzbrussel;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.regadb.csv.Table;
import net.sf.regadb.db.Attribute;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.PatientAttributeValue;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.io.db.util.ConsoleLogger;
import net.sf.regadb.io.db.util.NominalAttribute;
import net.sf.regadb.io.db.util.NominalTestMapper;
import net.sf.regadb.io.db.util.Utils;
import net.sf.regadb.io.util.StandardObjects;
import net.sf.regadb.io.util.WivObjects;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

public class ParseConsultDB {
    private static DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
    
    private String baseDir_;
    private Map<Integer, Patient> patients_;
    private ParseIds parseIds_;
    
    private NominalAttribute genderNominal_ = new NominalAttribute("Gender", -1, new String[] { "M", "F" },
            new String[] { "male", "female" } );
    private NominalAttribute countryOfOriginA;
    private NominalAttribute geographicOriginA;
    private NominalAttribute transmissionA;
    
    private NominalTestMapper therapyAdherenceT;
    
    private List<Attribute> regadbAttributes_;
    
    private Attribute patientNoAttribute;
    
    private Set<String> setset = new HashSet<String>();
    
    private Map<Integer, String> codepat_;
        
    public ParseConsultDB(String baseDir, Map<Integer, Patient> patients, ParseIds parseIds, String mappingBasePath, Map<Integer, String> codepat) {
        baseDir_ = baseDir;
        patients_ = patients;
        parseIds_ = parseIds;
        
        ParseMedication.mappingPath = mappingBasePath;
        ParseMedication.init();
    
        regadbAttributes_ = Utils.prepareRegaDBAttributes();
            
        genderNominal_.attribute.setAttributeGroup(StandardObjects.getGenderAttribute().getAttributeGroup());
        
        Table countryTable = Utils.readTable(mappingBasePath + File.separatorChar + "countryOfOrigin.mapping");
        countryOfOriginA = new NominalAttribute("Country of origin", countryTable, StandardObjects.getDemographicsAttributeGroup(), Utils.selectAttribute("Country of origin", regadbAttributes_), false);
        
        Table geoTable = Utils.readTable(mappingBasePath + File.separatorChar + "geographicOrigin.mapping");
        geographicOriginA = new NominalAttribute("Geographic origin", geoTable, StandardObjects.getGeoGraphicOriginAttribute().getAttributeGroup(), Utils.selectAttribute(StandardObjects.getGeoGraphicOriginAttribute().getName(), regadbAttributes_), false);
        
        Table transmissionTable = Utils.readTable(mappingBasePath + File.separatorChar + "transmissionRisk.mapping");
        transmissionA = new NominalAttribute("Transmission group", transmissionTable, StandardObjects.getTransmissionGroupAttribute().getAttributeGroup(), Utils.selectAttribute(StandardObjects.getTransmissionGroupAttribute().getName(), regadbAttributes_));
        
        therapyAdherenceT = new NominalTestMapper(mappingBasePath + File.separatorChar + "therapyAdherence.mapping", Items.getGenerichivTherapyAdherence());
    
        codepat_ = codepat;
        
        patientNoAttribute = new Attribute();
        patientNoAttribute.setName("PatientNo");
        patientNoAttribute.setAttributeGroup(StandardObjects.getClinicalAttributeGroup());
        patientNoAttribute.setValueType(StandardObjects.getStringValueType());
    }
    
    public void exec() {
        File consultDBXml = new File(baseDir_+"emd" + File.separatorChar + "patientdb.xml");
        
        SAXBuilder builder = new SAXBuilder();
        Document doc = null;
        try {
            doc = builder.build(consultDBXml);
        } catch (JDOMException e) {
            e.printStackTrace();
        	ConsoleLogger.getInstance().logError("Problem loading consult db xml file:" + consultDBXml.getAbsolutePath());
        } catch (IOException e) {
        	e.printStackTrace();
        	ConsoleLogger.getInstance().logError("Problem loading consult db xml file:" + consultDBXml.getAbsolutePath());
        }

        Element root = doc.getRootElement();

        List patientEls = root.getChildren("Patient");
        for(Object patientEl : patientEls) {
            parsePatient((Element)patientEl);
        }
        
        for(String s : setset) {
            ConsoleLogger.getInstance().logWarning("Setset: " + s);
        }
        
        for(String s : ParseMedication.notParsableMeds) {
            ConsoleLogger.getInstance().logWarning("Cannot parse drug: " + s);
        }
    }
    
    private void parsePatient(Element patientEl) {
        String consultId = text(patientEl, "PatientNo");
        Integer id = parseIds_.getPatientId(consultId);
        
        if(id != null) {
        	Patient p = patients_.get(id);
        	if(p==null) {
        		p = new Patient();
                p.setPatientId(id+"");
                patients_.put(id, p);
                
                p.createPatientAttributeValue(Items.getPatCodeAttribute()).setValue(codepat_.get(id));
        	}
        	
            String birthDate = text(patientEl, "BirthDate");
            String deathDate = text(patientEl, "DeathDate");
            String sex = text(patientEl, "Sex");
            String origin = text(patientEl, "Origin");
            String nationality = text(patientEl, "Nationality");
            String transmission = text(patientEl, "Transmission");
            String adherence = text(patientEl, "TherapyAdherence");
            String followup = text(patientEl, "Followup");
            
            if(followup!=null) {
                PatientAttributeValue pav = Utils.getAttributeValue("FOLLOW-UP", p); 
            	if(pav==null) {
	            	if(followup.equals("intern")) {
	            		WivObjects.createPatientAttributeNominalValue("FOLLOW-UP", '1', p);
	            	} else if(followup.equals("extern")) {
	            		WivObjects.createPatientAttributeNominalValue("FOLLOW-UP", '3', p);
	            	} else {
	            		ConsoleLogger.getInstance().logError("Illegal followup information for patient: " + p.getPatientId());
	            	}
            	} else {
            		if(followup.equals("intern"))
            			pav.setAttributeNominalValue(WivObjects.getANVFromAbbrev(pav.getAttribute(), "1"));
            	}
            } else {
            	ConsoleLogger.getInstance().logError("No followup information for patient: " + p.getPatientId());
            }
            
            if(consultId != null){
            	p.addPatientAttributeValue(Utils.createPatientAttributeValue(patientNoAttribute, consultId));
            }
            if(birthDate!=null) {
                try {
                    Utils.setBirthDate(p, dateFormatter.parse(birthDate));
                } catch (ParseException e) {
                    ConsoleLogger.getInstance().logError("Cannot parse patient's birthdate: " + birthDate);
                }
            }
            if(deathDate!=null) {
                try {
                    Utils.setDeathDate(p, dateFormatter.parse(deathDate));
                } catch (ParseException e) {
                    ConsoleLogger.getInstance().logError("Cannot parse patient's deathdate: " + deathDate);
                }
            }
            if(sex!=null) {
                sex = sex.toUpperCase().trim();
                if(Utils.getAttributeValue(genderNominal_.attribute, p)==null)
                	Utils.createPAV(genderNominal_, sex, p);
            }
            if(origin!=null) {
                Utils.addCountryOrGeographicOrigin(countryOfOriginA, geographicOriginA, origin.replaceAll("\"",""), p);
            }
            if(nationality!=null) {
            	//TODO
                //setset.add(nationality);
            }
            if(transmission!=null) {
                Utils.createPAV(transmissionA, transmission, p);
            }
            if(adherence!=null) {
                if(therapyAdherenceT.createTestResult(p, adherence)==null) {
                    ConsoleLogger.getInstance().logError("Cannot map adherence value: " + adherence);
                }
            }
            
            Element testsEl = patientEl.getChild("Tests");
            if(testsEl!=null) {
                for(Object analysis :  testsEl.getChildren("Analysis")) {
                    Element analysisEl = (Element)analysis;
                    parseAnalysis(analysisEl, p);
                }
            }
            
            Element contactsEl = patientEl.getChild("Contacts");
            if(contactsEl!=null) {
            	for(Object contact : contactsEl.getChildren("Contact")) {
            		Element contactE = (Element)contact;
            		String contactDateS = contactE.getChildText("ContactDate");
            		Date contactDate = null;
					try {
						contactDate = dateFormatter.parse(contactDateS);
					} catch (ParseException e) {
						ConsoleLogger.getInstance().logError("Cannot parse contact date: " + contactDateS);
					}
            		
                	TestResult t = p.createTestResult(StandardObjects.getContactTest());
                	t.setValue(contactDate.getTime()+"");
                	t.setTestDate(contactDate);
            	}
            }
            
            Element therapyEl = patientEl.getChild("Therapy");
            if(therapyEl!=null)
                ParseMedication.parseTherapy(therapyEl, p);
        } else {
            ConsoleLogger.getInstance().logWarning("No patient id found for:" + consultId);
        }
    }
    
    private void parseAnalysis(Element analysisEl, Patient p) {
        String collectionDate = text(analysisEl, "CollectionDate");
        String type = analysisEl.getAttributeValue("Parameter");
        String value = text(analysisEl, "Value");
        String unit = text(analysisEl, "Unit");
        
        TestResult tr = getTest(type, unit, value, p);
        if(tr!=null) {
            try {
                tr.setTestDate(dateFormatter.parse(collectionDate));
            } catch (ParseException e) {
                ConsoleLogger.getInstance().logError("Cannot parse test date: " + collectionDate);
            }
        }
    }
    
    private TestResult getTest(String type, String unit, String value, Patient p) {
        type = type.trim();
        if(unit!=null)
            unit = unit.trim();
        else
            unit = "";
        
        if(type.equals("ABSCD4")) {
            try {
                Double.parseDouble(value);
            } catch(NumberFormatException nfe) {
                ConsoleLogger.getInstance().logError("Cannot parse CD4 value: " + value);
                return null;
            }
            TestResult tr = p.createTestResult(StandardObjects.getGenericCD4Test());
            tr.setValue(value);
            return tr;
        } if(type.equals("CD4")) {
            try {
                Double.parseDouble(value);
            } catch(NumberFormatException nfe) {
                ConsoleLogger.getInstance().logError("Cannot parse CD4% value: " + value);
                return null;
            }
            TestResult tr = p.createTestResult(StandardObjects.getGenericCD4PercentageTest());
            tr.setValue(value);
            return tr;
        } else if(type.equals("ABSCD8")) {
            try {
                Double.parseDouble(value);
            } catch(NumberFormatException nfe) {
                ConsoleLogger.getInstance().logError("Cannot parse CD8 value: " + value);
                return null;
            }
            TestResult tr = p.createTestResult(StandardObjects.getGenericCD8Test());
            tr.setValue(value);
            return tr;
        } else if(type.equals("CD8")) {
            try {
                Double.parseDouble(value);
            } catch(NumberFormatException nfe) {
                ConsoleLogger.getInstance().logError("Cannot parse CD8% value: " + value);
                return null;
            }
            TestResult tr = p.createTestResult(StandardObjects.getGenericCD8PercentageTest());
            tr.setValue(value);
            return tr;
        } else if(type.equals("H2VL") || type.equals("HIVVL")) {
            String val = parseViralLoad(value);
            if(val==null)
            	return null;
            TestResult tr = p.createTestResult(StandardObjects.getGenericHiv1ViralLoadTest());
            tr.setValue(val);
            return tr;
        } else if(type.equals("VLLOGlog10") || type.equals("H2VLLlog10") || type.equals("VLLOG") || type.equals("H2VLL")) {
            String val = parseViralLoad(value);
            if(val==null)
            	return null;
            TestResult tr = p.createTestResult(StandardObjects.getGenericHiv1ViralLoadLog10Test());
            tr.setValue(val);
            return tr;
        } else if(type.equals("BHIVC")) {
            TestResult tr = p.createTestResult(WivObjects.getGenericwivConfirmation());
            if(value.trim().startsWith("NH1+") || value.trim().startsWith("FH1+")) {
                tr.setTestNominalValue(Utils.getNominalValue(WivObjects.getGenericwivConfirmation().getTestType(), "HIV 1"));
            } else if(value.trim().startsWith("NH2+") || value.trim().startsWith("FH2+")) {
                tr.setTestNominalValue(Utils.getNominalValue(WivObjects.getGenericwivConfirmation().getTestType(), "HIV 2"));
            } else if(value.trim().equals("N+12P") || value.trim().equals("F+12P")) {
                tr.setTestNominalValue(Utils.getNominalValue(WivObjects.getGenericwivConfirmation().getTestType(), "HIV Undetermined"));
            } else if(value.trim().equals("ZZ")) {
                tr.setTestNominalValue(Utils.getNominalValue(WivObjects.getGenericwivConfirmation().getTestType(), "Not performed"));
            } else {
                ConsoleLogger.getInstance().logWarning("Cannot parse BHIVC value: " + value);
            }
            return tr;
        }

        setset.add(type);
        
        return null;
    }
    
    public static String parseViralLoad(String value_orig) {
        String value = value_orig;
        
        if(value==null || "".equals(value)) {
            return null;
        }
        
        String val = null;
        
        value = value.replace(',', '.');
        
        if(Character.isDigit(value.charAt(0))) {
            value = "=" + value;
        }
        if(value.charAt(0)=='>' || value.charAt(0)=='<' || value.charAt(0)=='=') {
            try {
                Double.parseDouble(value.substring(1));
                val = value;
            } catch(NumberFormatException nfe) {
                ConsoleLogger.getInstance().logError("Cannot parse Viral Load value: " + value);
                return null;
            }
        }
        
        return val;
    }
    
    private String text(Element el, String name) {
        if(el.getChild(name)==null)
            return null;
        String toReturn = el.getChild(name).getText();
        el.getChild(name).detach();
        return toReturn != null ? toReturn.trim() : null;
    }
}
