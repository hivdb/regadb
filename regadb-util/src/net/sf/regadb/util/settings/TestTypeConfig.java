package net.sf.regadb.util.settings;

import org.jdom.Element;

public class TestTypeConfig {
	private String description;
	private String organism;
	
	public TestTypeConfig() {
		
	}
	
	public String getXmlTag() {
		return "testtype";
	}
	
	public TestTypeConfig(String description, String organism){
		this.setDescription(description);
		this.setOrganism(organism);
	}

	public void setDescription(String name) {
		this.description = name;
	}

	public String getDescription() {
		return description;
	}

	public void setOrganism(String group) {
		this.organism = group;
	}

	public String getOrganism() {
		return organism;
	}
	
	public void parseXml(RegaDBSettings settings, Element e) {
		description = e.getAttributeValue("description");
		organism = e.getAttributeValue("organism");
	}

	public Element toXml() {
		Element ee = new Element(getXmlTag());
		
		ee.setAttribute("description", getDescription());
		ee.setAttribute("organism", getOrganism());
		
		return ee;
	}
}
