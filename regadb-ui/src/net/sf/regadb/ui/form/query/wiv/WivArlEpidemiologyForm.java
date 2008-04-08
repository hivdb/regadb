package net.sf.regadb.ui.form.query.wiv;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.regadb.csv.Table;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.PatientAttributeValue;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.io.util.StandardObjects;
import net.sf.regadb.ui.form.singlePatient.DataComboMessage;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.fields.ComboBox;
import net.sf.regadb.util.date.DateUtils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

public class WivArlEpidemiologyForm extends WivIntervalQueryForm {
    private ComboBox<File> prevQueryRes;
    
    public WivArlEpidemiologyForm(){
        super(tr("menu.query.wiv.arl.epidemiology"),tr("form.query.wiv.label.arl.epidemiology"),tr("file.query.wiv.arl.epidemiology"));
        
//        String query = "select patcode, wiv.attribute, wiv from PatientImpl p inner join p.patientAttributeValues patcode inner join p.patientAttributeValues wiv " +
//        		"where patcode.attribute.name = 'PatCode' and wiv.attribute.attributeGroup.groupName = 'WIV' and :var_start_date > :var_end_date";
//        setQuery(query);
        
        setStartDate(DateUtils.getDateOffset(getEndDate(), Calendar.YEAR, -1));

        
        
        prevQueryRes = new ComboBox<File>(InteractionState.Editing,this);
        prevQueryRes.addItem(new DataComboMessage<File>(null,"none"));
        
        File resDir = getResultDir();
        for(File f : resDir.listFiles()){
            if(f.getName().contains(getFileName()))
                prevQueryRes.addItem(new DataComboMessage<File>(f,f.getName()));
        }
        prevQueryRes.sort();
        addParameter("previousQueryResult",lt("Previous Query Result"),prevQueryRes);
        
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
        length.add(10); position.put("REF_LABO", i++);
        length.add(8);  position.put("TestResult.testDate", i++);
        length.add(8);  position.put("Patient.birthDate", i++);
        length.add(1);  position.put("Gender", i++);
        length.add(1);  position.put("HIVTYPE", i++);
        length.add(0);  position.put("TestResult.value", i++);
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

            row = new String[position.size()];
            
            row[position.get("Patient.birthDate")] = getFormattedDate(p.getBirthDate());

            TestResult tr = getFirstTestResult(p, StandardObjects.getViralLoadTestType());
            if(tr != null){
                row[position.get("TestResult.value")]       = getFormattedViralLoadLog10(tr.getValue());
                row[position.get("TestResult.testDate")]    = getFormattedDate(tr.getTestDate());
            }
            
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
        File retFile;
        
        //rename the csv file to be time-identifiable and unique, it will serve as the basis for new queries
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmmss");
        String timestamp = sdf.format(new Date());
        File totalFile = new File(getResultDir().getAbsolutePath() + File.separatorChar + getFileName() +"_"+ timestamp +".csv");
        csvFile.renameTo(totalFile);
        
        File prevRes = prevQueryRes.currentValue();
        if(prevRes != null && prevRes.exists()){
            //totalFile contains all data, yet we only need new information
            retFile = diff(prevRes,totalFile);
        }
        else{
            //no previous result means no diffing
            retFile = totalFile;
        }
        
        return retFile;
    }
    
    private File diff(File prev, File curr){
        File newFile = null;
        try{
            newFile = File.createTempFile(curr.getName(), ".csv");
            
            Set<String> prevContent = new HashSet<String>();
            
            //System.out.println("MD5: "+);
            LineIterator li = FileUtils.lineIterator(prev);
            while(li.hasNext()){
                prevContent.add(li.nextLine());
            }
            
            PrintStream osNew = new PrintStream(new FileOutputStream(newFile));
            
            li = FileUtils.lineIterator(curr);
            while(li.hasNext()){
                String line = li.nextLine();
                
                if(!prevContent.contains(line)){
                    osNew.println(line);
                }
            }
            osNew.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return newFile;
    }
    
//    private String getMD5(String s){
//        try{
//            MessageDigest m=MessageDigest.getInstance("MD5");            
//            m.update(s.getBytes(),0,s.length());
//            return new BigInteger(1,m.digest()).toString(16);
//        }
//        catch(Exception e){
//            return null;
//        }
//    }
}
