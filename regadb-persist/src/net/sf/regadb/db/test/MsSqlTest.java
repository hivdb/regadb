package net.sf.regadb.db.test;

import java.sql.Connection;
import java.sql.DriverManager;

public class MsSqlTest {
    
    public static void main(String[] args){
        try{
            String dbhost = "localhost";
            String dbport = "1433";
            String dbuser="regadb_user";
            String dbpass="regadb_password";
            String dbname="regadb";
            
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
    
            String connectionUrl = "jdbc:sqlserver://"+ dbhost +":"+ dbport +
               ";databaseName="+ dbname +";user="+ dbuser +";password="+ dbpass +";";
            Connection con = DriverManager.getConnection(connectionUrl);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

}
