package net.sf.regadb.util.settings;

import org.jdom.Element;

public class AttributeConfig {
	private String name;
	private String group;
	
	public AttributeConfig() {
		
	}
	
	public String getXmlTag() {
		return "attribute";
	}
	
	public AttributeConfig(String name, String group){
		this.setName(name);
		this.setGroup(group);
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getGroup() {
		return group;
	}
	
	public void parseXml(RegaDBSettings settings, Element e) {
		name = e.getAttributeValue("name");
		group = e.getAttributeValue("group");
	}

	public Element toXml() {
		Element ee = new Element(getXmlTag());
		
		ee.setAttribute("name", getName());
		ee.setAttribute("group", getGroup());
		
		return ee;
	}
}
