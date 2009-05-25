package net.sf.regadb.io.db.ghb.filemaker;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Therapy;
import net.sf.regadb.io.db.ghb.GetViralIsolates;
import net.sf.regadb.io.db.ghb.lis.AutoImport;
import net.sf.regadb.io.db.util.ConsoleLogger;
import net.sf.regadb.io.db.util.mapping.OfflineObjectStore;
import net.sf.regadb.io.util.IOUtils;
import net.sf.regadb.util.settings.RegaDBSettings;


// Files needed from filemaker pro are:
//  - contacten.MER
//  - eadnr_emdnr.MER
//  - medication.MER
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
        String eadNrEmdNrFile;
        String patientenFile;
        String symptomenFile;
        String lisMappingFile;
        String lisNationMappingFile;
        String stalenLeuvenFile;
        String spreadStalenFile;
        String seqsToIgnoreFile;
        String macFastaFile;
        String pcFastaFile;
        String contactenFile;
        String medicatieFile;
        String filemakerMappingPath;
        String outputPath;
        
        String confDir = args[0];
        String importDir = new File(args[1]).getAbsolutePath() + File.separatorChar;
        String workspaceDir = new File(args[2]).getAbsolutePath() + File.separatorChar;
        
        RegaDBSettings.getInstance(confDir);
        
        String lisDir = importDir + "lis" + File.separatorChar;
        String filemakerDir = importDir + "filemaker" + File.separatorChar;
        String seqDir = importDir + "sequences" + File.separatorChar;
        
        eadNrEmdNrFile              = filemakerDir + "eadnr_emdnr.MER";
        patientenFile               = filemakerDir + "patienten.MER";
        symptomenFile               = filemakerDir + "symptomen.MER";
        contactenFile               = filemakerDir + "contacten.MER";
        medicatieFile               = filemakerDir + "medicatie.MER";
        
        filemakerMappingPath        = workspaceDir + "regadb-io-db/src/net/sf/regadb/io/db/ghb/filemaker/mappings/";
        lisMappingFile        		= workspaceDir + "regadb-io-db/src/net/sf/regadb/io/db/ghb/mapping/mapping.xml";
        lisNationMappingFile        = workspaceDir + "regadb-io-db/src/net/sf/regadb/io/db/ghb/mapping/LIS-nation.mapping";
        seqsToIgnoreFile            = workspaceDir + "regadb-io-db/src/net/sf/regadb/io/db/ghb/mapping/sequencesToIgnore.csv";
        
        stalenLeuvenFile            = seqDir + "Stalen Leuven.csv";
        spreadStalenFile            = seqDir + "SPREAD_stalen.csv";
        macFastaFile                = seqDir + "MAC_final.fasta";
        pcFastaFile                 = seqDir + "PC_final.fasta";
        outputPath                  = importDir;
        
        
        
        ParseEadEmd eadEmd = new ParseEadEmd();
        eadEmd.run(eadNrEmdNrFile,patientenFile);
        
        Map<String, Patient> eadPatients = new HashMap<String, Patient>();
        Map<String, Patient> patientIdPatients = new HashMap<String, Patient>();
        for(Entry<String, String> e : eadEmd.eadPatientId.entrySet()) {
            Patient p = new Patient();
            p.setPatientId(e.getKey());
            eadPatients.put(e.getKey(), p);
            patientIdPatients.put(e.getValue(), p);
        }
        
        OfflineObjectStore oos = new OfflineObjectStore();
        oos.setPatients(eadPatients);
        AutoImport ai = new AutoImport(new File(lisMappingFile), new File(lisNationMappingFile), oos);
        try {
			ai.run(new File(lisDir));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
        
        GetViralIsolates gvi = new GetViralIsolates();
        gvi.eadPatients = eadPatients;
        gvi.run(stalenLeuvenFile,spreadStalenFile,seqsToIgnoreFile,macFastaFile,pcFastaFile);
        
        ParsePatient parsePatient = new ParsePatient();
        parsePatient.parse( new File(patientenFile),
                            new File(filemakerMappingPath + "country_of_origin.mapping"),
                            new File(filemakerMappingPath + "geographic_origin.mapping"),
                            new File(filemakerMappingPath + "transmission_group.mapping"), patientIdPatients);
        
        ParseSymptom parseSymptom = new ParseSymptom();
        parseSymptom.parse( new File(symptomenFile),
                            new File(filemakerMappingPath + "aids_defining_illness.mapping"),
                            patientIdPatients);
        
        ParseContacts parseContacts = new ParseContacts(ai.firstCd4, ai.firstCd8, ai.firstViralLoad);
        parseContacts.run(patientIdPatients, contactenFile);
        
        ParseTherapy parseTherapy = new ParseTherapy();
        parseTherapy.parseTherapy(medicatieFile,filemakerMappingPath);
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
