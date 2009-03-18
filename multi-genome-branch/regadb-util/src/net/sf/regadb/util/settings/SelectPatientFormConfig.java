package net.sf.regadb.util.settings;

import java.util.ArrayList;
import java.util.List;

import org.jdom.Element;

public class SelectPatientFormConfig extends FormConfig {
	public static final String NAME = "datatable.patient.SelectPatientForm"; 
	
	private AttributeConfig attributeFilter;
	private List<AttributeConfig> attributes = new ArrayList<AttributeConfig>();
	
	private boolean showSampleIds = true;

	public SelectPatientFormConfig() {
		super(NAME);
		setDefaults();
	}

	public void parseXml(RegaDBSettings settings, Element e) {
	    setShowSampleIds("true".equals(e.getAttributeValue("showSampleIds")));
	    
		AttributeConfig ac = new AttributeConfig();
		ac.parseXml(settings, e.getChild("attributeFilter").getChild(ac.getXmlTag()));
		setAttributeFilter(ac);
		
		Element ee = e.getChild("attributes");
		if(ee != null){
			attributes.clear();
			
			for(Object o : ee.getChildren()){
				Element eee = (Element)o;
				
				AttributeConfig ai = new AttributeConfig();
				ai.parseXml(settings, eee);
				
				attributes.add(ai);
			}
		}
	}

	public void setDefaults() {
		setAttributeFilter(null);
		setShowSampleIds(true);
		
		attributes.clear();
		attributes.add(new AttributeConfig("First name", "Personal"));
		attributes.add(new AttributeConfig("Last name", "Personal"));
	}
	
	@Override
	public Element toXml(){
		Element r = super.toXml();
		r.setAttribute("showSampleIds", (getShowSampleIds() ? "true": "false"));
		
		Element e;
		
		if(getAttributeFilter() != null){
			e = new Element("attributeFilter");
			e.addContent(getAttributeFilter().toXml());
			r.addContent(e);
		}
		
		e = new Element("attributes");
		r.addContent(e);
		
		for(AttributeConfig ai : attributes){
			e.addContent(ai.toXml());
		}
		
		return r;
	}

	public void setAttributeFilter(AttributeConfig attributeFilter) {
		this.attributeFilter = attributeFilter;
	}

	public AttributeConfig getAttributeFilter() {
		return attributeFilter;
	}
	
	public List<AttributeConfig> getAttributes(){
		return attributes;
	}

    public void setShowSampleIds(boolean showSampleIds) {
        this.showSampleIds = showSampleIds;
    }

    public boolean getShowSampleIds() {
        return showSampleIds;
    }
}
