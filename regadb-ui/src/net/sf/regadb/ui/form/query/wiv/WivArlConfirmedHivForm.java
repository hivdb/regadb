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
import net.sf.regadb.util.date.DateUtils;
import net.sf.regadb.util.hibernate.HibernateFilterConstraint;

public class WivArlConfirmedHivForm extends WivIntervalQueryForm {
    
    public WivArlConfirmedHivForm(){
        super(tr("query.wiv.arl.confirmedHiv"),tr("query.wiv.arl.confirmedHiv.description"),tr("query.wiv.arl.confirmedHiv.file"));
        
        setStartDate(DateUtils.getDateOffset(getEndDate(), Calendar.MONTH, -9));
    }
    
    @Override
    protected void process(File csvFile) throws Exception{
    	Transaction t = createTransaction();
    	
        Date sdate = getStartDate();
        Date edate = getEndDate();
        
        HibernateFilterConstraint hfc = new HibernateFilterConstraint();
        hfc.setClause("tr.test.description=:description and tr.testDate >= :start_date and tr.testDate <= :end_date");
        hfc.addArgument("description",WivObjects.getGenericwivConfirmation().getDescription());
        hfc.addArgument("start_date",sdate);
        hfc.addArgument("end_date",edate);
        
        List<Patient> patients = t.getPatients("join patient.testResults as tr",hfc);
        
        if(patients.size() < 1)
            throw new EmptyResultException();

        
        Table res = getArlEpidemiologyTable(patients);
        
        t.commit();
        
        res.exportAsCsv(new FileOutputStream(csvFile), ';', false);
    }    
    
    protected List<Patient> getPatients(Transaction t){
        return t.getPatients();
    }
}

