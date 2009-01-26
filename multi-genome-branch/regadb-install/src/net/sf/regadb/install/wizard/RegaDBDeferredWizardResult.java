package net.sf.regadb.install.wizard;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;

import javax.swing.JOptionPane;

import net.sf.regadb.db.SettingsUser;
import net.sf.regadb.install.initdb.InitRegaDB;
import net.sf.regadb.swing.i18n.I18n;
import net.sf.regadb.util.encrypt.Encrypt;
import net.sf.regadb.util.pair.Pair;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.netbeans.spi.wizard.DeferredWizardResult;
import org.netbeans.spi.wizard.ResultProgressHandle;

public class RegaDBDeferredWizardResult extends DeferredWizardResult {
	@SuppressWarnings("unchecked")
	private Map settings_;
	private int stepAt = 0, totalSteps = 6;
	private ResultProgressHandle prog_;
	
	@SuppressWarnings("unchecked")
	@Override
	public void start(Map settings, ResultProgressHandle prog) {
		settings_ = settings;
		prog_ = prog;
		
		String jdbcUrl = "jdbc:postgresql://" + getString("psql_url") + "/";
		
		// Create Settings File
		
		setProgress(tr("install_progress_settingsFile"));
		
		Element root = new Element("regadb-settings");
		Document doc = new Document(root);
		
		Element driver = new Element("property");
		driver.setAttribute("name", "hibernate.connection.driver_class");
		driver.addContent("org.postgresql.Driver");
		root.addContent(driver);
		
		Element url = new Element("property");
		url.setAttribute("name", "hibernate.connection.url");
		url.addContent(jdbcUrl + getString("db_databaseName"));
		root.addContent(url);
		
		Element username = new Element("property");
		username.setAttribute("name", "hibernate.connection.username");
		username.addContent(getString("db_roleUser"));
		root.addContent(username);
		
		Element password = new Element("property");
		password.setAttribute("name", "hibernate.connection.password");
		password.addContent(getString("db_rolePass"));
		root.addContent(password);
		
		Element dialect = new Element("property");
		dialect.setAttribute("name", "hibernate.dialect");
		dialect.addContent("org.hibernate.dialect.PostgreSQLDialect");
		root.addContent(dialect);
		
		Element resultDir = new Element("property");
		resultDir.setAttribute("name", "regadb.query.resultDir");
		resultDir.addContent(getString("querydir"));
		root.addContent(resultDir);
		
		Element logDir = new Element("property");
		logDir.setAttribute("name", "regadb.log.dir");
		logDir.addContent(getString("logdir"));
		root.addContent(logDir);
		
		for(int i=1; getValue("proxyurl" + i) != null; i++) {
			String purl = getString("proxyurl" + i);
			int pport = getInteger("proxyport" + i);
			if ( purl.length() != 0 && pport != 0 ) {
				Element proxy = new Element("proxy");
				root.addContent(proxy);
				
				Element pu = new Element("property");
				pu.setAttribute("name", "http.proxy.url");
				pu.addContent(purl);
				proxy.addContent(pu);
				
				Element pp = new Element("property");
				pp.setAttribute("name", "http.proxy.port");
				pp.addContent("" + pport);
				proxy.addContent(pp);
			}
		}
		
		XMLOutputter out = new XMLOutputter();
		out.setFormat(Format.getPrettyFormat());
		
		try {
			new File(getString("directory_SettingsFile")).mkdirs();
			FileWriter writer = new FileWriter(
        			new File(getString("directory_SettingsFile") + File.separator + "global-conf.xml"));
        	out.output(doc, writer);
 	        writer.flush();
 	        writer.close();
        } catch ( IOException e ) {
        	setFailed(e.getLocalizedMessage());
        	return;
        }
		
        // Create logdir and querydir
        
        new File(getString("querydir")).mkdirs();
        new File(getString("logdir")).mkdirs();
        
		// Create Database
        
        setProgress(tr("install_progress_database"));
        
		try {
			Class.forName("org.postgresql.Driver");
			
			Properties props = new Properties();
			props.setProperty("user", getString("psql_adminUser"));
			props.setProperty("password", getString("psql_adminPass"));
			
			Connection conn = DriverManager.getConnection(jdbcUrl + "template1", props);
			
			Statement st = conn.createStatement();
			if(!getBoolean("roleExists")){
				st.execute("CREATE ROLE " + getString("db_roleUser") +
						" WITH ENCRYPTED PASSWORD '" + getString("db_rolePass") + "' LOGIN");
				
				set("roleExists", true);
			}
			
			if(!getBoolean("dbExists")){
				st.execute("CREATE DATABASE " + getString("db_databaseName") +
						" WITH OWNER " + getString("db_roleUser"));

				set("dbExists", true);
			}
			if(!getBoolean("schemaExists")){
				try{
					st.execute("CREATE SCHEMA regadbschema AUTHORIZATION "+ getString("db_roleUser"));
					set("schemaExists",true);
				}
				catch(SQLException e){
					e.printStackTrace();
				}
			}
			
			conn.close();
		} catch ( SQLException e ) {
			setFailed(e.getLocalizedMessage());
			return;
		} catch ( ClassNotFoundException e ) {
			setFailed("JDBC driver not found.");
			return;
		}
		
		// Fill Database
		
		setProgress(tr("install_progress_table"));
		
		try {
			Class.forName("org.postgresql.Driver");
			
			Properties props = new Properties();
			props.setProperty("user", getString("db_roleUser"));
			props.setProperty("password", getString("db_rolePass"));
			
			Connection conn = DriverManager.getConnection(jdbcUrl + getString("db_databaseName"), props);
			Statement st = conn.createStatement();
			
			File schemaFile = new File("src/net/sf/regadb/install/ddl/schema/postgresSchema.sql");
			BufferedReader read = new BufferedReader(new FileReader(schemaFile));
			
			clearDdlCreateLog();
			while( read.ready() ) {
				String line = read.readLine();
				
				try{
					st = conn.createStatement();
					st.execute(line);
				}
				catch(SQLException e){
					addToDdlCreateLog(e.getLocalizedMessage());
				}
			}
			
			conn.close();
		} catch ( SQLException e ) {
			setFailed(e.getLocalizedMessage());
			return;
		} catch ( FileNotFoundException e ) {
			setFailed("SQL schema not found.");
			return;
		} catch (ClassNotFoundException e) {
			setFailed("JDBC driver not found.");
			return;
		} catch ( IOException e ) {
			setFailed(e.getLocalizedMessage());
			return;
		}
		
		// Run InitDB
		
		setProgress(tr("install_progress_init"));
		
		ArrayList<Pair<String, String>> cfg = new ArrayList<Pair<String, String>>();
		cfg.add(new Pair<String, String>("hibernate.connection.driver_class", "org.postgresql.Driver"));
		cfg.add(new Pair<String, String>("hibernate.connection.url", jdbcUrl + getString("db_databaseName")));
		cfg.add(new Pair<String, String>("hibernate.connection.username", getString("db_roleUser")));
		cfg.add(new Pair<String, String>("hibernate.connection.password", getString("db_rolePass")));
		cfg.add(new Pair<String, String>("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect"));
		
		SettingsUser admin = new SettingsUser(getString("account_Uid"), 0, 0);
		admin.setFirstName(getString("account_FirstName"));
        admin.setLastName(getString("account_LastName"));
        admin.setAdmin(true);
        admin.setEnabled(true);
        admin.setPassword(Encrypt.encryptMD5(getString("account_Pass")));
        
		InitRegaDB init = new InitRegaDB();
		init.setSu_(admin);
		init.run(cfg);
		
		// Updating repo's
		
		setProgress(tr("install_progress_repo"));
		
		//TODO Updaten van repo's
		
		// Install Tomcat
		
		setProgress(tr("install_progress_deploy"));
		
		if ( getBoolean("deployDoWant") ) {
			File war = new File(getString("warfile"));
			File webapps = new File(getString("deploydir") + File.separator + "webapps");
			try {
				org.apache.commons.io.FileUtils.copyFileToDirectory(war, webapps);
			} catch ( IOException e ) {
				setFailed(e.getLocalizedMessage());
				return;
			}
		}
		
		prog.setProgress(tr("install_progress_done"), 6, totalSteps);
		
		JOptionPane.showMessageDialog(null, tr("install_done"));
		
		prog.finished(null);
	}
	
	private void setFailed(String message) {
		setFailed(message, false);
	}
	private void setFailed(String message, boolean allowBack) {
		JOptionPane.showMessageDialog(null, tr("install_fail") + "\n\n" + message);
		prog_.failed(tr("install_fail_more") + " : " + message, allowBack);
	}
	
	private void setProgress(String string) {
		prog_.setProgress(string, stepAt++, totalSteps);
	}
	
	private Object getValue(String key) {
		Object o = settings_.get(key);
		if ( o == null ) {
			System.out.println("error: " + key + " doesn't exist");
		}
		return o;
	}
	
	private String getString(String key) {
		return (String)getValue(key);
	}
	
	private int getInteger(String key) {
		try {
			return Integer.parseInt(getString(key));
		} catch ( NumberFormatException e ) {
			System.err.println("Warning: not a valid number to convert (" + key + "): `" + getString(key) + "`\n");
			return 0;
		}
	}
	
	private boolean getBoolean(String key) {
		Boolean b = (Boolean)getValue(key);
		return b != null && b;
	}
	
	@SuppressWarnings("unchecked")
	private void set(String key, Object value){
		settings_.put(key, value);
	}
	
	private String tr(String key) {
		return I18n.tr(key);
	}
	
	private void clearDdlCreateLog(){
	}
	private void addToDdlCreateLog(String s){
		System.out.println(s);
	}
}
