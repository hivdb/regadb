package net.sf.regadb.install.generateHsqldb;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import net.sf.regadb.install.initdb.InitRegaDB;
import net.sf.regadb.util.pair.Pair;

import org.apache.commons.io.FileUtils;
import org.hsqldb.Server;
import org.hsqldb.jdbc.jdbcDataSource;

public class HsqldbDatabaseCreator {
	private String databasePath_;
	private String databaseName_;
    private String hsqldbSchemaLocation_;
	
	private String url_;
	private String userName_;
	private String password_;
	
	public static void main(String[] args) {
        HsqldbDatabaseCreator hsqldb = new HsqldbDatabaseCreator("src/net/sf/regadb/install/hsqldb/database/", "regadb", "regadb", "regadb", "src/net/sf/regadb/install/ddl/schema/hsqldbSchema.sql");
        hsqldb.run();
    }
    
    public HsqldbDatabaseCreator(String databasePath, String databaseName, String userName, String password, String hsqlSchemaLocation) {
        databasePath_ = databasePath;
        databaseName_ = databaseName;
        userName_ = userName;
        password_ = password;
        hsqldbSchemaLocation_ = hsqlSchemaLocation;
        
        File database = new File(databasePath_);
        url_ = "jdbc:hsqldb:file:" + database.getAbsolutePath() + File.separatorChar + databaseName_;
    }
    
    public void run() {
        startupDatabase();
        createDatabase("sa", "");
        shutdownDatabase("sa", "");
        changeUser();
        initDatabase();
        shutdownDatabase("regadb", "regadb");
    }
	
	private void startupDatabase() {
		Server server = new Server();
		server.setDatabasePath(0, databasePath_ + File.separatorChar + databaseName_);
		server.setDatabaseName(0, databaseName_);
		server.start();
	}
	
	private void createDatabase(String user, String password) {
		File hsqldbSchemaFile = new File(hsqldbSchemaLocation_);
		
		String query = new String();
		
		try {
			query = FileUtils.readFileToString(hsqldbSchemaFile, null);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		executeQuery(query, user, password);
	}
	
	private void initDatabase() {
        InitRegaDB init = new InitRegaDB();
        
        ArrayList<Pair<String, String>> conf = new ArrayList<Pair<String, String>>();
        conf.add(new Pair<String, String>("hibernate.connection.driver_class", "org.hsqldb.jdbcDriver"));
        conf.add(new Pair<String, String>("hibernate.connection.password", "regadb"));
        conf.add(new Pair<String, String>("hibernate.connection.username", "regadb"));
        conf.add(new Pair<String, String>("hibernate.connection.url", url_));
        conf.add(new Pair<String, String>("hibernate.dialect", "org.hibernate.dialect.HSQLDialect"));

        init.run(conf);
	}
	
	private void shutdownDatabase(String user, String password) {
		executeQuery("SHUTDOWN", user, password);
	}
	
	private void changeUser() {
		File databaseScript = new File(databasePath_ + File.separatorChar + databaseName_ + ".script");
		
		String scriptFile = new String();
		
		try {
			scriptFile = FileUtils.readFileToString(databaseScript, null);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		scriptFile = scriptFile.replace("CREATE USER SA PASSWORD \"\"", "CREATE USER " + userName_.toUpperCase() + " PASSWORD \"" + password_.toUpperCase() + "\"");
		scriptFile = scriptFile.replace("GRANT DBA TO SA", "GRANT DBA TO " + userName_.toUpperCase());
		
		try {
			FileUtils.writeStringToFile(databaseScript, scriptFile, null);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private jdbcDataSource setupDataSource(String user, String password) {
		jdbcDataSource dataSource = new jdbcDataSource();
		dataSource.setDatabase(url_);
		dataSource.setUser(user);
		dataSource.setPassword(password);
		
		return dataSource;
	}
	
	private void executeQuery(String query, String user, String password) {
		jdbcDataSource dataSource = setupDataSource(user, password);
		
		try {
			Connection connection = dataSource.getConnection();
			
			Statement statement = connection.createStatement();
			statement.execute(query);
			statement.close();
			
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
