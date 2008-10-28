package net.sf.wts.install.config;

import java.io.FileWriter;
import java.util.Map;
import java.util.Map.Entry;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

public class ConfigFile {
	private String fileName;
	private String wtsDir;
	private Map<String, String> challengeSettings;
	
	private Document document;
	
	private Element rootElement;
	private Element wtsDirElement;
	private Element challengeSettingsElement;
	
	public ConfigFile(String fileName, String wtsDir, Map<String, String> challengeSettings) {
		this.fileName = fileName;
		this.wtsDir = wtsDir;
		this.challengeSettings = challengeSettings;
	}
	
	public void createConfigFile() {
		document = new Document();
		
		rootElement = new Element("wts-settings");
		rootElement.setName("wts-settings");
		document.addContent(rootElement);
		
		wtsDirElement = new Element("wts-dir");
		wtsDirElement.setName("wts-dir");
		wtsDirElement.addContent(wtsDir);
		rootElement.addContent(wtsDirElement);
		
		challengeSettingsElement = new Element("challenge-settings");
		challengeSettingsElement.setName("challenge-settings");
		rootElement.addContent(challengeSettingsElement);
		
		for (Entry<String, String> entry : challengeSettings.entrySet()) {
			addConfigElement(challengeSettingsElement, entry.getKey(), entry.getValue());
		}
		
		writeConfigFile();
	}
	
	public void writeConfigFile() {
		try {
		    XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
		    outputter.outputString(document);
		    
		    FileWriter writer = new FileWriter(fileName);
			outputter.output(document, writer);
			writer.close();
		}
		catch (Exception e) {
		    e.printStackTrace();
		}
	}
	
	public void addConfigElement(Element parent, String name, String value) {
		Element configElement = new Element(name);
		configElement.setName(name);
		configElement.addContent(value);
		parent.addContent(configElement);
	}
}
