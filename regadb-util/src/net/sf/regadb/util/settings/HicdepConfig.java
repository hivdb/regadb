package net.sf.regadb.util.settings;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	public static class Test {
		public String name;
		public String typeName;
		public Test(String name, String typeName) {
			this.name = name;
			this.typeName = typeName;
		}
	}
	
	private Attribute BAScenter;
	private Event BASenrol_d;
	private Test SAMPLESsamp_type;
	private Map<String, String> SAMPLESsamp_type_mapping = new HashMap<String, String>();
	
	public HicdepConfig(){
		super("hicdep");
	}

	public void parseXml(RegaDBSettings settings, Element e) {
		Element BAS = e.getChild("BAS");
		
		if (BAS != null) {
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
		
		Element SAMPLES = e.getChild("SAMPLES");
		if (SAMPLES != null) {
			final Element ee = SAMPLES.getChild("SAMP_TYPE");
			if (ee != null) {
				Element test = ee.getChild("test");
				final String name = test.getAttributeValue("name");
				final String type = test.getAttributeValue("type");
				SAMPLESsamp_type = new Test(name, type);
				
				List<Element> mappings = test.getChildren("map");
				for (Element mapping : mappings) {
					final String from = mapping.getAttributeValue("from");
					final String to = mapping.getAttributeValue("to");
					SAMPLESsamp_type_mapping.put(from, to);
				}
			}
		}
	}

	public void setDefaults() {
		BAScenter = null;
		BASenrol_d = null;
		SAMPLESsamp_type = null;
		SAMPLESsamp_type_mapping = new HashMap<String, String>();
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
	
	public Test getSAMPLESsamp_type() {
		return SAMPLESsamp_type;
	}

	public void setSAMPLESsamp_type(Test sAMPLESsamp_type) {
		SAMPLESsamp_type = sAMPLESsamp_type;
	}

	public Map<String, String> getSAMPLESsamp_type_mapping() {
		return SAMPLESsamp_type_mapping;
	}

	public void setSAMPLESsamp_type_mapping(
			Map<String, String> sAMPLESsamp_type_mapping) {
		SAMPLESsamp_type_mapping = sAMPLESsamp_type_mapping;
	}
}
