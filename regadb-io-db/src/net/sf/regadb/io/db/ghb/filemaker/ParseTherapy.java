package net.sf.regadb.io.db.ghb.filemaker;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sf.regadb.csv.Table;
import net.sf.regadb.db.DrugCommercial;
import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.TherapyGeneric;
import net.sf.regadb.io.db.drugs.ImportDrugsFromCentralRepos;
import net.sf.regadb.io.db.util.Utils;
import net.sf.regadb.io.db.util.file.ILineHandler;
import net.sf.regadb.io.db.util.file.ProcessFile;

public class ParseTherapy {
    private static DateFormat filemakerDateFormat = new SimpleDateFormat("dd-MM-yyyy");
    private static List<DrugCommercial> commercialDrugs;
    private static List<DrugCommercial> commercialDrugsNoDosages = new ArrayList<DrugCommercial>();
    private static List<DrugGeneric> genericDrugs;
    private Table drugMappings;
    
    private String mappingPath = "/home/plibin0/myWorkspace/regadb-io-db/src/net/sf/regadb/io/db/ghb/filemaker/mappings/";
    
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
        parseTherapy.parseTherapy(new File("/home/plibin0/import/ghb/filemaker/medicatie.csv"));
    }
    
    public void parseTherapy(File therapyCsv) {
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
            therapy = new Table(new BufferedInputStream(new FileInputStream(therapyCsv)), false, ';');
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
                String medication = mapDrug(therapy.valueAt(CMedication, i));
                if(!drugsToIgnore.contains(medication)) /*is haart*/ {
                    if(medication.toLowerCase().startsWith("ziagen/epivir")) {
                        medication = "Kivexa";
                    }
                        
                    DrugCommercial commercial = getCommercialDrug(medication);
                    DrugGeneric generic = getGenericDrug(medication);
                    if(commercial==null && generic==null) {
                        setDrugs.add(medication);
                    }
                    
                    try {
                        Date startDate = filemakerDateFormat.parse(therapy.valueAt(CDate, i).replace('/', '-'));
                    } catch(Exception e) {
                        //System.err.println("Invalid date on row " + i + "->" + therapy.valueAt(CDate, i));
                    }
                    double dosage = 0;
                    try {
                        dosage = Double.parseDouble(therapy.valueAt(CDosage, i));
                    } catch(NumberFormatException e) {
                        counterDosage++;
                    }
                    try {
                        double amountOfDosages = Double.parseDouble(therapy.valueAt(CAmountOfDosages, i));
                    } catch(NumberFormatException e) {
                        counterAmountDosage++;
                    }
                    
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
                    
                }
            }
        }
        
        for(String d : setDrugs) {
            System.err.println(d);
        }
        

        System.err.println("counterDosage " + counterDosage + " " + "counterAmountDosage" + counterAmountDosage);
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
        } else if (medication.toLowerCase().startsWith("ABT-378/r")) {
            generics.add(getGenericDrug("lopinavir"));
            generics.add(getGenericDrug("ritonavir"));
        } else if (medication.toLowerCase().startsWith("BMS 232632")) {
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
            if(dc.getName().startsWith(simpliefiedName)) {
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
}
