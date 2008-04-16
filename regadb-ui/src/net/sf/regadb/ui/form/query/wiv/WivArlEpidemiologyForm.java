package net.sf.regadb.ui.form.query.wiv;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.sf.regadb.csv.Table;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.PatientAttributeValue;
import net.sf.regadb.db.Transaction;
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
        
        setStartDate(DateUtils.getDateOffset(getEndDate(), Calendar.YEAR, -1));
        
//        prevQueryRes = new ComboBox<File>(InteractionState.Editing,this);
//        prevQueryRes.addNoSelectionItem();
//        
//        File resDir = getResultDir();
//        for(File f : resDir.listFiles()){
//            if(f.getName().contains(getFileName()))
//                prevQueryRes.addItem(new DataComboMessage<File>(f,f.getName()));
//        }
//        prevQueryRes.sort();
//        addParameter("previousQueryResult",lt("Previous Query Result"),prevQueryRes);
    }

    @Override
    protected boolean process(File csvFile){
        Transaction t = RegaDBMain.getApp().createTransaction();
        
        Date sdate = getStartDate();
        Date edate = getEndDate();
        
//        List<Patient> patients = t.getPatients(
//        		"join patient.patientAttributeValues as pav",
//        		"pav.attribute.name='FORM_IN' and pav.value >= "+ sdate +" and pav.value <= "+ edate);

        List<Patient> patients = t.getPatients();
        Iterator<Patient> it = patients.iterator();
        while(it.hasNext()){
        	Patient p = it.next();
        	
        	PatientAttributeValue pav = getPatientAttributeValue(p, "FORM_IN");
        	if(pav != null){
        		Date fidate = DateUtils.parseDate(pav.getValue());
        		if(sdate.after(fidate) || edate.before(fidate))
        			it.remove();
        	}
        	else
        		it.remove();
        }
        
        Table res = getArlEpidemiologyTable(patients);
        
        t.commit();
        
        try{
            res.exportAsCsv(new FileOutputStream(csvFile), ';', false);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        
        return true;
    }
    
//    @Override
//    protected File postProcess(File csvFile) {
//        File retFile;
//        
//        //rename the csv file to be time-identifiable and unique, it will serve as the basis for new queries
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmmss");
//        String timestamp = sdf.format(new Date());
//        File totalFile = new File(getResultDir().getAbsolutePath() + File.separatorChar + getFileName() +"_"+ timestamp +".csv");
//        csvFile.renameTo(totalFile);
//        
//        File prevRes = prevQueryRes.currentValue();
//        if(prevRes != null && prevRes.exists()){
//            //totalFile contains all data, yet we only need new information
//            retFile = diff(prevRes,totalFile);
//        }
//        else{
//            //no previous result means no diffing
//            retFile = totalFile;
//        }
//        
//        return retFile;
//    }
    
    private File diff(File prev, File curr){
        File newFile = null;
        try{
            newFile = File.createTempFile(curr.getName(), ".csv");
            
            Set<String> prevContent = new HashSet<String>();
            
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
}
