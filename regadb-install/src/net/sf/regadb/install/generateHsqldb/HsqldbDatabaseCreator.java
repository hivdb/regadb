package net.sf.regadb.install.generateHsqldb;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import net.sf.regadb.install.initdb.InitRegaDB;

import org.apache.commons.io.FileUtils;
import org.hsqldb.Server;
import org.hsqldb.jdbc.jdbcDataSource;

public class HsqldbDatabaseCreator {
	private final static String databasePath = "src/net/sf/regadb/install/hsqldb/database/";
	private final static String databaseName = "regadb";
	
	private static String url;
	private static String userName;
	private static String password;
	
	public static void main(String[] args) {
		File database = new File(databasePath);
		
		url = "jdbc:hsqldb:file:" + database.getAbsolutePath() + File.separatorChar + databaseName;
		userName = "regadb";
		password = "regadb";
		
		startupDatabase();
		createDatabase();
		initDatabase();
		shutdownDatabase();
		changeUser();
	}
	
	private static void startupDatabase() {
		Server server = new Server();
		server.setDatabasePath(0, databasePath + File.separatorChar + databaseName);
		server.setDatabaseName(0, databaseName);
		server.start();
	}
	
	private static void createDatabase() {
		String hsqldbSchemaLocation = "src/net/sf/regadb/install/ddl/schema/hsqldbSchema.sql";
		File hsqldbSchemaFile = new File(hsqldbSchemaLocation);
		
		String query = new String();
		
		try {
			query = FileUtils.readFileToString(hsqldbSchemaFile);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		executeQuery(query);
	}
	
	private static void initDatabase() {
		InitRegaDB.main(null);
	}
	
	private static void shutdownDatabase() {
		executeQuery("SHUTDOWN");
	}
	
	private static void changeUser() {
		File databaseScript = new File(databasePath + File.separatorChar + databaseName + ".script");
		
		String scriptFile = new String();
		
		try {
			scriptFile = FileUtils.readFileToString(databaseScript);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		scriptFile = scriptFile.replace("CREATE USER SA PASSWORD \"\"", "CREATE USER " + userName.toUpperCase() + " PASSWORD \"" + password.toUpperCase() + "\"");
		scriptFile = scriptFile.replace("GRANT DBA TO SA", "GRANT DBA TO " + userName.toUpperCase());
		
		try {
			FileUtils.writeStringToFile(databaseScript, scriptFile);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static jdbcDataSource setupDataSource() {
		jdbcDataSource dataSource = new jdbcDataSource();
		dataSource.setDatabase(url);
		dataSource.setUser("sa");
		dataSource.setPassword("");
		
		return dataSource;
	}
	
	private static void executeQuery(String query) {
		jdbcDataSource dataSource = setupDataSource();
		
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
