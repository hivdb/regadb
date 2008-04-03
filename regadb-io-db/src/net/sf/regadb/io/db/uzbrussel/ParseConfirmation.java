package net.sf.regadb.io.db.uzbrussel;

import java.io.File;
import java.io.FileWriter;
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

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import net.sf.regadb.db.AttributeGroup;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.PatientAttributeValue;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.io.db.util.ConsoleLogger;
import net.sf.regadb.io.db.util.NominalAttribute;
import net.sf.regadb.io.db.util.Utils;
import net.sf.regadb.io.util.StandardObjects;
import net.sf.regadb.io.util.WivObjects;

public class ParseConfirmation {
    private static DateFormat dateFormatter1 = new SimpleDateFormat("yyyyMMdd");
    private static DateFormat dateFormatter2 = new SimpleDateFormat("yyMMdd");
    
    private String basePath_;
    private ParseIds parseIds_;
    private Map<Integer, Patient> patients_;
    private FileWriter fw;
    
    private NominalAttribute genderNominal_ = new NominalAttribute("Gender", -1, new String[] { "M", "F" },
            new String[] { "male", "female" } );
    
    private AttributeGroup regadbAttributeGroup_ = new AttributeGroup("RegaDB");
    
    public ParseConfirmation(String basePath, ParseIds parseIds, Map<Integer, Patient> patients) {
        basePath_ = basePath;
        parseIds_ = parseIds;
        patients_ = patients;
        
        genderNominal_.attribute.setAttributeGroup(regadbAttributeGroup_);
        
        //Cannot retrieve patientId for patcode
        try {
            fw = new FileWriter(new File("/home/plibin0/Desktop/Annelies_pat.csv"));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
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
          
       try {
        fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                    if(p==null) {
                        //TODO don't do this, should always work!!!!
                       p = new Patient();
                       p.setPatientId(id+"");
                       patients_.put(id, p);
                    }
                } else {
                    ConsoleLogger.getInstance().logError("Cannot retrieve patientConsultId from : " + dossiernummer);
                }
            } else {
                Integer id = parseIds_.getPatientIdForPatcode("19" + code_pat);
                if(id==null) {
                    parseIds_.getPatientIdForPatcode(code_pat);
                }
                if(id != null) {
                    p = patients_.get(id);
                    if(p==null) {
                        p = new Patient();
                        //TODO should we make a new patient in this case????
                        p.setPatientId(id+"");
                        patients_.put(id, p);
                     }
                } else {
                    ConsoleLogger.getInstance().logError("Cannot retrieve patientId for patcode: " + code_pat);
                    try {
                        fw.write("19"+code_pat+";\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            if(p!=null) {
            String ref_labo = getValue(i, "REF_LABO", sheet, colMapping);
            String date_test = getValue(i, "DATE_TEST", sheet, colMapping);
            String birth_date = getValue(i, "BIRTH_DATE", sheet, colMapping);
            if(!"".equals(birth_date)) {
                Date birthDate = null;
                try {
                    birthDate = df.parse(birth_date);
                } catch (ParseException e) {
                    ConsoleLogger.getInstance().logError("Cannot parse birthDate for Patient with id: "+p.getPatientId() + "(" +birth_date+ ")");
                }
                if(birthDate!=null) {
                    if(p.getPatientId()!=null && p.getPatientId().equals("490")) {
                        System.err.println("lala");
                    }
                    if(p.getBirthDate()==null) {
                        p.setBirthDate(birthDate);
                    } else {
                        if(!p.getBirthDate().equals(birthDate)) {
                            ConsoleLogger.getInstance().logError("Confirmation birthDate and original birthDate are not the same for Patient with id: "+p.getPatientId());
                        }
                    }
                }
            }
            String sex = getValue(i, "SEX", sheet, colMapping);
            if(!"".equals(sex)) {
                PatientAttributeValue pav = getPAV("Gender", p);
                Utils.createPAV(genderNominal_, sex.toUpperCase(), p);
                PatientAttributeValue pavNew = getPAV("Gender", p);
                if(pav!=null) {
                    if(!pav.getAttributeNominalValue().getValue().equals(pavNew.getAttributeNominalValue().getValue())) {
                        ConsoleLogger.getInstance().logError("Confirmation sex and original sex are not the same for Patient with id: "+p.getPatientId());
                    }
                }
            }
            String hivtype = getValue(i, "HIVTYPE", sheet, colMapping);
            
            String virload = getValue(i, "VIRLOAD", sheet, colMapping);
                setTest(StandardObjects.getGenericViralLoadTest(), ParseConsultDB.parseViralLoad(virload), p);
            
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
                handleWIVNumericAttribute("ARRIVAL_B", arrival_b, p, 4);
            
            String sexcontact = getValue(i, "SEXCONTACT", sheet, colMapping);
                handleWIVNominalAttribute("SEXCONTACT", sexcontact, p);
            
            String sexpartner = getValue(i, "SEXPARTNER", sheet, colMapping);
                handleWIVMultipleNominalAttribute("SEXPARTNER", sexpartner, p);
            
            String natpartner = getValue(i, "NATPARTNER", sheet, colMapping);
            	handleWIVCountry("NATPARTNER", natpartner, p);
            
            String bloodborne = getValue(i, "BLOODBORNE", sheet, colMapping);
                handleWIVNominalAttribute("BLOODBORNE", bloodborne, p);
                
            String yeartransf = getValue(i, "YEARTRANSF", sheet, colMapping);
                handleWIVNumericAttribute("YEARTRANSF", yeartransf, p, 4);
                
            String trancountr = getValue(i, "TRANCOUNTR", sheet, colMapping);
            	handleWIVCountry("TRANCOUNTR", trancountr, p);
                
            String child = getValue(i, "CHILD", sheet, colMapping);
                handleWIVNominalAttribute("CHILD", child, p);
                
            String profrisk = getValue(i, "PROFRISK", sheet, colMapping);
                handleWIVNominalAttribute("PROFRISK", profrisk, p);
            
            String probyear = getValue(i, "PROBYEAR", sheet, colMapping);
                handleWIVNumericAttribute("PROBYEAR", probyear, p, 4);
            
            String probcountr = getValue(i, "PROBCOUNTR", sheet, colMapping);
            	handleWIVCountry("PROBCOUNTR", probcountr, p);
            
            String lympho = getValue(i, "LYMPHO", sheet, colMapping);
                if(!"".equals(lympho)) {
                    try{ 
                        setTest(StandardObjects.getGenericCD4Test(), Double.parseDouble(lympho)+"", p);
                    } catch (NumberFormatException nfe) {
                        ConsoleLogger.getInstance().logError("Cannot parse confirmations CD4 value: " + lympho);
                    }
                }
            
            String stad_clin = getValue(i, "STAD_CLIN", sheet, colMapping);
                handleWIVNominalAttribute("STAD_CLIN", stad_clin, p);
            String reasontest = getValue(i, "REASONTEST", sheet, colMapping);
                handleWIVNominalAttribute("REASONTEST", reasontest, p);
                
            String form_out = getValue(i, "FORM_OUT", sheet, colMapping);
                storeDateAttribute("FORM_OUT", form_out, p, df);

            String form_in = getValue(i, "FORM_IN", sheet, colMapping);
                storeDateAttribute("FORM_IN", form_in, p, df);
            
            //String labo = getValue(i, "LABO", sheet, colMapping);
            //String opmerking = getValue(i, "OPMERKING", sheet, colMapping);
            }
        }
    }
    
    public void storeDateAttribute(String attributeName, String value, Patient p, DateFormat df) {
        if(!"".equals(value)) {
            try {
                Date d = df.parse(value);
                PatientAttributeValue pav = p.createPatientAttributeValue(WivObjects.getAttribute(attributeName));
                pav.setValue(d.getTime()+"");
            } catch (ParseException e) {
                ConsoleLogger.getInstance().logError("Cannot parse date " + attributeName + " value:" + value);
            }
        }
    }
    
    public void setTest(Test test, String value, Patient p) {
        if(value==null || "".equals(value)) {
            return;
        }
        for(TestResult tr : p.getTestResults()) {
            if(tr.getTest().getTestType().getDescription().equals(test.getTestType().getDescription())) {
                return;
            }
        }
        TestResult tr = p.createTestResult(test);
        tr.setValue(value);
    }
    
    public void handleWIVCountry(String attributeName, String value, Patient p) {
        if("".equals(value)) {
            return;
        }
        if(value==null) {
        	System.err.println("---------------->" + attributeName + " - " + value);
        }
        
        if(WivObjects.createCountryPANV(attributeName, value.toUpperCase(), p)==null) {
        	ConsoleLogger.getInstance().logError("Cannot handle WIV country attribute - attributeNominalVal: " + attributeName + " - " + value + " (for Patient " + p.getPatientId() +")" );
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
                    ConsoleLogger.getInstance().logError("No valid " + attributeName + " value: " + value);
                }
            } else {
                ConsoleLogger.getInstance().logError("No valid " + attributeName + " value: " + value);
            }
        }
    }
    
    public void handleWIVMultipleNominalAttribute(String attributeName, String attributeNominalValue, Patient p) {
        if(attributeNominalValue.contains("/")) {
            StringTokenizer st = new StringTokenizer(attributeNominalValue, "/");
            while(st.hasMoreElements()) {
                handleWIVNominalAttribute(attributeName, st.nextToken(), p);
            }
        } else {
            handleWIVNominalAttribute(attributeName, attributeNominalValue, p);
        }
    }
    
    public void handleWIVNominalAttribute(String attributeName, String attributeNominalValue, Patient p) {
        if("".equals(attributeNominalValue)) {
            return;
        }
        if(attributeNominalValue.length()>1) {
            ConsoleLogger.getInstance().logError("Cannot handle WIV attribute - attributeNominalVal: " + attributeName + " - " + attributeNominalValue + " (for Patient " + p.getPatientId() +")" );
            return;
        }
        if(WivObjects.createPatientAttributeNominalValue(attributeName, attributeNominalValue.toUpperCase().charAt(0), p)==null) {
            ConsoleLogger.getInstance().logError("Cannot handle WIV attribute - attributeNominalVal: " + attributeName + " - " + attributeNominalValue + " (for Patient " + p.getPatientId() +")" );
        }
    }
    
    public void handleWIVAttribute(String attributeName, String value, Patient p) {
        if(WivObjects.createPatientAttributeValue(attributeName, value, p)==null) {
            ConsoleLogger.getInstance().logError("Cannot handle WIV attribute - value: " + attributeName + " - " + value + " (for Patient " + p.getPatientId() +")" );
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
}
