package net.sf.regadb.util.settings;

import org.jdom.Element;

public abstract class ConfigParser {
	private String xmlTag;
	boolean configured;
	
	public ConfigParser(String xmlTag){
		setXmlTag(xmlTag);
		setDefaults();
	}
	
	public void setXmlTag(String xmlTag){
		this.xmlTag = xmlTag;
	}
	public String getXmlTag(){
		return xmlTag;
	}
	
	public void setConfigured(boolean configured){
		this.configured = configured;
	}
	public boolean isConfigured(){
		return configured;
	}

	protected Element createRoot(){
		return new Element(getXmlTag());
	}
	
	public abstract void setDefaults();
	public abstract void parseXml(RegaDBSettings settings, Element e);
	public abstract Element toXml();
}
