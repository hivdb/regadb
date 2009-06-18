package net.sf.regadb.install.generateConfigFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.sf.regadb.util.settings.ProxyConfig;
import net.sf.regadb.util.settings.RegaDBSettings;

import org.apache.commons.io.FileUtils;

public class GenerateConfigFile {
	private static RegaDBSettings config;
	
	private static String configFile;
	
	private static String installDir;
	private static String driverClass;
	private static String dialect;
	private static String url;
	private static String username;
	private static String password;
	private static String queryDir;
	private static String logDir;
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
    	config = RegaDBSettings.create();
    	
        File installFile = new File(installFileName);
        
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
        
        config.write(new File(configFile));
    }
	
	@SuppressWarnings("unchecked")
	private static void readInstallFile(File installFile) {
		List configElements = new ArrayList();
		
		try {
			configElements = FileUtils.readLines(installFile, null);
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
		logDir = installDir + "logs";
		
		config.getHibernateConfig().setDriverClass(driverClass);
		config.getHibernateConfig().setDialect(dialect);
		config.getHibernateConfig().setUrl(url);
		config.getHibernateConfig().setUsername(username);
		config.getHibernateConfig().setPassword(password);
		
		config.getInstituteConfig().setQueryResultDir(new File(queryDir));
		config.getInstituteConfig().setLogDir(new File(logDir));
	}
	
	private static void generateProxyConfiguration() {
		addProxy(proxyUrlA, proxyPortA);
		addProxy(proxyUrlB, proxyPortB);
	}
	
	private static void addProxy(String proxyUrl, String proxyPort) {
		if (!proxyUrl.equals("") && !proxyPort.equals("")) {
    		if (proxyUrl.toLowerCase().equals("empty") && proxyPort.toLowerCase().equals("empty"))
    			config.getProxyConfig().getProxyList().add(new ProxyConfig.ProxyServer("",""));
    		else
    			config.getProxyConfig().getProxyList().add(new ProxyConfig.ProxyServer(proxyUrl,proxyPort));
    	}
	}
}
