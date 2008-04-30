package net.sf.regadb.ui.form.query.wiv;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.sf.regadb.csv.Table;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.util.date.DateUtils;
import net.sf.regadb.util.hibernate.HibernateFilterConstraint;

public class WivArlEpidemiologyForm extends WivIntervalQueryForm {
    
    public WivArlEpidemiologyForm(){
        super(tr("menu.query.wiv.arl.epidemiology"),tr("form.query.wiv.label.arl.epidemiology"),tr("file.query.wiv.arl.epidemiology"));
        
        setStartDate(DateUtils.getDateOffset(getEndDate(), Calendar.YEAR, -1));
    }

    @Override
    protected boolean process(File csvFile){
        Transaction t = createTransaction();
        
        Date sdate = getStartDate();
        Date edate = getEndDate();
        
        HibernateFilterConstraint hfc = new HibernateFilterConstraint();
        hfc.setClause("pav.attribute.name='FORM_IN' and pav.value >= :start_date and pav.value <= :end_date");
        hfc.addArgument("start_date",sdate.getTime()+"");
        hfc.addArgument("end_date",edate.getTime()+"");
        List<Patient> patients = t.getPatients("join patient.patientAttributeValues as pav",hfc);
        
        Table res = getArlEpidemiologyTable(patients);
        
        t.commit();
        
        try{
            res.exportAsCsv(new FileOutputStream(csvFile), ';', false);
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
        
        return true;
    }
}
