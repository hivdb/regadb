package net.sf.regadb.io.db.ghb.filemaker;

import java.text.ParseException;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;

import net.sf.regadb.csv.Table;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestNominalValue;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.meta.Equals;
import net.sf.regadb.io.db.ghb.GhbUtils;
import net.sf.regadb.io.db.util.Utils;
import net.sf.regadb.io.util.StandardObjects;

public class ParseContacts {
    //public Map<String, List<TestResult>> fileMakerTests = new HashMap<String, List<TestResult>>();
    
    private TestNominalValue posSeroStatus_ = Utils.getNominalValue(StandardObjects.getHiv1SeroStatusTestType(), "Positive");
    
    Date firstCd4_;
    Date firstCd8_;
    Date firstViralLoad_;
    
    public ParseContacts(Date firstCd4, Date firstCd8, Date firstViralLoad) {
        firstCd4_ = firstCd4;
        firstCd8_ = firstCd8;
        firstViralLoad_ = firstViralLoad;
    }
        
    public void run(Map<String,Patient> patients, String contactenFile) {
        Table contacts = Utils.readTable(contactenFile, ParseAll.getCharset(), ParseAll.getDelimiter());
        
        int CHIVPos = Utils.findColumn(contacts, "HIV_Status_Staal");
        int CCD4 = Utils.findColumn(contacts, "CD4_Absoluut");
        int CCD4Percent = Utils.findColumn(contacts, "CD4_%");
        int CCD8 = Utils.findColumn(contacts, "CD8_Absoluut");
        int CCD8Percent = Utils.findColumn(contacts, "CD8_%");
        int CViralLoad = Utils.findColumn(contacts, "Viral_Load_Absoluut");
        int CPatientId = Utils.findColumn(contacts, "Patient_ID");
        int CDate = Utils.findColumn(contacts, "Datum");
        //int CContact = Utils.findColumn(contacts, "AlleenContact");
        
        HashSet<String> setset = new HashSet<String>();
        
        for(int i = 1; i<contacts.numRows(); i++) {
            String patientId = contacts.valueAt(CPatientId, i);
            Date date = null;
            try {
                date = GhbUtils.filemakerDateFormat.parse(contacts.valueAt(CDate, i).replace('/', '-'));
            } catch (ParseException e) {
                
            }
            if(!"".equals(patientId) && date!=null) {
                Patient p = patients.get(patientId);
                if(p!=null) {
                	storeContact(date, p);
                	
                    if(!contacts.valueAt(CCD4, i).equals("")) {
                        try{
                        double cd4 = Double.parseDouble(contacts.valueAt(CCD4, i).replace(',', '.'));
                        storeCD4(date, cd4, null, p);
                        } catch(NumberFormatException nfe) {
                            System.err.println("Cannot parse cd4 value " + contacts.valueAt(CCD4, i));
                        }
                    }
                    
                    if(!contacts.valueAt(CCD8, i).equals("")) {
                        try{
                            double cd8 = Double.parseDouble(contacts.valueAt(CCD8, i).replace(',', '.'));
                            storeCD8(date, cd8, null, p);
                            } catch(NumberFormatException nfe) {
                                System.err.println("Cannot parse cd8 value " + contacts.valueAt(CCD8, i));
                            }
                    }
                    
                    if(!contacts.valueAt(CCD4Percent, i).equals("")) {
                        try{
                        double cd4p = Double.parseDouble(contacts.valueAt(CCD4Percent, i).replace(',', '.'));
                        storeCD4Percent(date, cd4p, null, p);
                        } catch(NumberFormatException nfe) {
                            System.err.println("Cannot parse cd4% value " + contacts.valueAt(CCD4Percent, i));
                        }
                    }
                    
                    if(!contacts.valueAt(CCD8Percent, i).equals("")) {
                        try{
                            double cd8p = Double.parseDouble(contacts.valueAt(CCD8Percent, i).replace(',', '.'));
                            storeCD8Percent(date, cd8p, null, p);
                            } catch(NumberFormatException nfe) {
                                System.err.println("Cannot parse cd8% value " + contacts.valueAt(CCD8Percent, i));
                            }
                    }
                    
                    if(!contacts.valueAt(CViralLoad, i).equals(""))
                        storeViralLoad(date, contacts.valueAt(CViralLoad, i), null, p);
                    
                    String sero = contacts.valueAt(CHIVPos, i);
                    if(!sero.equals("")) {
                        if(sero.toLowerCase().contains("hiv") && sero.toLowerCase().contains("positief")) {
                            storePosSero(date, null, p);
                        }
                    }
                } else {
                	if(setset.add(patientId)) {                    
                		System.err.println("invalid patientId****: " + patientId);
                	}
                }
            } else {
                System.err.println("Cannot parse contact, no date or wrong patientId");
            }
        }
    }
    
    private boolean hasTestResultOnDate(Patient p, Test t, Date d){
    	for(TestResult tr : p.getTestResults())
    		if(d.equals(tr.getTestDate()) && Equals.isSameTest(tr.getTest(), t))
    			return true;    			
    	return false;
    }
    
    private void storeTestResult(Date startLis, Test t, Date date, double value, String sampleId, Patient p){
    	if(date.before(startLis) || !hasTestResultOnDate(p, t, date)){
    		TestResult tr = p.createTestResult(t);
            tr.setValue(value+"");
            tr.setTestDate(date);
            tr.setSampleId(sampleId); 
    	}
    }
    
    private void storeCD4(Date date, double value, String sampleId, Patient p) {
    	storeTestResult(firstCd4_, StandardObjects.getGenericCD4Test(), date, value, sampleId, p);
    }
    
    private void storeCD8(Date date, double value, String sampleId, Patient p) {
    	storeTestResult(firstCd8_, StandardObjects.getGenericCD8Test(), date, value, sampleId, p);
    }
    
    private void storeCD4Percent(Date date, double value, String sampleId, Patient p) {
    	storeTestResult(firstCd4_, StandardObjects.getGenericCD4PercentageTest(), date, value, sampleId, p);
    }
    
    private void storeCD8Percent(Date date, double value, String sampleId, Patient p) {
    	storeTestResult(firstCd8_, StandardObjects.getGenericCD8PercentageTest(), date, value, sampleId, p);
    }
    
    private void storeContact(Date date, Patient p){
    	TestResult t = p.createTestResult(StandardObjects.getContactTest());
    	t.setValue(date.getTime()+"");
    	t.setTestDate(date);
    }
    
    private void storePosSero(Date date, String sampleId, Patient p) {
        TestResult t = p.createTestResult(StandardObjects.getGenericHiv1SeroStatusTest());
        t.setTestNominalValue(posSeroStatus_);
        t.setTestDate(date);
        t.setSampleId(sampleId);
    }
    
    private String removeCharsFromString(String src, char toRemove) {
        String toReturn = "";
        for(int i = 0; i<src.length(); i++) {
            if(src.charAt(i)!=toRemove) 
                toReturn += src.charAt(i);
        }
        return toReturn;
    }
    
    private void storeViralLoad(Date date, String value, String sampleId, Patient p) {
        String parsedValue = null;
        char sensChar = ' ';
        if(!Character.isDigit(value.charAt(0))) {
            sensChar = value.charAt(0);
        }

        try {
            if(sensChar==' ') {
                parsedValue = "="+ Double.parseDouble(removeCharsFromString(value,' '));
            }
            else {
                parsedValue = Character.toString(sensChar) + Double.parseDouble(removeCharsFromString(value.substring(1, value.length()), ' '));
                if(sensChar == '>' || sensChar == '<' || sensChar=='=') {
                    
                } else {
                    parsedValue = null;
                }
            }
        } catch(NumberFormatException nfe) {

        }
        
        if(date.before(firstViralLoad_)) {
            if(parsedValue!=null) {
                TestResult t = p.createTestResult(StandardObjects.getGenericHiv1ViralLoadTest());
                t.setValue(parsedValue+"");
                t.setTestDate(date);
                t.setSampleId(sampleId);
            } else {
                System.err.println("Cannot parse viral load value: " + value);
            }    
        }
    }
    
    public static void main(String [] args) {
        //ParseContacts pc = new ParseContacts();
        //pc.run();
    }
}
