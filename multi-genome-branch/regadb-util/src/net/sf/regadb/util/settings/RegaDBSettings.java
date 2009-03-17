package net.sf.regadb.util.settings;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

public class RegaDBSettings {
	private HashMap<String, IConfigParser> configs = new HashMap<String, IConfigParser>();

    private static RegaDBSettings instance_ = null;
    
    private RegaDBSettings() {
    	addConfig(new HibernateConfig());
    	addConfig(new ProxyConfig());
    	addConfig(new AccessPolicyConfig());
    	addConfig(new InstituteConfig());
    }
    
    private void addConfig(IConfigParser cfg){
    	configs.put(cfg.getXmlTag(),cfg);
    }

    public static RegaDBSettings getInstance()
    {
        if(instance_==null)
        {
            instance_ = new RegaDBSettings();
            
            // determine configuration directory
            String configFile = System.getenv("REGADB_CONF_DIR");
            if(configFile==null) {
                String osName = System.getProperty("os.name");
                osName = osName.toLowerCase();
                if(osName.startsWith("windows"))
                    configFile = "C:\\Program files\\rega_institute\\regadb\\global-conf.xml";
                else
                    configFile = "/etc/rega_institute/regadb/global-conf.xml";
            } else {
                configFile += File.separatorChar + "global-conf.xml";
            }
            
            instance_.parseConfFile(new File(configFile));
        }
        return instance_;
    }
    
    private void parseConfFile(File confFile) {
        SAXBuilder builder = new SAXBuilder();
        Document doc = null;
        try {
            doc = builder.build(confFile);
        } catch (JDOMException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Element root = doc.getRootElement();
        for(Object o : root.getChildren()){
        	Element e = (Element)o;
        	
        	IConfigParser cfg = configs.get(e.getName());
        	if(cfg != null){
        		cfg.parseXml(this, e);
        	}
        	else{
        		System.err.println("Config element not supported: "+ e.getName());
        	}
        }
    }
    
    void writeConfFileSkeleton(File confFile) {
        Element root = new Element("regadb-settings");
        for(IConfigParser cfg : configs.values())
        	root.addContent(cfg.toXml());
        writeXMLFile(root, confFile.getAbsolutePath());
    }

    private static void writeXMLFile(Element root, String fileName) {
        Document n = new Document(root);
        XMLOutputter outputter = new XMLOutputter();
        outputter.setFormat(Format.getPrettyFormat());
        try {
            outputter.output(n, System.out);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        java.io.FileWriter writer;
        try {
            writer = new java.io.FileWriter(fileName);
            outputter.output(n, writer);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        RegaDBSettings settings = new RegaDBSettings();
        settings.writeConfFileSkeleton(new File("settings" + File.separatorChar
                + "skeleton-settings.xml"));
    }

    
    public HibernateConfig getHibernateConfig(){
    	return (HibernateConfig)configs.get("hibernate");
    }
    public InstituteConfig getInstituteConfig(){
    	return (InstituteConfig)configs.get("institute");
    }
    public ProxyConfig getProxyConfig(){
    	return (ProxyConfig)configs.get("proxies");
    }
    public AccessPolicyConfig getAccessPolicyConfig(){
    	return (AccessPolicyConfig)configs.get("access-policies");
    }
    
    public String getDateFormat(){
    	return getInstituteConfig().getDateFormat();
    }
}
