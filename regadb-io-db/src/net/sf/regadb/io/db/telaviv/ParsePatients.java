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
import net.sf.regadb.db.Patient;
import net.sf.regadb.io.db.util.Logging;
import net.sf.regadb.io.db.util.NominalAttribute;
import net.sf.regadb.io.db.util.Utils;

public class ParsePatients extends Parser{

    private AttributeGroup regadbAttributeGroup_ = new AttributeGroup("RegaDB");
    private List<Attribute> regadbAttributes_;
    
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


        Map<String,Patient> patients = new HashMap<String,Patient>();
        
        Table patTable = Utils.readTable(patientsFile.getAbsolutePath());

        Table genderMapTable = Utils.readTable(genderMapFile.getAbsolutePath());
        Table countryMapTable = Utils.readTable(countryMapFile.getAbsolutePath());
        Table transmissionGroupMapTable = Utils.readTable(transmissionGroupMapFile.getAbsolutePath());
        
        int CId = patTable.findColumn("ID");
        int CRiskGrNo = patTable.findColumn("RiskGrNo");
        int CSexNo = patTable.findColumn("SexNo");
        int CBirthPlace = patTable.findColumn("BirthPlace");
        int CCenterNo = patTable.findColumn("CenterNo");
        int CDrNo = patTable.findColumn("DrNo");
        int CNote = patTable.findColumn("Note");
        int CBirthDate = patTable.findColumn("BirthDate");
        
        int CInfectionPlace = patTable.findColumn("InfectionPlace");

        logInfo("Retrieving standard RegaDB attributes");
        regadbAttributes_ = Utils.prepareRegaDBAttributes();
        
        NominalAttribute countryNominal = new NominalAttribute("Country of origin", countryMapTable, regadbAttributeGroup_, Utils.selectAttribute("Country of origin", regadbAttributes_));
        NominalAttribute genderNominal = new NominalAttribute("Gender", genderMapTable, regadbAttributeGroup_, Utils.selectAttribute("Gender", regadbAttributes_));
        NominalAttribute transmissionGroupNominal = new NominalAttribute("Transmission group", transmissionGroupMapTable, regadbAttributeGroup_, Utils.selectAttribute("Transmission group", regadbAttributes_));
        
        for(int i=1; i<patTable.numRows(); ++i){
            String id = patTable.valueAt(CId, i);
            String riskGrNo = patTable.valueAt(CRiskGrNo, i);
            String sexNo = patTable.valueAt(CSexNo, i);
            String birthPlace = patTable.valueAt(CBirthPlace, i);
            String centerNo = patTable.valueAt(CCenterNo, i);
            String drNo = patTable.valueAt(CDrNo, i);
            String note = patTable.valueAt(CNote, i);
            String birthDate = patTable.valueAt(CBirthDate, i);
            
            String infectionPlace = patTable.valueAt(CInfectionPlace, i);
            
            if(check(id)){
                Date d;
                Patient p = new Patient();
                patients.put(id, p);
                
                p.setPatientId(id);
                
                if((d = getDate(birthDate)) != null)
                    p.setBirthDate(d);
                else
                    logWarn(p,"Invalid birth date",patientsFile,i,birthDate);
                

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


            }
        }
        
        return patients;
    }

}
