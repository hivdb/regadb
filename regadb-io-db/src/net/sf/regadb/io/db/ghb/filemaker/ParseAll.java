package net.sf.regadb.io.db.ghb.filemaker;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import jxl.read.biff.BiffException;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestObject;
import net.sf.regadb.db.TestType;
import net.sf.regadb.db.Therapy;
import net.sf.regadb.db.ValueType;
import net.sf.regadb.io.db.ghb.GetViralIsolates;
import net.sf.regadb.io.db.ghb.ImportKwsContacts;
import net.sf.regadb.io.db.ghb.lis.AutoImport;
import net.sf.regadb.io.db.util.ConsoleLogger;
import net.sf.regadb.io.db.util.DrugsTimeLine;
import net.sf.regadb.io.db.util.mapping.OfflineObjectStore;
import net.sf.regadb.io.util.IOUtils;
import net.sf.regadb.io.util.StandardObjects;
import net.sf.regadb.util.args.Arguments;
import net.sf.regadb.util.args.PositionalArgument;
import net.sf.regadb.util.args.ValueArgument;
import net.sf.regadb.util.mapper.XmlMapper.MapperParseException;
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
        String kwsContactenFile;
        String outputPath;
        
        Arguments as = new Arguments();
        ValueArgument confDir = as.addValueArgument("c", "configuration-dir", false);
        PositionalArgument importDir = as.addPositionalArgument("import-dir", true);
        PositionalArgument workspaceDir = as.addPositionalArgument("workspace-dir", true);
        PositionalArgument datasetDescription = as.addPositionalArgument("dataset", false);
        datasetDescription.setValue("KUL");
        
        if(!as.handle(args))
        	return;
        
        if(confDir.isSet())
        	RegaDBSettings.createInstance(confDir.getValue());
        else
        	RegaDBSettings.createInstance();
        
        String lisDir = importDir.getValue() + File.separatorChar + "lis" + File.separatorChar;
        String filemakerDir = importDir.getValue() + File.separatorChar + "filemaker" + File.separatorChar;
        String seqDir = importDir.getValue() + File.separatorChar + "sequences" + File.separatorChar;
        
        eadNrEmdNrFile              = filemakerDir + "eadnr_emdnr.MER";
        patientenFile               = filemakerDir + "patienten.MER";
        symptomenFile               = filemakerDir + "symptomen.MER";
        contactenFile               = filemakerDir + "contacten.MER";
        medicatieFile               = filemakerDir + "medicatie.MER";
        
        filemakerMappingPath        = workspaceDir.getValue() + File.separatorChar + "regadb-io-db/src/net/sf/regadb/io/db/ghb/filemaker/mappings/";
        lisMappingFile        		= workspaceDir.getValue() + File.separatorChar + "regadb-io-db/src/net/sf/regadb/io/db/ghb/mapping/mapping.xml";
        lisNationMappingFile        = workspaceDir.getValue() + File.separatorChar + "regadb-io-db/src/net/sf/regadb/io/db/ghb/mapping/LIS-nation.mapping";
        seqsToIgnoreFile            = workspaceDir.getValue() + File.separatorChar + "regadb-io-db/src/net/sf/regadb/io/db/ghb/mapping/sequencesToIgnore.csv";
        
        stalenLeuvenFile            = seqDir + "Stalen Leuven.csv";
        spreadStalenFile            = seqDir + "SPREAD_stalen.csv";
        macFastaFile                = seqDir + "MAC_final.fasta";
        pcFastaFile                 = seqDir + "PC_final.fasta";
        outputPath                  = importDir.getValue();
        
        
        
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
        createNonStandardObjects(oos);
        oos.setPatients(eadPatients);
        try{
        	try {
        		ImportKwsContacts ikc = new ImportKwsContacts(oos, datasetDescription.getValue());
        		for(File f : new File(importDir.getValue()).listFiles()){
        			if(f.getName().toLowerCase().endsWith(".xls")
        					&& f.getName().toLowerCase().startsWith("contacten"))
        				ikc.run(f);
        		}
			} catch (BiffException e1) {
				e1.printStackTrace();
			}
        	
        	AutoImport ai = new AutoImport(new File(lisMappingFile), new File(lisNationMappingFile), oos, datasetDescription.getValue());
			ai.run(new File(lisDir));
        
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
	        parseTherapy.parse(new File(medicatieFile),new File(filemakerMappingPath));
	        for(Map.Entry<String, DrugsTimeLine> e : parseTherapy.getDrugsTimeLines().entrySet()) {
	            Patient p = patientIdPatients.get(e.getKey());
	            if(p!=null) {
	                for(Therapy t : e.getValue().getTherapies())
	                    p.getTherapies().add(t);
	            } else {
	                System.err.println("invalid patient id: " + e.getKey());
	            }
	        }
	        
	        GetViralIsolates gvi = new GetViralIsolates();
	        gvi.eadPatients = eadPatients;
	        gvi.run(stalenLeuvenFile,spreadStalenFile,seqsToIgnoreFile,macFastaFile,pcFastaFile);
	        
	        IOUtils.exportPatientsXML(eadPatients.values(), outputPath + File.separatorChar + "patients.xml", ConsoleLogger.getInstance());
	        IOUtils.exportNTXMLFromPatients(eadPatients.values(), outputPath + File.separatorChar + "viralisolates.xml", ConsoleLogger.getInstance());
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (MapperParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
    
    private static TestType createNominalTestType(OfflineObjectStore oos, String testTypeDescr, String... values){
    	TestObject patient = oos.getTestObject(StandardObjects.getPatientTestObject().getDescription());
    	ValueType nominal = oos.getValueType(StandardObjects.getNominalValueType().getDescription());
    	TestType tt = oos.createTestType(testTypeDescr, patient, null, nominal);
    	
    	for(String value : values)
    		oos.createTestNominalValue(tt,value);
    	return tt;
    }
    
    private static TestType createLimitedNumberTestType(OfflineObjectStore oos, String testTypeDescr){
    	TestObject patient = oos.getTestObject(StandardObjects.getPatientTestObject().getDescription());
    	ValueType nominal = oos.getValueType(StandardObjects.getLimitedNumberValueType().getDescription());
    	TestType tt = oos.createTestType(testTypeDescr, patient, null, nominal);    	
    	return tt;
    }

    private static TestType createNumberTestType(OfflineObjectStore oos, String testTypeDescr){
    	TestObject patient = oos.getTestObject(StandardObjects.getPatientTestObject().getDescription());
    	ValueType nominal = oos.getValueType(StandardObjects.getNumberValueType().getDescription());
    	TestType tt = oos.createTestType(testTypeDescr, patient, null, nominal);    	
    	return tt;
    }

    private static Test createNominalTest(OfflineObjectStore oos, String testTypeDescr, String testDescr, String... values){
    	TestType tt = createNominalTestType(oos, testTypeDescr, values);
    	return oos.createTest(tt, testDescr);
    }

    private static Test createBooleanTest(OfflineObjectStore oos, String testTypeDescr, String testDescr){
    	TestType tt = createNominalTestType(oos, testTypeDescr, new String[]{"Positive","Negative"});
    	return oos.createTest(tt, testDescr);
    }

    private static Test createLimitedNumberTest(OfflineObjectStore oos, String testTypeDescr, String testDescr){
    	TestType tt = createLimitedNumberTestType(oos, testTypeDescr);
    	return oos.createTest(tt, testDescr);
    }
    
    private static Test createNumberTest(OfflineObjectStore oos, String testTypeDescr, String testDescr){
    	TestType tt = createNumberTestType(oos, testTypeDescr);
    	return oos.createTest(tt, testDescr);
    }
    
    private static void createTests(OfflineObjectStore oos, TestType tt, String... testDescrs){
    	for(String testDescr : testDescrs){
    		oos.createTest(tt, testDescr);
    	}
    }
    
    private static void createNonStandardObjects(OfflineObjectStore oos){
    	TestType tt;

//    	createBooleanTest(oos,"Syphilis","Syphilis (generic)");
//    	
//    	Test hcvab = createBooleanTest(oos,"HCVAb (presence)","HCVAb (presence) (generic)");
//    	oos.createTest(hcvab.getTestType(),"HCVAb (presence) (Monolisa)");
//    	
//    	createBooleanTest(oos,"HBcAb (presence)","HBcAb (presence) (generic)");
//    	createBooleanTest(oos,"HBsAg (presence)","HBsAg (presence) (generic)");
//    	createBooleanTest(oos,"HAVAb (presence)","HAVAb (presence) (generic)");
    	
    	tt = oos.getTestType(StandardObjects.getContactTestType());
    	createTests(oos, tt,
    			"Consultation",
    			"Hospitalisation");
    	
    	tt = createNumberTestType(oos, "Absolute lymfocytose (bloed)");
    	createTests(oos, tt,
    			"Absolute lymfocytose (bloed) (cells/ul)",
    			"Absolute lymfocytose (bloed) (cells/mm3)");
    	
    	tt = oos.getTestType(StandardObjects.getCd4PercentageTestType());
    	createTests(oos, tt,
    			"CD3/CD4-plot (bloed)",
    			"CD4(+) T cellen (bloed)",
    			"T lymfocyten subpopulaties (bloed)",
    			"CD4 en CD8 T cellen (bloed)");
    	
    	tt = oos.getTestType(StandardObjects.getCd4TestType());
    	createTests(oos, tt,
    			"Absolute T4-lymfocytose (bloed)",
    			"CD3/CD4-plot (bloed)",
    			"CD4 en CD8 T cellen (bloed)",
    			"CD4(+) T cellen (bloed)");
    	
    	createNumberTest(oos, "CD4/CD8 (ratio)", "CD4/CD8 (ratio)");
    	
    	tt = oos.getTestType(StandardObjects.getCd8PercentageTestType());
    	createTests(oos, tt,
    			"CD3/CD8-plot (bloed)",
    			"CD8(+) T cellen (bloed)",
    			"T lymfocyten subpopulaties (bloed)",
    			"CD4 en CD8 T cellen (bloed)");
    	
    	tt = oos.getTestType(StandardObjects.getCd8TestType());
    	createTests(oos, tt,
    			"CD4 en CD8 T cellen (bloed)",
    			"CD3/CD8-plot (bloed)",
    			"CD8(+) T cellen (bloed)");
    	
    	tt = createNominalTestType(oos, "HBcAb (qualitative)",
    			"Positive",
    			"Negative",
    			"Positive (weak)");
    	createTests(oos, tt,
    			"Hepatitis B core antistoffen",
    			"Hepatitis B core antistoffen (Architect)");
    	
    	createNominalTest(oos, "HBc IgM (qualitative)", "Hepatitis B core IgM",
    			"Positive",
    			"Negative");
    	
    	tt = createLimitedNumberTestType(oos, "HBsAb (IU/L)");
    	createTests(oos, tt,
    			"Hepatitis B surface antistoffen (IU/L)",
    			"Hepatitis B surface antistoffen (Architect) (IU/L)");
    	
    	tt = createNominalTestType(oos, "HIV Ab/Ag (qualitative)",
    			"Positive",
    			"Negative",
    			"Positive (weak)",
    			"Positive (possibly false)",
    			"Limit");
    	createTests(oos, tt,
    			"Anti HIV-1/2 Plus (qualitative)",
    			"Enzygnost HIV Integral II (qualitative)",
    			"Enzygnost HIV Integral (qualitative)",
    			"HIV Ab/Ag (Architect) (qualitative)",
    			"HIV Ab/Ag (generic) (qualitative)");
    	
    	tt = createLimitedNumberTestType(oos, "HIV Ab/Ag (S/CO)");
    	createTests(oos, tt,
    			"Anti HIV-1/2 (S/CO)",
    			"Enzygnost HIV Integral (S/CO)",
    			"HIV Ab/Ag (generic) (S/CO)");
    	
    	tt = createLimitedNumberTestType(oos, "HIV Ab/Ag (OD/cutoff)");
    	createTests(oos, tt, "Enzygnost HIV Integral II (OD/cutoff)");
    	
    	tt = createNominalTestType(oos, "CMV IgG (qualitative)",
    			"Positive",
    			"Negative");
    	createTests(oos, tt,
    			"CMV IgG (qualitative)",
    			"CMV IgG (Architect) (qualitative)");
    	
    	tt = createNominalTestType(oos, "Syphilis serology (qualitative)",
    			"Positive",
    			"Negative");
    	createTests(oos, tt,
    			"Syphilis serology FTA (qualitative)",
    			"Syphilis serology RPR (qualitative)",
    			"Syphilis serology RPR (CSV) (qualitative)",
    			"Syphilis serology TPHA (qualitative)",
    			"Syphilis serology VDRL (CSV) (qualitative)",
    			"Syphilis serology VDRL (qualitative)");
    	
    	tt = createNumberTestType(oos, "Syphilis serology (titer)");
    	createTests(oos,tt,
    			"Syphilis serology TPHA (titer)",
    			"Syphilis serology RPR (titer)",
    			"Syphilis serology VDRL (CSV) (titer)",
    			"Syphilis serology VDRL (titer)");
    	
    	
    	tt = oos.getTestType(StandardObjects.getHiv1ViralLoadTestType());
    	createTests(oos, tt, "Abbott RealTime (copies/ml)");
    	
    	tt = oos.getTestType(StandardObjects.getHiv1ViralLoadLog10TestType());
    	createTests(oos, tt, "Abbott RealTime (log10)");
    }
}
