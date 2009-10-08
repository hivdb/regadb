package net.sf.regadb.ui.form.query.wiv;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.sf.regadb.csv.Table;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.PatientAttributeValue;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.TestType;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.meta.Equals;
import net.sf.regadb.io.util.StandardObjects;
import net.sf.regadb.ui.framework.forms.fields.DateField;
import net.sf.regadb.ui.framework.forms.fields.IFormField;
import net.sf.regadb.util.date.DateUtils;
import net.sf.regadb.util.hibernate.HibernateFilterConstraint;

import org.hibernate.Query;

public class WivArcLastContactForm extends WivIntervalQueryForm {
    
    public WivArcLastContactForm(){
        super(tr("menu.query.wiv.arc.lastContact"),tr("form.query.wiv.label.arc.lastContact"),tr("file.query.wiv.arc.lastContact"));
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
    
    protected PatientAttributeValue getPav(Patient p, String attribute){
    	for(PatientAttributeValue pav : p.getPatientAttributeValues())
    		if(pav.getAttribute().getName().equals(attribute))
    			return pav;
    	
    	return null;
    }
    protected List<TestResult> getTestResults(Patient p, TestType testType){
    	List<TestResult> ret = new ArrayList<TestResult>();
    	for(TestResult tr : p.getTestResults())
    		if(Equals.isSameTestType(tr.getTest().getTestType(),testType))
    			ret.add(tr);
    	
    	return ret;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    protected void process(File csvFile) throws Exception{
    	Transaction t = createTransaction();
    	
        Date sdate = getStartDate();
        Date edate = getEndDate();
        
        List<Patient> patients;
        
        String aq = getArcPatientQuery("patient.patientIi");
        if(aq != null){
		    HibernateFilterConstraint hfc = new HibernateFilterConstraint();
		    hfc.setClause(aq);
		    patients = t.getPatients("",hfc);
        }
        else{
        	patients = t.getPatients();
        }
        
        ArrayList<String> row;
        Table out = new Table();
        
        PatientAttributeValue pav;
        for(Patient p : patients){
        	pav = getPav(p, "PatCode");
        	if(pav == null)
        		continue;
        	String patcode = pav.getValue();
        	
        	List<TestResult> trs = getTestResults(p, StandardObjects.getContactTestType());
        	
        	Date maxDate = sdate;
        	for(TestResult tr : trs){
        		Date d = tr.getTestDate();
        		
        		if(d != null && maxDate.before(d) && d.before(edate)){
        			maxDate = d;
        		}
        	}
        	if(maxDate != sdate){
	            row = new ArrayList<String>();
	            
	            row.add(getCentreName());
	            row.add(OriginCode.ARC.getCode()+"");
	            row.add(patcode);
	            row.add(getFormattedDate(maxDate));
	            row.add(TypeOfInformationCode.LAST_CONTACT_DATE.getCode()+"");
	            row.add("");
	
	            out.addRow(row);
        	}
        }
        
        t.commit();
        
        if(out.numRows() == 0)
        	throw new EmptyResultException();
        else
        	out.exportAsCsv(new FileOutputStream(csvFile),';',false);
    }
}