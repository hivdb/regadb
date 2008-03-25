package net.sf.regadb.io.db.uzbrussel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import net.sf.regadb.db.Patient;

public class ParseAll {
    public static void main(String [] args) {
        Properties props = System.getProperties();
        props.put("http.proxyHost", "www-proxy");
        props.put("http.proxyPort", "3128");
        
        String baseDir = "/home/plibin0/import/jette/import/cd/080320/";
        
        Map<Integer, List<String>> consultCodeHistory = new HashMap<Integer, List<String>>();
        Map<Integer, String> codepat = new HashMap<Integer, String>();
        ParseIds parseIds = new ParseIds(baseDir, consultCodeHistory, codepat);
        parseIds.exec();
        
        Map<Integer, Patient> patients = new HashMap<Integer, Patient>();
        ParseConsultDB parseDB = new ParseConsultDB(baseDir,patients, parseIds);
        parseDB.exec();
        
        ParseConfirmation pc = new ParseConfirmation(baseDir, parseIds, patients);
        pc.exec();
        
        ParseSeqs parseSeqs = new ParseSeqs(baseDir,parseIds, patients);
        parseSeqs.exec();
    }
}
