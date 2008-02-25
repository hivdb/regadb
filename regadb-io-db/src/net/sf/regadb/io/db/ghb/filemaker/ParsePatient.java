package net.sf.regadb.io.db.ghb.filemaker;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import net.sf.regadb.csv.Table;
import net.sf.regadb.db.Attribute;
import net.sf.regadb.db.AttributeGroup;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.PatientAttributeValue;
import net.sf.regadb.io.db.util.NominalAttribute;
import net.sf.regadb.io.db.util.Utils;
import net.sf.regadb.io.util.StandardObjects;

public class ParsePatient {

    public static void main(String [] args) {
        ParsePatient parsePatient = new ParsePatient();
        parsePatient.parse( new File("/home/simbre0/import/ghb/filemaker/patienten.csv"),
                            new File("/home/simbre0/import/ghb/filemaker/mappings/geographic_origin.mapping"),
                            new File("/home/simbre0/import/ghb/filemaker/mappings/transmission_group.mapping"), null);

    }
    
    public void parse(File patientFile, File geographicOriginMapFile, File transmissionGroupMapFile, Map<String,Patient> patientIdPatients){
        
        if(!patientFile.exists() && !patientFile.isFile()){
            System.err.println("File does not exist: "+ patientFile.getAbsolutePath());
        }
        if(!geographicOriginMapFile.exists() && !geographicOriginMapFile.isFile()){
            System.err.println("Mapping file does not exist: "+ geographicOriginMapFile.getAbsolutePath());
        }
        if(!transmissionGroupMapFile.exists() && !transmissionGroupMapFile.isFile()){
            System.err.println("Mapping file does not exist: "+ transmissionGroupMapFile.getAbsolutePath());
        }
        
        Table patientTable = Utils.readTable(patientFile.getAbsolutePath(),"ISO-8859-15",';');
        
        int CPatientId = Utils.findColumn(patientTable, "Patient_ID");
        int CBirthDate = Utils.findColumn(patientTable, "Geboortedatum");
        int CDeathDate = Utils.findColumn(patientTable, "Overleden");
        int CFirstName = Utils.findColumn(patientTable, "Voornaam");
        int CLastName  = Utils.findColumn(patientTable, "Naam");
        
        int CTransmissionGroup = Utils.findColumn(patientTable, "Besmettingsbron");
        int CGeographicOrigin = Utils.findColumn(patientTable, "herkomst");
        int CPatCode = Utils.findColumn(patientTable, "patcode");

        //int CFirstHIVPosTest = Utils.findColumn(patientTable, "Datum_1e_positieve_test");
        //int CGender = Utils.findColumn(patientTable, "Geslacht");
        
        List<Attribute> regadbAttributes = Utils.prepareRegaDBAttributes();
        AttributeGroup regadbAttributeGroup = new AttributeGroup("RegaDB");
        
        Attribute patCodeAttribute = new Attribute("PatCode");
        patCodeAttribute.setAttributeGroup(regadbAttributeGroup);
        patCodeAttribute.setValueType(StandardObjects.getNumberValueType());
        
        
        Table geographicOriginTable = Utils.readTable(geographicOriginMapFile.getAbsolutePath());
        Table transmissionGroupTable = Utils.readTable(transmissionGroupMapFile.getAbsolutePath());
        NominalAttribute geographicOriginA = new NominalAttribute("Geographic origin", geographicOriginTable, regadbAttributeGroup, Utils.selectAttribute("Geographic origin", regadbAttributes));
        NominalAttribute transmissionGroupA = new NominalAttribute("Transmission group", transmissionGroupTable, regadbAttributeGroup, Utils.selectAttribute("Transmission group", regadbAttributes));
        
        for(int i=1; i<patientTable.numRows(); ++i){
            String SPatientId   = patientTable.valueAt(CPatientId,i);

            if(!isEmpty(SPatientId)){
                Date birthDate = parseDate(patientTable.valueAt(CBirthDate,i));
                Date deathDate = parseDate(patientTable.valueAt(CDeathDate,i));
                
                String SFirstName = patientTable.valueAt(CFirstName,i);
                String SLastName  = patientTable.valueAt(CLastName,i);
                
                String STransmissionGroup = patientTable.valueAt(CTransmissionGroup,i);
                String SGeographicOrigin  = patientTable.valueAt(CGeographicOrigin,i);
                String SPatCode           = patientTable.valueAt(CPatCode,i);
                
                Patient p = patientIdPatients.get(SPatientId);

                if(p!=null) {
                    if(deathDate != null){
                        p.setDeathDate(deathDate);
                    }
                    
                    PatientAttributeValue pav = p.createPatientAttributeValue(patCodeAttribute);
                    pav.setValue(SPatCode);
                    
                    //p.setFirstName(SFirstName);
                    //p.setLastName(SLastName);
                    
                    if(Utils.checkColumnValue(SGeographicOrigin, i, SPatientId))
                    {
                        Utils.handlePatientAttributeValue(geographicOriginA, SGeographicOrigin, p);
                    }
                    if(Utils.checkColumnValue(STransmissionGroup, i, SPatientId))
                    {
                        Utils.handlePatientAttributeValue(transmissionGroupA, STransmissionGroup, p);
                    }
                } else {
                    System.err.println("No valid id: "+ SPatientId);
                }
            }
            else{
                System.err.println("No valid patient id on line: "+ i);
            }
        }
    }
    
    private boolean isEmpty(String s){
        if(s == null) return true;
        if(s.trim().length() == 0) return true;
        return false;
    }
    
    private static DateFormat filemakerDateFormat = new SimpleDateFormat("dd-MM-yyyy");
    private static Date parseDate(String sdate){
        Date d = null;
        if(!sdate.equals("")) {
            try {
                d = filemakerDateFormat.parse(sdate.replace('/', '-'));
            } catch(Exception e) {
                System.err.println("Invalid date: "+ sdate);
            } 
        }
        return d;
    }
    
    public void printDistinctRow(Table t, int row){
        HashSet<String> values = new HashSet<String>();
        
        for(int i=1; i<t.numRows();++i){
            values.add(t.valueAt(row, i));
        }
        
        for(String s : values){
            System.out.println(s);
        }
    }
}
