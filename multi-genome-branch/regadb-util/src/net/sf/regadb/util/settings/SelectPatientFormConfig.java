package net.sf.regadb.util.settings;

import org.jdom.Element;

public class SelectPatientFormConfig extends FormConfig {
	public static final String NAME = "datatable.patient.SelectPatientForm"; 
	
	private String attributeFilter;

	public SelectPatientFormConfig() {
		super(NAME);
	}

	@Override
	public void parseXml(RegaDBSettings settings, Element e) {
		setAttributeFilter(e.getChildTextTrim("attributeFilter"));
		if(getAttributeFilter() != null && getAttributeFilter().length() == 0)
			setAttributeFilter(null);
	}

	@Override
	public void setDefaults() {
		setAttributeFilter(null);
	}
	
	@Override
	public Element toXml(){
		Element r = super.toXml();
		Element e;
		
		if(getAttributeFilter() != null && getAttributeFilter().length() != 0){
			e = new Element("attributeFilter");
			e.setText(getAttributeFilter());
			r.addContent(e);
		}
		
		return r;
	}

	public void setAttributeFilter(String attributeFilter) {
		this.attributeFilter = attributeFilter;
	}

	public String getAttributeFilter() {
		return attributeFilter;
	}

}
