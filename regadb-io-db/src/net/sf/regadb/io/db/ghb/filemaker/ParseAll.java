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
        String importGhbPath;
        String eclipseMappingDir;
        String eclipseFileMakerMappingDir;
        String eadEmdNameFile;
        String patientenFile;
        String lisNationMappingFile;
        String lisWorkingDir;
        String stalenLeuvenFile;
        String spreadStalenFile;
        String seqsToIgnoreFile;
        String macFastaFile;
        String pcFastaFile;
        String contactenFile;
        String medFinalFile;
        String filemakerMappingPath;
        String outputPath;
        
        if(args.length >= 16){
            int i=0;
            importGhbPath               = args[i++];
            eclipseMappingDir           = args[i++];
            eclipseFileMakerMappingDir  = args[i++];
            eadEmdNameFile              = args[i++];
            patientenFile               = args[i++];
            lisNationMappingFile        = args[i++];
            lisWorkingDir               = args[i++];
            stalenLeuvenFile            = args[i++];
            spreadStalenFile            = args[i++];
            seqsToIgnoreFile            = args[i++];
            macFastaFile                = args[i++];
            pcFastaFile                 = args[i++];
            contactenFile               = args[i++];
            medFinalFile                = args[i++];
            filemakerMappingPath        = args[i++];
            outputPath                  = args[i];
        }
        else{
            importGhbPath               = "/home/simbre1/tmp/import/ghb/";
            eclipseMappingDir           = "/home/simbre1/workspace/regadb-io-db/src/net/sf/regadb/io/db/ghb/mapping/";
            eclipseFileMakerMappingDir  = "/home/simbre1/workspace/regadb-io-db/src/net/sf/regadb/io/db/ghb/filemaker/mappings/";
            eadEmdNameFile              = "/home/simbre1/tmp/import/ghb/filemaker/ead_emd_name.csv";
            patientenFile               = "/home/simbre1/tmp/import/ghb/filemaker/patienten.csv";
            lisNationMappingFile        = "/home/simbre1/workspace/regadb-io-db/src/net/sf/regadb/io/db/ghb/mapping/LIS-nation.mapping";
            lisWorkingDir               = "/home/simbre1/tmp/import/ghb/";
            stalenLeuvenFile            = "/home/simbre1/tmp/import/ghb/seqs/Stalen Leuven.csv";
            spreadStalenFile            = "/home/simbre1/tmp/import/ghb/seqs/SPREAD_stalen.csv";
            seqsToIgnoreFile            = "/home/simbre1/workspace/regadb-io-db/src/net/sf/regadb/io/db/ghb/mapping/sequencesToIgnore.csv";
            macFastaFile                = "/home/simbre1/tmp/import/ghb/seqs/MAC_final.fasta";
            pcFastaFile                 = "/home/simbre1/tmp/import/ghb/seqs/PC_final.fasta";
            contactenFile               = "/home/simbre1/tmp/import/ghb/filemaker/contacten.csv";
            medFinalFile                = "/home/simbre1/tmp/import/ghb/filemaker/med_final.csv";
            filemakerMappingPath        = "/home/simbre1/workspace/regadb-io-db/src/net/sf/regadb/io/db/ghb/filemaker/mappings/";
            outputPath                  = "/home/simbre1/tmp/import/ghb/xmlOutput/";
        }
        
        ParseEadEmd eadEmd = new ParseEadEmd();
        eadEmd.run(eadEmdNameFile,patientenFile);
        
        Map<String, Patient> eadPatients = new HashMap<String, Patient>();
        Map<String, Patient> patientIdPatients = new HashMap<String, Patient>();
        for(Entry<String, String> e : eadEmd.eadPatientId.entrySet()) {
            Patient p = new Patient();
            p.setPatientId(e.getKey());
            eadPatients.put(e.getKey(), p);
            patientIdPatients.put(e.getValue(), p);
        }
        
        MergeLISFiles mergeLIS = new MergeLISFiles(lisNationMappingFile);
        mergeLIS.patients = eadPatients;
        mergeLIS.run(lisWorkingDir);
        
        GetViralIsolates gvi = new GetViralIsolates();
        gvi.eadPatients = eadPatients;
        gvi.run(stalenLeuvenFile,spreadStalenFile,seqsToIgnoreFile,macFastaFile,pcFastaFile);
        
        ParsePatient parsePatient = new ParsePatient();
        parsePatient.parse( new File(importGhbPath + "filemaker/patienten.csv"),
                            new File(eclipseFileMakerMappingDir + "geographic_origin.mapping"),
                            new File(eclipseFileMakerMappingDir + "transmission_group.mapping"), patientIdPatients);
        
        ParseSymptom parseSymptom = new ParseSymptom();
        parseSymptom.parse( new File(importGhbPath + "filemaker/symptomen.csv"),
                            new File(eclipseFileMakerMappingDir + "aids_defining_illness.mapping"),
                            patientIdPatients);
        
        ParseContacts parseContacts = new ParseContacts(mergeLIS.firstCd4, mergeLIS.firstCd8, mergeLIS.firstViralLoad);
        parseContacts.run(patientIdPatients, contactenFile);
        
        ParseTherapy parseTherapy = new ParseTherapy();
        parseTherapy.parseTherapy(medFinalFile,filemakerMappingPath);
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
        
        Utils.exportPatientsXML(eadPatients, outputPath + File.separatorChar + "patients.xml");
        Utils.exportNTXMLFromPatients(eadPatients, outputPath + File.separatorChar + "viralisolates.xml");
    }
}
