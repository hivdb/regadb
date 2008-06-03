package net.sf.regadb.io.db.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;

import net.sf.regadb.csv.Table;

public class Mappings {
    private String mappingBasePath_;

    private HashMap<String, HashMap<String, String>> mappings_ = new HashMap<String, HashMap<String, String>>();
    
    private Mappings() {
        
    }
    
    public static Mappings getInstance(String basePath) {
        Mappings m = new Mappings();
        m.mappingBasePath_ = basePath;
        return m;
    }
    
    public String getMapping(String fileName, String value) {
        HashMap<String, String> mappings = mappings_.get(fileName);
        if(mappings == null) {
            mappings = new HashMap<String, String>();
            File mappingFile = new File(mappingBasePath_ + File.separatorChar + fileName);
            Table mappingCsvTable = null;
            try {
                mappingCsvTable = new Table(new BufferedInputStream(new FileInputStream(mappingFile)), false);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            
            for(int i = 1; i<mappingCsvTable.numRows(); i++) {
                mappings.put(mappingCsvTable.valueAt(0, i).toUpperCase(), mappingCsvTable.valueAt(1, i).toUpperCase());
            }
            
            mappings_.put(fileName, mappings);
        }
        return mappings.get(value);
    }
}
