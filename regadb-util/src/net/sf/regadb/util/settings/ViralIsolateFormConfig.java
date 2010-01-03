package net.sf.regadb.util.settings;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Comment;
import org.jdom.Element;

public class ViralIsolateFormConfig extends FormConfig {
	public static class TestItem{
        public TestItem(){
        }
        public TestItem(String d){
            description = d;
        }
        public TestItem(String d, String o){
            description = d;
            organism = o;
        }
        public String description = null;
        public String organism = null;
    }
	
	public static class ScoreInfo {
		private Color color;
		private Color backgroundColor;
		private String stringRepresentation;
		
		public Color getColor() {
			return color;
		}
		
		public void setColor(Color color) {
			this.color = color;
		}
		
		public Color getBackgroundColor() {
			return backgroundColor;
		}
		
		public void setBackgroundColor(Color backgroundColor) {
			this.backgroundColor = backgroundColor;
		}
		
		public String getStringRepresentation() {
			return stringRepresentation;
		}
		
		public void setStringRepresentation(String stringRepresentation) {
			this.stringRepresentation = stringRepresentation;
		}
	}

    public static final String NAME = "form.viralIsolate";
    
    private List<TestItem> tests = new ArrayList<TestItem>();
    private List<String> algorithms;
    private Map<Double, ScoreInfo> gss = new HashMap<Double, ScoreInfo>();
    private Map<Double, ScoreInfo> gssAssumptions = new HashMap<Double, ScoreInfo>();
    
	public ViralIsolateFormConfig() {
		super(NAME);
		setDefaults();
	}

	public void parseXml(RegaDBSettings settings, Element e) {
		tests.clear();
		
		Element ee = (Element)e.getChild("tests");
		
        for(Object o : ee.getChildren()){
        	String d = ((Element)o).getAttributeValue("description");
        	String g = ((Element)o).getAttributeValue("organism");
        	if(d != null)
        		tests.add(new TestItem(d,g));
        }
        
        ee = e.getChild("resistance");
        if (ee != null) {
	        Element eee = ee.getChild("algorithms");
	        if (eee != null) {
	        	algorithms = new ArrayList<String>();
		        for (Object o : eee.getChildren()) {
		        	String algorithm = ((Element)o).getAttributeValue("name");
		        	algorithms.add(algorithm);
		        }
	        }
	        
	        eee = ee.getChild("scores");
	        readScoreInfos(eee, gss);
	        
	        eee = ee.getChild("scores-assumptions");
	        readScoreInfos(eee, gssAssumptions);
        }
	}
	
	private void readScoreInfos(Element eee, Map<Double, ScoreInfo> gssInfo) {
		if (eee == null) 
			return;
		
		for (Object o : eee.getChildren()) {
			String gss = ((Element) o).getAttributeValue("gss");
			String color = ((Element) o).getAttributeValue("color");
			String backgroundColor = ((Element) o)
					.getAttributeValue("background-color");
			String stringRepresentation = ((Element) o).getAttributeValue("string");

			ScoreInfo si = new ScoreInfo();
			si.setColor(Color.decode(color));
			si.setBackgroundColor(Color.decode(backgroundColor));
			si.setStringRepresentation(stringRepresentation);
			gssInfo.put(Double.parseDouble(gss), si);
		}
	}

	public void setDefaults() {
		tests.clear();
	}

	public Element toXml(){
		Element r = super.toXml();
		Element e;
		
		r.addContent(new Comment("Viral Isolate form configuration."));
		
		if(tests.size() > 0){
			e = new Element("tests");
			r.addContent(e);
			e.addContent(new Comment("List of tests avaiable in the viral isolate form."));
			
			for(TestItem ti : tests){
				Element ee = new Element("test");
				ee.setAttribute("description", ti.description);
				if(ti.organism != null)
					ee.setAttribute("organism",ti.organism);
				
				e.addContent(ee);
			}
		}

		return r;
	}
	
	public List<TestItem> getTests(){
		return tests;
	}
	
	public List<String> getAlgorithms() {
		return algorithms;
	}
	
	public ScoreInfo getScoreInfo(double score, boolean assumption) {
		if (assumption) 
			return gssAssumptions.get(score);
		else
			return gss.get(score);
	}
}
