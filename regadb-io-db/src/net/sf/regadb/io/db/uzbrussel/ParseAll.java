package net.sf.regadb.io.db.uzbrussel;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import net.sf.regadb.db.Patient;
import net.sf.regadb.io.db.util.Utils;

public class ParseAll {
    public static void main(String [] args) {
    	exec(	"/home/plibin0/import/jette/import/cd/080321/",
    			"/home/plibin0/myWorkspace/regadb-io-db/src/net/sf/regadb/io/db/uzbrussel/mappings",
    			"www-proxy",
    			"3128",
    			"/home/plibin0/Desktop/" + File.separatorChar + "patients.xml");
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
        
        ParseConfirmation pc = new ParseConfirmation(baseDir, parseIds, patients);
        pc.exec();
        
        ParseSeqs parseSeqs = new ParseSeqs(baseDir,parseIds, patients);
        parseSeqs.exec();
        
        Utils.exportPatientsXMLI(patients, regadbXmlFile);
    }
}
