package net.sf.regadb.install.generateConfigFile;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.regadb.util.pair.Pair;

import org.apache.commons.io.FileUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

public class GenerateConfigFile {
	private static String configFile;
	
	private static Map<String, String> configMap;
	private static ArrayList<Pair<String, String>> proxyConfigList;
	
	private static String installDir;
	private static String driverClass;
	private static String dialect;
	private static String url;
	private static String username;
	private static String password;
	private static String queryDir;
	private static String proxyUrlA;
	private static String proxyPortA;
	private static String proxyUrlB;
	private static String proxyPortB;
	
	public static void main(String[] args) {
		if (args.length == 1) {
            GenerateConfigFile gcf = new GenerateConfigFile();
            gcf.run(true, args[0]);
		}
	}
    
    public void run(boolean inConfDir, String installFileName) {
        File installFile = new File(installFileName);
        
        configMap = new HashMap<String, String>();
        proxyConfigList = new ArrayList<Pair<String, String>>();
        
        installDir = "";
        driverClass = "";
        dialect = "";
        url = "";
        username = "";
        password = "";
        queryDir = "";
        proxyUrlA = "";
        proxyPortA = "";
        proxyUrlB = "";
        proxyPortB = "";
        
        readInstallFile(installFile);
        generateConfiguration(inConfDir);
        generateProxyConfiguration();
        writeConfigFile(generateConfigFile());
    }
	
	private static void readInstallFile(File installFile) {
		List configElements = new ArrayList();
		
		try {
			configElements = FileUtils.readLines(installFile);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		for (Object o : configElements) {
			String configElement = (String)o;
			String configElementValue = configElement.substring(configElement.indexOf(' ') + 1);
			
			if (configElement.startsWith("install_dir")) {
    			installDir = configElementValue;
    		}
			else if (configElement.startsWith("db_dialect")) {
    			dialect = configElementValue.toLowerCase();
    		}
    		else if (configElement.startsWith("db_url")) {
    			url = configElementValue;
    		}
    		else if (configElement.startsWith("db_user")) {
    			username = configElementValue;
    		}
    		else if (configElement.startsWith("db_password")) {
    			password = configElementValue;
    		}
    		else if (configElement.startsWith("proxy_url_a")) {
    			proxyUrlA = configElementValue;
    		}
    		else if (configElement.startsWith("proxy_port_a")) {
    			proxyPortA = configElementValue;
    		}
    		else if (configElement.startsWith("proxy_url_b")) {
    			proxyUrlB = configElementValue;
    		}
    		else if (configElement.startsWith("proxy_port_b")) {
    			proxyPortB = configElementValue;
    		}
    	}
	}
	
	private static void generateConfiguration(boolean inConfDir) {
		if (installDir.charAt(installDir.length() - 1) != File.separatorChar) {
			installDir += File.separatorChar;
		}
		
		configFile = installDir + (inConfDir?"conf":"") + File.separatorChar + "global-conf.xml";
		
		if (dialect.equals("default")) {
    		dialect = "org.hibernate.dialect.HSQLDialect";
    		driverClass = "org.hsqldb.jdbcDriver";
    		url = "jdbc:hsqldb:file:" + installDir + "hsqldb" + File.separatorChar + "regadb";
    		username = "regadb";
    		password = "regadb";
    	}
    	else if (dialect.equals("postgresql")) {
    		dialect = "org.hibernate.dialect.PostgreSQLDialect";
    		driverClass = "org.postgresql.Driver";
    		if(!url.startsWith("jdbc")) {
    			url = "jdbc:postgresql://" + url;
    		}
    	}
		
		queryDir = installDir + "queryResult";
		
		configMap.put("hibernate.connection.driver_class", driverClass);
    	configMap.put("hibernate.dialect", dialect);
    	configMap.put("hibernate.connection.url", url);
    	configMap.put("hibernate.connection.username", username);
    	configMap.put("hibernate.connection.password", password);
    	configMap.put("regadb.query.resultDir", queryDir);
	}
	
	private static void generateProxyConfiguration() {
		addProxy(proxyUrlA, proxyPortA);
		addProxy(proxyUrlB, proxyPortB);
	}
	
	private static Element generateConfigFile() {
    	Element rootElement = new Element("regadb-settings");
    	
    	for (Entry<String, String> entry : configMap.entrySet()) {
    		Element propertyElement = new Element("property".toLowerCase());
    		propertyElement.setAttribute("name", entry.getKey());
    		propertyElement.addContent(entry.getValue());
    		rootElement.addContent(propertyElement);
    	}
    	
    	for (Pair<String, String> pair : proxyConfigList) {
    		Element proxyElement = new Element("proxy");
    		
    		Element proxyUrlElement = new Element("property");
    		proxyUrlElement.setAttribute("name", "http.proxy.url");
    		proxyUrlElement.addContent(pair.getKey());
    		proxyElement.addContent(proxyUrlElement);
    		
    		Element proxyPortElement = new Element("property");
    		proxyPortElement.setAttribute("name", "http.proxy.port");
    		proxyPortElement.addContent(pair.getValue());
    		proxyElement.addContent(proxyPortElement);
    		
    		rootElement.addContent(proxyElement);
    	}
    	
    	return rootElement;
	}
	
	private static void writeConfigFile(Element rootElement) {
		Document document = new Document(rootElement);
		
		try {
		    XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
		    outputter.outputString(document);
		    
		    FileWriter writer = new FileWriter(configFile);
			outputter.output(document, writer);
			writer.close();
		}
		catch (IOException e) {
		    e.printStackTrace();
		}
	}
	
	private static void addProxy(String proxyUrl, String proxyPort) {
		if (!proxyUrl.equals("") && !proxyPort.equals("")) {
    		if (proxyUrl.toLowerCase().equals("empty") && proxyPort.toLowerCase().equals("empty")) {
    			proxyConfigList.add(new Pair<String, String>("", ""));
    		}
    		else {
    			proxyConfigList.add(new Pair<String, String>(proxyUrl, proxyPort));
    		}
    	}
	}
}
