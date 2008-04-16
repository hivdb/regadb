package net.sf.regadb.ui.form.query.wiv;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;

import net.sf.regadb.csv.Table;
import net.sf.regadb.ui.framework.forms.fields.DateField;
import net.sf.regadb.ui.framework.forms.fields.IFormField;
import net.sf.regadb.util.date.DateUtils;

import org.hibernate.Query;

public class WivArcLastContactForm extends WivIntervalQueryForm {
    
    public WivArcLastContactForm(){
        super(tr("menu.query.wiv.arc.lastContact"),tr("form.query.wiv.label.arc.lastContact"),tr("file.query.wiv.arc.lastContact"));
        setQuery("select p, pav, pav2 from PatientImpl as p inner join p.patientAttributeValues pav inner join p.patientAttributeValues pav2 " +
                "where pav.attribute.name = 'PatCode' " +
                "and pav2.attribute.name = 'Last contact' " +
                "and pav2.value >= :var_start_date " +
                "and pav2.value <= :var_end_date ");
        
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
    protected File postProcess(File csvFile) {
        File outFile = new File(csvFile.getAbsolutePath()+".processed.csv");
        
        ArrayList<String> row;

        Table in = readTable(csvFile);

        Table out = new Table();
        
        int CPatCode = in.findColumn("PatientAttributeValue.value");
        int CLastContact = in.findColumn(CPatCode+1,"PatientAttributeValue.value");
        
        for(int i=1; i<in.numRows(); ++i){
            row = new ArrayList<String>();
            
            row.add(in.valueAt(CPatCode, i));
            row.add(getFormattedDate(getDate(in.valueAt(CLastContact, i))));
            row.add(TypeOfInformationCode.LAST_CONTACT_DATE.getCode()+"");
            row.add("");

            out.addRow(row);
        }
        
        try{
            out.exportAsCsv(new FileOutputStream(outFile),';',false);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        
        return outFile;
    }
}