package net.sf.regadb.io.db.ghb.filemaker;

import java.io.File;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import net.sf.regadb.csv.Table;
import net.sf.regadb.db.Attribute;
import net.sf.regadb.db.AttributeNominalValue;
import net.sf.regadb.db.Event;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.PatientAttributeValue;
import net.sf.regadb.db.PatientEventValue;
import net.sf.regadb.io.db.ghb.GhbUtils;
import net.sf.regadb.io.db.util.NominalEvent;
import net.sf.regadb.io.db.util.Utils;
import net.sf.regadb.io.util.StandardObjects;

public class ParseSymptom {
    public static void main(String [] args) {
        ParseSymptom parseSymptom = new ParseSymptom();
        parseSymptom.parse( new File("/home/simbre0/import/ghb/filemaker/symptomen.csv"),
                            new File("/home/simbre0/import/ghb/filemaker/mappings/aids_defining_illness.mapping"),null);

    }
    
    public void parse(File f, File mapping, Map<String,Patient> patients){
        if(!f.exists() && !f.isFile()){
            System.err.println("File does not exist: "+ f.getAbsolutePath());
            return;
        }
        if(!mapping.exists() && !mapping.isFile()){
            System.err.println("Mapping file does not exist: "+ mapping.getAbsolutePath());
            return;
        }
        
        List<Event> regadbEvents = Utils.prepareRegaDBEvents();
        
        Table symptomTable = Utils.readTable(f.getAbsolutePath(), ParseAll.getCharset(), ParseAll.getDelimiter());
        Table adiMapTable = Utils.readTable(mapping.getAbsolutePath());
        
        NominalEvent aidsDefiningIllness    = new NominalEvent("Aids defining illness", adiMapTable, Utils.selectEvent("Aids defining illness", regadbEvents));
        Event symptomen = new Event("Symptomen");
        symptomen.setValueType(StandardObjects.getStringValueType());
        
        Attribute symptomClassAttribute = new Attribute();
        symptomClassAttribute.setName("CDC Class");
        symptomClassAttribute.setValueType(StandardObjects.getNominalValueType());
        symptomClassAttribute.setAttributeGroup(StandardObjects.getClinicalAttributeGroup());
        
        
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
            String SName        = symptomTable.valueAt(CName,i);
            String SSKlasse     = symptomTable.valueAt(CSKlasse,i);
            
            Patient p;
            if(!isEmpty(SPatientId) && (p = patients.get(SPatientId)) != null){
                
                Date startDate = null;
                try {
                    startDate = GhbUtils.filemakerDateFormat.parse(symptomTable.valueAt(CStartDate, i).replace('/', '-'));
                } catch(Exception e) {
                    //System.err.println("Invalid date on row " + i + "->" + therapy.valueAt(CDate, i));
                }
                
                if(startDate != null){
                    
                    Utils.handlePatientEventValue(aidsDefiningIllness, SName, startDate, null, p);
                    PatientEventValue pev = p.createPatientEventValue(symptomen);
                    pev.setStartDate(startDate);
                    pev.setValue(SName);
                    
                    //pevs.add(pev);
                   
                    if(!isEmpty(SSKlasse)){
                        PatientAttributeValue pav = Utils.getAttributeValue(symptomClassAttribute, p);
                        AttributeNominalValue newAnv = null;
                        AttributeNominalValue oldAnv = null;
                        if(pav != null)
                            oldAnv = pav.getAttributeNominalValue();

                        if(SSKlasse.equals("A")) newAnv = scA;
                        else if(SSKlasse.equals("B")) newAnv = scB;
                        else if(SSKlasse.equals("C")) newAnv = scC;
                        
                        if(newAnv != null && (oldAnv == null || (newAnv.getValue().compareTo(oldAnv.getValue()) > 0))){
                            if(pav == null)
                                p.createPatientAttributeValue(symptomClassAttribute).setAttributeNominalValue(newAnv);
                            else
                                pav.setAttributeNominalValue(newAnv);
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
