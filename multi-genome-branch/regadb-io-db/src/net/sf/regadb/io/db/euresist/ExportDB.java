package net.sf.regadb.io.db.euresist;

import java.io.File;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.regadb.db.Attribute;
import net.sf.regadb.db.AttributeGroup;
import net.sf.regadb.db.AttributeNominalValue;
import net.sf.regadb.db.Dataset;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestType;
import net.sf.regadb.io.db.util.ConsoleLogger;
import net.sf.regadb.io.db.util.Mappings;
import net.sf.regadb.io.db.util.MysqlDatabase;
import net.sf.regadb.io.db.util.Utils;
import net.sf.regadb.io.util.IOUtils;
import net.sf.regadb.io.util.StandardObjects;

public class ExportDB {
    private MysqlDatabase db;
    
    private List<Attribute> regadbAttributes=null;
    private String mappingPath=null;
    private Mappings mappings=null;
    private String outputPath=null;
    
    public static void main(String[] args){
        /*System.setProperty("http.proxyHost", "www-proxy");
        System.setProperty("http.proxyPort", "3128");*/
        ExportDB edb = new ExportDB(args[0], args[1], args[2], args[3], args[4]);
        edb.run();
    }

    public ExportDB(String database, String user, String password, String mappingPath, String outputPath){
        setDb(new MysqlDatabase(database, user, password));
        
        setMappingPath(mappingPath);
        setOutputPath(outputPath);
    }
    
    public void run(){
        try{
            setMappings(Mappings.getInstance(getMappingPath()));
            
            logInfo("Exporting patients");
            Map<String,Patient> patients = exportPatients();

            logInfo("Exporting tests");
            HandleTests tests = new HandleTests(this);
            tests.run(patients);
            
            logInfo("Exporting sequences");
            HandleSequences seqs = new HandleSequences(this);
            seqs.run(patients);
            
            logInfo("Exporting therapies");
            HandleTherapies ht = new HandleTherapies(this);
            ht.run(patients);
            
            logInfo("Generating xml");
            IOUtils.exportPatientsXML(patients.values(), getOutputPath() + File.separatorChar + "patients.xml", ConsoleLogger.getInstance());
            
            logInfo("Done");
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public Map<String,Patient> exportPatients() throws Exception{
        ResultSet rs = getDb().executeQuery("select * from Patients order by originalID");
        Map<String,Patient> patients = new HashMap<String,Patient>();
        
        SimpleDateFormat yearDf = new SimpleDateFormat("yyyy");
        
        String s;
        java.sql.Date sDate;
        
        setRegadbAttributes(Utils.prepareRegaDBAttributes());
        
        AttributeGroup eurAttrGrp = new AttributeGroup("EuResist");
        Attribute coiAttr = new Attribute();
        coiAttr.setAttributeGroup(eurAttrGrp);
        coiAttr.setValueType(StandardObjects.getNominalValueType());
        coiAttr.setName("Country of infection");
        
        Attribute gooiAttr = new Attribute();
        gooiAttr.setAttributeGroup(eurAttrGrp);
        gooiAttr.setValueType(StandardObjects.getNominalValueType());
        gooiAttr.setName("Geographic origin of infection");
        
        Map<String,AttributeNominalValue> tgMap =  createMap("Transmission group", "transmission_group.mapping", "RiskGroups", "risk_name", "riskID");
        Map<String,AttributeNominalValue> genMap = createMap("Gender", "gender.mapping", "Genders", "name", "genderID");
        Map<String,AttributeNominalValue> cooMap = createMap("Country of origin", "country_of_origin.mapping", "Countries", "iso_name_en", "countryID");
        Map<String,AttributeNominalValue> goMap =  createMap("Geographic origin", "geographic_origin.mapping", "Countries", "iso_name_en", "countryID");
        Map<String,AttributeNominalValue> ethMap = createMap("Ethnicity", "ethnicity.mapping", "EthnicGroups", "ethnic_group", "ethnicID");
        Map<String,AttributeNominalValue> coiMap = copyNominals(cooMap,coiAttr);
        Map<String,AttributeNominalValue> gooiMap = copyNominals(goMap,gooiAttr);
        
        Map<String,Dataset> dsMap = createDatasetMap();

        while(rs.next()){
            Patient p = new Patient();
            Dataset ds = dsMap.get(rs.getString("databaseID"));
            patients.put(rs.getString("patientID"), p);
            
            p.addDataset(ds);
            p.setPatientId(rs.getString("originalID"));
            
            s = rs.getString("year_of_birth");
            if(check(s))
                Utils.setBirthDate(p, yearDf.parse(s));
            
            sDate = rs.getDate("deceased_date");
            if(sDate != null)
                Utils.setDeathDate(p, convert(sDate));
            
            createAttributeNominalValue(p,tgMap, rs.getString("riskID"));
            createAttributeNominalValue(p,genMap,rs.getString("genderID"));
            createAttributeNominalValue(p,cooMap,rs.getString("country_of_originID"));
            createAttributeNominalValue(p,goMap, rs.getString("country_of_originID"));
            createAttributeNominalValue(p,ethMap,rs.getString("ethnicID"));
            createAttributeNominalValue(p,coiMap,rs.getString("country_of_infectionID"));
            createAttributeNominalValue(p,gooiMap,rs.getString("country_of_infectionID"));
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
                s = mapping.getMapping(mapFile, rs.getString(mapColumn), true);
                map.put(rs.getString(idColumn), Utils.getNominalValue(attr, s));
            }
            
            rs.close();
        }
        else
            throw new Exception("Attribute "+ attributeName +" doesn't exist.");
        
        return map;
    }
    
    public Map<String,Test> createTestMap(TestType testType, String tableName, String mapColumn, String idColumn) throws Exception{
        Map<String, Test> map = new HashMap<String,Test>();
        ResultSet rs = getDb().executeQuery("SELECT * FROM "+ tableName);
        
        String suffix="";
        if(testType.equals(StandardObjects.getHiv1ViralLoadLog10TestType()))
            suffix = " log10";
        
        while(rs.next()){
            map.put(rs.getString(idColumn), new Test(testType, rs.getString(mapColumn)+suffix));
        }
        
        rs.close();
        return map;
    }

    public Map<String,AttributeNominalValue> copyNominals(Map<String,AttributeNominalValue> anvs, Attribute coi){
        Map<String,AttributeNominalValue> map = new HashMap<String,AttributeNominalValue>();
        Map<String,AttributeNominalValue> uniques = new HashMap<String,AttributeNominalValue>();
        AttributeNominalValue anv,nanv;
        
        for(String id : anvs.keySet()){
            anv = anvs.get(id);
            
            if(anv != null){
                nanv = uniques.get(anv.getValue());
                if(nanv == null){
                    nanv = new AttributeNominalValue(coi,anv.getValue());
                    coi.getAttributeNominalValues().add(nanv);
                    uniques.put(anv.getValue(), nanv);
                }
            
                map.put(id, nanv);
            }
        }
        
        return map;
    }
    
    public Map<String,Dataset> createDatasetMap() throws Exception{
        Map<String, Dataset> map = new HashMap<String,Dataset>();
        
        ResultSet rs = getDb().executeQuery("select * from DataSources");
        
        Dataset ds;
        
        while(rs.next()){
            ds = new Dataset();
            ds.setCreationDate(convert(rs.getDate("version_date")));
            ds.setDescription(rs.getString("database_name"));
            ds.setRevision(1);
            
            map.put(rs.getString("databaseID"), ds);
        }
        
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
        return (sqlDate == null? null : new java.util.Date(sqlDate.getTime()));
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

    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }

    public String getOutputPath() {
        return outputPath;
    }
    
    public void logInfo(String msg){
        System.out.println(msg);
    }
    
    public void logWarn(String msg){
        System.out.println(msg);
    }
    
    public void logError(String msg){
        System.err.println(msg);
    }
}
