package net.sf.regadb.io.db.jerusalem;

import java.io.File;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.regadb.csv.Table;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.io.db.util.Logging;
import net.sf.regadb.io.db.util.Utils;
import net.sf.regadb.io.util.StandardObjects;

public class ParseTests extends Parser {

    public ParseTests(Logging logger, DateFormat df){
        super(logger,df);
        setName("Tests");
    }
    
    public void run(Map<String,Patient> patients, File sampleFile, File testResultFile, File testFile, File testUnitFile){
        logInfo("Parsing tests...");
        
        if(!check(testFile) || !check(testUnitFile))
            return;
        
        Map<String,Test> tests = createTestMap(testFile, testUnitFile);
        
//        if(!check(sampleFile))
//            parseTestResults(patients, tests, sampleFile);

        if(check(testResultFile)){
            parseTestResults(patients, tests, testResultFile);
        }
    }
    
    private void parseTestResults(Map<String,Patient> patients, Map<String,Test> tests, File testResultFile){
        
        Table testTable = Utils.readTable(testResultFile.getAbsolutePath());
        
        int CId = testTable.findColumn("ID");
        int CSampleNo = testTable.findColumn("SampleNo");
        int CVLDate = testTable.findColumn("VLDate");
        int CVL = testTable.findColumn("VL");
        int CVLSysNo = testTable.findColumn("VLSysNo");
        int CCD4 = testTable.findColumn("CD4");
        int CCD4P = testTable.findColumn("CD4%");
        int CResDate = testTable.findColumn("ResDate");
        int CUnitNo = testTable.findColumn("UnitNo");
        
        for(int i=1; i<testTable.numRows(); ++i){
            String id = testTable.valueAt(CId, i);
            String sampleNo = testTable.valueAt(CSampleNo, i);
            String vlDate = testTable.valueAt(CVLDate, i);
            String vl = testTable.valueAt(CVL, i);
            String vlSysNo = testTable.valueAt(CVLSysNo, i);
            String cd4 = testTable.valueAt(CCD4, i);
            String cd4p = testTable.valueAt(CCD4P, i);
            String resDate = testTable.valueAt(CResDate, i);
            String unitNo = testTable.valueAt(CUnitNo, i);
            
            Patient p = patients.get(id);
            
            if(p != null){
                Date d;
                d = getDate(vlDate);
                if(d != null){
                    
                    if(check(vl)){
                        Test t = tests.get(vlSysNo);
                        
                        if(t == null)
                            t = StandardObjects.getGenericViralLoadTest();
                        
                        createTestResult(p,t,d,getFormattedValue(unitNo, vl),sampleNo);
                    }
                    if(check(cd4)){
                        createTestResult(p,StandardObjects.getGenericCD4Test(),d,cd4,sampleNo);
                    }
                    if(check(cd4p)){
                        createTestResult(p,StandardObjects.getGenericCD4PercentageTest(),d,cd4p,sampleNo);
                    }
                }
                else
                    logWarn("Invalid test date",testResultFile,i,vlDate);
            }
            else{
                logWarn("Invalid patient ID",testResultFile,i,id);
            }
        }
    }
    
    private TestResult createTestResult(Patient p, Test t, Date d, String v, String sampleId){
        TestResult tr = p.createTestResult(t);
        tr.setTestDate(d);
        tr.setValue(v);
        
        if(check(sampleId))
            tr.setSampleId(sampleId);
        
        return tr;
    }
    
    private Map<String,Test> createTestMap(File testFile, File testUnitFile){
        Map<String,Test> res = new HashMap<String,Test>();
        
        Table brands = Utils.readTable(testFile.getAbsolutePath());
        //Table units = Utils.readTable(testUnitFile.getAbsolutePath());
        
        int CVLSysNo = brands.findColumn("VLSysNo");
        int CVLName = brands.findColumn("VLName");
        
        for(int i=1;i<brands.numRows();++i){
            String no = brands.valueAt(CVLSysNo, i);
            String name = brands.valueAt(CVLName, i);
            Test t = new Test(StandardObjects.getViralLoadTestType(),name);
            res.put(no, t);
        }
        
        return res;
    }
    
    private String getFormattedValue(String unitNo, String value){
        String res=null;
        
        int no = 0;
        if(check(unitNo)) no = Integer.parseInt(unitNo);

        switch(no){
        case 0:
        case 1:
            res = "="+ value;
            break;
        case 2:
            res = "<"+ value;
            break;
        case 3:
            res = value;
            break;
        case 4:
            res = value;
            break;
        case 5:
            res = ">"+ value;
            break;
        case 6:
            res = value;
            break;
        }
        
        return res;
    }
}
