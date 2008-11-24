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
import net.sf.regadb.io.db.util.ConsoleLogger;
import net.sf.regadb.io.util.IOUtils;


// Files needed from filemaker pro are:
//  - contacten.MER
//  - eadnr_emdnr.MER
//  - med_final.MER
//  - patienten.MER
//  - symptomen.MER
//
// export them using this format:
//  - Merge
//  - formatted
//  - windows ansi charset
//

public class ParseAll {
    private static String charset = "ISO-8859-15";
    private static char delimiter = ';';
    
    public static void main(String [] args) {
        String eclipseFileMakerMappingDir;
        String eadEmdNameFile;
        String patientenFile;
        String symptomenFile;
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
        
        String importDir = args[0];
        String workspace = args[1];
        
            eclipseFileMakerMappingDir  = workspace + "/regadb-io-db/src/net/sf/regadb/io/db/ghb/filemaker/mappings/";
            eadEmdNameFile              = importDir + "/import/ghb/filemaker/eadnr_emdnr.MER";
            patientenFile               = importDir + "/import/ghb/filemaker/patienten.MER";
            symptomenFile               = importDir + "/import/ghb/filemaker/symptomen.MER";
            lisNationMappingFile        = workspace + "/regadb-io-db/src/net/sf/regadb/io/db/ghb/mapping/LIS-nation.mapping";
            lisWorkingDir               = importDir + "/import/ghb/";
            stalenLeuvenFile            = importDir + "/import/ghb/seqs/Stalen Leuven.csv";
            spreadStalenFile            = importDir + "/import/ghb/seqs/SPREAD_stalen.csv";
            seqsToIgnoreFile            = workspace + "/regadb-io-db/src/net/sf/regadb/io/db/ghb/mapping/sequencesToIgnore.csv";
            macFastaFile                = importDir + "/import/ghb/seqs/MAC_final.fasta";
            pcFastaFile                 = importDir + "/import/ghb/seqs/PC_final.fasta";
            contactenFile               = importDir + "/import/ghb/filemaker/contacten.MER";
            medFinalFile                = importDir + "/import/ghb/filemaker/med_final.MER";
            filemakerMappingPath        = workspace + "/regadb-io-db/src/net/sf/regadb/io/db/ghb/filemaker/mappings/";
            outputPath                  = importDir + "/import/ghb/xmlOutput/";
        
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
        parsePatient.parse( new File(patientenFile),
                            new File(eclipseFileMakerMappingDir + "country_of_origin.mapping"),
                            new File(eclipseFileMakerMappingDir + "geographic_origin.mapping"),
                            new File(eclipseFileMakerMappingDir + "transmission_group.mapping"), patientIdPatients);
        
        ParseSymptom parseSymptom = new ParseSymptom();
        parseSymptom.parse( new File(symptomenFile),
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
        
        IOUtils.exportPatientsXML(eadPatients, outputPath + File.separatorChar + "patients.xml", ConsoleLogger.getInstance());
        IOUtils.exportNTXMLFromPatients(eadPatients, outputPath + File.separatorChar + "viralisolates.xml", ConsoleLogger.getInstance());
    }

    public static void setCharset(String charset) {
        ParseAll.charset = charset;
    }

    public static String getCharset() {
        return charset;
    }

    public static void setDelimiter(char delimiter) {
        ParseAll.delimiter = delimiter;
    }

    public static char getDelimiter() {
        return delimiter;
    }
}
