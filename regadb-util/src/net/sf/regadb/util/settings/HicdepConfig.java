package net.sf.regadb.util.settings;

import java.util.ArrayList;
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
	public static class LabTest {
		public String regadb_name;
		public String regadb_type_name;
		public String hicdep_lab_id;
		public Integer hicdep_lab_unit;
		public List<Mapping> mappings;
		public LabTest(String regadb_name, String regadb_type_name,
				String hicdep_lab_id, Integer hicdep_lab_unit,
				List<Mapping> mappings) {
			this.regadb_name = regadb_name;
			this.regadb_type_name = regadb_type_name;
			this.hicdep_lab_id = hicdep_lab_id;
			this.hicdep_lab_unit = hicdep_lab_unit;
			this.mappings = mappings;
		}
	}
	public static class Mapping {
		public enum Type {
			String,
			Interval
		}
		public Type type;
		public String from;
		public String to;	
		public Mapping(Type type, String from, String to) {
			this.type = type;
			this.from = from;
			this.to = to;
		}
	}
	
	private LabTest parseLabTest(Element test) {
		String regadb_name = test.getAttributeValue("regadb-name");
		String regadb_type_name = test.getAttributeValue("regadb-type-name");
		String hicdep_lab_id = test.getAttributeValue("hicdep-lab-id");
		Integer hicdep_lab_unit = intOrNull(test.getAttributeValue("hicdep-lab-unit"));
		
		LabTest lt = new LabTest(regadb_name, regadb_type_name, hicdep_lab_id, hicdep_lab_unit, new ArrayList<Mapping>());
		
		List<Element> mappings = test.getChildren("map");
		for (Element mapping : mappings) {
			String type = mapping.getAttributeValue("type");
			String from = mapping.getAttributeValue("from");
			String to = mapping.getAttributeValue("to");
			
			Mapping.Type t = null;
			if (type.equals("string"))
				t = Mapping.Type.String;
			else if (type.equals("interval"))
				t = Mapping.Type.Interval;
			
			lt.mappings.add(new Mapping(t, from, to));
		}
		
		return lt;
	}
	
	private Attribute BAScenter;
	private Event BASenrol_d;
	private Test SAMPLESsamp_type;
	private Map<String, String> SAMPLESsamp_type_mapping = new HashMap<String, String>();
	private List<LabTest> LABtests = new ArrayList<LabTest>();

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
		
		Element LAB = e.getChild("LAB");
		if (LAB != null) {
			List<Element> tests = LAB.getChildren("test");
			
			for (Element test : tests)
				LABtests.add(parseLabTest(test));
		}
		}
	}
	
	private Integer intOrNull(String s) {
		if (s != null) 
			return Integer.parseInt(s);
		else 
			return null;
	}

	public void setDefaults() {
		BAScenter = null;
		BASenrol_d = null;
		SAMPLESsamp_type = null;
		SAMPLESsamp_type_mapping = new HashMap<String, String>();
		LABtests = new ArrayList<LabTest>();
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
	
	public List<LabTest> getLABtests() {
		return LABtests;
	}

	public void setLABtests(List<LabTest> lABtests) {
		LABtests = lABtests;
	}
}
