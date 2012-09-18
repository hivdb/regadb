package net.sf.regadb.ui.form.query.wiv;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.regadb.csv.Table;
import net.sf.regadb.db.PatientAttributeValue;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.io.util.StandardObjects;
import net.sf.regadb.util.date.DateUtils;
import net.sf.regadb.util.settings.RegaDBSettings;
import net.sf.regadb.util.settings.TestTypeConfig;

import org.hibernate.Query;

public class WivArcViralLoadForm extends WivIntervalQueryForm {
    public WivArcViralLoadForm(){
        super(tr("menu.query.wiv.arc.viralLoad"),tr("form.query.wiv.label.arc.viralLoad"),tr("file.query.wiv.arc.viralLoad"));
        
        String query =  "select tr, pav, tr.test.testType.description "
            + "from TestResult tr join tr.patient p join p.patientAttributeValues pav "
            + "where pav.attribute.name = 'PatCode' "
            + "and "+ getTestDescriptionConstraint() +" "
            + "and tr.testDate >= :var_start_date and tr.testDate <= :var_end_date "
            + "and "+ getArcPatientQuery("pav.patient.id") +" "
            + "and "+ getContactConstraint("pav.patient.id");
        
        setQuery(query);
        
        setStartDate(DateUtils.getDateOffset(getEndDate(), Calendar.YEAR, -1));
    }
    
    private String getTestDescriptionConstraint(){
    	TestTypeConfig ttc = RegaDBSettings.getInstance().getInstituteConfig().getWivConfig().getViralLoadTestType();
    	
    	if(ttc != null){
        	return "tr.test.testType.description = '"+ ttc.getDescription() +"'";
    	}else{
    		return "( tr.test.testType.description = '"+ StandardObjects.getViralLoadDescription() +"'"
    				+" or "
    				+"tr.test.testType.description = '"+ StandardObjects.getViralLoadLog10Description() +"' )";
    	}
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
        
        Map<String, Set<String>> patientViralLoads = new HashMap<String, Set<String>>(); 
        
        for(Object[] o : list){
        	TestResult tr = (TestResult)o[0];
        	PatientAttributeValue pav = (PatientAttributeValue)o[1];
        	String formattedDate = getFormattedDate(tr.getTestDate());
        	
        	Set<String> viralLoads = patientViralLoads.get(pav.getValue());
        	if(viralLoads == null){
        		viralLoads = new HashSet<String>();
        		patientViralLoads.put(pav.getValue(), viralLoads);
        	}
        	
        	if(viralLoads.add(formattedDate)){
	            boolean toLog10 = !((String)o[2]).contains("log10");
	            row = new ArrayList<String>();
	            
	            row.add(getCentreName());
	            row.add(OriginCode.ARC.getCode()+"");
	            row.add(pav.getValue());
	            row.add(formattedDate);
	            row.add(TypeOfInformationCode.LAB_RESULT.getCode()+"");
	            row.add(TestCode.VL.getCode()+"");
	            row.add(getFormattedViralLoadResult(tr.getValue(),false,toLog10));
	            row.add("");
	            
	            out.addRow(row);
        	}
        }
        
        t.commit();
        out.exportAsCsv(new FileOutputStream(csvFile),';',false);
    }
}
