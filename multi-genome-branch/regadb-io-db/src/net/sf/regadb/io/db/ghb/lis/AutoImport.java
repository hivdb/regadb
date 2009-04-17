package net.sf.regadb.io.db.ghb.lis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import net.sf.regadb.csv.Table;
import net.sf.regadb.db.Attribute;
import net.sf.regadb.db.Dataset;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.PatientAttributeValue;
import net.sf.regadb.db.TestNominalValue;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.TestType;
import net.sf.regadb.db.session.Login;
import net.sf.regadb.io.db.ghb.GhbUtils;
import net.sf.regadb.io.db.util.ConsoleLogger;
import net.sf.regadb.io.db.util.Utils;
import net.sf.regadb.io.db.util.mapping.DbObjectStore;
import net.sf.regadb.io.db.util.mapping.ObjectMapper;
import net.sf.regadb.io.db.util.mapping.ObjectStore;
import net.sf.regadb.io.db.util.mapping.ObjectMapper.MappingException;
import net.sf.regadb.io.db.util.mapping.ObjectMapper.ObjectDoesNotExistException;
import net.sf.regadb.io.util.StandardObjects;
import net.sf.regadb.util.mapper.XmlMapper;

public class AutoImport {
    private class FileLogger{
        private PrintStream out;
        
        public FileLogger(File file) throws FileNotFoundException{
            out = new PrintStream(new FileOutputStream(file));
        }
        
        public void print(String msg){
            out.print(msg);
        }
        public void println(String msg){
            out.println(msg);
        }
        
        public void close(){
            out.close();
        }
    }
    
    private FileLogger logger;
    private Date earliestDate = new Date(System.currentTimeMillis());
    
    public Date firstCd4 = new Date();
    public Date firstCd8 = new Date();
    public Date firstViralLoad = new Date();
    public Date firstSeroStatus = new Date();
    
    private Table nationMapping;
    
    //for checking nation codes
    Set<String> temp;
    Set<String> uniqueTests = new TreeSet<String>();
    
    private ObjectMapper objMapper;
    private XmlMapper xmlMapper;
    private ObjectStore objectStore;
    
    public static void main(String [] args) {
        AutoImport ai = new AutoImport(
                new File("/home/simbre1/tmp/ghb-ai-log.txt"),
                new File("/home/simbre1/workspaces/regadb.import/regadb-io-db/src/net/sf/regadb/io/db/ghb/mapping/mapping.xml"),
                new File("/home/simbre1/workspaces/regadb.import/regadb-io-db/src/net/sf/regadb/io/db/ghb/mapping/LIS-nation.mapping"));
                
        ai.run(new File("/home/simbre1/import/ghb/2009-04-07"));
    }
    
    public AutoImport(File logFile, File mappingFile, File nationMappingFile) {
        try {
            logger = new FileLogger(logFile);
            
            xmlMapper = new XmlMapper(mappingFile);
            
            Login login = Login.authenticate("admin", "admin");
            objectStore = new DbObjectStore(login);
            
            objMapper = new ObjectMapper(objectStore, xmlMapper);
            
            nationMapping = Utils.readTable(nationMappingFile.getAbsolutePath());
            
            temp = new HashSet<String>();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public TestNominalValue getNominalValue(TestType tt, String str){
        for(TestNominalValue tnv : tt.getTestNominalValues()){
            if(tnv.getTestType().equals(tt) && tnv.getValue().equals(str)){
                return tnv;
            }
        }
        return null;
    }
    
    public void run(File dir) {
                
        File[] files = dir.listFiles();
        for(final File f : files) {
            if(f.getAbsolutePath().endsWith(".txt") && f.getName().startsWith("GHB")){
                parse(f);
            }
        }
        
        for(String s: temp) {
            System.err.println(s);
        }
        
        System.err.println("--- tests ---");
        for(String s : uniqueTests)
            System.err.println(s);
        System.err.println("--- /tests ---");
        
        logger.close();
    }
    
    private void parse(File file){
        ConsoleLogger.getInstance().logWarning("processing file: "+ file.getAbsolutePath());
        try {
            InputStream is = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            String line = br.readLine();
            if(line != null){
                Map<String, Integer> headers = new HashMap<String, Integer>(); 
                int i=0;
                for(String s : tokenizeTab(line))
                    headers.put(s, i++);
            
                int n = 1;
                while((line = br.readLine()) != null) {
                    String[] fields = tokenizeTab(line);
                    
                    if(fields.length > 0 && headers.get(fields[0]) == null){
                        Patient p = handlePatient(headers, fields);
                        if(p != null)
                            handleTest(p, headers, fields);
                        objectStore.commit();
                    }
                    ++n;
                }
            }
            
            br.close();
            is.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private Patient handlePatient(Map<String,Integer> headers, String[] line) {
        String ead = line[headers.get("EADnr")];
        if(ead == null || ead.length() == 0)
            return null;
        String emd = line[headers.get("EMDnr")];
        Date birthDate = null;
        try {
            birthDate = GhbUtils.LISDateFormat.parse(line[headers.get("geboortedatum")]);
        } catch (ParseException e) {
           e.printStackTrace();
        }
        char sex = line[headers.get("geslacht")].toUpperCase().charAt(0);
        String nation = line[headers.get("nation")].toUpperCase();
        
        
        Patient p = getPatient(ead);
        if(p==null){
            p = createPatient(ead);
        }
        
        Attribute emdAttribute = objectStore.getAttribute("EMD Number", "UZ Leuven");
        Attribute genderAttribute = objectStore.getAttribute(
                StandardObjects.getGenderAttribute().getName(),
                StandardObjects.getGenderAttribute().getAttributeGroup().getGroupName());
        Attribute birthDateAttribute = objectStore.getAttribute("Birth date","Personal");

        if(!containsAttribute(emdAttribute, p))
            p.createPatientAttributeValue(emdAttribute).setValue(emd);
        if(!containsAttribute(genderAttribute, p)){
            try {
                handleNominalAttributeValue(p, genderAttribute.getName(), sex + "");
            } catch (MappingException e) {
                e.printStackTrace();
            }
        }
        if(!containsAttribute(birthDateAttribute, p))
            p.createPatientAttributeValue(birthDateAttribute).setValue(birthDate.getTime()+"");
        
        if(mapCountry(nation)==null) {
            temp.add(nation);
        }
        
        return p;
    }
    
    public boolean containsAttribute(Attribute a, Patient p) {
        String name = a.getName();
        for(PatientAttributeValue pav : p.getPatientAttributeValues()) {
            if(pav.getAttribute().getName().equals(name)) {
                return true;
            }
        }
        return false;
    }
    
    public String mapCountry(String code) {
        for(int i = 1; i<nationMapping.numRows(); i++) {
            if(nationMapping.valueAt(0, i).equals(code)) {
                return nationMapping.valueAt(1, i);
            }
        }
        return null;
    }
    
    private void handleTest(Patient p, Map<String,Integer> headers, String[] line) {
        String correctId = getCorrectSampleId(headers, line);
        Date sampleDate = null;
        try {
            sampleDate = GhbUtils.LISDateFormat.parse(line[headers.get("afname")]);
            if(sampleDate.before(earliestDate)) {
                earliestDate = sampleDate;
            }
            uniqueTests.add(line[headers.get("aanvraagTestNaam")]);
            
            String reeel = line[headers.get("reeel")];
            String resultaat = line[headers.get("resultaat")];
            
            //work with a mapping files
            if((reeel != null && reeel.length() > 0)
                    || (resultaat != null && resultaat.length() > 0)) {
                
                Map<String,String> variables = new HashMap<String,String>();
                
                variables.put("reeel", reeel);
                variables.put("resultaat",resultaat);
                variables.put("elementNaam", line[headers.get("elementNaam")]);
                variables.put("aanvraagTestNaam", line[headers.get("aanvraagTestNaam")]);
                variables.put("relatie", line[headers.get("relatie")]);
                variables.put("eenheden", line[headers.get("eenheden")]);
                variables.put("relatie+reeel", line[headers.get("relatie")]+reeel);
                
                double ul = 0;
                try{
                    ul = Double.parseDouble(reeel) * 1000;
                }
                catch(Exception e){
                }
                variables.put("reeel*1000", ""+ul);
                
                try {
                    TestResult tr = objMapper.getTestResult(variables);
                    if(tr != null){
                        tr.setSampleId(correctId);
                        tr.setTestDate(sampleDate);
                        
                        if(duplicateTestResult(p, tr))
                            return;
                        
                        p.addTestResult(tr);
                    }
                } catch (MappingException e) {
                    log(line.toString());
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
       
    private String getCorrectSampleId(Map<String,Integer> headers, String[] line) {
        String id = line[headers.get("otheeknr")];
        if(id.equals("")) {
            id = line[headers.get("staalId")];
        }
        if(id.equals("")) {
            id = line[headers.get("metingId")];
        }
        if(id.equals("")) {
           id = line[headers.get("berekeningId")];
        }

        return id;
    }
    
    private void handleNominalAttributeValue(Patient p, String name, String nominalValue) throws MappingException {
        try {
            Map<String,String> variables = new HashMap<String,String>();
            variables.put("name", name);
            variables.put("value", nominalValue);
            PatientAttributeValue pav = objMapper.getAttributeValue(variables);
            p.addPatientAttributeValue(pav);

        } catch (ObjectDoesNotExistException e) {
            ConsoleLogger.getInstance().logWarning("Unsupported attribute value" + name + ": "+nominalValue);
        }
    }
    
    private String[] tokenizeTab(String line) {
        return line.split("\t");
    }
    
    private boolean duplicateTestResult(Patient p, TestResult result) {
        for(TestResult tr : p.getTestResults()) {
            if(tr.getTest().getDescription().equals(result.getTest().getDescription()) &&
                    tr.getTestDate().equals(result.getTestDate()) &&
                    tr.getSampleId().equals(result.getSampleId())) {
                if(tr.getTestNominalValue() == null)
                    return tr.getValue().equals(result.getValue());
                else if(result.getTestNominalValue() != null)
                    return tr.getTestNominalValue().getValue().equals(result.getTestNominalValue().getValue());
                return false;
            }
        }
        return false;
    }
    
    private Patient getPatient(String ead){
        Dataset dataset = objectStore.getDataset("GHB");
        return objectStore.getPatient(dataset, ead);
    }
    private Patient createPatient(String ead){
        Dataset dataset = objectStore.getDataset("GHB");
        return objectStore.createPatient(dataset, ead);
    }
    
    private void log(String msg){
        logger.println(msg);
    }
}
