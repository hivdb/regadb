package net.sf.regadb.io.db.ghb.filemaker;

import java.io.File;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import net.sf.regadb.csv.Table;
import net.sf.regadb.db.Attribute;
import net.sf.regadb.db.AttributeGroup;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.PatientAttributeValue;
import net.sf.regadb.io.db.ghb.GhbUtils;
import net.sf.regadb.io.db.util.NominalAttribute;
import net.sf.regadb.io.db.util.Utils;
import net.sf.regadb.io.util.StandardObjects;

public class ParsePatient {

    public void parse(File patientFile, File countryOfOriginMapFile, File geographicOriginMapFile, File transmissionGroupMapFile, Map<String,Patient> patientIdPatients){
        
        if(!patientFile.exists() && !patientFile.isFile()){
            System.err.println("File does not exist: "+ patientFile.getAbsolutePath());
        }
        if(!countryOfOriginMapFile.exists() && !countryOfOriginMapFile.isFile()){
            System.err.println("Mapping file does not exist: "+ countryOfOriginMapFile.getAbsolutePath());
        }
        if(!geographicOriginMapFile.exists() && !geographicOriginMapFile.isFile()){
            System.err.println("Mapping file does not exist: "+ geographicOriginMapFile.getAbsolutePath());
        }
        if(!transmissionGroupMapFile.exists() && !transmissionGroupMapFile.isFile()){
            System.err.println("Mapping file does not exist: "+ transmissionGroupMapFile.getAbsolutePath());
        }
        
        Table patientTable = Utils.readTable(patientFile.getAbsolutePath(),ParseAll.getCharset(),ParseAll.getDelimiter());
        
        int CPatientId = Utils.findColumn(patientTable, "Patient_ID");
        int CBirthDate = Utils.findColumn(patientTable, "Geboortedatum");
        int CDeathDate = Utils.findColumn(patientTable, "Overleden");
        int CFirstName = Utils.findColumn(patientTable, "Voornaam");
        int CLastName  = Utils.findColumn(patientTable, "Naam");
        int CCountryOfOrigin = Utils.findColumn(patientTable, "Herkomst_Land");
        
        int CTransmissionGroup = Utils.findColumn(patientTable, "Besmettingsbron");
        int CGeographicOrigin = Utils.findColumn(patientTable, "herkomst");
        int CPatCode = Utils.findColumn(patientTable, "patcode");

        int CGender = Utils.findColumn(patientTable, "Geslacht");
//        int CCountryOfOrigin = Utils.findColumn(patientTable, "Land");
        
        List<Attribute> regadbAttributes = Utils.prepareRegaDBAttributes();
        AttributeGroup regadbAttributeGroup = new AttributeGroup("RegaDB");
        AttributeGroup ghbAttributeGroup = new AttributeGroup("UZ Leuven");
        
        Attribute patCodeAttribute = new Attribute("PatCode");
        patCodeAttribute.setAttributeGroup(ghbAttributeGroup);
        patCodeAttribute.setValueType(StandardObjects.getStringValueType());
        
        Table countryOfOriginTable = Utils.readTable(countryOfOriginMapFile.getAbsolutePath());
        Table geographicOriginTable = Utils.readTable(geographicOriginMapFile.getAbsolutePath());
        Table transmissionGroupTable = Utils.readTable(transmissionGroupMapFile.getAbsolutePath());
        
        NominalAttribute countryOfOriginA = new NominalAttribute("Country of origin", countryOfOriginTable, regadbAttributeGroup, Utils.selectAttribute("Country of origin", regadbAttributes));
        NominalAttribute geographicOriginA = new NominalAttribute("Geographic origin", geographicOriginTable, regadbAttributeGroup, Utils.selectAttribute("Geographic origin", regadbAttributes));
        NominalAttribute transmissionGroupA = new NominalAttribute("Transmission group", transmissionGroupTable, regadbAttributeGroup, Utils.selectAttribute("Transmission group", regadbAttributes));
        NominalAttribute genderA = new NominalAttribute("Gender", CGender, new String[] { "M", "V" },
                new String[] { "male", "female" } );
        genderA.attribute.setAttributeGroup(regadbAttributeGroup);
        
       
        for(int i=1; i<patientTable.numRows(); ++i){
            String SPatientId   = patientTable.valueAt(CPatientId,i);

            if(!isEmpty(SPatientId)){
                Date birthDate = parseDate(patientTable.valueAt(CBirthDate,i));
                String sDeathDate = patientTable.valueAt(CDeathDate,i);
                Date deathDate = parseDate(sDeathDate);
                
                String SFirstName = patientTable.valueAt(CFirstName,i);
                String SLastName  = patientTable.valueAt(CLastName,i);
                
                String STransmissionGroup = patientTable.valueAt(CTransmissionGroup,i);
                String SGeographicOrigin  = patientTable.valueAt(CGeographicOrigin,i);
                String SPatCode           = patientTable.valueAt(CPatCode,i);
                               
                String SGender = patientTable.valueAt(CGender,i);
                String SCountryOfOrigin = patientTable.valueAt(CCountryOfOrigin,i);
                
                Patient p = patientIdPatients.get(SPatientId);

                if(p!=null) {
                    if(p.getBirthDate() == null && birthDate != null){
                        p.setBirthDate(birthDate);
                    }
                    if(deathDate != null){
                        p.setDeathDate(deathDate);
                    }
                    
                    if(!isEmpty(SPatCode)){
                    	PatientAttributeValue pav = p.createPatientAttributeValue(patCodeAttribute);
                    	pav.setValue(SPatCode);
                    }
                    
                    p.setFirstName(SFirstName);
                    p.setLastName(SLastName);

                    if(Utils.checkColumnValueForExistance("country of origin", SCountryOfOrigin, i, SPatientId))
                    {
                    	Utils.handlePatientAttributeValue(countryOfOriginA, SCountryOfOrigin, p);
                    }
                    if(Utils.checkColumnValueForExistance("geographic origin", SGeographicOrigin, i, SPatientId))
                    {
                        Utils.handlePatientAttributeValue(geographicOriginA, SGeographicOrigin, p);
                    }
                    if(Utils.checkColumnValueForEmptiness("risk group", STransmissionGroup, i, SPatientId))
                    {
                        Utils.handlePatientAttributeValue(transmissionGroupA, STransmissionGroup, p);
                    }
                    if(Utils.checkColumnValueForEmptiness("gender", SGender, i, SPatientId))
                    {
                        if(Utils.getAttributeValue("Gender", p) == null)
                            Utils.handlePatientAttributeValue(genderA, SGender, p);
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
    
    private static Date parseDate(String sdate){
        Date d = null;
        if(!sdate.equals("")) {
            try {
                d = GhbUtils.filemakerDateFormat.parse(sdate.replace('/', '-'));
            } catch(Exception e) {
                System.err.println("Invalid date: "+ sdate);
            } 
        }
        return d;
    }
    
    public static String trimCountryOfOrigin(String country){
        int trim = country.indexOf("  ");
        if(trim != -1)
            return country.substring(0, trim);
        else
            return country;
    }
    
    public void printDistinctRow(Table t, int row){
        HashSet<String> values = new HashSet<String>();
        
        for(int i=1; i<t.numRows();++i){
            String s = t.valueAt(row, i);
            values.add(s);
        }
        
        for(String s : values){
            System.out.println(s);
        }
    }
}
