package net.sf.regadb.ui.form.query.wiv;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import net.sf.regadb.csv.Table;
import net.sf.regadb.io.db.util.Utils;
import net.sf.regadb.io.util.StandardObjects;

public class WivArcViralLoadForm extends WivIntervalQueryForm {
    
    public WivArcViralLoadForm(){
        super(tr("menu.query.wiv.arc.viralLoad"),tr("form.query.wiv.label.arc.viralLoad"),tr("file.query.wiv.arc.viralLoad"));
        String query =  "select p, tr, pav "+
                        "from TestResult tr join tr.patient p, PatientAttributeValue pav " +
                        "where pav.patient = p and pav.attribute.name = 'PatCode' "+
                        "where tr.test.testType.description = '"+ StandardObjects.getViralLoadTestType().getDescription() +"' "+
                        "and tr.testDate >= :var_start_date and tr.testDate <= :var_end_date";
        setQuery(query);
    }

    @Override
    protected File postProcess(File csvFile) {
        File outFile = new File(csvFile.getAbsolutePath()+".processed.csv");

        ArrayList<String> row;

        Table in = Utils.readTable(csvFile.getAbsolutePath());
        Table out = new Table();

        int CValue = Utils.findColumn(in,"TestResult.value");
        int CTestDate = Utils.findColumn(in, "TestResult.testDate");
        int CPatCode = Utils.findColumn(in, "PatientAttributeValue.value");

        for(int i=1; i<in.numRows(); ++i){
            row = new ArrayList<String>();

            row.add(getCentreName());
            row.add("1");
            row.add(in.valueAt(CPatCode, i));
            row.add(getFormattedDate(getDate(in.valueAt(CTestDate, i))));
            row.add("1");
            row.add("1");   // VL=1 CD4=2
            row.add(getFormattedDecimal(in.valueAt(CValue,i)));
            row.add("");
            row.add("");
            row.add("");

            out.addRow(row);
        }

        try{
            out.setDelimiter(';');
            out.exportAsCsv(new FileOutputStream(outFile));
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return outFile;
    }
}