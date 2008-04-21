package net.sf.regadb.ui.form.query.wiv;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.sf.regadb.csv.Table;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.io.util.WivObjects;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.util.date.DateUtils;
import net.sf.regadb.util.hibernate.HibernateFilterConstraint;

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
        
        HibernateFilterConstraint hfc = new HibernateFilterConstraint();
        hfc.setClause("tr.test.description=:description and tr.testDate >= :start_date and tr.testDate <= :end_date");
        hfc.addArgument("description",WivObjects.getGenericwivConfirmation().getDescription());
        hfc.addArgument("start_date",sdate);
        hfc.addArgument("end_date",edate);
        
        List<Patient> patients = t.getPatients("join patient.testResults as tr",hfc);
        
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

