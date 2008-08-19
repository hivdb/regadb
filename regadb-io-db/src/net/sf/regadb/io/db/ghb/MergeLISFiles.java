package net.sf.regadb.io.db.ghb;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import net.sf.regadb.csv.Table;
import net.sf.regadb.db.Attribute;
import net.sf.regadb.db.AttributeGroup;
import net.sf.regadb.db.AttributeNominalValue;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.PatientAttributeValue;
import net.sf.regadb.db.TestNominalValue;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.TestType;
import net.sf.regadb.io.db.util.ConsoleLogger;
import net.sf.regadb.io.db.util.NominalAttribute;
import net.sf.regadb.io.db.util.Utils;
import net.sf.regadb.io.db.util.file.ILineHandler;
import net.sf.regadb.io.db.util.file.ProcessFile;
import net.sf.regadb.io.util.StandardObjects;

public class MergeLISFiles {
    public Map<String, Patient> patients = new HashMap<String, Patient>();
    public Map<String, Patient> lisOnlyPatients = new HashMap<String, Patient>();
    
    private Date earliestDate = new Date(System.currentTimeMillis());
    
    private Attribute emdAttribute;
    private AttributeGroup ghbAttributeGroup = new AttributeGroup("UZ Leuven");
    private AttributeGroup regadbAttributeGroup = new AttributeGroup("RegaDB");
    private NominalAttribute gender;
    
    public Date firstCd4 = new Date();
    public Date firstCd8 = new Date();
    public Date firstViralLoad = new Date();
    public Date firstSeroStatus = new Date();
    
    private Table nationMapping;
    
    private TestNominalValue posSeroStatus_;
    private TestNominalValue negSeroStatus_;
    
    //for checking nation codes
    Set<String> temp;
    Set<String> uniqueTests = new TreeSet<String>();
    
    public static void main(String [] args) {
        MergeLISFiles mlisf;
        if(args.length >= 1)
            mlisf = new MergeLISFiles(args[0]);
        else
            mlisf = new MergeLISFiles("/home/simbre1/workspace/regadb-io-db/src/net/sf/regadb/io/db/ghb/mapping/LIS-nation.mapping");

        if(args.length >= 2)
            mlisf.run(args[1]);
        else
            mlisf.run("/home/simbre1/tmp/import/ghb/");
    }
    
    private List<String> headers_ = new ArrayList<String>();
    private List<String> headers2_ = new ArrayList<String>();
    
    public MergeLISFiles(String listNationMappingFile) {
        emdAttribute = new Attribute();
        emdAttribute.setAttributeGroup(ghbAttributeGroup);
        emdAttribute.setValueType(StandardObjects.getStringValueType());
        emdAttribute.setName("EMD Number");
        
        gender = new NominalAttribute("Gender", -1, new String[] { "M", "V" },
                new String[] { "male", "female" } );
        gender.attribute.setAttributeGroup(regadbAttributeGroup);
        
        nationMapping = Utils.readTable(listNationMappingFile);
        
        temp = new HashSet<String>();
        
        posSeroStatus_ = getNominalValue(StandardObjects.getHiv1SeroStatusTestType(), "Positive");
        negSeroStatus_ = getNominalValue(StandardObjects.getHiv1SeroStatusTestType(), "Negative");
    }
    
    public TestNominalValue getNominalValue(TestType tt, String str){
        for(TestNominalValue tnv : tt.getTestNominalValues()){
            if(tnv.getTestType().equals(tt) && tnv.getValue().equals(str)){
                return tnv;
            }
        }
        return null;
    }
    
    public void run(String workingDir) {
        File dir = new File(workingDir);
        
        ProcessFile pf = new ProcessFile();
        pf.process(new File(dir.getAbsolutePath()+File.separatorChar+"headers.txt"), new ILineHandler(){
            public void handleLine(String line, int counter) {
                headers_.addAll(tokenizeTab(line));
            }
        });

        ProcessFile pf2 = new ProcessFile();
        pf2.process(new File(dir.getAbsolutePath()+File.separatorChar+"headers_20080422.txt"), new ILineHandler(){
            public void handleLine(String line, int counter) {
                headers2_.addAll(tokenizeTab(line));
            }
        });
                
        File[] files = dir.listFiles();
        for(final File f : files) {
            if(f.getAbsolutePath().endsWith(".txt")) {
                pf.process(f, new ILineHandler() {
                    public void handleLine(String line, int counter) {
                        if(!line.startsWith("EADnr\tEMDnr") && line.length()!=0) {
                         List<String> list = tokenizeTab(line);
                             if(list.size()==headers_.size()) {
                                 String ead = list.get(headers_.indexOf("EADnr"));
                                 
                                 handlePatient(headers_, ead, list);
                                 if(lisOnlyPatients.containsKey(ead))
                                     handleTest(headers_, ead, list);
                             } else if(list.size()==headers2_.size()) {
                                 String ead = list.get(headers2_.indexOf("EADnr"));
                                 
                                 handlePatient(headers2_, ead, list);
                                 if(lisOnlyPatients.containsKey(ead))
                                     handleTest(headers2_, ead, list);
                             } else {
                                 System.err.println("Incorrect amount of columns in file " + f.getName() + " on line number" + counter); 
                             }
                        }
                    }
                });
            }
        }
        
        for(String s: temp) {
            System.err.println(s);
        }
        
        System.err.println("--- tests ---");
        for(String s : uniqueTests)
            System.err.println(s);
        System.err.println("--- /tests ---");
    }
    
    private void handlePatient(List<String> headers, String ead, List<String> line) {
    	if(ead == null || ead.length() == 0)
    		return;
        String emd = line.get(headers.indexOf("EMDnr"));
        Date birthDate = null;
        try {
            birthDate = GhbUtils.LISDateFormat.parse(line.get(headers.indexOf("geboortedatum")));
        } catch (ParseException e) {
           e.printStackTrace();
        }
        char sex = line.get(headers.indexOf("geslacht")).toUpperCase().charAt(0);
        String nation = line.get(headers.indexOf("nation")).toUpperCase();
        
        Patient p = patients.get(ead);
        if(p==null){
            p = lisOnlyPatients.get(ead);
            if(p == null){
                p = new Patient();
                p.setPatientId(ead);
                lisOnlyPatients.put(ead, p);
            }
        }
        
        p.setBirthDate(birthDate);
        if(!containsAttribute(emdAttribute, p))
            p.createPatientAttributeValue(emdAttribute).setValue(emd);
        if(!containsAttribute(gender.attribute, p))
            handleNominalAttributeValue(gender, p, sex + "");
        if(mapCountry(nation)==null) {
            temp.add(nation);
        }
    }
    
    public boolean containsAttribute(Attribute a, Patient p) {
        for(PatientAttributeValue pav : p.getPatientAttributeValues()) {
            if(pav.getAttribute().getName().equals(a.getName())) {
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
    
    private void handleTest(List<String> headers, String ead, List<String> line) {
    	if(ead == null || ead.length() == 0)
    		return;
    	
        Patient p = lisOnlyPatients.get(ead);
        if(p == null)
            return;
        
        String correctId = getCorrectSampleId(headers, line);
        Date sampleDate = null;
        try {
            sampleDate = GhbUtils.LISDateFormat.parse(line.get(headers.indexOf("afname")));
            if(sampleDate.before(earliestDate)) {
                earliestDate = sampleDate;
            }
            uniqueTests.add(line.get(headers.indexOf("aanvraagTestNaam")));
            
            //work with a mapping files
            if(!line.get(headers.indexOf("reeel")).equals("")) {
                //TODO change this to handle all tests from the moment the LIS query returns only HIV infected patients
                patients.put(ead, p);
                
                if(line.get(headers.indexOf("aanvraagTestNaam")).contains("CD4(+) T cellen")) {
                    storeCD4(Double.parseDouble(line.get(headers.indexOf("reeel"))), sampleDate, p, correctId);
                } else if(line.get(headers.indexOf("aanvraagTestNaam")).contains("CD8(+) T cellen")) {
                    storeCD8(Double.parseDouble(line.get(headers.indexOf("reeel"))), sampleDate, p, correctId);
                } else if(line.get(headers.indexOf("aanvraagTestNaam")).contains("HIV-1 viral load")) {
                    storeViralLoad(line.get(headers.indexOf("relatie")) + Double.parseDouble(line.get(headers.indexOf("reeel"))), sampleDate, p, correctId);
                } else {
                    //System.err.println(line.get(headers.indexOf("aanvraagTestNaam")));
                } 
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    
    private void storeCD4(double value, Date date, Patient p, String sampleId) {
        TestResult t = p.createTestResult(StandardObjects.getGenericCD4Test());
        t.setValue(value+"");
        t.setTestDate(date);
        t.setSampleId(sampleId);
        if(date.before(firstCd4)) {
            firstCd4 = date;
        }
    }
    
    private void storeCD8(double value, Date date, Patient p, String sampleId) {
        TestResult t = p.createTestResult(StandardObjects.getGenericCD8Test());
        t.setValue(value+"");
        t.setTestDate(date);
        t.setSampleId(sampleId);
        if(date.before(firstCd8)) {
            firstCd8 = date;
        }
    }
    
    private void storeViralLoad(String value, Date date, Patient p, String sampleId) {
        TestResult t = p.createTestResult(StandardObjects.getGenericHiv1ViralLoadTest());
        t.setValue(value);
        t.setTestDate(date);
        t.setSampleId(sampleId);
        if(date.before(firstViralLoad)) {
            firstViralLoad = date;
        }
    }
    
    private String getCorrectSampleId(List<String> headers, List<String> line) {
        String id = line.get(headers.indexOf("otheeknr"));
        if(id.equals("")) {
            id = line.get(headers.indexOf("staalId"));
        }
        if(id.equals("")) {
            id = line.get(headers.indexOf("metingId"));
        }
        if(id.equals("")) {
           id = line.get(headers.indexOf("berekeningId"));
        }

        return id;
    }
    
    private void handleNominalAttributeValue(NominalAttribute attribute, Patient p, String nominalValue) {
        AttributeNominalValue vv = attribute.nominalValueMap.get(nominalValue);
        
        if (vv != null) {
            PatientAttributeValue v = p.createPatientAttributeValue(gender.attribute);
            v.setAttributeNominalValue(vv);
        } else {
            ConsoleLogger.getInstance().logWarning("Unsupported attribute value" + gender.attribute.getName() + ": "+nominalValue);
        }
    }
    
    private List<String> tokenizeTab(String line) {
        List<String> list = new ArrayList<String>();
        int formerTab = -1;
        char c;
        for(int i = 0; i<line.length(); i++) {
            c = line.charAt(i);
            if(c=='\t') {
                list.add(line.substring(formerTab+1, i));
                formerTab = i;
            }
        }
        list.add(line.substring(formerTab+1, line.length()));
        
        return list;
    }

    public Map<String, Patient> getPatients() {
        return patients;
    }
}
