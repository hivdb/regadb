package net.sf.regadb.util.settings;

import org.jdom.Element;

public class WivConfig implements IConfigParser{
	private String centreName;
	private TestTypeConfig viralLoadTestType;
	private AttributeConfig arcPatientFilter;
	
	public WivConfig(){
		setDefaults();
	}

	public String getXmlTag() {
		return "wiv";
	}

	public void parseXml(RegaDBSettings settings, Element e) {
		Element ee;
		
		ee = e.getChild("arc-patient-filter");
		if(ee != null){
			arcPatientFilter = new AttributeConfig();
			arcPatientFilter.parseXml(settings, ee.getChild("attribute"));
		}
		
		ee = e.getChild("viral-load-testtype");
		if(ee != null){
			viralLoadTestType = new TestTypeConfig();
			viralLoadTestType.parseXml(settings, ee.getChild("testtype"));
		}
		
		setCentreName(e.getChildTextTrim("centre-name"));
	}

	public void setDefaults() {
		centreName = null;
		viralLoadTestType = null;
		arcPatientFilter = null;
	}

	public Element toXml() {
		Element r = new Element(getXmlTag());
		Element e;
		
		if(getCentreName() != null){
			e = new Element("centre-name");
			e.setText(getCentreName());
			r.addContent(e);
		}
		
		if(getArcPatientFilter() != null){
			e = new Element("arc-patient-filter");
			e.addContent(getArcPatientFilter().toXml());
			r.addContent(e);
		}

		if(getViralLoadTestType() != null){
			e = new Element("viral-load-testtype");
			e.addContent(getViralLoadTestType().toXml());
			r.addContent(e);
		}
		
		return r;
	}

	public String getCentreName(){
		return centreName;
	}
	public void setCentreName(String centreName){
		this.centreName = centreName;
	}
	
	public TestTypeConfig getViralLoadTestType(){
		return viralLoadTestType;
	}
	public void setViralLoadTest(TestTypeConfig viralLoadTest){
		this.viralLoadTestType = viralLoadTest;
	}
	
	public AttributeConfig getArcPatientFilter(){
		return arcPatientFilter;
	}
	public void setArcPatientFilter(AttributeConfig arcPatientFilter){
		this.arcPatientFilter = arcPatientFilter;
	}
}
