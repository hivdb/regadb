package net.sf.regadb.ui.form.query.wiv;

import java.io.File;

public class WivArcTherapyAtcForm extends WivQueryForm {
    
    public WivArcTherapyAtcForm(){
        super(tr("menu.query.wiv.arc.therapyAtc"),tr("form.query.wiv.label.arc.therapyAtc"),tr("file.query.wiv.arc.therapyAtc"));
        setQuery("select tp from Therapy tp order by tp.patient.patientIi, tp.startDate");
    }

    @Override
    protected File postProcess(File csvFile) {
        return csvFile;
    }

//    private void printARCATC(List<Object> objs){
//        // alle therapy data, een rij per molecule (ATC)
//        HashSet<String> atcs=null;
//        PatientImpl p=null;
//        
//        int counter=0;
//        
//        System.out.println("Results: ");
//        for(Object o : objs){
//            
//            Therapy tp = (Therapy)o;
//            if(tp.getPatient() != p){
//                if(p != null)
//                    printPatient(p,atcs);
//                
//                p = tp.getPatient();
//                atcs = new HashSet<String>();
//                
//                counter++;
//            }
//            
//            for(TherapyGeneric tg : tp.getTherapyGenerics()){
//                atcs.add(tg.getId().getDrugGeneric().getAtcCode());
//            }
//            for(TherapyCommercial tc : tp.getTherapyCommercials()){
//                for(DrugGeneric dg : tc.getId().getDrugCommercial().getDrugGenerics()){
//                    atcs.add(dg.getAtcCode());
//                }
//            }
//        }
//        System.out.println("Count: "+ counter);
//    }
//    
//    private void printPatient(PatientImpl p, HashSet<String> atcs){
//        System.out.println("--- "+ p.getPatientIi() +" ---");
//        for(String i : atcs)
//            System.out.println(i);
//    }
}
