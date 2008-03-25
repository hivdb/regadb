package net.sf.regadb.ui.form.query.wiv;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import net.sf.regadb.csv.Table;

public class WivArcLastContactForm extends WivIntervalQueryForm {
    
    public WivArcLastContactForm(){
        super(tr("menu.query.wiv.arc.lastContact"),tr("form.query.wiv.label.arc.lastContact"),tr("file.query.wiv.arc.lastContact"));
        setQuery("select p, pav, pav2 from PatientImpl as p inner join p.patientAttributeValues pav inner join p.patientAttributeValues pav2 " +
                "where pav.attribute.name = 'PatCode' " +
                "and pav2.attribute.name = 'Last contact'");
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
            
            row.add(in.valueAt(CPatCode, i));   //patcode
            row.add(getFormattedDate(getDate(in.valueAt(CLastContact, i))));  //last contact date
            row.add("3");   //type of information 3=last contact
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