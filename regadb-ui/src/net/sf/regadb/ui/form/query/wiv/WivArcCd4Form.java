package net.sf.regadb.ui.form.query.wiv;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;

import net.sf.regadb.csv.Table;
import net.sf.regadb.io.util.StandardObjects;
import net.sf.regadb.util.date.DateUtils;

public class WivArcCd4Form extends WivIntervalQueryForm {
    
    public WivArcCd4Form(){
        super(tr("menu.query.wiv.arc.cd4"),tr("form.query.wiv.label.arc.cd4"),tr("file.query.wiv.arc.cd4"));
        
        String query =  "select tr, pav "+
                        "from TestResult tr join tr.patient p, PatientAttributeValue pav " +
                        "where pav.patient = p and pav.attribute.name = 'PatCode' "+
                        "and tr.test.testType.description = '"+ StandardObjects.getCd4TestType().getDescription() +"' "+
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
            
            row.add(getCentreName());
            row.add(OriginCode.ARC.getCode()+"");
            row.add(in.valueAt(CPatCode, i));
            row.add(getFormattedDate(getDate(in.valueAt(CTestDate, i))));
            row.add(TypeOfInformationCode.LAB_RESULT.getCode()+"");
            row.add(TestCode.T4.getCode()+"");
            row.add(getFormattedDecimal(in.valueAt(CValue,i),0));
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
