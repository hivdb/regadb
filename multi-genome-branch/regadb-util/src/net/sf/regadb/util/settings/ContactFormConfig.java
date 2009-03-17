package net.sf.regadb.util.settings;

import java.util.ArrayList;
import java.util.List;

import org.jdom.Element;

public class ContactFormConfig extends FormConfig {
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
    public static class EventItem{
    	public EventItem(){
    		
    	}
    	public EventItem(String n){
    		name = n;
    	}
    	public String name;
    }

    public static final String NAME = "form.multipleTestResults.contact";
    
    private List<TestItem> tests = new ArrayList<TestItem>();
    private List<EventItem> events = new ArrayList<EventItem>();
    
    private boolean useContactDate = false;

	public ContactFormConfig() {
		super(NAME);
		setDefaults();
	}

	public void parseXml(RegaDBSettings settings, Element e) {
		tests.clear();
		events.clear();
		
		Element ee = (Element)e.getChild("tests");
		
        for(Object o : ee.getChildren()){
        	String d = ((Element)o).getAttributeValue("description");
        	String g = ((Element)o).getAttributeValue("organism");
        	if(d != null)
        		tests.add(new TestItem(d,g));
        }
        
        ee = (Element)e.getChild("events");
        if(ee != null){
        	String s = ee.getAttributeValue("useContactDate");
        	setUseContactDate(s != null && s.equals("true"));
        	
            for(Object o : ee.getChildren()){
            	s = ((Element)o).getAttributeValue("name");
            	if(s != null)
            		events.add(new EventItem(s));
            }
        }
	}

	public void setDefaults() {
		tests.clear();
		events.clear();
		
		tests.add(new TestItem("CD4 Count (generic)"));//StandardObjects.getGenericCD4Test().getDescription()));
        tests.add(new TestItem("CD8 Count (generic)"));//StandardObjects.getGenericCD8Test().getDescription()));
        tests.add(new TestItem("Viral Load (copies/ml) (generic)","HIV-1"));//StandardObjects.getGenericHiv1ViralLoadTest().getDescription(), StandardObjects.getHiv1Genome().getOrganismName()));
	}

	public Element toXml(){
		Element r = super.toXml();
		Element e;
		
		if(tests.size() > 0){
			e = new Element("tests");
			r.addContent(e);
			
			for(TestItem ti : tests){
				Element ee = new Element("test");
				ee.setAttribute("description", ti.description);
				if(ti.organism != null)
					ee.setAttribute("organism",ti.organism);
				
				e.addContent(ee);
			}
		}

		if(events.size() > 0){
			e = new Element("events");
			e.setAttribute("useContactDate",(getUseContactDate() ? "true":"false"));
			r.addContent(e);
			
			for(EventItem ei : events){
				Element ee = new Element("event");
				ee.setAttribute("name", ei.name);
				e.addContent(ee);
			}
		}

		return r;
	}
	
	public boolean getUseContactDate(){
		return useContactDate;
	}
	public void setUseContactDate(boolean useContactDate){
		this.useContactDate = useContactDate;
	}
	
	public List<TestItem> getTests(){
		return tests;
	}
	public List<EventItem> getEvents(){
		return events;
	}
}
