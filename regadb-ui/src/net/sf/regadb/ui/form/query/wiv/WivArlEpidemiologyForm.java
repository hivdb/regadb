package net.sf.regadb.ui.form.query.wiv;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.regadb.csv.Table;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.PatientAttributeValue;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.ui.framework.RegaDBMain;

public class WivArlEpidemiologyForm extends WivIntervalQueryForm {
    
    public WivArlEpidemiologyForm(){
        super(tr("menu.query.wiv.arl.epidemiology"),tr("form.query.wiv.label.arl.epidemiology"),tr("file.query.wiv.arl.epidemiology"));
        
//        String query = "select patcode, wiv.attribute, wiv from PatientImpl p inner join p.patientAttributeValues patcode inner join p.patientAttributeValues wiv " +
//        		"where patcode.attribute.name = 'PatCode' and wiv.attribute.attributeGroup.groupName = 'WIV' and :var_start_date > :var_end_date";
//        setQuery(query);
    }

    @Override
    protected boolean process(File csvFile){
        Transaction t = RegaDBMain.getApp().createTransaction();
        
        List<Patient> patients = t.getPatients();
        
        Date startDate = getStartDate();
        Date endDate = getEndDate();
        
        Map<String, Integer> position = new HashMap<String, Integer>();
        List<Integer> length = new ArrayList<Integer>();
        int i=0;
        length.add(13); position.put("PatCode", i++);
        length.add(10);  position.put("REF_LABO", i++);
        length.add(8);  position.put("DATE_TEST", i++);
        length.add(8);  position.put("BIRTH_DATE", i++);
        length.add(1);  position.put("Gender", i++);
        length.add(1);  position.put("HIVTYPE", i++);
        length.add(0);  position.put("VIRLOAD", i++);
        length.add(3);  position.put("NATION", i++);
        length.add(3);  position.put("COUNTRY", i++);
        length.add(2);  position.put("RESID_B", i++);
        length.add(3);  position.put("ORIGIN", i++);
        length.add(4);  position.put("ARRIVAL_B", i++);
        length.add(1);  position.put("SEXCONTACT", i++);
        length.add(4);  position.put("SEXPARTNER", i++);
        length.add(3);  position.put("NATPARTNER", i++);
        length.add(1);  position.put("BLOODBORNE", i++);
        length.add(4);  position.put("YEARTRANSF", i++);
        length.add(3);  position.put("TRANCOUNTR", i++);
        length.add(1);  position.put("CHILD", i++);
        length.add(1);  position.put("PROFRISK", i++);
        length.add(4);  position.put("PROBYEAR", i++);
        length.add(3);  position.put("PROBCOUNTR", i++);
        length.add(4);  position.put("LYMPHO", i++);
        length.add(1);  position.put("STAD_CLIN", i++);
        length.add(1);  position.put("REASONTEST", i++);
        length.add(8);  position.put("FORM_OUT", i++);
        length.add(8);  position.put("FORM_IN", i++);
        length.add(3);  position.put("LABO", i);
        
        
        Table res = new Table();
        ArrayList<String> rowlist;
        String [] row;
        
        for(Patient p : patients){
            row = new String[i+1];
            
            for(PatientAttributeValue pav : p.getPatientAttributeValues()){
                Integer pos = position.get(pav.getAttribute().getName());
                if(pos != null){
                    String s = null;

                    if(pav.getAttribute().getAttributeGroup().getGroupName().equals("WIV")){
                        s = pav.getValue();
                        if(s == null){
                            s = getAbbreviation(pav.getAttributeNominalValue().getValue());
                        }
                        
                    }
                    else{
                        s = getFormattedString(pav);
                    }
                    if(row[pos] != null)
                        s = row[pos] + s;
 
                    row[pos] = s;
                }
                
            }
            
            //fill empty fields with question marks
            for(int j=0; j<row.length; ++j){
                if(row[j] == null){
                    row[j] = "?";
                    for(int k=1; k<length.get(j); ++k)
                        row[j] += "?";
                }
            }

            ArrayList<String> lRow = new ArrayList<String>(Arrays.asList(row));
            lRow.add("");
            res.addRow(lRow);
        }
        
        t.commit();
        
        try{
            res.exportAsCsv(new FileOutputStream(csvFile), ';', false);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        
        return true;
    }
    
    @Override
    protected File postProcess(File csvFile) {
        return csvFile;
    }
    
    private String getAbbreviation(String nominal){
        int i = nominal.indexOf(':');
        if(i != -1)
            return nominal.substring(0,i);
        else
            return nominal;
    }
    
    private String getFormattedString(PatientAttributeValue pav){
        String s = null;
        String attr = pav.getAttribute().getName();
        
        if(attr.equals("PatCode"))
            return pav.getValue();
        
        if(attr.equals("Gender")){
            if(pav.getAttributeNominalValue().getValue().equals("male"))
                return "M";
            else
                return "F";
        }
        
        return s;
    }
}
