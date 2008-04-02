package net.sf.regadb.ui.form.query.wiv;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.regadb.csv.Table;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.PatientAttributeValue;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.util.date.DateUtils;

import org.hibernate.Query;

public class WivArlConfirmedHivForm extends WivIntervalQueryForm {
    
    public WivArlConfirmedHivForm(){
        super(tr("menu.query.wiv.arl.confirmedHiv"),tr("form.query.wiv.label.arl.confirmedHiv"),tr("file.query.wiv.arl.confirmedHiv"));
        
        String query =  "select patcode, ref from PatientImpl p " +
        "left join p.patientAttributeValues patcode left join patcode.attribute patcode_a with patcode_a.name = 'PatCode' " +
        "left join p.patientAttributeValues ref left join ref.attribute ref_a with ref_a.name = 'REF_LABO' " +
        "where 1=1 " +
        //"and p.patientIi = patcode.patient.patientIi and p.patientIi = ref.patient.patientIi " +
        //"and patcode.attribute.name = 'PatCode' and ref.attribute.name = 'REF_LABO' " +
        "and :var_start_date < :var_end_date";
        
        setQuery(query);
        
        setStartDate(DateUtils.getDateOffset(getEndDate(), Calendar.MONTH, -9));
    }
    
    @Override
    protected boolean process(File csvFile){
        Transaction t = RegaDBMain.getApp().createTransaction();
        
        List<Patient> patients = getPatients(t);
        
        Map<String, Integer> position = new HashMap<String, Integer>();
        List<Integer> length = new ArrayList<Integer>();
        int i=0;
        length.add(13); position.put("PatCode", i++);
        length.add(10); position.put("REF_LABO", i++);
        length.add(8);  position.put("DATE_TEST", i++);
        length.add(8);  position.put("BIRTH_DATE", i++);
        length.add(1);  position.put("Gender", i++);
        length.add(1);  position.put("HIVTYPE", i++);
        length.add(0);  position.put("VIRLOAD", i++);
        length.add(3);  position.put("NATION", i++);
        length.add(3);  position.put("COUNTRY", i++);
        length.add(2);  position.put("RESID_B", i);
        
        Table res = new Table();
        ArrayList<String> rowlist;
        String [] row;
        
        for(Patient p : patients){
            row = new String[position.size()];
            
            for(PatientAttributeValue pav : p.getPatientAttributeValues()){
                Integer pos = position.get(pav.getAttribute().getName());
                if(pos != null){
                    String s = null;

                    s = getFormattedString(pav);
                    if(row[pos] != null)
                        s = row[pos] + s;
 
                    row[pos] = s;
                }
                
            }
            
            //fill empty fields with question marks
            for(int j=0; j<row.length; ++j){
                if(row[j] == null){
                    row[j] = getPadding(length.get(j));
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
    
    protected List<Patient> getPatients(Transaction t){
//      Query q = createQuery(t);

        return t.getPatients();
    }
}

