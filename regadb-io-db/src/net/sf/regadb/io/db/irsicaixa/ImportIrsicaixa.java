package net.sf.regadb.io.db.irsicaixa;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import net.sf.regadb.csv.Table;
import net.sf.regadb.db.Attribute;
import net.sf.regadb.db.AttributeNominalValue;
import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.PatientAttributeValue;
import net.sf.regadb.db.TestNominalValue;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.Therapy;
import net.sf.regadb.db.TherapyGeneric;
import net.sf.regadb.db.TherapyGenericId;
import net.sf.regadb.io.db.util.ConsoleLogger;
import net.sf.regadb.io.db.util.Logging;
import net.sf.regadb.io.db.util.Mappings;
import net.sf.regadb.io.db.util.NominalAttribute;
import net.sf.regadb.io.db.util.Utils;
import net.sf.regadb.io.util.StandardObjects;

public class ImportIrsicaixa {
    private Logging logger_;
    private String basePath_;
    private Mappings mappings_;
    
    private Table generalDataTable_;
    private Table cd4Table_;
    private Table therapyTable_;
    
    private List<Attribute> regadbAttributes_;
    
    private List<DrugGeneric> regaDrugGenerics;

    public ImportIrsicaixa(Logging logger, String basePath, String mappingBasePath) {
        logger_ = logger;
        
        basePath_ = basePath;
        mappings_ = Mappings.getInstance(mappingBasePath);
    }
    
    public void run() {
        generalDataTable_ = Utils.readTable(basePath_ + File.separatorChar + "dbo_dadesgenerals.csv");
        cd4Table_ = Utils.readTable(basePath_ + File.separatorChar + "dbo_dadescd.csv");
        therapyTable_ = Utils.readTable(basePath_ + File.separatorChar + "dbo_dadestractaments.csv");
        
        logger_.logInfo("Retrieving standard RegaDB attributes");
        regadbAttributes_ = Utils.prepareRegaDBAttributes();
        
        logger_.logInfo("Retrieving standard RegaDB generic drugs");
        regaDrugGenerics = Utils.prepareRegaDrugGenerics();
        
        logger_.logInfo("Handling general patient data");
        HashMap<String, Patient> patients = handleGeneralData();
        logger_.logInfo("Handling cd4 data");
        handleCD4(patients);
        logger_.logInfo("Handling therapy data");
        handleTherapy(patients);
    }
    
    public HashMap<String, Patient> handleGeneralData() {
        HashMap<String, Patient> patients = new HashMap<String, Patient>();
        
        int CPatientId = Utils.findColumn(generalDataTable_, "PATIENID");
        int CGender = Utils.findColumn(generalDataTable_, "GENDER");
        int CBirthdate = Utils.findColumn(generalDataTable_, "DATE_OF_BIRTH");
        int CCountry = Utils.findColumn(generalDataTable_, "COUNTRY");
        int CFirstHIVPosTest = Utils.findColumn(generalDataTable_, "Date_of_first_positive_HIV-test");
        int CRouteOfTransmission = Utils.findColumn(generalDataTable_, "ROUTE_OF_TRANSMISION");
        
        NominalAttribute genderNominal = new NominalAttribute("Gender", CGender, new String[] { "M", "F" },
                new String[] { "male", "female" } );
        
        for(int i = 1; i<generalDataTable_.numRows(); i++) {
            String patientId = generalDataTable_.valueAt(CPatientId, i);
            String gender = generalDataTable_.valueAt(CGender, i);
            String birthdate = generalDataTable_.valueAt(CBirthdate, i);
            String country = generalDataTable_.valueAt(CCountry, i);
            String firstPosHIVTest = generalDataTable_.valueAt(CFirstHIVPosTest, i);
            String routeOfTransmission = generalDataTable_.valueAt(CRouteOfTransmission, i);
            
            System.err.println(patientId);
            
            Patient p = new Patient();
            p.setPatientId(patientId);
            patients.put(patientId, p);
            if(!birthdate.equals("NULL")) {
                Date birthdateDate = Utils.parseMysqlDate(birthdate);
                if(birthdateDate!=null)
                    p.setBirthDate(birthdateDate);
                else
                    logger_.logWarning("Unparsable birthdate for patient with patientId " + patientId + " for date " + birthdate);
            }
            
            AttributeNominalValue gnv = genderNominal.nominalValueMap.get(gender.toUpperCase().trim());
            if (gnv != null) {
                PatientAttributeValue v = p.createPatientAttributeValue(genderNominal.attribute);
                v.setAttributeNominalValue(gnv);
            }
            
            if(!country.equals("NULL")) {
                Attribute countryOfOrigin = Utils.selectAttribute("Country of origin", regadbAttributes_);
                String mapping = mappings_.getMapping("country_of_origin.mapping", country);
                if(mapping==null) {
                    logger_.logWarning("Could not find mapping for attributeNominalValue " + country);
                } else {
                    AttributeNominalValue cornv = new AttributeNominalValue(countryOfOrigin, mapping);
                    PatientAttributeValue v = p.createPatientAttributeValue(countryOfOrigin);
                    v.setAttributeNominalValue(cornv);
                }
            }
            
            if(!firstPosHIVTest.equals("NULL")) {
                Date hivPosDate = Utils.parseMysqlDate(firstPosHIVTest);
                if(hivPosDate==null) {
                    logger_.logWarning("Could not parse firstHivPos date " + hivPosDate);
                } else {
                    TestResult t = p.createTestResult(StandardObjects.getGenericHivSeroStatusTest());
                    t.setTestNominalValue(new TestNominalValue(StandardObjects.getHivSeroStatusTestType(), "Positive"));
                    t.setTestDate(hivPosDate);
                }
            }
            
            if(!routeOfTransmission.equals("NULL") && !routeOfTransmission.equals("Unknown")) {
                Attribute transmissionGroup = Utils.selectAttribute("Transmission group", regadbAttributes_);
                String mapping = mappings_.getMapping("transmission_group.mapping", routeOfTransmission);
                if(mapping==null) {
                    logger_.logWarning("Could not find mapping for attributeNominalValue " + routeOfTransmission);
                } else {
                    AttributeNominalValue cornv = new AttributeNominalValue(transmissionGroup, mapping);
                    PatientAttributeValue v = p.createPatientAttributeValue(transmissionGroup);
                    v.setAttributeNominalValue(cornv);
                }
            }
        }
        
        return patients;
    }
    
    public void handleCD4(HashMap<String, Patient> patients) {
        int CPatientId = Utils.findColumn(cd4Table_, "PATIENTID");
        int Ccd4Date = Utils.findColumn(cd4Table_, "CD4_DATE");
        int Ccd4Count = Utils.findColumn(cd4Table_, "CD4_COUNT");
        
        for(int i = 1; i<cd4Table_.numRows(); i++) {
            String patientId = cd4Table_.valueAt(CPatientId, i);
            Patient p = patients.get(patientId);
            if(p!=null) {
                Date cd4Date = Utils.parseMysqlDate(cd4Table_.valueAt(Ccd4Date, i));
                if(cd4Date!=null) {
                    try {
                        double value = Double.parseDouble(cd4Table_.valueAt(Ccd4Count, i));
                        TestResult t = p.createTestResult(StandardObjects.getGenericCD4Test());
                        t.setValue(value+"");
                        t.setTestDate(cd4Date);
                    }
                    catch(NumberFormatException nfe) {
                        logger_.logWarning("This is not a correct CD4 value (should be floating point or integer number: " + cd4Table_.valueAt(Ccd4Count, i));
                    }
                } else {
                    logger_.logWarning("CD4 tests require a date");
                }
            } else {
                logger_.logWarning("Could not find a patient with id " + patientId + " in the CD4 file");
            }
            
        }
    }
    
    public void handleTherapy(HashMap<String, Patient> patients) {
        int CPatientId = Utils.findColumn(therapyTable_, "PATIENTID");
        int CStartDate = Utils.findColumn(therapyTable_, "INITIATION_DATE");
        int CEndDate = Utils.findColumn(therapyTable_, "END_DATE");
        int CDrugs = Utils.findColumn(therapyTable_, "DRUGS");
        
        for(int i = 1; i<therapyTable_.numRows(); i++) {
            String patientId = therapyTable_.valueAt(CPatientId, i);
            Patient p = patients.get(patientId);
            if(p!=null) {
                Date startDate = Utils.parseMysqlDate(therapyTable_.valueAt(CStartDate, i));
                if(startDate!=null) {
                    Date endDate = Utils.parseMysqlDate(therapyTable_.valueAt(CEndDate, i));
                    
                    String drugs = therapyTable_.valueAt(CDrugs, i);
                    List<String> drugsList = processDrugs(drugs);
                    
                    Therapy t = p.createTherapy(startDate);
                    t.setStopDate(endDate);

                    for (String d : drugsList) {
                        TherapyGeneric tg = new TherapyGeneric(new TherapyGenericId(t, new DrugGeneric(null, d ,null)));
                        t.getTherapyGenerics().add(tg);
                    }
                } else {
                    logger_.logWarning("Therapy without startdate for patient " + patientId);
                }
            } else {
                logger_.logWarning("Could not find a patient with id " + patientId + " in the therapy file");
            }
        }
    }
    
    public List<String> processDrugs(String drugs) {
        StringTokenizer st = new StringTokenizer(drugs, ",");
        List<String> genericDrugs = new ArrayList<String>();
        if(drugs.equals("NULL"))
            return genericDrugs;
        while(st.hasMoreTokens())  {
            String drug = st.nextToken().trim();
            drug = Utils.checkDrugsWithRepos(drug, regaDrugGenerics, mappings_);
            if(drug!=null) {
                genericDrugs.add(drug);
            }
        }
        
        return genericDrugs;
    }
    
    public static void main(String [] args) {
        ImportIrsicaixa imp = new ImportIrsicaixa(ConsoleLogger.getInstance(), "/home/plibin0/import/spain/files", "/home/plibin0/import/spain/mapping");
        imp.run();
    }
}
