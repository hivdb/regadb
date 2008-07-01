package net.sf.regadb.io.db.ghb.filemaker;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import net.sf.regadb.csv.Table;
import net.sf.regadb.db.DrugCommercial;
import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.Therapy;
import net.sf.regadb.db.TherapyCommercial;
import net.sf.regadb.db.TherapyCommercialId;
import net.sf.regadb.db.TherapyGeneric;
import net.sf.regadb.db.TherapyGenericId;
import net.sf.regadb.io.db.drugs.ImportDrugsFromCentralRepos;
import net.sf.regadb.io.db.ghb.GhbUtils;
import net.sf.regadb.io.db.util.Utils;
import net.sf.regadb.io.db.util.file.ILineHandler;
import net.sf.regadb.io.db.util.file.ProcessFile;

public class ParseTherapy {
    private static List<DrugCommercial> commercialDrugs;
    private static List<DrugCommercial> commercialDrugsNoDosages = new ArrayList<DrugCommercial>();
    private static List<DrugGeneric> genericDrugs;
    private Table drugMappings;
    
    public static DateFormat filemakerTherapyDateFormat = new SimpleDateFormat("dd-MM-yy");
    
    Map<String, List<Therapy>> therapies = new HashMap<String, List<Therapy>>();
    
    //!!!!!!!!!!!!!!before running this script!!!!!!!!!!!!!!
    //replace all accented chars
    //sort on patientId, date
    
    public ParseTherapy() {
        System.setProperty("http.proxyHost", "www-proxy");
        System.setProperty("http.proxyPort", "3128");
        ImportDrugsFromCentralRepos imDrug = new ImportDrugsFromCentralRepos();
        commercialDrugs = imDrug.getCommercialDrugs();
        for(DrugCommercial dc : commercialDrugs) {
            if(!dc.getName().contains("mg") && !dc.getName().contains("ml"))
            commercialDrugsNoDosages.add(dc);
        }
        genericDrugs = imDrug.getGenericDrugs();
    }
    
    public static void main(String [] args) {
        ParseTherapy parseTherapy = new ParseTherapy();
        parseTherapy.parseTherapy("/home/simbre1/tmp/import/ghb/filemaker/med_final.csv","/home/simbre1/workspace/regadb-io-db/src/net/sf/regadb/io/db/ghb/filemaker/mappings/");
        System.err.println("merging therapies");
        for(Entry<String, List<Therapy>> e : parseTherapy.therapies.entrySet()) {
            //System.err.println("patient" + e.getKey());
            parseTherapy.mergeTherapies(e.getValue());
            parseTherapy.setStopDates(e.getValue());
        }
    }
    
    public void parseTherapy(String therapyFile, String mappingPath) {
        File therapyCsv = new File(therapyFile);
        final List<String> drugsToIgnore = new ArrayList<String>();
        
        try {
            drugMappings = new Table(new BufferedInputStream(new FileInputStream(new File(mappingPath + "drugs.mapping"))), false);
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }
        
        ProcessFile pf = new ProcessFile();
        pf.process(new File(mappingPath + "drugsToIgnore.mapping"),
                new ILineHandler(){
                    public void handleLine(String line, int lineNumber) {
                        drugsToIgnore.add(line);
                    }
                });
        
        Table therapy = null;
        try {
            therapy = new Table(new BufferedInputStream(new FileInputStream(therapyCsv)), false);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //Patient_ID    Datum   Medicatie   Dosis   Aantal_Dosissen Frekwentie
        int CPatientId = Utils.findColumn(therapy, "Patient_ID");
        int CDate = Utils.findColumn(therapy, "Datum");
        int CMedication = Utils.findColumn(therapy, "Medicatie");
        int CDosage = Utils.findColumn(therapy, "Dosis");
        int CAmountOfDosages = Utils.findColumn(therapy, "Aantal_Dosissen");
        int CFrequence = Utils.findColumn(therapy, "Frekwentie");
        int CBlind = Utils.findColumn(therapy, "blind");

        int counterDosage = 0;
        int counterAmountDosage = 0;
        
        Set<String> setDrugs = new HashSet<String>();
        
        StringBuffer dosagedDrug = new StringBuffer();
        String dosagedDrugString;
        
        for(int i=1; i<therapy.numRows(); i++) {
            if(!"".equals(therapy.valueAt(CPatientId, i))) {
                int patientId = -1;
                try {
                    patientId = Integer.parseInt(therapy.valueAt(CPatientId, i));
                } catch(NumberFormatException e) {
                    System.err.println("Invalid patientId on row " + i);
                }
                String blind = therapy.valueAt(CBlind, i);

                String medication = mapDrug(therapy.valueAt(CMedication, i));
                if(!drugsToIgnore.contains(medication)) /*is haart*/ {
                    if(medication.toLowerCase().startsWith("ziagen/epivir")) {
                        medication = "Kivexa";
                    }
                        
                    DrugCommercial commercial = getCommercialDrug(medication);
                    DrugGeneric generic = getGenericDrug(medication);
                    List<DrugGeneric> genericsHardMapping = hardMapping(medication);
                    if(commercial==null && generic==null && genericsHardMapping==null) {
                        setDrugs.add(medication);
                    }
                    
                    Date startDate = null;
                    try {
                        startDate = filemakerTherapyDateFormat.parse(therapy.valueAt(CDate, i).replace('/', '-'));
                    } catch(Exception e) {
                        //System.err.println("Invalid date on row " + i + "->" + therapy.valueAt(CDate, i));
                    }
                    double dosage = 0;
                    try {
                        dosage = Double.parseDouble(therapy.valueAt(CDosage, i));
                    } catch(NumberFormatException e) {
                        counterDosage++;
                    }
                    double amountOfDosages = 0;
                    try {
                        amountOfDosages = Double.parseDouble(therapy.valueAt(CAmountOfDosages, i));
                    } catch(NumberFormatException e) {
                        counterAmountDosage++;
                    }
                    
                    //handle dosages for drugs
                    if(commercial!=null) {
                        boolean found = false;
                        dosagedDrug.delete(0, dosagedDrug.length());
                        dosagedDrug.append(commercial.getName());
                        int indexOfBracket = dosagedDrug.indexOf("(");
                        dosagedDrug.delete(indexOfBracket, dosagedDrug.length());
                        dosagedDrug.append((int)dosage);
                        dosagedDrugString = dosagedDrug.toString();
                        
                        for(DrugCommercial dc : commercialDrugs) {
                            if(dc.getName().startsWith(dosagedDrugString)) {
                                commercial = dc;
                                found = true;
                            }
                        }
                        //if found==false, the provided dosage is not compatible with the drug
                        //if(!found)
                        //    System.err.println("NOT->" + dosagedDrugString);
                    }
                    
                    
                    if(startDate!=null) {
                        if(!blind.equals("1")) {
                            List<Therapy> ts = therapies.get(patientId+"");
                            if(ts==null) {
                                ts = new ArrayList<Therapy>();
                                therapies.put(patientId+"", ts);
                            }
                            Therapy tSelected = null;
                            for(Therapy t : ts) {
                                if(t.getStartDate().equals(startDate)) {
                                    tSelected = t;
                                    break;
                                }
                            }
                            if(tSelected==null) {
                                tSelected = new Therapy();
                                tSelected.setStartDate(startDate);
                                ts.add(tSelected);
                            }
                            if(commercial!=null) {
                                storeCommercialTherapy(tSelected, commercial, amountOfDosages);
                            } else if(generic!=null) {
                                storeGenericTherapy(tSelected, generic, dosage*amountOfDosages);
                            } else if(genericsHardMapping!=null) {
                                for(DrugGeneric dg : genericsHardMapping) {
                                    storeGenericTherapy(tSelected, dg, dosage*amountOfDosages);
                                }
                            }
                        }
                    } else {
                        System.err.println("Startdate null for " + patientId);
                    }
                }
            }
        }
        
        System.err.println("Unsupported drugs");
        for(String d : setDrugs) {
            System.err.println(d);
        }
        

        System.err.println("counterDosage " + counterDosage + " " + "counterAmountDosage" + counterAmountDosage);
    }
    
    private void storeCommercialTherapy(Therapy t, DrugCommercial dc, double dosage) {
        for(TherapyCommercial tc : t.getTherapyCommercials()) {
            if(tc.getId().getDrugCommercial().getName().equals(dc.getName())) {
                tc.setDayDosageUnits(tc.getDayDosageUnits() + dosage);
                return;
            }
        }
        TherapyCommercial tg = new TherapyCommercial(new TherapyCommercialId(t, dc),false,false);
        tg.setDayDosageUnits(dosage);
        t.getTherapyCommercials().add(tg);
    }
    
    private void storeGenericTherapy(Therapy t, DrugGeneric dg, double dosage) {
        for(TherapyGeneric tg : t.getTherapyGenerics()) {
            if(tg.getId().getDrugGeneric().getGenericId().equals(dg.getGenericId())) {
                tg.setDayDosageMg(tg.getDayDosageMg()+dosage);
                return;
            }
        }
        TherapyGeneric tg = new TherapyGeneric(new TherapyGenericId(t, dg),false,false);
        tg.setDayDosageMg(dosage);
        t.getTherapyGenerics().add(tg);
    }
    
    private String mapDrug(String name) {
        for(int i=1; i<drugMappings.numRows(); i++) {
            if(drugMappings.valueAt(0, i).equals(name))
                return drugMappings.valueAt(1, i);
        }
        return name;
    }
    
    private List<DrugGeneric> hardMapping(String medication) {
        List<DrugGeneric> generics = new ArrayList<DrugGeneric>();
        if(medication.toLowerCase().startsWith("meltrex")) {
            generics.add(getGenericDrug("lopinavir"));
            generics.add(getGenericDrug("ritonavir"));
        } else if (medication.toLowerCase().startsWith("prezista")) {
            generics.add(getGenericDrug("darunavir"));
        } else if (medication.toLowerCase().startsWith("aptivus")) {
            generics.add(getGenericDrug("tipranavir"));
        } else if (medication.toLowerCase().startsWith("abt-378/r")) {
            generics.add(getGenericDrug("lopinavir"));
            generics.add(getGenericDrug("ritonavir"));
        } else if (medication.toLowerCase().startsWith("bms 232632")) {
            generics.add(getGenericDrug("atazanavir"));
        } else if (medication.toLowerCase().startsWith("bms 232 632")) {
            generics.add(getGenericDrug("atazanavir"));
        } else { 
            return null;
        }
        return generics;
    }
    
    private DrugCommercial getCommercialDrug(String commercialDescription) {
        for(DrugCommercial dc : commercialDrugsNoDosages) {
            //use the non-dosaged version of the drug
            if(dc.getName().toLowerCase().startsWith(commercialDescription.toLowerCase()) && !dc.getName().contains("mg") && !dc.getName().contains("ml")) {
                return dc;
            }
        }
        
        String simpliefiedName = "";
        for(int i = 0; i<commercialDescription.length(); i++) {
            if(Character.isDigit(commercialDescription.charAt(i))||commercialDescription.charAt(i)==' '||commercialDescription.charAt(i)=='.') {
                break;
            } else {
                simpliefiedName += commercialDescription.charAt(i);
            }
        }
        simpliefiedName = simpliefiedName.trim();
        
        for(DrugCommercial dc : commercialDrugsNoDosages) {
            if(dc.getName().toLowerCase().startsWith(simpliefiedName.toLowerCase())) {
                return dc;
            }
        }
        return null;
    }
    
    private DrugGeneric getGenericDrug(String genericDescription) {
        for(DrugGeneric dc : genericDrugs) {
            if(dc.getGenericName().startsWith(genericDescription.toLowerCase())) {
                return dc;
            }
        }
        
        String simpliefiedName = "";
        for(int i = 0; i<genericDescription.length(); i++) {
            if(Character.isDigit(genericDescription.charAt(i))||genericDescription.charAt(i)==' '||genericDescription.charAt(i)=='.') {
                break;
            } else {
                simpliefiedName += genericDescription.charAt(i);
            }
        }
        simpliefiedName = simpliefiedName.trim().toLowerCase();
        
        for(DrugGeneric dc : genericDrugs) {
            if(dc.getGenericName().startsWith(simpliefiedName)) {
                return dc;
            }
        }
        return null;
    }
    
    private double parseDouble(String doubleStr) {
        if(doubleStr.trim().equals("")) {
            return 0;
        } else if(doubleStr.equals("1/2")) {
            return 0.5;
        } else {
            try {
                return Double.parseDouble(doubleStr.trim().replace(',', '.'));
            } catch (NumberFormatException nfe) {
                return -1;
            }
        }
    }
    
    private int parseInt(String integer) {
        if(integer.trim().equals("")) {
            return 0;
        } else {
            return Integer.parseInt(integer.trim().replace(',', '.'));
        }
    }
    
    public void mergeTherapies(List<Therapy> therapies) {
        //System.err.println("old size" + therapies.size());
        Therapy former = null;
        Therapy current;
        for(Iterator<Therapy> i = therapies.iterator(); i.hasNext();) {
            current = i.next();
            if(former!=null) {
                if(compareTherapyOnDrugs(former, current)) {
                    i.remove();
                }
            } 
            former = current;
        }
        //System.err.println("new size" + therapies.size());
    }
    
    public void setStopDates(List<Therapy> therapies) {
        for(int i = 0; i<therapies.size(); i++) {
            Therapy a = therapies.get(i);
            if((i+1)<therapies.size()) {
                Therapy b = therapies.get(i+1);
                a.setStopDate(b.getStartDate());
            }
        }
    }
    
    public boolean compareTherapyOnDrugs(Therapy t1, Therapy t2) {
        List<String> genericDrugs1 = new ArrayList<String>();
        List<String> genericDrugs2 = new ArrayList<String>();
        List<String> commercialDrugs1 = new ArrayList<String>();
        List<String> commercialDrugs2 = new ArrayList<String>();
        for(TherapyGeneric tg : t1.getTherapyGenerics()) {
            genericDrugs1.add(tg.getId().getDrugGeneric().getGenericName() + tg.getDayDosageMg());
        }
        for(TherapyGeneric tg : t2.getTherapyGenerics()) {
            genericDrugs2.add(tg.getId().getDrugGeneric().getGenericName() + tg.getDayDosageMg());
        }
        for(TherapyCommercial tc : t1.getTherapyCommercials()) {
            commercialDrugs1.add(tc.getId().getDrugCommercial().getName()+tc.getDayDosageUnits());
        }
        for(TherapyCommercial tc : t2.getTherapyCommercials()) {
            commercialDrugs2.add(tc.getId().getDrugCommercial().getName()+tc.getDayDosageUnits());
        }
        
        Collections.sort(genericDrugs1);
        Collections.sort(genericDrugs2);
        Collections.sort(commercialDrugs1);
        Collections.sort(commercialDrugs2);
        
        if(genericDrugs1.size()==genericDrugs2.size()) {
            for(int i = 0; i<genericDrugs1.size(); i++) {
                if(!genericDrugs1.get(i).equals(genericDrugs2.get(i))) {
                    return false;
                }
            }
        } else {
            return false;
        }
        if(commercialDrugs1.size()==commercialDrugs2.size()) {
            for(int i = 0; i<commercialDrugs1.size(); i++) {
                if(!commercialDrugs1.get(i).equals(commercialDrugs2.get(i))) {
                    return false;
                }
            }
        } else {
            return false;
        }
        return true;
    }
}
