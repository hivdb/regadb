package net.sf.regadb.util.settings;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.jdom.Element;

public class CronConfig extends ConfigParser{
	private List<JobElement> jobs = new LinkedList<JobElement>();
	
	public static class JobElement{
		private String name, className, expression;
		private Map<String,String> params = new TreeMap<String,String>();
		
		public JobElement(Element e){
			parseXml(e);
		}
		
		public JobElement(String name, String className){
			setName(name);
			setClassName(className);
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
		
		public void setExpression(String expression) {
			this.expression = expression;
		}
		
		public String getExpression() {
			return expression;
		}

		public void setClassName(String className) {
			this.className = className;
		}

		public String getClassName() {
			return className;
		}
		
		public Map<String,String> getParams(){
			return params;
		}
		
		public void parseXml(Element e){
			setName(e.getAttributeValue("name"));
			setExpression(e.getAttributeValue("expression"));
			setClassName(e.getAttributeValue("class"));
			
			for(Object o : e.getChildren("param")){
				Element ee = (Element)o;
				getParams().put(
						ee.getAttributeValue("name"),
						ee.getAttributeValue("value"));
			}
		}
		
		public Element toXml(){
			Element e = new Element("job");
			e.setAttribute("name",getName());
			e.setAttribute("expression", getExpression());
			e.setAttribute("class",getClassName());
			
			for(Map.Entry<String, String> me : getParams().entrySet()){
				Element ee = new Element("param");
				ee.setAttribute("name", me.getKey());
				ee.setAttribute("value", me.getValue());
				e.addContent(ee);
			}
			return e;
		}
	}

	public CronConfig() {
		super("cron");
	}

	public void parseXml(RegaDBSettings settings, Element e) {
		for(Object o : e.getChildren("job"))
			jobs.add(new JobElement((Element)o));
	}

	public void setDefaults() {
	}

	public Element toXml() {
		Element e = new Element(getXmlTag());
		for(JobElement j : jobs)
			e.addContent(j.toXml());
		return e;
	}
	
	public List<JobElement> getJobs(){
		return jobs;
	}
}
