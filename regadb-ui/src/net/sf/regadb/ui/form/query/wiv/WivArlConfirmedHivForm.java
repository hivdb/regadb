package net.sf.regadb.ui.form.query.wiv;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import net.sf.regadb.csv.Table;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.TestType;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.io.util.WivObjects;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.util.date.DateUtils;

public class WivArlConfirmedHivForm extends WivIntervalQueryForm {
    
    public WivArlConfirmedHivForm(){
        super(tr("menu.query.wiv.arl.confirmedHiv"),tr("form.query.wiv.label.arl.confirmedHiv"),tr("file.query.wiv.arl.confirmedHiv"));
        
        setStartDate(DateUtils.getDateOffset(getEndDate(), Calendar.MONTH, -9));
    }
    
    @Override
    protected boolean process(File csvFile){
    	Transaction t = RegaDBMain.getApp().createTransaction();
    	
        Date sdate = getStartDate();
        Date edate = getEndDate();
        
//        List<Patient> patients = t.getPatients(
//        		"join patient.testResults as tr",
//        		"tr.test.description='"+ WivObjects.getGenericwivConfirmation().getDescription() +"' ");
//        		//"and tr.testDate >= "+ sdate +" and tr.testDate <= "+ edate +" and ( tr.testNominalValue.value = 'HIV 1' or tr.testNominalValue.value = 'HIV 2' )");
        
        List<Patient> patients = t.getPatients();
        Iterator<Patient> it = patients.iterator();
        while(it.hasNext()){
        	Patient p = it.next();
        	
        	TestResult tr = getFirstTestResult(p, new TestType[]{WivObjects.getGenericwivConfirmation().getTestType()});
        	if(tr == null || sdate.after(tr.getTestDate()) || edate.before(tr.getTestDate())){
        		it.remove();
        	}
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

    @Override
    protected File postProcess(File csvFile) {
        return csvFile;
    }
    
    protected List<Patient> getPatients(Transaction t){
        return t.getPatients();
    }
}

