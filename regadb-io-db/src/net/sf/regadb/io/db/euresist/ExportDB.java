package net.sf.regadb.io.db.euresist;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import net.sf.regadb.db.AttributeNominalValue;
import net.sf.regadb.db.Dataset;
import net.sf.regadb.db.Patient;
import net.sf.regadb.io.db.util.MysqlDatabase;

public class ExportDB {
    private MysqlDatabase db;
    
    public static void main(String[] args){
        ExportDB edb = new ExportDB(args[0], args[1], args[2]);
        edb.run();
    }

    public ExportDB(String database, String user, String password){
        setDb(new MysqlDatabase(database, user, password));
    }
    
    public void run(){
        try{
            Map<String,Patient> patients = exportPatients();
            
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public Map<String,Patient> exportPatients() throws Exception{
        ResultSet rs = getDb().executeQuery("SELECT * FROM Patients");
        Map<String,Patient> patients = new HashMap<String,Patient>();
        
        SimpleDateFormat yearDf = new SimpleDateFormat("yyyy");
        
        String s;
        AttributeNominalValue anv;
        java.sql.Date sDate;
        
        Map<String,AttributeNominalValue> tgMap = createTransmissionGroupMap();
        Map<String,AttributeNominalValue> genMap = createGenderMap();
        Map<String,AttributeNominalValue> cooMap = createCoOMap();
        Map<String,AttributeNominalValue> coiMap = createCoIMap();
        Map<String,AttributeNominalValue> ethMap = createEthnicityMap();
        Map<String,Dataset> dsMap = createDatasetMap();
        
        while(rs.next()){
            Patient p = new Patient();
            patients.put(rs.getString("patientID"), p);
            
            p.setPatientId(rs.getString("originalID"));
            
            s = rs.getString("year_of_birth");
            if(check(s))
                p.setBirthDate(yearDf.parse(s));
            
            sDate = rs.getDate("deceased_date");
            if(sDate != null)
                p.setDeathDate(convert(sDate));
            
            createAttributeNominalValue(p,tgMap,rs.getString("riskID"));
            createAttributeNominalValue(p,genMap,rs.getString("genderID"));
            createAttributeNominalValue(p,cooMap,rs.getString("country_of_originID"));
            createAttributeNominalValue(p,coiMap,rs.getString("country_of_infectionID"));
            createAttributeNominalValue(p,ethMap,rs.getString("ethnicID"));
        }
        
        return patients;
    }
    
    public void createAttributeNominalValue(Patient p, Map<String,AttributeNominalValue> map, String value){
        AttributeNominalValue anv = map.get(value);
        if(anv != null){
            p.createPatientAttributeValue(anv.getAttribute()).setAttributeNominalValue(anv);
        }
    }
    
    public Map<String,AttributeNominalValue> createTransmissionGroupMap(){
        Map<String,AttributeNominalValue> map = new HashMap<String,AttributeNominalValue>();
        return map;
    }
    
    public Map<String,AttributeNominalValue> createGenderMap(){
        Map<String, AttributeNominalValue> map = new HashMap<String,AttributeNominalValue>();
        return map;
    }
    
    public Map<String,AttributeNominalValue> createCoOMap(){
        Map<String, AttributeNominalValue> map = new HashMap<String,AttributeNominalValue>();
        return map;
    }
    
    public Map<String,AttributeNominalValue> createCoIMap(){
        Map<String, AttributeNominalValue> map = new HashMap<String,AttributeNominalValue>();
        return map;
    }
    
    public Map<String,AttributeNominalValue> createEthnicityMap(){
        Map<String, AttributeNominalValue> map = new HashMap<String,AttributeNominalValue>();
        return map;
    }
    
    public Map<String,Dataset> createDatasetMap(){
        Map<String, Dataset> map = new HashMap<String,Dataset>();
        return map;
    }
    

    protected void setDb(MysqlDatabase db) {
        this.db = db;
    }

    public MysqlDatabase getDb() {
        return db;
    }
    
    public boolean check(String s){
        return s != null && s.trim().length() > 0;
    }
    
    public java.util.Date convert(java.sql.Date sqlDate){
        return new java.util.Date(sqlDate.getTime());
    }
}
