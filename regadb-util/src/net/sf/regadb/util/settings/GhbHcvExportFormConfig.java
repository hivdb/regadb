package net.sf.regadb.util.settings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Element;

public class GhbHcvExportFormConfig extends FormConfig {
    public static final String NAME = "form.export.ghb-hcv-export";
    
    private List<String> columnNames = new ArrayList<String>();
    
    //TODO, somehow add support for event intervals?
    private static class Cell<T> {
        private enum Kind {
        	Value, Date
        }
       
        public Cell(Kind kind, T object) {
        	this.kind = kind;
        	this.object = object;
        }
        
    	public Kind kind;
    	public T object;
    	Map<String, String> properties = new HashMap<String, String>();
    }

    private Map<String, Cell<TestItem>> tests = new HashMap<String, Cell<TestItem>>();
    private Map<String, TestItem> therapyTests = new HashMap<String, TestItem>();
	private Map<String, Cell<EventItem>> events = new HashMap<String, Cell<EventItem>>();
    private Map<String, AttributeItem> attributes = new HashMap<String, AttributeItem>();
    
	public GhbHcvExportFormConfig() {
		super(NAME);
		setDefaults();
	}

	private static String DATE_FORMAT = "date-format";
	public void parseXml(RegaDBSettings settings, Element e) {
		tests.clear();
		therapyTests.clear();
		events.clear();
		attributes.clear();
		
		Element ee = (Element)e.getChild("export-definition");
		for (Element columnE : (List<Element>)ee.getChildren("column")) {
			String colName = columnE.getAttributeValue("name");
			columnNames.add(colName);
			List<Element> colConfigurationEs = columnE.getChildren();
			if (colConfigurationEs.size() != 1)
				throw new RuntimeException(colError(colName));
			Element colConfigurationE = colConfigurationEs.get(0);
			String name = colConfigurationE.getName();
			if (name.startsWith("test")) {
				String testType = colConfigurationE.getAttributeValue("type");
				String testDescription = colConfigurationE.getAttributeValue("description");
				TestItem ti = new TestItem();
				ti.type = testType;
				ti.description = testDescription;
				if (name.equals("test-value")) {
					tests.put(colName, new Cell<TestItem>(Cell.Kind.Value, ti));
				} else if (name.equals("test-date")) {
					Cell<TestItem> c = new Cell<TestItem>(Cell.Kind.Date, ti);
					c.properties.put(DATE_FORMAT, colConfigurationE.getAttributeValue(DATE_FORMAT));
					tests.put(colName, c);
				} else {
					throw new RuntimeException(colError(colName));
				}
			} else if (name.startsWith("event")) {
				String eventName = colConfigurationE.getAttributeValue("name");
				EventItem ei = new EventItem();
				ei.name = eventName;
				if (name.equals("event-value")) {
					events.put(colName, new Cell<EventItem>(Cell.Kind.Value, ei));
				} else {
					throw new RuntimeException(colError(colName));
				}
			} else if (name.equals("therapy-test-value")) {
				String testType = colConfigurationE.getAttributeValue("type");
				String testDescription = colConfigurationE.getAttributeValue("description");
				TestItem ti = new TestItem();
				ti.type = testType;
				ti.description = testDescription;
				therapyTests.put(colName, ti);
			} else if (name.equals("attribute")) {
				String attributeGroup = colConfigurationE.getAttributeValue("group");
				String attributeName = colConfigurationE.getAttributeValue("name");
				AttributeItem ai = new AttributeItem();
				ai.group = attributeGroup;
				ai.name = attributeName;
				attributes.put(colName, ai);
			} else {
				throw new RuntimeException(colError(colName));
			}
		}
	}
	
	private String colError(String colName) {
		return "Column \"" + colName + "\" is not properly configured!";
	}

	public void setDefaults() {
		tests.clear();
		therapyTests.clear();
		events.clear();
		attributes.clear();
	}

	public Element toXml(){
		//TODO
		return null;
	}
	
    public List<String> getColumnNames() {
		return columnNames;
	}

	public Map<String, Cell<TestItem>> getTests() {
		return tests;
	}
	
	public Map<String, TestItem> getTherapyTests() {
		return therapyTests;
	}

	public Map<String, Cell<EventItem>> getEvents() {
		return events;
	}

	public Map<String, AttributeItem> getAttributes() {
		return attributes;
	}
}
