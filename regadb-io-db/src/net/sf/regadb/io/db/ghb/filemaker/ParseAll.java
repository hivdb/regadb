package net.sf.regadb.io.db.ghb.filemaker;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Therapy;
import net.sf.regadb.io.db.ghb.GetViralIsolates;
import net.sf.regadb.io.db.ghb.MergeLISFiles;
import net.sf.regadb.io.db.util.Utils;

public class ParseAll {    
    public static void main(String [] args) {
        String importGhbPath = "/home/simbre1/tmp/import/ghb/";
        String eclipeMappingDir = "/home/simbre1/workspace/regadb-io-db/src/net/sf/regadb/io/db/ghb/mapping/";
        String eclipeFileMakerMappingDir = "/home/simbre1/workspace/regadb-io-db/src/net/sf/regadb/io/db/ghb/filemaker/mappings/";
        
        ParseEadEmd eadEmd = new ParseEadEmd();
        eadEmd.run();
        
        Map<String, Patient> eadPatients = new HashMap<String, Patient>();
        Map<String, Patient> patientIdPatients = new HashMap<String, Patient>();
        for(Entry<String, String> e : eadEmd.eadPatientId.entrySet()) {
            Patient p = new Patient();
            p.setPatientId(e.getKey());
            eadPatients.put(e.getKey(), p);
            patientIdPatients.put(e.getValue(), p);
        }
        
        MergeLISFiles mergeLIS = new MergeLISFiles();
        mergeLIS.patients = eadPatients;
        mergeLIS.run();
        
        GetViralIsolates gvi = new GetViralIsolates();
        gvi.eadPatients = eadPatients;
        gvi.run();
        
        ParsePatient parsePatient = new ParsePatient();
        parsePatient.parse( new File(importGhbPath + "filemaker/patienten.csv"),
                            new File(eclipeFileMakerMappingDir + "geographic_origin.mapping"),
                            new File(eclipeFileMakerMappingDir + "transmission_group.mapping"), patientIdPatients);
        
        ParseSymptom parseSymptom = new ParseSymptom();
        parseSymptom.parse( new File(importGhbPath + "filemaker/symptomen.csv"),
                            new File(eclipeFileMakerMappingDir + "aids_defining_illness.mapping"),
                            patientIdPatients);
        
        ParseContacts parseContacts = new ParseContacts(mergeLIS.firstCd4, mergeLIS.firstCd8, mergeLIS.firstViralLoad);
        parseContacts.run(patientIdPatients);
        
        ParseTherapy parseTherapy = new ParseTherapy();
        parseTherapy.parseTherapy(new File("/home/simbre1/tmp/import/ghb/filemaker/medicatie.csv"));
        for(Entry<String, List<Therapy>> e : parseTherapy.therapies.entrySet()) {
            parseTherapy.mergeTherapies(e.getValue());
            parseTherapy.setStopDates(e.getValue());
        }
        for(Entry<String, List<Therapy>> e : parseTherapy.therapies.entrySet()) {
            Patient p = patientIdPatients.get(e.getKey());
            if(p!=null) {
                for(Therapy t : e.getValue())
                    p.getTherapies().add(t);
            } else {
                System.err.println("invalid patient id: " + e.getKey());
            }
        }
        
        Utils.exportPatientsXML(eadPatients, "/home/simbre1/tmp/import/ghb/xmlOutput/" + File.separatorChar + "patients.xml");
        Utils.exportNTXMLFromPatients(eadPatients, "/home/simbre1/tmp/import/ghb/xmlOutput/" + File.separatorChar + "viralisolates.xml");
    }
}
