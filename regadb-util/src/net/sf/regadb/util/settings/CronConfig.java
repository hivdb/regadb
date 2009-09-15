package net.sf.regadb.util.settings;

import java.util.LinkedList;
import java.util.List;

import org.jdom.Element;

public class CronConfig implements IConfigParser{
	private List<JobElement> jobs = new LinkedList<JobElement>();
	
	public static class JobElement{
		private String name, className, expression;
		
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
		
		public void parseXml(Element e){
			setName(e.getAttributeValue("name"));
			setExpression(e.getAttributeValue("expression"));
			setClassName(e.getAttributeValue("class"));
		}
		
		public Element toXml(){
			Element e = new Element("job");
			e.setAttribute("name",getName());
			e.setAttribute("expression", getExpression());
			e.setAttribute("class",getClassName());
			return e;
		}
	}

	public String getXmlTag() {
		return "cron";
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
