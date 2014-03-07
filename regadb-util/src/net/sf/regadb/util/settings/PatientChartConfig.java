package net.sf.regadb.util.settings;

import java.util.ArrayList;
import java.util.List;

import org.jdom.Element;

public class PatientChartConfig extends ConfigParser {
	public static class TestType {
		public String type;
		public boolean log = false;
		
		public TestType() {
			
		}
		
		public TestType(String type) {
			this.type = type;
		}
		
		public TestType(String type, boolean log) {
			this.type = type;
			this.log = log;
		}
	}
	public static class Item {
		public final static String ORGANISM_VARIABLE = "${organism}";
		public enum Kind {
			ViralLoad,
			CellCount
		}
		public Kind kind;
		public String name;
		public List<TestType> testTypes = new ArrayList<TestType>();
	}
	public static class Axis {
		public List<Item> items = new ArrayList<Item>();
	}

	private Axis y1 = new Axis();
	private Axis y2 = new Axis();
	
	public PatientChartConfig(){
		super("patient-chart");
	}

	public void parseXml(RegaDBSettings settings, Element e) {
		Element ee;
		
		ee = e.getChild("y1");
		if(ee != null){
			for (Object o : ee.getChildren("item")) {
				Element itemE = (Element)o;
				y1.items.add(parseItem(itemE));
			}
		}
		
		ee = e.getChild("y2");
		if(ee != null){
			for (Object o : ee.getChildren("item")) {
				Element itemE = (Element)o;
				y2.items.add(parseItem(itemE));
			}
		}
	}
	
	private Item parseItem(Element itemE) {
		Item item = new Item();
		item.kind = toKind(itemE.getAttributeValue("kind"));
		item.name = itemE.getAttributeValue("name");
		for (Object oo : itemE.getChildren("test-type")) {
			Element testTypeE = (Element)oo;
			TestType testType = new TestType();
			testType.type = testTypeE.getAttributeValue("type");
			testType.log = false;
			String log = testTypeE.getAttributeValue("log");
			if ("true".equals(log))
				testType.log = true;
			item.testTypes.add(testType);
		}
		return item;
	}
	
	private Item.Kind toKind(String s) {
		if ("cell-count".equals(s))
			return Item.Kind.CellCount;
		else if ("viral-load".equals(s))
			return Item.Kind.ViralLoad;
		else
			return null;
	}

	public void setDefaults() {
		Item cd4 = new Item();
		cd4.kind = Item.Kind.CellCount;
		cd4.name = "CD4";
		cd4.testTypes.add(new TestType("CD4 Count (cells/ul)"));
		y1.items.add(cd4);
		
		Item vl = new Item();
		vl.kind = Item.Kind.ViralLoad;
		vl.name = "Viral Load (${organism})";
		vl.testTypes.add(new TestType("Viral Load (copies/ml)"));
		vl.testTypes.add(new TestType("Viral Load (log10)", true));
		y2.items.add(vl);
		
	}

	public Element toXml() {
		return null;
	}

	public Axis getY1(){
		return y1;
	}
	
	public Axis getY2(){
		return y2;
	}
}
