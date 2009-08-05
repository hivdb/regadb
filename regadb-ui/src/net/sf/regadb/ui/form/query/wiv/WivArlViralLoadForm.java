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
        
        String query =  "select tr, pav, fu.attributeNominalValue "+
				        "from TestResult tr join tr.patient p, PatientAttributeValue pav, PatientAttributeValue fu " +
				        "where pav.patient = p and pav.attribute.name = 'PatCode' "+
				        "and fu.patient = p and fu.attribute.name = 'FOLLOW-UP' "+
				        "and tr.test.testType.description = '"+ StandardObjects.getHiv1ViralLoadTestType().getDescription() +"' "+
				        "and tr.testDate >= :var_start_date and tr.testDate <= :var_end_date";
        setQuery(query);
        
        setStartDate(DateUtils.getDateOffset(getEndDate(), Calendar.YEAR, -1));
    }

    @Override
    protected File postProcess(File csvFile) throws Exception{

        Table in = readTable(csvFile);
        if(in.numRows() < 2)
            throw new EmptyResultException();

        File outFile = new File(csvFile.getAbsolutePath()+".processed.csv");
        Table out = new Table();
        ArrayList<String> row;
        
        int CValue = in.findColumn("TestResult.value");
        int CTestDate = in.findColumn("TestResult.testDate");
        int CPatCode = in.findColumn("PatientAttributeValue.value");
        int CFollowUp = in.findColumn("AttributeNominalValue.value");
        
        for(int i=1; i<in.numRows(); ++i){
            row = new ArrayList<String>();
            
            row.add(getCentreName());
            row.add(OriginCode.ARL.getCode()+"");
            row.add(in.valueAt(CPatCode, i));
            row.add(getAbbreviation(in.valueAt(CFollowUp, i)));
            row.add(getFormattedDate(getDate(in.valueAt(CTestDate, i))));
            row.add(TypeOfInformationCode.LAB_RESULT.getCode()+"");
            row.add(TestCode.VL.getCode()+"");
            row.add(getFormattedViralLoadResult(in.valueAt(CValue,i),false,true));
            row.add("");

            out.addRow(row);
        }
        
        out.exportAsCsv(new FileOutputStream(outFile),';',false);
        
        return outFile;
    }
}
