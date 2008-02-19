package net.sf.regadb.io.db.ghb;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import net.sf.regadb.db.Attribute;
import net.sf.regadb.db.AttributeGroup;
import net.sf.regadb.db.AttributeNominalValue;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.PatientAttributeValue;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestNominalValue;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.TestType;
import net.sf.regadb.io.db.util.ConsoleLogger;
import net.sf.regadb.io.db.util.NominalAttribute;
import net.sf.regadb.io.db.util.file.ILineHandler;
import net.sf.regadb.io.db.util.file.ProcessFile;
import net.sf.regadb.io.util.StandardObjects;

public class MergeLISFiles {
    private Map<String, Patient> patients = new HashMap<String, Patient>();
    private static DateFormat LISDateFormat = new SimpleDateFormat("MM/dd/yyyy");
    
    private Date earliestDate = new Date(System.currentTimeMillis());
    
    private static Attribute emdAttribute;
    private static AttributeGroup ghbAttributeGroup = new AttributeGroup("UZ Leuven");
    private static AttributeGroup regadbAttributeGroup = new AttributeGroup("UZ Leuven");
    private static NominalAttribute gender;
    
    private TestType cd8TestType = new TestType(StandardObjects.getNumberValueType(), StandardObjects.getPatientObject(), "CD8 Count", new TreeSet<TestNominalValue>());
    private Test cd8Test = new Test(cd8TestType, "CD8 Count (generic)");
    
    static {
        emdAttribute = new Attribute();
        emdAttribute.setAttributeGroup(ghbAttributeGroup);
        emdAttribute.setValueType(StandardObjects.getStringValueType());
        emdAttribute.setName("EMD Number");
        
        gender = new NominalAttribute("Gender", -1, new String[] { "M", "V" },
                new String[] { "male", "female" } );
        gender.attribute.setAttributeGroup(regadbAttributeGroup);
    }
    
    public static void main(String [] args) {
        MergeLISFiles mlisf = new MergeLISFiles();
        mlisf.run();
    }
    
    private List<String> headers = new ArrayList<String>();
    
    public MergeLISFiles() {

    }
    
    public void run() {
        File dir = new File("/home/plibin0/import/ghb/");
        
        ProcessFile pf = new ProcessFile();
        pf.process(new File(dir.getAbsolutePath()+File.separatorChar+"headers.txt"), new ILineHandler(){
            public void handleLine(String line, int counter) {
                headers.addAll(tokenizeTab(line));
            }
        });
        
        File[] files = dir.listFiles();
        for(final File f : files) {
            if(f.getAbsolutePath().endsWith(".txt")) {
                pf.process(f, new ILineHandler() {
                    public void handleLine(String line, int counter) {
                        if(!line.startsWith("EADnr\tEMDnr") && line.length()!=0) {
                         List<String> list = tokenizeTab(line);
                             if(list.size()==headers.size()) {
                                 String ead = list.get(headers.indexOf("EADnr"));
                                 
                                 handlePatient(ead, list);
                                 handleTest(ead, list);
                             } else {
                                 System.err.println("Incorrect amount of columns in file " + f.getName() + " on line number" + counter); 
                             }
                        }
                    }
                });
            }
        }
    }
    
    private void handlePatient(String ead, List<String> line) {
        String emd = line.get(headers.indexOf("EMDnr"));
        Date birthDate = null;
        try {
            birthDate = LISDateFormat.parse(line.get(headers.indexOf("geboortedatum")));
        } catch (ParseException e) {
           e.printStackTrace();
        }
        char sex = line.get(headers.indexOf("geslacht")).toUpperCase().charAt(0);
        String nation = line.get(headers.indexOf("nation")).toUpperCase();
        
        Patient p = patients.get(ead);
        if(p==null) {
            p = new Patient();
            p.setPatientId(ead);
            p.setBirthDate(birthDate);
            p.createPatientAttributeValue(emdAttribute).setValue(emd);
            handleNominalAttributeValue(gender, p, sex + "");
            //TODO nation
            patients.put(ead, p);
        }
    }
    
    private void handleTest(String ead, List<String> line) {
        String correctId = getCorrectSampleId(line);
        Date sampleDate = null;
        try {
            sampleDate = LISDateFormat.parse(line.get(headers.indexOf("afname")));
            if(sampleDate.before(earliestDate)) {
                earliestDate = sampleDate;
            }
            //work with a mapping files
            if(!line.get(headers.indexOf("reeel")).equals("")) {
                if(line.get(headers.indexOf("aanvraagTestNaam")).contains("CD4(+) T cellen")) {
                    storeCD4(Double.parseDouble(line.get(headers.indexOf("reeel"))), sampleDate, patients.get(ead), correctId);
                } else if(line.get(headers.indexOf("aanvraagTestNaam")).contains("CD8(+) T cellen")) {
                    storeCD8(Double.parseDouble(line.get(headers.indexOf("reeel"))), sampleDate, patients.get(ead), correctId);
                } else if(line.get(headers.indexOf("aanvraagTestNaam")).contains("HIV-1 viral load")) {
                    storeViralLoad(line.get(headers.indexOf("relatie")) + Double.parseDouble(line.get(headers.indexOf("reeel"))), sampleDate, patients.get(ead), correctId);
                } else {
                    System.err.println(line.get(headers.indexOf("aanvraagTestNaam")));
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
    }
    
    private void storeCD8(double value, Date date, Patient p, String sampleId) {
        TestResult t = p.createTestResult(cd8Test);
        t.setValue(value+"");
        t.setTestDate(date);
        t.setSampleId(sampleId);
    }
    
    private void storeViralLoad(String value, Date date, Patient p, String sampleId) {
        TestResult t = p.createTestResult(StandardObjects.getGenericViralLoadTest());
        t.setValue(value);
        t.setTestDate(date);
        t.setSampleId(sampleId);
    }
    
    private String getCorrectSampleId(List<String> line) {
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
