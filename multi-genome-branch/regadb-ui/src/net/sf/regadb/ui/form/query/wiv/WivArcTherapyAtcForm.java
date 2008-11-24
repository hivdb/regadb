package net.sf.regadb.ui.form.query.wiv;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import net.sf.regadb.csv.Table;
import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.PatientAttributeValue;
import net.sf.regadb.db.Therapy;
import net.sf.regadb.db.TherapyCommercial;
import net.sf.regadb.db.TherapyGeneric;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.util.date.DateUtils;

import org.hibernate.Query;

public class WivArcTherapyAtcForm extends WivIntervalQueryForm {

    public WivArcTherapyAtcForm(){
        super(tr("menu.query.wiv.arc.therapyAtc"),tr("form.query.wiv.label.arc.therapyAtc"),tr("file.query.wiv.arc.therapyAtc"));

        String query = "select tp, pav "+
        "from Therapy tp inner join tp.patient p inner join p.patientAttributeValues pav "+
        "where not ((tp.startDate > :var_end_date and not cast(:var_end_date as date) is null) or ( tp.stopDate < :var_start_date and not tp.stopDate is null )) " +
        "and pav.attribute.name = 'PatCode' and p.patientIi in ("+ getArcPatientQuery() +")";

        setQuery(query);

        setStartDate(DateUtils.getDateOffset(getEndDate(), Calendar.YEAR, -1));
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

        String date = getFormattedDate(getEndDate());
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
                String atc = s;
                if(atc == null || atc.length() == 0)
                    atc = "9999";
    
                addRow(out, me.getKey(), date, atc);
            }
        }

        
        q = t.createQuery("select pav "+
        		"from PatientAttributeValue pav "+
        		"where pav.patient not in (" +
        			"select tp.patient from Therapy tp where " +
        				" not ((tp.startDate > :var_end_date and not cast(:var_end_date as date) is null) or ( tp.stopDate < :var_start_date and not tp.stopDate is null ))" +
        			") " +
        		"and pav.attribute.name = 'PatCode' and pav.patient.patientIi in ("+ getArcPatientQuery() +")");
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
            atcs.add(tg.getId().getDrugGeneric().getAtcCode());
        }
        for(TherapyCommercial tc : tp.getTherapyCommercials()){
            for(DrugGeneric dg : tc.getId().getDrugCommercial().getDrugGenerics()){
            	String ss[] = dg.getAtcCode().split("[+]");
            	for(String s : ss)
            		atcs.add(s.trim());
            }
        }
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
