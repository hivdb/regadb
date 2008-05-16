package net.sf.regadb.ui.form.query.wiv;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;

import net.sf.regadb.csv.Table;
import net.sf.regadb.io.util.StandardObjects;
import net.sf.regadb.util.date.DateUtils;

public class WivArlViralLoadForm extends WivIntervalQueryForm {
    public WivArlViralLoadForm(){
        super(tr("menu.query.wiv.arl.viralLoad"),tr("form.query.wiv.label.arl.viralLoad"),tr("file.query.wiv.arl.viralLoad"));
        
        String query =  "select p, tr, pav "+
                        "from TestResult tr join tr.patient p, PatientAttributeValue pav " +
                        "where pav.patient = p and pav.attribute.name = 'PatCode' "+
                        "and tr.test.testType.description = '"+ StandardObjects.getHiv1ViralLoadTestType().getDescription() +"' "+
                        "and tr.testDate >= :var_start_date and tr.testDate <= :var_end_date";
        setQuery(query);
        
        setStartDate(DateUtils.getDateOffset(getEndDate(), Calendar.YEAR, -1));
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
            vl = java.lang.Math.log10(vl);
            
            row.add(getCentreName());
            row.add(OriginCode.ARL.getCode()+"");
            row.add(in.valueAt(CPatCode, i));
            row.add(getFormattedDate(getDate(in.valueAt(CTestDate, i))));
            row.add(TypeOfInformationCode.LAB_RESULT.getCode()+"");
            row.add(TestCode.VL.getCode()+"");
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
