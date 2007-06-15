package net.sf.regadb.util.settings;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private static RegaDBSettings instance_ = null;

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
    }
    
    public String getPropertyValue(String name)
    {
        return settingsMap_.get(name);
    }

    public static RegaDBSettings getInstance()
    {
        if(instance_==null)
        {
            instance_ = new RegaDBSettings();
            
            // determine configuration directory
            String configFile;
            String osName = System.getProperty("os.name");
            osName = osName.toLowerCase();
            if(osName.startsWith("windows"))
                configFile = "C:\\Program files\\rega_institute\\regadb\\global-conf.xml";
            else
                configFile = "/etc/rega_institute/regadb/global-conf.xml";
            
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
    }

    private void writeConfFileSkeleton(File confFile) {
        Element root = new Element("regadb-settings");
        for (String s : settings_) {
            Element property = new Element("property");
            property.setAttribute("name", s);
            property.addContent(new Text("default"));
            root.addContent(property);
        }
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
}
