package net.sf.regadb.io.db.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public abstract class GenericDatabase {
    private String driver;
    private String user;
    private String password;
    private String database="";
    private String host="";
    private String port="";
    
    private String url;
    
    private Connection connection;
    
    public GenericDatabase(String database, String user, String password){
        setDatabase(database);
        setUser(user);
        setPassword(password);
        setHost("localhost");
    }
    
    public Connection createConnection() throws ClassNotFoundException, SQLException{
        Class.forName(getDriver());
        Properties prop = new Properties();            
        prop.put("charSet", "UTF-8");
        prop.put("user", getUser());
        prop.put("password", getPassword());
        String url = getJdbcUrl();
        return DriverManager.getConnection(url,prop);
    }
    
    public Connection getConnection(){
        if(connection == null){
            try{
                connection = createConnection();
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
        return connection;
    }
    
    public String getJdbcUrl(){
        return getUrl().replace("$HOST", getHost()).replace("$PORT",getPort()).replace("$DATABASE", getDatabase());
    }
    
    public ResultSet executeQuery(String query) throws SQLException{
        Connection c = getConnection();
        Statement s = c.createStatement();
        
        return s.executeQuery(query);
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getDriver() {
        return driver;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getUser() {
        return user;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getDatabase() {
        return database;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getHost() {
        return host;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getPort() {
        return port;
    }
}
