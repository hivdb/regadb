package net.sf.regadb.util.settings;

import org.jdom.Element;

public class HicdepConfig extends ConfigParser{
	public static class Attribute {
		public String name;
		public String group;
		public Attribute(String name, String group) {
			this.name = name;
			this.group = group;
		}
	}
	public static class Event {
		public String name;
		public Event(String name) {
			this.name = name;
		}
	}
	
	private Attribute center;
	private Event enrol_d;
	
	public HicdepConfig(){
		super("hicdep");
	}

	public void parseXml(RegaDBSettings settings, Element e) {
		Element ee;
		
		ee = e.getChild("CENTER");
		if (ee != null){
			Element attributeE = ee.getChild("attribute");
			final String name = attributeE.getAttributeValue("name");
			final String group = attributeE.getAttributeValue("group");
			center = new Attribute(name, group);
		}
		
		ee = e.getChild("ENROL_D");
		if (ee != null) {
			Element eventE = ee.getChild("event");
			final String name = eventE.getAttributeValue("name");
			enrol_d = new Event(name);
		}
	}

	public void setDefaults() {
		center = null;
	}

	public Element toXml() {
		return null;
	}
	
	public Attribute getCenter() {
		return center;
	}

	public void setCenter(Attribute center) {
		this.center = center;
	}
	
	public Event getEnrol_d() {
		return enrol_d;
	}

	public void setEnrol_d(Event enrol_d) {
		this.enrol_d = enrol_d;
	}
}
