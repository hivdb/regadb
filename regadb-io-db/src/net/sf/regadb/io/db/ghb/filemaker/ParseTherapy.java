package net.sf.regadb.io.db.ghb.filemaker;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import net.sf.regadb.csv.Table;
import net.sf.regadb.io.db.util.Utils;

public class ParseTherapy {
    public static void main(String [] args) {
        ParseTherapy parseTherapy = new ParseTherapy();
        parseTherapy.parseTherapy(new File("/home/plibin0/import/ghb/filemaker/medicatie.csv"));
    }
    
    public void parseTherapy(File therapyCsv) {
        Table therapy = null;
        try {
            therapy = new Table(new BufferedInputStream(new FileInputStream(therapyCsv)), false, ';');
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        int CPatientId = Utils.findColumn(therapy, "Patient_ID");
        int CDate = Utils.findColumn(therapy, "Datum");
        int CSymptom = Utils.findColumn(therapy, "Symptoom");
        int CMedication = Utils.findColumn(therapy, "Medicatie");
        int CDosage = Utils.findColumn(therapy, "Dosis");
        int CAmountOfDosages = Utils.findColumn(therapy, "Aantal_Dosissen");
        int CFrequence = Utils.findColumn(therapy, "Frekwentie");
        int CTiming = Utils.findColumn(therapy, "Tijdstip");
        int CPeriod = Utils.findColumn(therapy, "Periode");
        int CRepeat_after = Utils.findColumn(therapy, "Herhalen_Na");
        int CBlind = Utils.findColumn(therapy, "blind");
        int CShow = Utils.findColumn(therapy, "Toon");
        int CDup = Utils.findColumn(therapy, "Dup");
        //DupPatient is the same as patient_id or blank
        //int CDupPatient = Utils.findColumn(therapy, "Dup_Patient_ID");
        int CFreq = Utils.findColumn(therapy, "freq");
        //een is always 1
        //int CEen = Utils.findColumn(therapy, "een");
        int CLab = Utils.findColumn(therapy, "lab");
        int CLetOp = Utils.findColumn(therapy, "letop");
        int CMedicalPrescription = Utils.findColumn(therapy, "med_voorschrift");
        int CSortHIV = Utils.findColumn(therapy, "Sort_HIV");
        int CSortDate = Utils.findColumn(therapy, "sort_Datum");
        int CSubj = Utils.findColumn(therapy, "subj");
        //T_Patient_ID and Toon_Patient_ID are the same as patient_id or blank
        //int CT_Patient_Id = Utils.findColumn(therapy, "T_Patient_ID");
        //int CToon_Patient_Id = Utils.findColumn(therapy, "Toon_Patient_ID");
        int CTotalDosage = Utils.findColumn(therapy, "Totale_Dosis");
        int CPrescription = Utils.findColumn(therapy, "Voorschrift");
        
        List<String> dup = new ArrayList<String>();
        for(int i = 0; i<therapy.numRows(); i++) {
            String dupPatientVal = therapy.valueAt(CShow, i);
            if(!dup.contains(dupPatientVal))
                dup.add(dupPatientVal);
        }
        for(String d : dup) {
            System.err.println("--" + d);
        }
    }
}
