package net.sf.regadb.io.db.uzbrussel;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import net.sf.regadb.csv.Table;
import net.sf.regadb.db.Attribute;
import net.sf.regadb.db.AttributeGroup;
import net.sf.regadb.db.AttributeNominalValue;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.PatientAttributeValue;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestNominalValue;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.io.db.util.ConsoleLogger;
import net.sf.regadb.io.db.util.NominalAttribute;
import net.sf.regadb.io.db.util.Utils;
import net.sf.regadb.io.util.StandardObjects;
import net.sf.regadb.io.util.WivObjects;

public class ParseConfirmation {
    private static DateFormat dateFormatter1 = new SimpleDateFormat("yyyyMMdd");
    private static DateFormat dateFormatter2 = new SimpleDateFormat("yyMMdd");
    private static DateFormat yearFormat = new SimpleDateFormat("yyyy");
    
    private String basePath_;
    private ParseIds parseIds_;
    private Map<Integer, Patient> patients_;
    
    private Map<Patient,Set<String>> patientAttributes_;
    
    private Table patcodesToIgnore;
    
    private NominalAttribute genderNominal_ = new NominalAttribute("Gender", -1, new String[] { "M", "F" },
            new String[] { "male", "female" } );
    
    private AttributeGroup regadbAttributeGroup_ = new AttributeGroup("RegaDB");
    private Attribute commentAttribute = new Attribute(StandardObjects.getStringValueType(),regadbAttributeGroup_,"Comment",new TreeSet<AttributeNominalValue>());
    
    public ParseConfirmation(String basePath, ParseIds parseIds, Map<Integer, Patient> patients) {
        basePath_ = basePath;
        parseIds_ = parseIds;
        patients_ = patients;
        
        genderNominal_.attribute.setAttributeGroup(regadbAttributeGroup_);
        
        patcodesToIgnore = Utils.readTable(basePath_+ File.separatorChar + "emd" + File.separatorChar + "patcodesToIgnore.csv");
        
        patientAttributes_ = new HashMap<Patient,Set<String>>();
    }
    
    public void exec() {
        Workbook wb = null;
        try {
            wb = Workbook.getWorkbook(new File(basePath_+File.separatorChar+"labo" + File.separatorChar + "conf" + File.separatorChar + "HIV_CONFI.XLS" ));
        } catch (BiffException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        System.err.println("CONFIRMATION EPIDEM=================================");
        
          parseSheet(wb.getSheet(0), this.dateFormatter1);
          parseSheet(wb.getSheet(1), this.dateFormatter2);
       
        System.err.println("CONFIRMATION EPIDEM=================================");
    }
    
    public boolean canIgnorePatCode(String patCode) {
        for(int i = 0; i<patcodesToIgnore.numRows(); i++) {
            if(patcodesToIgnore.valueAt(0, i).trim().equals(patCode.trim())) {
                return true;
            }
        }
        
        for(int i = 0; i<patcodesToIgnore.numRows(); i++) {
            if(patcodesToIgnore.valueAt(0, i).trim().equals("19" + patCode.trim())) {
                return true;
            }
        }
        
        return false;
    }
    
    private void parseSheet(Sheet sheet, DateFormat df) {
        Map<String, Integer> colMapping = new HashMap<String, Integer>();
        for (int i = 1 ; i < sheet.getRows() ; i++) {
            String dossiernummer = getValue(i, "dossiernummer", sheet, colMapping);
            String code_pat = getValue(i, "CODE_PAT", sheet, colMapping);
            Patient p = null;
            if(dossiernummer!=null) {
                Integer id = parseIds_.getPatientId(dossiernummer);
                if(id!=null) {
                    p = patients_.get(id);
                } else {
                    ConsoleLogger.getInstance().logWarning("Cannot retrieve patientConsultId from : " + dossiernummer);
                }
            } else {
                Integer id = parseIds_.getPatientIdForPatcode("19" + code_pat);
                if(id==null) {
                    parseIds_.getPatientIdForPatcode(code_pat);
                }
                if(id != null) {
                    p = patients_.get(id);
                } else {
                    if(canIgnorePatCode(code_pat) || canIgnorePatCode("19" + code_pat)) {
                        
                    } else {
                        ConsoleLogger.getInstance().logWarning("Cannot retrieve patientId for patcode (confirmation): " + code_pat);
                    }
               }
            }
            if(p!=null) {
            
            String date_test = getValue(i, "DATE_TEST", sheet, colMapping);
            Date testDate = null;
            try {
                testDate = df.parse(date_test);
                //handleWivDateAttribute("DATE_TEST", date_test, p, df);
			} catch (ParseException e1) {
				e1.printStackTrace();
			}
            
            String birth_date = getValue(i, "BIRTH_DATE", sheet, colMapping);
            if(!"".equals(birth_date)) {
                Date birthDate = null;
                try {
                    birthDate = df.parse(birth_date);
                } catch (ParseException e) {
                    ConsoleLogger.getInstance().logWarning("Cannot parse birthDate for Patient with id: "+p.getPatientId() + "(" +birth_date+ ")");
                }
                if(birthDate!=null) {
                    if(p.getBirthDate()==null) {
                        p.setBirthDate(birthDate);
                    } else {
                        if(!p.getBirthDate().equals(birthDate)) {
                            ConsoleLogger.getInstance().logWarning("Confirmation birthDate and original birthDate are not the same for Patient with id: "+p.getPatientId() + "(pat code =" + code_pat + ")");
                        }
                    }
                }
            }
            String sex = getValue(i, "SEX", sheet, colMapping);
            if(!"".equals(sex)) {
                PatientAttributeValue pav = getPAV("Gender", p);
                if(pav==null) {
                	Utils.createPAV(genderNominal_, sex.toUpperCase(), p);
                } else {
                	AttributeNominalValue gnv = genderNominal_.nominalValueMap.get(sex.toUpperCase());
                    if(!pav.getAttributeNominalValue().getValue().equals(gnv.getValue())) {
                        ConsoleLogger.getInstance().logWarning("Confirmation sex and original sex are not the same for Patient with id: "+p.getPatientId());
                    }
                }
            }
            
            String hivtype = getValue(i, "HIVTYPE", sheet, colMapping);
            if(hivtype!=null && !hivtype.equals("")) {
                TestNominalValue nominalValue = null;
                if(hivtype.equals("1")) {
                    nominalValue = Utils.getNominalValue(WivObjects.getGenericwivConfirmation().getTestType(), "HIV 1");
                } else if (hivtype.equals("2")) {
                    nominalValue = Utils.getNominalValue(WivObjects.getGenericwivConfirmation().getTestType(), "HIV 2");
                } else if (hivtype.equals("3")) {
                    nominalValue = Utils.getNominalValue(WivObjects.getGenericwivConfirmation().getTestType(), "HIV 1/2 Coinfection");
                } else if (hivtype.equals("4")) {
                    nominalValue = Utils.getNominalValue(WivObjects.getGenericwivConfirmation().getTestType(), "HIV Undetermined");
                } else {
                    System.err.println("Cannot parse HIVTYPE: "+hivtype);
                }
                
                if(nominalValue!=null) {
                    setTest(WivObjects.getGenericwivConfirmation(), nominalValue, testDate, p);
                }
            }
            
            String comment = getValue(i, "OPMERKING", sheet, colMapping);
            if(comment != null && comment.length() > 0){
                (p.createPatientAttributeValue(commentAttribute)).setValue(comment);
            }
            
            String ref_labo = getValue(i, "REF_LABO", sheet, colMapping);
                handleWIVStringAttribute("REF_LABO", ref_labo, p);

            String virload = getValue(i, "VIRLOAD", sheet, colMapping);
                setTest(StandardObjects.getGenericHiv1ViralLoadTest(), ParseConsultDB.parseViralLoad(virload), testDate, p);
            
            String nation = getValue(i, "NATION", sheet, colMapping);
            	handleWIVCountry("NATION", nation, p);
            String country = getValue(i, "COUNTRY", sheet, colMapping);
            	handleWIVCountry("COUNTRY", country, p);
            
            String resid_b = getValue(i, "RESID_B", sheet, colMapping);
                handleWIVNumericAttribute("RESID_B", resid_b, p, 2);
                
            String origin = getValue(i, "ORIGIN", sheet, colMapping);
        		if(origin!=null) 
        			handleWIVCountry("ORIGIN", origin, p);
            
            String arrival_b = getValue(i, "ARRIVAL_B", sheet, colMapping);
                if(arrival_b!=null)
                    handleWivDateAttribute("ARRIVAL_B", arrival_b, p, yearFormat);
            
            String sexcontact = getValue(i, "SEXCONTACT", sheet, colMapping);
                handleWIVNominalAttribute("SEXCONTACT", sexcontact, p);
            
            String sexpartner = getValue(i, "SEXPARTNER", sheet, colMapping);
                handleWIVMultipleNominalAttribute("SEXPARTNER", sexpartner, p);
            
            String natpartner = getValue(i, "NATPARTNER", sheet, colMapping);
            	handleWIVCountry("NATPARTNER", natpartner, p);
            
            String bloodborne = getValue(i, "BLOODBORNE", sheet, colMapping);
                handleWIVNominalAttribute("BLOODBORNE", bloodborne, p);
                
            String yeartransf = getValue(i, "YEARTRANSF", sheet, colMapping);
                handleWivDateAttribute("YEARTRANSF", yeartransf, p, yearFormat);
                
            String trancountr = getValue(i, "TRANCOUNTR", sheet, colMapping);
            	handleWIVCountry("TRANCOUNTR", trancountr, p);
                
            String child = getValue(i, "CHILD", sheet, colMapping);
            if(!child.equals("C"))
                handleWIVNominalAttribute("CHILD", child, p);
                
            String profrisk = getValue(i, "PROFRISK", sheet, colMapping);
                handleWIVNominalAttribute("PROFRISK", profrisk, p);
            
            String probyear = getValue(i, "PROBYEAR", sheet, colMapping);
                handleWivDateAttribute("PROBYEAR", probyear, p, yearFormat);
            
            String probcountr = getValue(i, "PROBCOUNTR", sheet, colMapping);
            	handleWIVCountry("PROBCOUNTR", probcountr, p);
            
            String lympho = getValue(i, "LYMPHO", sheet, colMapping);
                if(!"".equals(lympho) && !"U".equals(lympho.trim())) {
                    try{ 
                        setTest(StandardObjects.getGenericCD4Test(), Double.parseDouble(lympho)+"", testDate, p);
                    } catch (NumberFormatException nfe) {
                        ConsoleLogger.getInstance().logWarning("Cannot parse confirmations CD4 value: " + lympho);
                    }
                }
            
            String stad_clin = getValue(i, "STAD_CLIN", sheet, colMapping);
                handleWIVNominalAttribute("STAD_CLIN", stad_clin, p);
            String reasontest = getValue(i, "REASONTEST", sheet, colMapping);
                handleWIVNominalAttribute("REASONTEST", reasontest, p);
                
            String form_out = getValue(i, "FORM_OUT", sheet, colMapping);
                handleWivDateAttribute("FORM_OUT", form_out, p, df);

            String form_in = getValue(i, "FORM_IN", sheet, colMapping);
                handleWivDateAttribute("FORM_IN", form_in, p, df);
            
            //String labo = getValue(i, "LABO", sheet, colMapping);
            //String opmerking = getValue(i, "OPMERKING", sheet, colMapping);
            }
        }
    }
    
    public void handleWivDateAttribute(String attributeName, String value, Patient p, DateFormat df) {
        if(!"".equals(value) && !"U".equals(value)) {
            try {
                Date d = df.parse(value);
                handleWIVAttribute(attributeName, d.getTime()+"", p);
            } catch (ParseException e) {
                ConsoleLogger.getInstance().logWarning("Cannot parse date " + attributeName + " value:" + value);
            }
        }
    }
    
    public void setTest(Test test, String value, Date date, Patient p) {
        if(value==null || "".equals(value)) {
            return;
        }
        for(TestResult tr : p.getTestResults()) {
            if(tr.getTest().getTestType().getDescription().equals(test.getTestType().getDescription())) {
                return;
            }
        }
        if(date==null) {
        	ConsoleLogger.getInstance().logWarning("No date for confirmation test " + p.getPatientId());
        	return;
        }
        TestResult tr = p.createTestResult(test);
        tr.setValue(value);
        tr.setTestDate(date);
    }
    
    public void setTest(Test test, TestNominalValue tnv, Date date, Patient p) {
        for(TestResult tr : p.getTestResults()) {
            if(tr.getTest().getTestType().getDescription().equals(test.getTestType().getDescription())) {
                return;
            }
        }
        if(date==null) {
            ConsoleLogger.getInstance().logWarning("No date for confirmation test " + p.getPatientId());
            return;
        }
        TestResult tr = p.createTestResult(test);
        tr.setTestNominalValue(tnv);
        tr.setTestDate(date);
    }
    
    public void handleWIVCountry(String attributeName, String value, Patient p) {
        if("".equals(value)) {
            return;
        }
        if(value==null) {
        	System.err.println("---------------->" + attributeName + " - " + value);
        }
        
        if(patientPutAttribute(p, attributeName)){  //check duplicate
            if(WivObjects.createCountryPANV(attributeName, value.toUpperCase(), p)==null) {
            	ConsoleLogger.getInstance().logWarning("Cannot handle WIV country attribute - attributeNominalVal: " + attributeName + " - " + value + " (for Patient " + p.getPatientId() +")" );
            }
        }
    }
    
    public void handleWIVStringAttribute(String attributeName, String value, Patient p) {
        if(value != null && !"".equals(value) && !value.toUpperCase().equals("U")) {
            handleWIVAttribute(attributeName, value, p);
        }
    }
    
    public void handleWIVNumericAttribute(String attributeName, String value, Patient p, int length) {
        if(!"".equals(value)) {
            if(value.toUpperCase().equals("U")) {
                
            } else if(value.length()==length) {
                try {
                int i_value = Integer.parseInt(value);
                handleWIVAttribute(attributeName, value, p);
                } catch(NumberFormatException nfe) {
                    ConsoleLogger.getInstance().logWarning("No valid " + attributeName + " value: " + value);
                }
            } else {
                ConsoleLogger.getInstance().logWarning("No valid " + attributeName + " value: " + value);
            }
        }
    }
    
    public void handleWIVMultipleNominalAttribute(String attributeName, String attributeNominalValue, Patient p) {
        if(attributeNominalValue.contains("/")) {
            StringTokenizer st = new StringTokenizer(attributeNominalValue, "/");
            while(st.hasMoreElements()) {
                handleWIVNominalAttribute(attributeName, st.nextToken(), p,true);
            }
        } else {
            handleWIVNominalAttribute(attributeName, attributeNominalValue, p,true);
        }
    }
    
    public void handleWIVNominalAttribute(String attributeName, String attributeNominalValue, Patient p) {
        handleWIVNominalAttribute(attributeName, attributeNominalValue, p, false);
    }
    
    public void handleWIVNominalAttribute(String attributeName, String attributeNominalValue, Patient p, boolean multiple) {
        if("".equals(attributeNominalValue)) {
            return;
        }
        if(attributeNominalValue.length()>1) {
            ConsoleLogger.getInstance().logWarning("Cannot handle WIV attribute - attributeNominalVal: " + attributeName + " - " + attributeNominalValue + " (for Patient " + p.getPatientId() +")" );
            return;
        }
        if(WivObjects.createPatientAttributeNominalValue(attributeName, attributeNominalValue.toUpperCase().charAt(0), p)==null) {
            ConsoleLogger.getInstance().logWarning("Cannot handle WIV attribute - attributeNominalVal: " + attributeName + " - " + attributeNominalValue + " (for Patient " + p.getPatientId() +")" );
        }
    }
    
    public void handleWIVAttribute(String attributeName, String value, Patient p){
        handleWIVAttribute(attributeName, value, p, false);
    }
    
    public void handleWIVAttribute(String attributeName, String value, Patient p, boolean multiple) {
        if(!multiple){
            if(!patientPutAttribute(p,attributeName))
                return; //already exists
        }
        
        if(WivObjects.createPatientAttributeValue(attributeName, value, p)==null) {
            ConsoleLogger.getInstance().logWarning("Cannot handle WIV attribute - value: " + attributeName + " - " + value + " (for Patient " + p.getPatientId() +")" );
        }
    }
    
    public PatientAttributeValue getPAV(String attributeName, Patient p) {
        try {
    	for(PatientAttributeValue pav : p.getPatientAttributeValues()) {
            if(pav.getAttribute().getName().equals(attributeName)) {
                return pav;
            }
        } }
        catch(Exception e) {
        	System.err.println("test");
        }
        return null;
    }
    
    public String getValue(int row, String colName, Sheet sheet, Map<String, Integer> colMapping) { 
        Integer col = colMapping.get(colName);
        if(col==null) {
            Cell[] rowCells = sheet.getRow(0);
            for(Cell c : rowCells) {
                if(c.getContents().equals(colName)) {
                    col = c.getColumn(); 
                }
            }
            if(col==null) {
                return null;
            }
        }
        return sheet.getCell(col, row).getContents().trim();
    }
    
    public boolean patientHasAttribute(Patient p, String attributeName){
        Set<String> set = patientAttributes_.get(p);
        if(set == null) return false;
        return set.contains(attributeName);
    }
    
    public boolean patientPutAttribute(Patient p, String attributeName){
        Set<String> set = patientAttributes_.get(p);
        if(set == null){
            set = new HashSet<String>();
            patientAttributes_.put(p, set);
        }
        return set.add(attributeName);
    }
}
