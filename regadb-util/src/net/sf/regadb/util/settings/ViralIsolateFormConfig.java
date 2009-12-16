package net.sf.regadb.util.settings;

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
        public TestItem(String d, String o){
            description = d;
            organism = o;
        }
        public String description = null;
        public String organism = null;
    }

    public static final String NAME = "form.viralIsolate";
    
    private List<TestItem> tests = new ArrayList<TestItem>();

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
}
