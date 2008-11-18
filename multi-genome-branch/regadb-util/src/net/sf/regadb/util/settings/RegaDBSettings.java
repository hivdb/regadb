package net.sf.regadb.util.settings;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.regadb.util.pair.Pair;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Text;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

public class RegaDBSettings {
    private List<String> settings_ = new ArrayList<String>();

    private Map<String, String> settingsMap_ = new HashMap<String, String>();
    private Map<String, String> defaultsMap_ = new HashMap<String, String>();
    private Map<String, Filter> filtersMap_ = new HashMap<String, Filter>();
    
    private ArrayList<Pair<String, String>> proxyList_ = new ArrayList<Pair<String, String>>();

    private static RegaDBSettings instance_ = null;
    
    private Map<String,Element> customizeElements = new HashMap<String, Element>();

    private RegaDBSettings() {
        // hibernate settings
        settings_.add("hibernate.connection.driver_class");
        settings_.add("hibernate.connection.password");
        settings_.add("hibernate.connection.url");
        settings_.add("hibernate.connection.username");
        settings_.add("hibernate.dialect");

        // regadb query results
        settings_.add("regadb.query.resultDir");

        // http proxy settings
        settings_.add("http.proxy.url");
        settings_.add("http.proxy.port");
        
        settings_.add("centre.name");
        
        settings_.add("regadb.report.dateTolerance");
        settings_.add("regadb.log.dir");
        
        settings_.add("custom.dir");
    }
    
    public String getPropertyValue(String name)
    {
        return settingsMap_.get(name);
    }
    
    public String getDefaultValue(String name)
    {
        return defaultsMap_.get(name);
    }
    
    public Filter getFilter(String name) {
    	return filtersMap_.get(name);
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
    
    public void initProxySettings()
    {
        if(proxyList_.size()==1)
        {
            setProxySettings(proxyList_.get(0));
        }
    }
    
    public void setProxySettings(Pair<String, String> proxyInfo)
    {
        String proxyHost = proxyInfo.getKey();
        String proxyPort = proxyInfo.getValue();
        
        if(!"".equals(proxyHost))
        {
            if(proxyHost!=null)
            {
                System.setProperty("http.proxyHost", proxyHost);
            }
            if(proxyPort!=null)
            {
                System.setProperty("http.proxyPort", proxyPort);
            }
        }
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

        List children = root.getChildren("property");
        Element e;
        String name;
        for (Object o : children) {
            e = ((Element) o);
            name = e.getAttributeValue("name");
            if (settings_.contains(name)) {
                settingsMap_.put(name, e.getTextTrim());
            }
        }
        
        List proxys = root.getChildren("proxy");
        Element proxy;
        for (Object o : proxys)
        {
            proxy = ((Element) o);
            Element ee;
            String proxyUrl = "";
            String proxyPort = "";
            
            for (Object oo : proxy.getChildren("property"))
            {
                ee = ((Element) oo);
                if(ee.getAttributeValue("name").equals("http.proxy.url")) proxyUrl = ee.getTextTrim();
                if(ee.getAttributeValue("name").equals("http.proxy.port")) proxyPort = ee.getTextTrim();
            }
            proxyList_.add(new Pair<String, String>(proxyUrl,proxyPort));
        }
        
        Element customize = root.getChild("customize");
        if(customize != null){
	        for(Object o : customize.getChildren()){
	        	customizeElements.put(((Element)o).getAttributeValue("name"), (Element)o);
	        }
        }
        
        List defaults = root.getChildren("default");
        Element dft;
        for(Object o : defaults){
            dft = ((Element) o);
            name = dft.getAttributeValue("name");
            defaultsMap_.put(name, dft.getTextTrim());
        }
        
        List filters = root.getChildren("filter");
        Element filterE;
        for(Object o : filters){
        	filterE = ((Element) o);
            name = filterE.getAttributeValue("name");
            filtersMap_.put(name, new Filter(filterE.getTextTrim()));
        }
    }
    
    public ArrayList<Pair<String, String>> getProxyList()
    {
        return proxyList_;
    }

    private void writeConfFileSkeleton(File confFile) {
        Element root = new Element("regadb-settings");
        for (String s : settings_) {
            Element property = new Element("property");
            property.setAttribute("name", s);
            property.addContent(new Text("default"));
            root.addContent(property);
        }
        Element proxy = new Element("proxy");
        Element proxyPropertyUrl = new Element("property");
        proxyPropertyUrl.setAttribute("name", "http.proxy.url");
        proxyPropertyUrl.addContent(new Text("default"));
        Element proxyPropertyPort = new Element("property");
        proxyPropertyPort.setAttribute("name", "http.proxy.port");
        proxyPropertyPort.addContent(new Text("default"));
        proxy.addContent(proxyPropertyUrl);
        proxy.addContent(proxyPropertyPort);
        root.addContent(proxy);
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

    public Element getCustomSettings(String elementName){
    	return customizeElements.get(elementName);
    }
}
