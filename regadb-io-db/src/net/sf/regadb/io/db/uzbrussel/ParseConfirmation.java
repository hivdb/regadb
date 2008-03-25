package net.sf.regadb.io.db.uzbrussel;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.swing.text.DateFormatter;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import net.sf.regadb.db.AttributeGroup;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.PatientAttributeValue;
import net.sf.regadb.io.db.util.ConsoleLogger;
import net.sf.regadb.io.db.util.NominalAttribute;
import net.sf.regadb.io.db.util.Utils;

public class ParseConfirmation {
    private static DateFormat dateFormatter1 = new SimpleDateFormat("yyyyMMdd");
    private static DateFormat dateFormatter2 = new SimpleDateFormat("yyMMdd");
    
    private String basePath_;
    private ParseIds parseIds_;
    private Map<Integer, Patient> patients_;
    
    private NominalAttribute genderNominal_ = new NominalAttribute("Gender", -1, new String[] { "M", "F" },
            new String[] { "male", "female" } );
    
    private AttributeGroup regadbAttributeGroup_ = new AttributeGroup("RegaDB");
    
    public ParseConfirmation(String basePath, ParseIds parseIds, Map<Integer, Patient> patients) {
        basePath_ = basePath;
        parseIds_ = parseIds;
        patients_ = patients;
        
        genderNominal_.attribute.setAttributeGroup(regadbAttributeGroup_);
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
        
          parseSheet(wb.getSheet(0), this.dateFormatter1);
          parseSheet(wb.getSheet(1), this.dateFormatter2);
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
                       p = new Patient();
                       patients_.put(id, p);
                    }
                } else {
                    ConsoleLogger.getInstance().logError("Cannot retrieve patientConsultId from : " + dossiernummer);
                }
            } else {
                Integer id = parseIds_.getPatientIdForPatcode("19" + code_pat);
                if(id != null) {
                    p = patients_.get(id);
                    if(p==null) {
                        p = new Patient();
                        patients_.put(id, p);
                     }
                } else {
                    //ConsoleLogger.getInstance().logError("Cannot retrieve patientId for patcode: " + code_pat);
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
            String nation = getValue(i, "NATION", sheet, colMapping);
            String country = getValue(i, "COUNTRY", sheet, colMapping);
            String resid_b = getValue(i, "RESID_B", sheet, colMapping);
            String origin = getValue(i, "ORIGIN", sheet, colMapping);
            String arrival_b = getValue(i, "ARRIVAL_B", sheet, colMapping);
            String sexcontact = getValue(i, "SEXCONTACT", sheet, colMapping);
            String sexpartner = getValue(i, "SEXPARTNER", sheet, colMapping);
            String natpartner = getValue(i, "NATPARTNER", sheet, colMapping);
            String bloodborne = getValue(i, "BLOODBORNE", sheet, colMapping);
            String yeartransf = getValue(i, "YEARTRANSF", sheet, colMapping);
            String trancountr = getValue(i, "TRANCOUNTR", sheet, colMapping);
            String child = getValue(i, "CHILD", sheet, colMapping);
            String profrisk = getValue(i, "PROFRISK", sheet, colMapping);
            String probyear = getValue(i, "PROBYEAR", sheet, colMapping);
            String probcountr = getValue(i, "PROBCOUNTR", sheet, colMapping);
            String lympho = getValue(i, "LYMPHO", sheet, colMapping);
            String stad_clin = getValue(i, "STAD_CLIN", sheet, colMapping);
            String reasontest = getValue(i, "REASONTEST", sheet, colMapping);
            String form_out = getValue(i, "FORM_OUT", sheet, colMapping);
            String form_in = getValue(i, "FORM_IN", sheet, colMapping);
            //String labo = getValue(i, "LABO", sheet, colMapping);
            //String opmerking = getValue(i, "OPMERKING", sheet, colMapping);
            }
        }
    }
    
    public PatientAttributeValue getPAV(String attributeName, Patient p) {
        for(PatientAttributeValue pav : p.getPatientAttributeValues()) {
            if(pav.getId().getAttribute().getName().equals(attributeName)) {
                return pav;
            }
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
