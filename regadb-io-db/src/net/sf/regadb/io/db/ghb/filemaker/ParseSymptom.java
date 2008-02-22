package net.sf.regadb.io.db.ghb.filemaker;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import net.sf.regadb.csv.Table;
import net.sf.regadb.db.Attribute;
import net.sf.regadb.db.AttributeNominalValue;
import net.sf.regadb.db.Event;
import net.sf.regadb.db.PatientEventValue;
import net.sf.regadb.io.db.util.NominalEvent;
import net.sf.regadb.io.db.util.Utils;

public class ParseSymptom {
    private static DateFormat filemakerDateFormat = new SimpleDateFormat("dd-MM-yyyy");

    public static void main(String [] args) {
        Map<String,List<PatientEventValue>> eventValues = new HashMap<String,List<PatientEventValue>>();
        Map<String,AttributeNominalValue> cdcClassValue = new HashMap<String,AttributeNominalValue>();
        
        ParseSymptom parseSymptom = new ParseSymptom();
        parseSymptom.parse( new File("/home/simbre0/import/ghb/filemaker/symptomen.csv"),
                            new File("/home/simbre0/import/ghb/filemaker/mappings/aids_defining_illness.mapping"), eventValues,cdcClassValue);

    }
    
    public void parse(File f, File mapping, Map<String,List<PatientEventValue>> eventValues, Map<String,AttributeNominalValue> cdcClassValue){
        if(!f.exists() && !f.isFile()){
            System.err.println("File does not exist: "+ f.getAbsolutePath());
            return;
        }
        if(!mapping.exists() && !mapping.isFile()){
            System.err.println("Mapping file does not exist: "+ mapping.getAbsolutePath());
            return;
        }
        
        //Map<String,List<PatientEventValue>> eventValues = new HashMap<String,List<PatientEventValue>>();
        //Map<String,AttributeNominalValue> cdcClassValue = new HashMap<String,AttributeNominalValue>();
        
        List<Event> regadbEvents = Utils.prepareRegaDBEvents();
        
        Table symptomTable = Utils.readTable(f.getAbsolutePath(),';');
        Table adiMapTable = Utils.readTable(mapping.getAbsolutePath());
        
        NominalEvent aidsDefiningIllness    = new NominalEvent("Aids defining illness", adiMapTable, Utils.selectEvent("Aids defining illness", regadbEvents));
        
        Attribute symptomClassAttribute = new Attribute();
        symptomClassAttribute.setName("CDC Class");
        
        AttributeNominalValue scA = new AttributeNominalValue(symptomClassAttribute,"A");
        AttributeNominalValue scB = new AttributeNominalValue(symptomClassAttribute,"B");
        AttributeNominalValue scC = new AttributeNominalValue(symptomClassAttribute,"C");
        
        symptomClassAttribute.getAttributeNominalValues().add(scA);
        symptomClassAttribute.getAttributeNominalValues().add(scB);
        symptomClassAttribute.getAttributeNominalValues().add(scC);
        
        
        int CPatientId  = Utils.findColumn(symptomTable, "Patient_ID");
        int CStartDate  = Utils.findColumn(symptomTable, "Datum");
        int CName       = Utils.findColumn(symptomTable, "Symptoom");
        int CSKlasse    = Utils.findColumn(symptomTable, "Symptoom_Klasse");
        
        for(int i=1; i<symptomTable.numRows(); ++i){
            String SPatientId   = symptomTable.valueAt(CPatientId,i);
            String SStartDate   = symptomTable.valueAt(CStartDate,i);
            String SName        = symptomTable.valueAt(CName,i);
            String SSKlasse     = symptomTable.valueAt(CSKlasse,i);
            
            if(!isEmpty(SPatientId)){
                
                Date startDate = null;
                try {
                    startDate = filemakerDateFormat.parse(symptomTable.valueAt(CStartDate, i).replace('/', '-'));
                } catch(Exception e) {
                    //System.err.println("Invalid date on row " + i + "->" + therapy.valueAt(CDate, i));
                }
                
                if(startDate != null){
                    
                    PatientEventValue pev = Utils.handlePatientEventValue(aidsDefiningIllness, SName, startDate,null);
                    
                    List<PatientEventValue> pevs = eventValues.get(SPatientId);
                    if(pevs == null){
                        pevs = new ArrayList<PatientEventValue>();
                        eventValues.put(SPatientId, pevs);
                    }
                    pevs.add(pev);
                   
                    if(!isEmpty(SSKlasse)){
                        AttributeNominalValue newAnv = null;
                        AttributeNominalValue oldAnv = cdcClassValue.get(SPatientId);
                        
                        if(SSKlasse.equals("A")) newAnv = scA;
                        else if(SSKlasse.equals("B")) newAnv = scB;
                        else if(SSKlasse.equals("C")) newAnv = scC;
                            
                        if(newAnv != null && (oldAnv == null || (newAnv.getValue().compareTo(oldAnv.getValue()) > 0))){
                            cdcClassValue.put(SPatientId,newAnv);
                        }
                    }
                    else{
                        System.err.println("no valid symptom class specified on line: "+ i);
                    }
                }
                else{
                    System.err.println("no valid date specified on line: "+ i);
                }
            }
            else{
                System.err.println("no valid patient id specified on line: "+ i);
            }
        }
    }
    
    public void printDistinctSymptoms(Table symptomTable){
        int CSymptoom = Utils.findColumn(symptomTable, "Symptoom");

        HashSet<String> symptoms = new HashSet<String>();
        
        for(int i=1; i<symptomTable.numRows();++i){
            symptoms.add(symptomTable.valueAt(CSymptoom, i));
        }
        
        for(String s : symptoms){
            System.out.println(s);
        }
    }
    
    private boolean isEmpty(String s){
        if(s == null) return true;
        if(s.trim().length() == 0) return true;
        return false;
    }
}
