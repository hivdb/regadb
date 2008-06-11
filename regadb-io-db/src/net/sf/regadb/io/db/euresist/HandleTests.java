package net.sf.regadb.io.db.euresist;

import java.sql.ResultSet;
import java.util.Map;

import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.io.util.StandardObjects;

public class HandleTests {
    private ExportDB edb;
    
    public HandleTests(ExportDB edb){
        this.setEdb(edb);
    }
    
    public void run(Map<String,Patient> patients) throws Exception{
        
        String s;
        TestResult tr;
        java.util.Date d;
        
        ResultSet rs = getEdb().getDb().executeQuery("SELECT * FROM Cd4Isolates");
        while(rs.next()){
            s = rs.getString("patientID");
            
            Patient p = patients.get(s);
            if(p == null)
                continue;
            
            d = rs.getDate("date_of_cd4");
            
            tr = p.createTestResult(StandardObjects.getGenericCD4Test());
            tr.setTestDate(d);
            tr.setValue(rs.getString("cd4"));
            
            tr = p.createTestResult(StandardObjects.getGenericCD4PercentageTest());
            tr.setTestDate(d);
            tr.setValue(rs.getString("cd4_percent"));
        }
        rs.close();
        
        Map<String,Test> testMap = getEdb().createTestMap(StandardObjects.getHiv1ViralLoadTestType(), "Methods", "method", "methodID");
        char prefix;
        
        rs = getEdb().getDb().executeQuery("SELECT * FROM ViralLoadIsolates");
        while(rs.next()){
            s = rs.getString("patientID");
            
            Patient p = patients.get(s);
            if(p == null)
                continue;
            
            d = rs.getDate("date_of_viral_load");
            
            prefix = '=';
            if(rs.getBoolean("below_detection"))
                prefix = '<';
            
            if(rs.getBoolean("above_detection"))
                prefix = '>';
            
            tr = p.createTestResult(StandardObjects.getGenericHiv1ViralLoadTest());
            tr.setTestDate(d);
            tr.setValue(prefix + rs.getString("viral_load"));
            
            tr = p.createTestResult(StandardObjects.getGenericHiv1ViralLoadLog10Test());
            tr.setTestDate(d);
            tr.setValue(prefix + rs.getString("viral_load_log"));
        }
        rs.close();
    }

    private void setEdb(ExportDB edb) {
        this.edb = edb;
    }

    private ExportDB getEdb() {
        return edb;
    }

}
