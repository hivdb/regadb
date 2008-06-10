package net.sf.regadb.io.db.euresist;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

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
            Map<Integer,Patient> patients = exportPatients();
            
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public Map<Integer,Patient> exportPatients() throws Exception{
        ResultSet rs = getDb().executeQuery("SELECT * FROM Patients");
        Map<Integer,Patient> patients = new HashMap<Integer,Patient>();
        
        while(rs.next()){
            Patient p = new Patient();
            patients.put(rs.getInt("patientID"), p);
            
            p.setPatientId(rs.getString("originalID"));
        }
        
        return patients;
    }

    protected void setDb(MysqlDatabase db) {
        this.db = db;
    }

    public MysqlDatabase getDb() {
        return db;
    }
}
