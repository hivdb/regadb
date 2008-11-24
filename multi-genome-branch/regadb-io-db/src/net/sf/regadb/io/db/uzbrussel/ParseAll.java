package net.sf.regadb.io.db.uzbrussel;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FileUtils;

import net.sf.regadb.db.Patient;
import net.sf.regadb.io.db.util.ConsoleLogger;
import net.sf.regadb.io.util.IOUtils;

public class ParseAll {
    public static void main(String [] args) {
    	if(args.length >= 3){
    		String baseDir = args[0];
    		String mappingDir = args[1];
    		String regadbXmlFile = args[2];
    		String regadbVIXmlFile = args[3];
    		String proxyHost=null;
    		String proxyPort=null;
    		
    		UZBrusselAutoImport.splitExcelFile(baseDir);
    		
    		if(args.length >= 6){
    			proxyHost = args[4];
    			proxyPort = args[5];
    		}
    		
    		exec(baseDir,mappingDir,proxyHost,proxyPort,regadbXmlFile, regadbVIXmlFile);
    	}
    	else{
    		System.err.println("Usage baseDir mappingDir patientXml viralIsolateXml [proxyHost proxyPort]");
//    		UZBrusselAutoImport.splitExcelFile("/home/plibin0/import/jette/import/cd/080420/");
//	    	try {
//				String patientdb = FileUtils.readFileToString(new File("/home/plibin0/import/jette/import/cd/080420/emd/patientdb.xml"));
//				patientdb = patientdb.replace('é', 'e');
//				FileUtils.writeStringToFile(new File("/home/plibin0/import/jette/import/cd/080420/emd/patientdb.xml"), patientdb);
//	    	} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//    		exec(	"/home/plibin0/import/jette/import/cd/080420/",
//	    			"/home/plibin0/myWorkspace/regadb-io-db/src/net/sf/regadb/io/db/uzbrussel/mappings",
//	    			"www-proxy",
//	    			"3128",
//	    			"/home/plibin0/Desktop/" + File.separatorChar + "patients-uzbrussel.xml",
//	    			"/home/plibin0/Desktop/" + File.separatorChar + "vi-uzbrussel.xml");
    	}
    }
    
    public static void exec(String baseDir, String mappingDir, String proxyHost, String proxyPort, String regadbXmlFile, String viXmlFile) {
        if(proxyHost!=null) {
	    	Properties props = System.getProperties();
	        props.put("http.proxyHost", proxyHost);
	        props.put("http.proxyPort", proxyPort);
        }
        
        Map<Integer, List<String>> consultCodeHistory = new HashMap<Integer, List<String>>();
        Map<Integer, String> codepat = new HashMap<Integer, String>();
        ParseIds parseIds = new ParseIds(baseDir, consultCodeHistory, codepat);
        parseIds.exec();
        
        Map<Integer, Patient> patients = new HashMap<Integer, Patient>();
        ParseConsultDB parseDB = new ParseConsultDB(baseDir,patients, parseIds, mappingDir, codepat);
        parseDB.exec();
        
        ParseOldViralLoad povl = new ParseOldViralLoad();
        povl.run(baseDir + File.separatorChar + "old_vl", parseIds, patients, new File(baseDir+"emd" + File.separatorChar + "ignoreOldViralLoad.csv"));
        
        ParseConfirmation pc = new ParseConfirmation(baseDir, parseIds, patients);
        pc.exec();
        
        ParseSeqs parseSeqs = new ParseSeqs(baseDir,parseIds, patients, povl.seqMathOldVL);
        parseSeqs.exec();
        
        IOUtils.exportPatientsXMLI(patients, regadbXmlFile, ConsoleLogger.getInstance());
        IOUtils.exportViralIsolatesXMLFromPatientsI(patients, viXmlFile, ConsoleLogger.getInstance());
    }
}
