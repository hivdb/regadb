package net.sf.regadb.io.db.ghb.filemaker;

import java.io.File;
import java.util.Map;

import net.sf.regadb.db.Patient;

public class ParseAll {
    
    public static void main(String [] args) {
        Map<String,Patient> patients;
        
        ParsePatient parsePatient = new ParsePatient();
        patients = parsePatient.parse( new File("/home/simbre0/import/ghb/filemaker/patienten.csv"),
                            new File("/home/simbre0/import/ghb/filemaker/mappings/geographic_origin.mapping"),
                            new File("/home/simbre0/import/ghb/filemaker/mappings/transmission_group.mapping"));
        
        if(patients != null){
            ParseSymptom parseSymptom = new ParseSymptom();
            parseSymptom.parse( new File("/home/simbre0/import/ghb/filemaker/symptomen.csv"),
                    new File("/home/simbre0/import/ghb/filemaker/mappings/aids_defining_illness.mapping"),patients);
        }
    }
}
