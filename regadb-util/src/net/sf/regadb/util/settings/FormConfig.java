package net.sf.regadb.util.settings;

import org.jdom.Element;

public abstract class FormConfig extends ConfigParser{
	private String formName;
	
	public FormConfig(String formName){
		super("form");
		setFormName(formName);
	}

	public Element toXml() {
		Element e = new Element(getXmlTag());
		e.setAttribute("name", getFormName());
		return e;
	}

	public String getFormName(){
		return formName;
	}
	public void setFormName(String name){
		formName = name;
	}
}
