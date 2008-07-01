package net.sf.regadb.io.db.util;


public class MysqlDatabase extends GenericDatabase {
    
    public MysqlDatabase(String database, String user, String password){
        super(database,user,password);
        
        setDriver("com.mysql.jdbc.Driver");
        setUrl("jdbc:mysql://$HOST:$PORT/$DATABASE?zeroDateTimeBehavior=convertToNull");

        setPort("3306");
    }

}
