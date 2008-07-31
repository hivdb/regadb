package net.sf.regadb.ui.form.query.wiv;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.sf.regadb.csv.Table;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.util.date.DateUtils;
import net.sf.regadb.util.hibernate.HibernateFilterConstraint;

public class WivArlEpidemiologyForm extends WivIntervalQueryForm {
    
    public WivArlEpidemiologyForm(){
        super(tr("query.wiv.arl.epidemiology"),tr("query.wiv.arl.epidemiology.description"),tr("query.wiv.arl.epidemiology.file"));
        
        setStartDate(DateUtils.getDateOffset(getEndDate(), Calendar.YEAR, -1));
    }

    @Override
    protected void process(File csvFile) throws Exception{
        Transaction t = createTransaction();
        
        Date sdate = getStartDate();
        Date edate = getEndDate();
        
        HibernateFilterConstraint hfc = new HibernateFilterConstraint();
        hfc.setClause("pav.attribute.name='FORM_IN' and pav.value >= :start_date and pav.value <= :end_date");
        hfc.addArgument("start_date",sdate.getTime()+"");
        hfc.addArgument("end_date",edate.getTime()+"");
        List<Patient> patients = t.getPatients("join patient.patientAttributeValues as pav",hfc);
        
        if(patients.size() < 1)
            throw new EmptyResultException();
        
        Table res = getArlEpidemiologyTable(patients);
        
        t.commit();
        
        res.exportAsCsv(new FileOutputStream(csvFile), ';', false);
    }
}
