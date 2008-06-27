package net.sf.regadb.ui.form.query.wiv;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;

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
        "where ( tp.startDate >= :var_start_date and tp.startDate <= :var_end_date and tp.stopDate is null ) " +
        "and pav.attribute.name = 'PatCode' and p.patientIi in ("+ getArcPatientQuery() +")";

        setQuery(query);

        setStartDate(DateUtils.getDateOffset(getEndDate(), Calendar.YEAR, -1));
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void process(File csvFile) throws Exception{
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

            addTherapy(out,tp,patcode,date);
        }
        
        q = t.createQuery("select pav "+
        		"from PatientAttributeValue pav "+
        		"where pav.patient not in (" +
        			"select tp.patient from Therapy tp where " +
        				" ( tp.startDate >= :var_start_date and tp.startDate <= :var_end_date and tp.stopDate is null )" +
        			") " +
        		"and pav.attribute.name = 'PatCode'");
        q.setDate("var_start_date", getStartDate());
        q.setDate("var_end_date", getEndDate());
        List<PatientAttributeValue> pavs = q.list();
        
        for(PatientAttributeValue pav : pavs){
        	addEmptyTherapy(out,pav,date);
        }
            
        out.exportAsCsv(new FileOutputStream(csvFile), ';', false);
       	t.commit();
    }

    private void addTherapy(Table table, Therapy tp, String patcode, String date){
        HashSet<String> atcs = new HashSet<String>();

        for(TherapyGeneric tg : tp.getTherapyGenerics()){
            atcs.add(tg.getId().getDrugGeneric().getAtcCode());
        }
        for(TherapyCommercial tc : tp.getTherapyCommercials()){
            for(DrugGeneric dg : tc.getId().getDrugCommercial().getDrugGenerics()){
            	String ss[] = dg.getAtcCode().split("[+]");
            	for(String s : ss)
            		atcs.add(s);
            }
        }
        for(String s : atcs){
            String atc = s;
            if(atc == null || atc.length() == 0)
                atc = "9999";

            addRow(table, patcode, date, atc);
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
