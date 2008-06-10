package net.sf.regadb.io.db.euresist;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.regadb.db.Attribute;
import net.sf.regadb.db.AttributeNominalValue;
import net.sf.regadb.db.Dataset;
import net.sf.regadb.db.Patient;
import net.sf.regadb.io.db.util.Mappings;
import net.sf.regadb.io.db.util.MysqlDatabase;
import net.sf.regadb.io.db.util.Utils;

public class ExportDB {
    private MysqlDatabase db;
    private String mappingPath_;
    
    private List<Attribute> regadbAttributes=null;
    private String mappingPath=null;
    private Mappings mappings=null;
    
    public static void main(String[] args){
        ExportDB edb = new ExportDB(args[0], args[1], args[2], args[3]);
        edb.run();
    }

    public ExportDB(String database, String user, String password, String mappingPath){
        setDb(new MysqlDatabase(database, user, password));
        
        setMappingPath(mappingPath);
        
        System.setProperty("http.proxyHost", "www-proxy");
        System.setProperty("http.proxyPort", "3128");
    }
    
    public void run(){
        try{
            setMappings(Mappings.getInstance(getMappingPath()));
            
            Map<String,Patient> patients = exportPatients();
            HandleSequences seqs = new HandleSequences(this);
            seqs.run(patients);
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
        java.sql.Date sDate;
        
        setRegadbAttributes(Utils.prepareRegaDBAttributes());
        
        Map<String,AttributeNominalValue> tgMap = createTransmissionGroupMap();
        Map<String,AttributeNominalValue> genMap = createGenderMap();
        Map<String,AttributeNominalValue> cooMap = createCoOMap();
        Map<String,AttributeNominalValue> ethMap = createEthnicityMap();
        Map<String,Dataset> dsMap = createDatasetMap();
        
        //Map<String,AttributeNominalValue> coiMap = createCoIMap();
        
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
            createAttributeNominalValue(p,ethMap,rs.getString("ethnicID"));
            //createAttributeNominalValue(p,coiMap,rs.getString("country_of_infectionID"));
        }
        
        return patients;
    }
    
    public void createAttributeNominalValue(Patient p, Map<String,AttributeNominalValue> map, String value){
        AttributeNominalValue anv = map.get(value);
        if(anv != null){
            p.createPatientAttributeValue(anv.getAttribute()).setAttributeNominalValue(anv);
        }
    }
    
    public Map<String,AttributeNominalValue> createMap(String attributeName, String mapFile, String tableName, String mapColumn, String idColumn) throws Exception{
        Map<String, AttributeNominalValue> map = new HashMap<String,AttributeNominalValue>();
        Attribute attr = Utils.selectAttribute(attributeName, getRegadbAttributes());
        
        Mappings mapping = getMappings();
        String s;
        
        if(attr != null){
            ResultSet rs = getDb().executeQuery("SELECT * FROM "+ tableName);
            
            while(rs.next()){
                s = mapping.getMapping(mapFile, rs.getString(mapColumn));
                map.put(rs.getString(idColumn), Utils.getNominalValue(attr, s));
            }
            
            rs.close();
        }
        else
            throw new Exception("Attribute "+ attributeName +" doesn't exist.");
        
        return map;
    }

    public Map<String,AttributeNominalValue> createTransmissionGroupMap() throws Exception{
        return createMap("Transmission group", "transmission_group.mapping", "RiskGroups", "risk_name", "riskID");
    }

    public Map<String,AttributeNominalValue> createGenderMap() throws Exception{
        return createMap("Gender", "gender.mapping", "Genders", "name", "genderID");
    }
    
    public Map<String,AttributeNominalValue> createCoOMap() throws Exception{
        return createMap("Country of origin", "country_of_origin.mapping", "Countries", "iso_name_en", "countryID");
    }
    
    public Map<String,AttributeNominalValue> createEthnicityMap() throws Exception{
        return createMap("Ethnicity", "ethnicity.mapping", "EthnicGroups", "ethnic_group", "ethnicID");
    }
    
    public Map<String,AttributeNominalValue> createCoIMap(){
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

    public void setRegadbAttributes(List<Attribute> regadbAttributes) {
        this.regadbAttributes = regadbAttributes;
    }

    public List<Attribute> getRegadbAttributes() {
        return regadbAttributes;
    }

    public void setMappingPath(String mappingsPath) {
        this.mappingPath = mappingsPath;
    }

    public String getMappingPath() {
        return mappingPath;
    }

    public void setMappings(Mappings mappings) {
        this.mappings = mappings;
    }

    public Mappings getMappings() {
        return mappings;
    }
}
