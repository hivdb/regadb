package net.sf.regadb.ui.form.query.wiv;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import net.sf.regadb.csv.Table;
import net.sf.regadb.db.DrugCommercial;
import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.PatientAttributeValue;
import net.sf.regadb.db.Therapy;
import net.sf.regadb.db.TherapyCommercial;
import net.sf.regadb.db.TherapyGeneric;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.fields.DateField;
import net.sf.regadb.util.settings.RegaDBSettings;

import org.hibernate.Query;

public class WivArcTherapyAtcForm extends WivIntervalQueryForm {
    private String therapyDateConstraint = " not ((tp.startDate > :var_date and not cast(:var_date as date) is null) or ( not tp.stopDate is null and tp.stopDate <= :var_date )) ";
    
    private DateField dateField;

    public WivArcTherapyAtcForm(){
        super(tr("menu.query.wiv.arc.therapyAtc"),tr("form.query.wiv.label.arc.therapyAtc"),tr("file.query.wiv.arc.therapyAtc"));

        String query = "select tp, pav "+
        	"from Therapy tp inner join tp.patient p inner join p.patientAttributeValues pav "+
        	"where "+ therapyDateConstraint +
        	"and pav.attribute.name = 'PatCode' "
        	+"and "+ getArcPatientQuery("p.id") +" "
        	+"and "+ getContactConstraint("p.id");
        
        setQuery(query);
    }
    
    public void init(){
        super.init();

        dateField = new DateField(InteractionState.Editing, this, RegaDBSettings.getInstance().getDateFormat());
        dateField.setDate(new Date());
        super.addParameter("var_date", tr("form.query.wiv.label.therapyDate"), dateField);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void process(File csvFile) throws Exception{
        Map<String,HashSet<String>> patcodeAtcs = new HashMap<String,HashSet<String>>();
        
        Transaction t = createTransaction();
        Query q = createQuery(t);
        List<Object[]> res = q.list();
        
        if(res.size() < 1)
            throw new EmptyResultException();

        String date = getFormattedDate(dateField.getDate());
        String patcode;

        Table out = new Table();

        for(Object[] o : res){
            Therapy tp = (Therapy)o[0];
            PatientAttributeValue pav = (PatientAttributeValue)o[1];

            patcode = getPadding(13);
            if(pav != null){
                patcode = pav.getValue();
            }
            HashSet<String> atcs = patcodeAtcs.get(patcode);
            if(atcs == null){
                atcs = new HashSet<String>();
                patcodeAtcs.put(patcode, atcs);
            }
            addTherapy(atcs,tp);
        }
        for(Map.Entry<String, HashSet<String>> me : patcodeAtcs.entrySet()){
            for(String s : me.getValue()){
                addRow(out, me.getKey(), date, s);
            }
        }

        
        q = t.createQuery("select pav "+
        		"from PatientAttributeValue pav "+
        		"where pav.patient not in (" +
        			"select tp.patient from Therapy tp where " +
        				therapyDateConstraint +
        			") " +
        		"and pav.attribute.name = 'PatCode' and "+ getArcPatientQuery("pav.patient.patientIi")
        		+" and "+ getContactConstraint("pav.patient.id"));
        q.setDate("var_date", dateField.getDate());
        q.setDate("var_start_date", getStartDate());
        q.setDate("var_end_date", getEndDate());
        
        List<PatientAttributeValue> pavs = q.list();
        
        for(PatientAttributeValue pav : pavs){
        	addEmptyTherapy(out,pav,date);
        }
            
        out.exportAsCsv(new FileOutputStream(csvFile), ';', false);
       	t.commit();
    }

    private void addTherapy(HashSet<String> atcs, Therapy tp){

        for(TherapyGeneric tg : tp.getTherapyGenerics()){
        	if(tg.isPlacebo())
        		continue;
        	else if(tg.isBlind())
        		atcs.add("ART_01");
        	else
        		atcs.add(getAtcCode(tg.getId().getDrugGeneric()));
        }
        for(TherapyCommercial tc : tp.getTherapyCommercials()){
        	DrugCommercial dc = tc.getId().getDrugCommercial();
        	if(tc.isPlacebo())
        		continue;
        	else if(tc.isBlind())
        		atcs.add("ART_01");
        	else{
	        	if(dc.getAtcCode() != null && dc.getAtcCode().length() != 0){
	        		atcs.add(dc.getAtcCode());
	        	}
	        	else{
		            for(DrugGeneric dg : dc.getDrugGenerics()){
		            	String ss[] = getAtcCode(dg).split("[+]");
		            	for(String s : ss){
		            		atcs.add(s.trim());
		            	}
		            }
	        	}
        	}
        }
    }
    
    private String getAtcCode(DrugGeneric dg){
    	if(dg.getGenericId().toLowerCase().startsWith("unknown"))
    		return "9999";
    	else
    		return dg.getAtcCode() == null ? "" : dg.getAtcCode();
    }
    
    private void addEmptyTherapy(Table table, PatientAttributeValue pav, String date){
    	addRow(table,pav.getValue(),date,"0000");
    }
    
    private void addRow(Table table, String patcode, String date, String atc){
        ArrayList<String> row = new ArrayList<String>();

        row.add(getCentreName());
        row.add(OriginCode.ARC.getCode()+"");
        row.add(patcode);
        row.add(date);
        row.add(TypeOfInformationCode.THERAPY.getCode()+"");
        row.add(atc);
        row.add("");

        table.addRow(row);
    }
    
}
