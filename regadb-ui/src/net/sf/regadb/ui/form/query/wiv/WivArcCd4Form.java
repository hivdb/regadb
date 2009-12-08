package net.sf.regadb.ui.form.query.wiv;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.sf.regadb.csv.Table;
import net.sf.regadb.db.PatientAttributeValue;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.io.util.StandardObjects;
import net.sf.regadb.util.date.DateUtils;

import org.hibernate.Query;

public class WivArcCd4Form extends WivIntervalQueryForm {
    
    public WivArcCd4Form(){
        super(tr("menu.query.wiv.arc.cd4"),tr("form.query.wiv.label.arc.cd4"),tr("file.query.wiv.arc.cd4"));
        
        String query =  "select tr, pc, bd "+
            "from TestResult tr join tr.patient p join p.patientAttributeValues bd join p.patientAttributeValues pc " +
            "where pc.attribute.name = 'PatCode' and bd.attribute.name = 'Birth date' "+
            "and (tr.test.testType.description = '"+ StandardObjects.getCd4TestType().getDescription() +"' "+
            "or tr.test.testType.description = '"+ StandardObjects.getCd4PercentageTestType().getDescription() +"') "+ 
            "and tr.testDate >= :var_start_date and tr.testDate <= :var_end_date " +
            "and "+ getArcPatientQuery("p.id") +" "+
            "and "+ getContactConstraint("p.id");

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
        
        for(Object[] o : list){
        	TestResult tr = (TestResult)o[0];
        	PatientAttributeValue pc = (PatientAttributeValue)o[1];
            PatientAttributeValue bdt = (PatientAttributeValue)o[2];
            Date bd = DateUtils.parseDate(bdt.getValue());
        	
        	Date tDate = tr.getTestDate();
        	int testCode = TestCode.T4.getCode();

        	if(bd != null && tDate != null && DateUtils.getDateOffset(bd, Calendar.YEAR, 15).after(tDate)){
        		// < 15 years old at time of test
        		testCode = TestCode.T4PERCENT.getCode();
        		if(!tr.getTest().getTestType().getDescription().equals(StandardObjects.getCd4PercentageTestType().getDescription()))
        			continue;
        	}
        	else{
                if(tr.getTest().getTestType().getDescription().equals(StandardObjects.getCd4PercentageTestType().getDescription()))
                    continue;
        	}
        	
        	String value = tr.getValue();
        	
            row = new ArrayList<String>();
            
            row.add(getCentreName());
            row.add(OriginCode.ARC.getCode()+"");
            row.add(pc.getValue());
            row.add(getFormattedDate(tDate));
            row.add(TypeOfInformationCode.LAB_RESULT.getCode()+"");
            row.add(testCode +"");
            row.add(getFormattedDecimal(value,0,0));
            row.add("");
            
            out.addRow(row);
        }
        
        t.commit();
        out.exportAsCsv(new FileOutputStream(csvFile),';',false);
    }
}
