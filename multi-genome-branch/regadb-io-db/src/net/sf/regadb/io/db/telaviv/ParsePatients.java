package net.sf.regadb.io.db.telaviv;

import java.io.File;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.regadb.csv.Table;
import net.sf.regadb.db.Attribute;
import net.sf.regadb.db.AttributeGroup;
import net.sf.regadb.db.AttributeNominalValue;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.PatientAttributeValue;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.io.db.util.Logging;
import net.sf.regadb.io.db.util.NominalAttribute;
import net.sf.regadb.io.db.util.Parser;
import net.sf.regadb.io.db.util.Utils;
import net.sf.regadb.io.util.StandardObjects;

public class ParsePatients extends Parser{
    private AttributeGroup personal = new AttributeGroup("Personal");
    private AttributeGroup demographics = new AttributeGroup("Demographics");
    private AttributeGroup clinical = new AttributeGroup("Clinical");
    private AttributeGroup telavivGroup_ = new AttributeGroup("Tel Aviv");
    
    private List<Attribute> regadbAttributes_;
    
    private Map<String, AttributeNominalValue> infectionPlaces_ = new HashMap<String, AttributeNominalValue>();
    
    public ParsePatients(Logging logger, List<DateFormat> df){
        super(logger,df);
        setName("Patients");
    }
    
    public Map<String,Patient> run(File patientsFile, File genderMapFile, File countryMapFile, File transmissionGroupMapFile){
        logInfo("Parsing patients...");
        
        
        
        if(    !check(patientsFile)
            || !check(genderMapFile)
            || !check(countryMapFile)
            || !check(transmissionGroupMapFile))
            return null;


        
        Attribute immigrationDateAttr = new Attribute("Immigration date");
        immigrationDateAttr.setAttributeGroup(telavivGroup_);
        immigrationDateAttr.setValueType(StandardObjects.getDateValueType());
        
        Attribute infectionPlaceAttr = new Attribute("Infection place");
        infectionPlaceAttr.setAttributeGroup(telavivGroup_);
        infectionPlaceAttr.setValueType(StandardObjects.getNominalValueType());
        
        Attribute patientNumber = new Attribute("Patient number");
        patientNumber.setAttributeGroup(telavivGroup_);
        patientNumber.setValueType(StandardObjects.getStringValueType());
        
        Map<String,Patient> patients = new HashMap<String,Patient>();
        
        Table patTable = Utils.readTable(patientsFile.getAbsolutePath());

        Table genderMapTable = Utils.readTable(genderMapFile.getAbsolutePath());
        Table countryMapTable = Utils.readTable(countryMapFile.getAbsolutePath());
        Table transmissionGroupMapTable = Utils.readTable(transmissionGroupMapFile.getAbsolutePath());
        
        int CId = patTable.findColumn("ID");
        int CRiskGrNo = patTable.findColumn("RiskGrNo");
        int CSexNo = patTable.findColumn("SexNo");
        int CBirthPlace = patTable.findColumn("BirthPlace");
//        int CCenterNo = patTable.findColumn("CenterNo");
//        int CDrNo = patTable.findColumn("DrNo");
//        int CNote = patTable.findColumn("Note");
        int CBirthDate = patTable.findColumn("BirthDate");
        int CDeathDate = patTable.findColumn("YDeath");
        
        int CImmigrationDate = patTable.findColumn("ImigrationDate");
        int CInfectionPlace = patTable.findColumn("InfectionPlace");
        int CPtNum = patTable.findColumn("PtNum");
        int CPrivateName = patTable.findColumn("PrivateName");
        int CFamilyName = patTable.findColumn("FamilyName");
        int CFirstWB = patTable.findColumn("FirstWB");

        logInfo("Retrieving standard RegaDB attributes");
        regadbAttributes_ = Utils.prepareRegaDBAttributes();
        
        NominalAttribute countryNominal = new NominalAttribute("Country of origin", countryMapTable, demographics, Utils.selectAttribute("Country of origin", regadbAttributes_));
        NominalAttribute genderNominal = new NominalAttribute("Gender", genderMapTable, personal, Utils.selectAttribute("Gender", regadbAttributes_));
        NominalAttribute transmissionGroupNominal = new NominalAttribute("Transmission group", transmissionGroupMapTable, clinical, Utils.selectAttribute("Transmission group", regadbAttributes_));
        
        for(int i=1; i<patTable.numRows(); ++i){
            String id = patTable.valueAt(CId, i);
            String riskGrNo = patTable.valueAt(CRiskGrNo, i);
            String sexNo = patTable.valueAt(CSexNo, i);
            String birthPlace = patTable.valueAt(CBirthPlace, i);
            //String centerNo = patTable.valueAt(CCenterNo, i);
            //String drNo = patTable.valueAt(CDrNo, i);
            //String note = patTable.valueAt(CNote, i);
            String birthDate = patTable.valueAt(CBirthDate, i);
            String deathDate = patTable.valueAt(CDeathDate, i);
            
            String immigrationDate = patTable.valueAt(CImmigrationDate, i);
            String infectionPlace = patTable.valueAt(CInfectionPlace, i);
            String ptNum = patTable.valueAt(CPtNum, i);
            String privateName = patTable.valueAt(CPrivateName, i);
            String familyName = patTable.valueAt(CFamilyName, i);
            String firstWB = patTable.valueAt(CFirstWB, i);
            
            if(check(id)){
                Date d;
                Patient p = new Patient();
                patients.put(id, p);
                
                p.setPatientId(id);
                
                if((d = getDate(birthDate)) != null)
                    Utils.setBirthDate(p, d);
                else
                    logWarn(p,"Invalid birth date",patientsFile,i,birthDate);
                
                if((d = getDate(deathDate)) != null)
                	Utils.setDeathDate(p, d);
                
                if((d = getDate(immigrationDate)) != null)
                	p.createPatientAttributeValue(immigrationDateAttr).setValue(d.getTime()+"");

                if(Utils.checkColumnValueForEmptiness("gender", sexNo, i, id))
                {
                    Utils.handlePatientAttributeValue(genderNominal, sexNo, p);
                }
                
                if(Utils.checkColumnValueForEmptiness("birthplace", birthPlace, i, id))
                {
                    Utils.handlePatientAttributeValue(countryNominal, birthPlace, p);
                }
                
                if(Utils.checkColumnValueForEmptiness("risk group", riskGrNo, i, id))
                {
                    Utils.handlePatientAttributeValue(transmissionGroupNominal, riskGrNo, p);
                }
                if(check(infectionPlace)){
                    createNominalAttribute(p,infectionPlaceAttr,infectionPlaces_,infectionPlace);
                }
                if(check(ptNum)){
                    p.createPatientAttributeValue(patientNumber).setValue(ptNum);
                }
                if(check(privateName)){
                    Utils.setFirstName(p, privateName);
                }
                if(check(familyName)){
                    Utils.setLastName(p, familyName);
                }
                if(check(firstWB)){
                    Date testDate = getDate(firstWB);
                    if(testDate != null){
                        TestResult tr = p.createTestResult(StandardObjects.getGenericHiv1SeroStatusTest());
                        tr.setTestDate(testDate);
                        tr.setTestNominalValue(Utils.getNominalValue(StandardObjects.getHiv1SeroStatusTestType(), "Positive"));
                    }
                }
            }
        }
        
        return patients;
    }
    
    public void createNominalAttribute(Patient p, Attribute attribute, Map<String, AttributeNominalValue> nominals, String value){
        AttributeNominalValue anv = nominals.get(value);
        if(anv == null){
            anv = new AttributeNominalValue(attribute, value);
            attribute.getAttributeNominalValues().add(anv);
            nominals.put(value, anv);
        }
        
        PatientAttributeValue pav = p.createPatientAttributeValue(attribute);
        pav.setAttributeNominalValue(anv);
    }
}
