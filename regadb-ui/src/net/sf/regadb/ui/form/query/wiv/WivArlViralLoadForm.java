package net.sf.regadb.ui.form.query.wiv;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import net.sf.regadb.csv.Table;
import net.sf.regadb.io.util.StandardObjects;

public class WivArlViralLoadForm extends WivIntervalQueryForm {
    public WivArlViralLoadForm(){
        super(tr("menu.query.wiv.arl.viralLoad"),tr("form.query.wiv.label.arl.viralLoad"),tr("file.query.wiv.arl.viralLoad"));
        
        String query =  "select p, tr, pav "+
                        "from TestResult tr join tr.patient p, PatientAttributeValue pav " +
                        "where pav.patient = p and pav.attribute.name = 'PatCode' "+
                        "and tr.test.testType.description = '"+ StandardObjects.getViralLoadTestType().getDescription() +"' "+
                        "and tr.testDate >= :var_start_date and tr.testDate <= :var_end_date";
        setQuery(query);
    }

    @Override
    protected File postProcess(File csvFile) {
        File outFile = new File(csvFile.getAbsolutePath()+".processed.csv");
        
        ArrayList<String> row;

        Table in = readTable(csvFile);

        Table out = new Table();
        
        int CValue = in.findColumn("TestResult.value");
        int CTestDate = in.findColumn("TestResult.testDate");
        int CPatCode = in.findColumn("PatientAttributeValue.value");
        
        for(int i=1; i<in.numRows(); ++i){
            row = new ArrayList<String>();
            
            double vl = parseValue(in.valueAt(CValue,i));
            vl = java.lang.Math.log(vl);
            
            row.add(getCentreName());
            row.add("1");
            row.add(in.valueAt(CPatCode, i));
            row.add(getFormattedDate(getDate(in.valueAt(CTestDate, i))));
            row.add("1");
            row.add("1");   //VL=1 CD4=2
            row.add(getFormattedDecimal(vl));
            row.add("");
            row.add("");
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
