package net.sf.regadb.io.db.ghb.filemaker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sf.regadb.csv.Table;
import net.sf.regadb.io.db.util.Utils;

public class ParseEadEmd {
    public Map<String, String> eadPatientId = new HashMap<String, String>();
    
    public ParseEadEmd() {
        
    }
    
    public void run() {
        Set<String> emd  = new HashSet<String>();
        Table eadEmd = Utils.readTable("/home/simbre1/tmp/import/ghb/filemaker/ead_emd_name.csv");
        
        System.err.println("Non-unique emd's -------------------------");
        for(int i = 1; i<eadEmd.numRows(); i++) {
            if(!emd.add(eadEmd.valueAt(1, i)))
                System.err.println("NONE" + eadEmd.valueAt(1, i));
        }
        System.err.println("Non-unique emd's -------------------------");
        
        Table patientFilemakerTable = Utils.readTable("/home/simbre1/tmp/import/ghb/filemaker/patienten.csv", ';');
        int CDossierNr = Utils.findColumn(patientFilemakerTable, "DossierNr");
        int CNaam = Utils.findColumn(patientFilemakerTable, "Naam");
        int CVoornaam = Utils.findColumn(patientFilemakerTable, "Voornaam");
        int CPatientId = Utils.findColumn(patientFilemakerTable, "Patient_ID");
        
        //only in kws
        ArrayList<String> onlyInKWS = new ArrayList<String>();
        for(int i = 1; i<eadEmd.numRows(); i++) {
            String emdKWSValue = eadEmd.valueAt(1, i);
            boolean found = false;
            for(int j = 1; j<patientFilemakerTable.numRows() && !found; j++) {
                String emdFileMaker = patientFilemakerTable.valueAt(CDossierNr, j);
                if(emdFileMaker.equals(emdKWSValue)) {
                    found = true;
                }
            }
            if(!found) {
                onlyInKWS.add(emdKWSValue + " " + eadEmd.valueAt(2, i) + " " + eadEmd.valueAt(3, i));
            }
        }
        
        System.err.println("Only in KWS " + onlyInKWS.size());
        for(String s : onlyInKWS) {
            System.err.println("Only in KWS " + s);
        }
        
        //only in filemaker
        ArrayList<String> onlyInFilemaker = new ArrayList<String>();
        for(int i = 1; i<patientFilemakerTable.numRows(); i++) {
            String emdFileMaker = patientFilemakerTable.valueAt(CDossierNr, i);
            boolean found = false;
            for(int j = 1; j<eadEmd.numRows() && !found; j++) {
                String emdKWSValue = eadEmd.valueAt(1, j);
                if(emdFileMaker.equals(emdKWSValue)) {
                    found = true;
                }
            }
            if(!found) {
                onlyInFilemaker.add(emdFileMaker + " " + patientFilemakerTable.valueAt(CNaam, i).toUpperCase() + " " + patientFilemakerTable.valueAt(CVoornaam, i).toUpperCase());
            }
        }
        
        System.err.println("Only in filemaker " + onlyInFilemaker.size());
        for(String s : onlyInFilemaker) {
            System.err.println("Only in filemaker " + s);
        }
        
        //build mapping
        for(int i = 1; i<eadEmd.numRows(); i++) {
            String emdKWSValue = eadEmd.valueAt(1, i);
            String eadKWSValue = eadEmd.valueAt(0, i);
            for(int j = 1; j<patientFilemakerTable.numRows(); j++) {
                String emdFileMaker = patientFilemakerTable.valueAt(CDossierNr, j);
                String patientID = patientFilemakerTable.valueAt(CPatientId, j);
                if(emdKWSValue.equals(emdFileMaker)) {
                    eadPatientId.put(eadKWSValue, patientID);
                    break;
                }
            }
        }
    }
    
    public static void main(String [] args) {
        ParseEadEmd parse = new ParseEadEmd();
        parse.run();
    }
}
