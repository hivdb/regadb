package net.sf.regadb.util.settings;

import java.util.ArrayList;
import java.util.List;

import org.jdom.Element;

public class SelectPatientFormConfig extends FormConfig {
	public static class AttributeItem{
		private String name;
		private String group;
		
		public AttributeItem(String name, String group){
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
	}
	
	public static final String NAME = "datatable.patient.SelectPatientForm"; 
	
	private String attributeFilter;
	private List<AttributeItem> attributes = new ArrayList<AttributeItem>();
	

	public SelectPatientFormConfig() {
		super(NAME);
		setDefaults();
	}

	@Override
	public void parseXml(RegaDBSettings settings, Element e) {
		setAttributeFilter(e.getChildTextTrim("attributeFilter"));
		if(getAttributeFilter() != null && getAttributeFilter().length() == 0)
			setAttributeFilter(null);
		
		Element ee = e.getChild("attributes");
		if(ee != null){
			attributes.clear();
			
			for(Object o : ee.getChildren()){
				Element eee = (Element)o;
				attributes.add(new AttributeItem(eee.getAttributeValue("name"),eee.getAttributeValue("group")));
			}
		}
	}

	@Override
	public void setDefaults() {
		setAttributeFilter(null);
		
		attributes.clear();
		attributes.add(new AttributeItem("First name", "RegaDB"));
		attributes.add(new AttributeItem("Last name", "RegaDB"));
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
		
		e = new Element("attributes");
		r.addContent(e);
		
		for(AttributeItem ai : attributes){
			Element ee = new Element("attribute");
			ee.setAttribute("name", ai.getName());
			ee.setAttribute("group", ai.getGroup());
			e.addContent(ee);
		}
		
		return r;
	}

	public void setAttributeFilter(String attributeFilter) {
		this.attributeFilter = attributeFilter;
	}

	public String getAttributeFilter() {
		return attributeFilter;
	}
	
	public List<AttributeItem> getAttributes(){
		return attributes;
	}
}
