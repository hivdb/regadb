package net.sf.regadb.ui.form.query.wiv;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import net.sf.regadb.csv.Table;
import net.sf.regadb.db.PatientAttributeValue;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.util.date.DateUtils;
import net.sf.regadb.util.settings.RegaDBSettings;

import org.hibernate.Query;

public class WivArcViralLoadForm extends WivIntervalQueryForm {
    public WivArcViralLoadForm(){
        super(tr("menu.query.wiv.arc.viralLoad"),tr("form.query.wiv.label.arc.viralLoad"),tr("file.query.wiv.arc.viralLoad"));
        
        String query =  "select tr, pav "+
            "from TestResult tr join tr.patient p, PatientAttributeValue pav " +
            "where pav.patient = p and pav.attribute.name = 'PatCode' "+
            "and tr.test.testType.description = '"+ RegaDBSettings.getInstance().getInstituteConfig().getWivConfig().getViralLoadTestType().getDescription() +"' "+
            "and tr.testDate >= :var_start_date and tr.testDate <= :var_end_date and "+ getArcPatientQuery("pav.patient.patientIi");
        
        setQuery(query);
        
        setStartDate(DateUtils.getDateOffset(getEndDate(), Calendar.YEAR, -1));
    }
    
    @Override
    @SuppressWarnings("unchecked")
    protected void process(File csvFile) throws Exception{
        
        Transaction t = createTransaction();
        Query q = createQuery(t);
        
        List<Object[]> list = (List<Object[]>)q.list();
        if(list.size() < 1)
            throw new EmptyResultException();
        
        ArrayList<String> row;
        Table out = new Table();
        
        boolean toLog10 = !RegaDBSettings.getInstance().getInstituteConfig().getWivConfig().getViralLoadTestType().getDescription().contains("log10");
        
        for(Object[] o : list){
        	TestResult tr = (TestResult)o[0];
        	PatientAttributeValue pav = (PatientAttributeValue)o[1];
        	
            row = new ArrayList<String>();
            
            row.add(getCentreName());
            row.add(OriginCode.ARC.getCode()+"");
            row.add(pav.getValue());
            row.add(getFormattedDate(tr.getTestDate()));
            row.add(TypeOfInformationCode.LAB_RESULT.getCode()+"");
            row.add(TestCode.VL.getCode()+"");
            row.add(getFormattedViralLoadResult(tr.getValue(),false,toLog10));
            row.add("");
            
            out.addRow(row);
        }
        
        t.commit();
        out.exportAsCsv(new FileOutputStream(csvFile),';',false);
    }
}
