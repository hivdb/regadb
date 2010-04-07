package net.sf.regadb.ui.form.query.wiv;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.sf.regadb.csv.Table;
import net.sf.regadb.db.PatientAttributeValue;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.ui.framework.forms.fields.DateField;
import net.sf.regadb.ui.framework.forms.fields.IFormField;
import net.sf.regadb.util.date.DateUtils;

import org.hibernate.Query;

public class WivArcDeathsForm extends WivIntervalQueryForm {
    
    public WivArcDeathsForm(){
        super(tr("menu.query.wiv.arc.deaths"),tr("form.query.wiv.label.arc.deaths"),tr("file.query.wiv.arc.deaths"));
        String query = "select pc, dd from PatientImpl as p inner join p.patientAttributeValues pc inner join p.patientAttributeValues dd where "
        	+ getArcPatientQuery("p.patientIi") +" and "
        	+"pc.attribute.name = 'PatCode' and dd.attribute.name = 'Death date'"
        	+" and case when dd.attribute.name = 'Death date' then cast(dd.value as long) else 0 end between :var_start_date and :var_end_date order by dd.value desc";
        setQuery(query);
        
        setStartDate(DateUtils.getDateOffset(getEndDate(), Calendar.YEAR, -1));
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void process(File csvFile) throws Exception{
        Transaction t = createTransaction();
        Query q = createQuery(t);
        
        List<Object[]> list = (List<Object[]>)q.list();
        if(list.size() < 1)
            throw new EmptyResultException();
        
        ArrayList<String> row;
        Table out = new Table();
        
        for(Object[] o : list){
            PatientAttributeValue pc = (PatientAttributeValue)o[0];
            PatientAttributeValue ddt = (PatientAttributeValue)o[1];
            Date dd = DateUtils.parseDate(ddt.getValue());
            
            row = new ArrayList<String>();
            row.add(getCentreName());
            row.add(OriginCode.ARC.getCode()+"");
            row.add(pc.getValue());
            row.add(getFormattedDate(dd));
            row.add(TypeOfInformationCode.DEATH.getCode()+"");
            row.add(CauseOfDeathCode.UNKNOWN.getCode()+"");
            row.add("");

            out.addRow(row);
        }
        t.commit();
        
        out.exportAsCsv(new FileOutputStream(csvFile),';',false);
    }
    
    protected void setQueryParameter(Query q, String name, IFormField f){
        if(f.getClass() == DateField.class)
            q.setLong(name, ((DateField)f).getDate().getTime());
    }
}
