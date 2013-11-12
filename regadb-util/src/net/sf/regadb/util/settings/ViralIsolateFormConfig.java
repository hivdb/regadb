package net.sf.regadb.util.settings;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.jdom.Comment;
import org.jdom.Element;

public class ViralIsolateFormConfig extends FormConfig {
	public static class TestItem{
        public TestItem(){
        }
        public TestItem(String d){
            description = d;
        }
        public TestItem(String d, String o, String defaultValue, boolean noValueSelected){
            description = d;
            organism = o;
            this.defaultValue = defaultValue;
            this.noValueSelected = noValueSelected;
        }
        public String description = null;
        public String organism = null;
        public String defaultValue = null;
        public boolean noValueSelected = true;
    }
	
	public static class ScoreInfo {
		private Color color;
		private Color backgroundColor;
		private String stringRepresentation;
		private String description;
		private double gssCutoff;
		
		public ScoreInfo() {
			
		}
		
		public ScoreInfo(Color color, Color backgroundColor,
				String stringRepresentation, String description, double gssCutoff) {
			this.color = color;
			this.backgroundColor = backgroundColor;
			this.stringRepresentation = stringRepresentation;
			this.description = description;
			this.gssCutoff = gssCutoff;
		}

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
		
		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}
		
		public double getGssCutoff() {
			return gssCutoff;
		}

		public void setGssCutoff(double gssCutoff) {
			this.gssCutoff = gssCutoff;
		}
	}
	
	public static class Algorithm {
		private String name;
		private String organism;
		
		public Algorithm(String name, String organism) {
			this.name = name;
			this.organism = organism;
		}
		
		public String getName() {
			return name;
		}
		
		public void setName(String name) {
			this.name = name;
		}
		
		public String getOrganism() {
			return organism;
		}
		
		public void setOrganism(String organism) {
			this.organism = organism;
		}
	}

    public static final String NAME = "form.viralIsolate";
    
    private List<TestItem> sequenceTests = new ArrayList<TestItem>();
    private List<TestItem> tests;
    private List<Algorithm> algorithms;
    private List<ScoreInfo> gss = new ArrayList<ScoreInfo>();
    
	public ViralIsolateFormConfig() {
		super(NAME);
		setDefaults();
	}
	
	private TestItem parseTestItem(Element e) {
       	String d = e.getAttributeValue("description");
    	String g = e.getAttributeValue("organism");
    	String defaultValue = e.getAttributeValue("defaultValue");
    	String noValueSelected = e.getAttributeValue("noValueSelected");
    	if (noValueSelected == null)
    		noValueSelected = "true";
    	
    	if(d != null)
    		return new TestItem(d,g,defaultValue, Boolean.parseBoolean(noValueSelected));
    	else
    		return null;
	}

	public void parseXml(RegaDBSettings settings, Element e) {
		sequenceTests.clear();
		tests = null;
		
		Element ee = null; 
		
		ee = (Element)e.getChild("tests");	
		if (ee != null) {
			tests = new ArrayList<TestItem>();
	        for(Object o : ee.getChildren()){
	        	TestItem ti = parseTestItem((Element)o);
	        	if (ti != null)	
	        		tests.add(ti);
	        }
		}
		
		ee = (Element)e.getChild("sequence-tests");
		if (ee != null) {
	        for(Object o : ee.getChildren()){
	        	TestItem ti = parseTestItem((Element)o);
	        	if (ti != null)	
	        		sequenceTests.add(ti);
	        }
		}
        
        ee = e.getChild("resistance");
        if (ee != null) {
	        Element eee = ee.getChild("algorithms");
	        if (eee != null) {
	        	algorithms = new ArrayList<Algorithm>();
		        for (Object o : eee.getChildren()) {
		        	String algorithm = ((Element)o).getAttributeValue("name");
		        	String organism = ((Element)o).getAttributeValue("organism");
		        	if (algorithm == null || organism == null)
		        		throw new RuntimeException("Error: incorrect algorithm configuration with algorithm '" + algorithm + "' and organism '" + organism + "'");
		        	algorithms.add(new Algorithm(algorithm, organism));
		        }
	        }
	        
	        eee = ee.getChild("scores");
	        readScoreInfos(eee, gss);
        }
	}
	
	public boolean containsAlgorithm(String algorithm, String genome){
		if(algorithms == null)
			return false;
		
		algorithm = algorithm.trim().toLowerCase();
		genome = genome.trim().toLowerCase();
		
		for(Algorithm algo : algorithms){
			if(algo.getName().toLowerCase().equals(algorithm)
					&& algo.getOrganism().toLowerCase().equals(genome))
				return true;
		}
		
		return false;
	}
	
	private void readScoreInfos(Element eee, List<ScoreInfo> gssInfo) {
		if (eee == null) 
			return;
		
		gssInfo.clear();
		for (Object o : eee.getChildren()) {
			String gssCutoff = ((Element) o).getAttributeValue("gssCutoff");
			String color = ((Element) o).getAttributeValue("color");
			String backgroundColor = ((Element) o)
					.getAttributeValue("background-color");
			String stringRepresentation = ((Element) o).getAttributeValue("string");
			String description = ((Element) o).getAttributeValue("description");

			ScoreInfo si = new ScoreInfo();
			si.setColor(Color.decode(color));
			si.setBackgroundColor(Color.decode(backgroundColor));
			si.setStringRepresentation(stringRepresentation);
			si.setDescription(description);
			si.setGssCutoff(Double.parseDouble(gssCutoff));
			gssInfo.add(si);
		}
	}

	public void setDefaults() {
		sequenceTests.clear();
		tests = null;
		
		gss.clear();
		gss.add(new ScoreInfo(Color.decode("#000"), Color.decode("#FF0000"), "R", "Resistant", 0.0));
		gss.add(new ScoreInfo(Color.decode("#000"), Color.decode("#FFFF00"), "I", "Intermediate resistant", 0.25));
		gss.add(new ScoreInfo(Color.decode("#000"), Color.decode("#FFFF00"), "I", "Intermediate resistant", 0.5));
		gss.add(new ScoreInfo(Color.decode("#000"), Color.decode("#FFFF00"), "I", "Intermediate resistant", 0.75));
		gss.add(new ScoreInfo(Color.decode("#000"), Color.decode("#00ff00"), "S", "Susceptible", Double.POSITIVE_INFINITY));
	}

	private Element toXml(TestItem ti) {
		Element ee = new Element("test");
		ee.setAttribute("description", ti.description);
		if(ti.organism != null)
			ee.setAttribute("organism",ti.organism);
		return ee;
	}
	
	public Element toXml(){
		Element r = super.toXml();
		Element e;
		
		r.addContent(new Comment("Viral Isolate form configuration."));
		
		if(tests != null){
			e = new Element("tests");
			r.addContent(e);
			e.addContent(new Comment("List of tests avaiable in the viral isolate form."));
			
			for(TestItem ti : tests){
				e.addContent(toXml(ti));
			}
		}
		
		if(sequenceTests.size() > 0){
			e = new Element("seqeunce-tests");
			r.addContent(e);
			e.addContent(new Comment("List of sequence tests avaiable in the sequence part of the viral isolate form."));
			
			for(TestItem ti : tests){
				e.addContent(toXml(ti));
			}
		}

		return r;
	}
	
	public List<TestItem> getTests(){
		return tests;
	}
	
	public List<TestItem> getSequenceTests(){
		return sequenceTests;
	}
	
	public List<Algorithm> getAlgorithms() {
		return algorithms;
	}
	
	public ScoreInfo getScoreInfo(double score) {
		for (ScoreInfo si : gss) {
			if (score <= si.getGssCutoff()) {
				return si;
			}
		}
		return null;
	}
	
	public List<ScoreInfo> getScoreInfos() {
		return gss;
	}
}
