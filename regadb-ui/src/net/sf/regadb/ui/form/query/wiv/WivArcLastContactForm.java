package net.sf.regadb.ui.form.query.wiv;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.sf.regadb.csv.Table;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.io.util.StandardObjects;
import net.sf.regadb.ui.framework.forms.fields.DateField;
import net.sf.regadb.ui.framework.forms.fields.IFormField;
import net.sf.regadb.util.date.DateUtils;

import org.hibernate.Query;

public class WivArcLastContactForm extends WivIntervalQueryForm {
    
    public WivArcLastContactForm(){
        super(tr("menu.query.wiv.arc.lastContact"),tr("form.query.wiv.label.arc.lastContact"),tr("file.query.wiv.arc.lastContact"));
        setQuery("select pav.value, max(tr.testDate) from PatientAttributeValue pav, TestResult tr " +
                "where pav.patient = tr.patient and pav.attribute.name = 'PatCode' " +
                "and tr.test.description = '"+ StandardObjects.getContactTest().getDescription() +"' " +
                "and :var_start_date < :var_end_date group by pav.value");
        
        setStartDate(DateUtils.getDateOffset(getEndDate(), Calendar.YEAR, -1));
    }

    @Override
    protected void setQueryParameter(Query q, String name, IFormField f){
        if(f.getClass() == DateField.class){
            q.setString(name, ((DateField)f).getDate().getTime()+"");
        }
        else
            super.setQueryParameter(q, name, f);
    }
    
    @Override
    @SuppressWarnings("unchecked")
    protected boolean process(File csvFile) {
        ArrayList<String> row;
        Table out = new Table();
        
        Transaction t = createTransaction();
        Query q = createQuery(t);
        
        List<Object[]> list = q.list();
        
        for(Object[] o : list){
        	String patcode = (String)o[0];
        	Date d = (Date)o[1];
        	
            row = new ArrayList<String>();
            
            row.add(patcode);
            row.add(getFormattedDate(d));
            row.add(TypeOfInformationCode.LAST_CONTACT_DATE.getCode()+"");
            row.add("");

            out.addRow(row);
        }
        
        t.commit();
        
        try{
            out.exportAsCsv(new FileOutputStream(csvFile),';',false);
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
        
        return true;
    }
}