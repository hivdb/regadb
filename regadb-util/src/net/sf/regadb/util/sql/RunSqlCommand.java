package net.sf.regadb.util.sql;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import net.sf.regadb.util.settings.RegaDBSettings;

public class RunSqlCommand {
    Connection conn = null;

    public static void main(String[] args) {
        if(args.length < 1){
            System.err.println("Usage: <filename>");
            System.exit(1);
        }
        
        RunSqlCommand rsc = new RunSqlCommand();
        rsc.executeBatch(args[0]);
    }

    public RunSqlCommand(){

    }
    
    public Connection getConnection(){
        if(conn == null){
            try
            {
                Class.forName(getDriver());
                conn = DriverManager.getConnection(getUrl(), getUsername(), getPassword());
                logInfo("Connected.");
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return conn;
    }
    
    public void executeBatch(String filename){
        File f = new File(filename);
        
        if(!f.exists()){
            logError("No such file: "+ filename);
            return;
        }
        
        try{
            BufferedReader fr = new BufferedReader(new FileReader(f));
            String query;
            while((query = fr.readLine()) != null){
                if(query.length() > 0)
                    executeQuery(query);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
            
    }
    
    public void executeQuery(String query){
        try{
            Statement s = getConnection().createStatement();
            logInfo("Executing: "+ query);
            s.execute(query);
            s.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public String getDriver(){
        return RegaDBSettings.getInstance().getPropertyValue("hibernate.connection.driver_class");
    }
    public String getUrl(){
        return RegaDBSettings.getInstance().getPropertyValue("hibernate.connection.url");
    }
    public String getUsername(){
        return RegaDBSettings.getInstance().getPropertyValue("hibernate.connection.username");
    }
    public String getPassword(){
        return RegaDBSettings.getInstance().getPropertyValue("hibernate.connection.password");
    }
    
    public void logInfo(String s){
        System.out.println(s);
    }
    public void logError(String s){
        System.err.println(s);
    }
}
