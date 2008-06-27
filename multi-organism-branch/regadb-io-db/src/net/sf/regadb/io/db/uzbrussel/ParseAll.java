package net.sf.regadb.io.db.uzbrussel;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import net.sf.regadb.db.Patient;
import net.sf.regadb.io.db.util.ConsoleLogger;
import net.sf.regadb.io.util.IOUtils;

public class ParseAll {
    public static void main(String [] args) {
    	if(args.length >= 3){
    		String baseDir = args[0];
    		String mappingDir = args[1];
    		String regadbXmlFile = args[2];
    		String proxyHost=null;
    		String proxyPort=null;
    		
    		if(args.length >= 5){
    			proxyHost = args[3];
    			proxyPort = args[4];
    		}
    		
    		exec(baseDir,mappingDir,proxyHost,proxyPort,regadbXmlFile);
    	}
    	else{
	    	exec(	"/home/plibin0/import/jette/import/cd/080417/",
	    			"/home/plibin0/myWorkspace/regadb-io-db/src/net/sf/regadb/io/db/uzbrussel/mappings",
	    			"www-proxy",
	    			"3128",
	    			"/home/plibin0/Desktop/" + File.separatorChar + "patients.xml");
    	}
    }
    
    public static void exec(String baseDir, String mappingDir, String proxyHost, String proxyPort, String regadbXmlFile) {
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
        povl.run("/home/plibin0/import/jette/old_vl/", parseIds, patients);
        
        ParseConfirmation pc = new ParseConfirmation(baseDir, parseIds, patients);
        pc.exec();
        
        ParseSeqs parseSeqs = new ParseSeqs(baseDir,parseIds, patients);
        parseSeqs.exec();
        
        IOUtils.exportPatientsXMLI(patients, regadbXmlFile, ConsoleLogger.getInstance());
    }
}
