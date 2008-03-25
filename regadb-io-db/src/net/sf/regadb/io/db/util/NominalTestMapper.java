package net.sf.regadb.io.db.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.sf.regadb.csv.Table;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestNominalValue;
import net.sf.regadb.db.TestResult;

public class NominalTestMapper {
    private Map<String, String> mapping = new HashMap<String, String>();
    private Test test_;
    
    public NominalTestMapper(String mappingFile, Test test) {
        Table mappingTable = Utils.readTable(mappingFile);
        for(int i = 1; i<mappingTable.numRows(); i++) {
            mapping.put(mappingTable.valueAt(0, i), mappingTable.valueAt(1, i));
        }
        test_ = test;
    }
    
    public TestResult createTestResult(Patient p, String val) {
        TestResult t = p.createTestResult(test_);
        TestNominalValue tnv = Utils.getNominalValue(test_.getTestType(), mapping.get(val));
        if(tnv==null)
                return null;
        t.setTestNominalValue(tnv);
        t.setTestDate(new Date());
        return t;
    }
}
