package net.sf.regadb.ui.form.query.wiv;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import net.sf.regadb.csv.Table;
import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.PatientAttributeValue;
import net.sf.regadb.db.Therapy;
import net.sf.regadb.db.TherapyCommercial;
import net.sf.regadb.db.TherapyGeneric;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.util.date.DateUtils;

import org.hibernate.Query;

public class WivArcTherapyAtcForm extends WivIntervalQueryForm {
    
    public WivArcTherapyAtcForm(){
        super(tr("menu.query.wiv.arc.therapyAtc"),tr("form.query.wiv.label.arc.therapyAtc"),tr("file.query.wiv.arc.therapyAtc"));
//        String query =  "select tp, dg, dc "+
//                        "from Therapy tp inner join tp.therapyCommercials tc, DrugGeneric dg, DrugCommercial dc "+
//                        "where ( tp.startDate >= :var_start_date and ( ( tp.stopDate is null ) or tp.stopDate <= :var_end_date )) " +
//                        "and ( ( dg in (select tg.id.drugGeneric from tp.therapyGenerics tg )) "+
//                        "or (( dc = tc.id.drugCommercial ) " +
//                        "and (dg in elements(dc.drugGenerics)))" +
//                        ")";
        
//        String query =  "select tp, p, pav, tc, tg from PatientImpl p inner join p.patientAttributeValues pav left outer join p.therapies tp left outer join tp.therapyCommercials tc left outer join tp.therapyGenerics tg " +
//                        "where ( tp.startDate >= :var_start_date and tp.startDate <= :var_end_date and tp.stopDate is null) and (pav.attribute.name = 'PatCode')";

//        String query = "select p, pav, tp from PatientImpl p inner join p.patientAttributeValues pav left outer join p.therapies tp " +
//                        "where ( tp.startDate >= :var_start_date and tp.startDate <= :var_end_date and tp.stopDate is null) and (pav.attribute.name = 'PatCode')";

        String query = "select tp, pav "+
                "from Therapy tp inner join tp.patient p inner join p.patientAttributeValues pav "+
                "where ( tp.startDate >= :var_start_date and tp.startDate <= :var_end_date and tp.stopDate is null ) " +
                "and pav.attribute.name = 'PatCode' ";
        
        setQuery(query);
        
        setStartDate(DateUtils.getDateOffset(getEndDate(), Calendar.YEAR, -1)); //?
    }
    
    @Override
    protected boolean process(File csvFile){
        Transaction t = RegaDBMain.getApp().createTransaction();
        Query q = createQuery(t);
        List<Object[]> res = q.list();
        
        Calendar cal = Calendar.getInstance();
        String date = cal.get(Calendar.YEAR) +"0401";
        String patcode;
        
        Table out = new Table();
        
        try{
//            PrintStream ps = new PrintStream(new FileOutputStream(csvFile));
        
            for(Object[] o : res){
                Therapy tp = (Therapy)o[0];
                PatientAttributeValue pav = (PatientAttributeValue)o[1];
                
                patcode = getPadding(13);
                if(pav != null){
                    patcode = pav.getValue();
                }
                
                addTherapy(out,tp,patcode,date);
            }
            out.exportAsCsv(new FileOutputStream(csvFile), ';', false);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        
        return true;
    }

    
    @Override
    protected File postProcess(File csvFile) {
        return csvFile;
    }


    private void addTherapy(Table table, Therapy tp, String patcode, String date){
        HashSet<String> atcs = new HashSet<String>();
        ArrayList<String> row;
        
        for(TherapyGeneric tg : tp.getTherapyGenerics()){
            atcs.add(tg.getId().getDrugGeneric().getAtcCode());
        }
        for(TherapyCommercial tc : tp.getTherapyCommercials()){
            for(DrugGeneric dg : tc.getId().getDrugCommercial().getDrugGenerics()){
                atcs.add(dg.getAtcCode());
            }
        }
        for(String s : atcs){
            String atc = s;
            if(atc == null || atc.length() == 0)
                atc = "9999";
            
            row = new ArrayList<String>();
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
}
