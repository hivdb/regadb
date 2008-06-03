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
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;

import javax.swing.JOptionPane;

import net.sf.regadb.install.initdb.InitRegaDB;
import net.sf.regadb.util.pair.Pair;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.netbeans.spi.wizard.WizardException;
import org.netbeans.spi.wizard.WizardPage;

public class RegaDBInstallHandler implements WizardPage.WizardResultProducer {
	@SuppressWarnings("unchecked")
	private Map settings_;
	
	@SuppressWarnings("unchecked")
	public boolean cancel(Map settings) {
		settings_ = settings;
		
		boolean dialogShouldClose = JOptionPane.showConfirmDialog (null,
				RegaDBWizardPage.tr("cancelInstall")) == JOptionPane.OK_OPTION;
		
		return dialogShouldClose;
	}
	
	@SuppressWarnings("unchecked")
	public Object finish(Map settings) throws WizardException {
		settings_ = settings;
		
		String jdbcUrl = "jdbc:postgresql://" + getString("psql_url") + "/";
		
		// Settings file aanmaken
		
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
			if ( !purl.isEmpty() && pport != 0 ) {
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
		
		String globalXMLFileName;
		if ( getBoolean("useExisting") ) {
			globalXMLFileName = getString("configExistingFile");
		} else {
			globalXMLFileName = getString("configCreateFile") + File.separator + "global-conf.xml";
		}
		
		try {
        	FileWriter writer = new FileWriter(new File(globalXMLFileName));
        	out.output(doc, writer);
 	        writer.flush();
 	        writer.close();
        } catch ( IOException e ) {
        	e.printStackTrace();
        }
		
		// Database aanmaken
		if ( getBoolean("initData") ) {
			// Create database
			try {
				Class.forName("org.postgresql.Driver");
				
				Properties props = new Properties();
				props.setProperty("user", getString("psql_adminUser"));
				props.setProperty("password", getString("psql_adminPass"));
				
				Connection conn = DriverManager.getConnection(jdbcUrl + "template1", props);
				
				Statement st = conn.createStatement();
				st.execute("CREATE DATABASE " + getString("db_databaseName"));
				
				conn.close();
			} catch ( SQLException e ) {
				e.printStackTrace();
			} catch ( ClassNotFoundException e ) {
				System.err.println("jdbc driver not found.");
			}
			
			// Fill database
			try {
				Class.forName("org.postgresql.Driver");
				
				Properties props = new Properties();
				props.setProperty("user", getString("db_roleUser"));
				props.setProperty("password", getString("db_rolePass"));
				
				Connection conn = DriverManager.getConnection(jdbcUrl + getString("db_databaseName"), props);
				
				File schemaFile = new File("src/net/sf/regadb/install/ddl/schema/postgresSchema.sql");
				BufferedReader read = new BufferedReader(new FileReader(schemaFile));
				
				while( read.ready() ) {
					String line = read.readLine();
					Statement st = conn.createStatement();
					st.execute(line);
				}
				
				conn.close();
			} catch ( SQLException e ) {
				System.err.println("An SQL error occured, perhaps the the database was not empty.");
			} catch ( FileNotFoundException e ) {
				System.err.println("SQL schema not found.");
			} catch (ClassNotFoundException e) {
				System.err.println("jdbc driver not found.");
			} catch ( IOException e ) {
				e.printStackTrace();
			}
			
			// InitDB runnen
			
			ArrayList<Pair<String, String>> cfg = new ArrayList<Pair<String, String>>();
			cfg.add(new Pair<String, String>("hibernate.connection.driver_class", "org.postgresql.Driver"));
			cfg.add(new Pair<String, String>("hibernate.connection.url", jdbcUrl + getString("db_databaseName")));
			cfg.add(new Pair<String, String>("hibernate.connection.username", getString("db_roleUser")));
			cfg.add(new Pair<String, String>("hibernate.connection.password", getString("db_rolePass")));
			cfg.add(new Pair<String, String>("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect"));
			
			InitRegaDB init = new InitRegaDB();
			//TODO set user getString("regadb_user"), getString("regadb_pass")
			init.setSu_(null);
			init.run(cfg);
			
			// TODO Updaten van repo's
		}
		
		if ( getBoolean("deployDoWant") ) {
			// Install Tomcat
			File war = new File("./dist/regadb-ui.war");
			File webapps = new File(getString("deploydir") + File.separator + "webapps");
			try {
				org.apache.commons.io.FileUtils.copyFileToDirectory(war, webapps);
			} catch ( IOException e ) {
				e.printStackTrace();
			}
		}
		
		// Set system environment
//		RegaDBInstallHandler.setSystemVariable("REGADB_CONF_DIR", globalXMLFileName);
		
		return null;
	}
	
	private static void setSystemVariable(String key, String value) {
		// TODO system variable fixen
		
		String cmdLine = null;
		String osName = System.getProperty("os.name");
		
		if( osName.startsWith("windows") ) {
			String ADD_CMD = "cmd /c reg add \"HKEY_LOCAL_MACHINE\\SYSTEM\\CurrentControlSet\\Control\\Session Manager\\Environment\" /v \"{0}\" /d \"{1}\" /t REG_EXPAND_SZ";
		    cmdLine = MessageFormat.format(ADD_CMD, new Object[] { key, value });
		} else {
			cmdLine = "echo \"export " + key + "=" + value + "\" >> ~/.profile";
		}
		
		if ( cmdLine != null ) {
			System.err.println(cmdLine);
			try {
		    	Runtime.getRuntime().exec(cmdLine);
		    } catch ( IOException e ) {
		    	System.err.println("Command could't be executed..");
		    	//System.err.println(cmdLine);
		    	System.err.println(e.getLocalizedMessage());
		    }
		}
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
		return ((Boolean)getValue(key)).booleanValue();
	}
}
