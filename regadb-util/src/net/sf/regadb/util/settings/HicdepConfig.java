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
	
	private Attribute BAScenter;
	private Event BASenrol_d;
	
	public HicdepConfig(){
		super("hicdep");
	}

	public void parseXml(RegaDBSettings settings, Element e) {
		Element BAS = e.getChild("BAS");
		
		Element ee = BAS.getChild("CENTER");
		if (ee != null){
			Element attributeE = ee.getChild("attribute");
			final String name = attributeE.getAttributeValue("name");
			final String group = attributeE.getAttributeValue("group");
			BAScenter = new Attribute(name, group);
		}
		
		ee = BAS.getChild("ENROL_D");
		if (ee != null) {
			Element eventE = ee.getChild("event");
			final String name = eventE.getAttributeValue("name");
			BASenrol_d = new Event(name);
		}
	}

	public void setDefaults() {
		BAScenter = null;
		BASenrol_d = null;
	}

	public Element toXml() {
		return null;
	}
	
	public Attribute getBASCenter() {
		return BAScenter;
	}

	public void setBASCenter(Attribute center) {
		this.BAScenter = center;
	}
	
	public Event getBASEnrol_d() {
		return BASenrol_d;
	}

	public void setBASEnrol_d(Event enrol_d) {
		this.BASenrol_d = enrol_d;
	}
}
