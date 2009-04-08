package net.sf.regadb.util.settings;

import java.util.ArrayList;
import java.util.List;

import org.jdom.Element;

public class Role implements IConfigParser{
	private String name;
	private boolean admin;
	private boolean singlePatientView;
	
	private List<AttributeConfig> blockedAttributes = new ArrayList<AttributeConfig>();

	public Role(){
		setDefaults();
	}
	
	@Override
	public String getXmlTag() {
		return "role";
	}

	@Override
	public void parseXml(RegaDBSettings settings, Element e) {
		name = e.getChildTextTrim("name");
		admin = "true".equals(e.getChildTextTrim("admin"));
		singlePatientView = "true".equals(e.getChildTextTrim("single-patient-view"));
		
		Element ee = e.getChild("blocked-attributes");
		if(ee != null){
			for(Object o : ee.getChildren()){
				Element eee = (Element)o;
				AttributeConfig ac = new AttributeConfig();
				ac.parseXml(settings, eee);
				blockedAttributes.add(ac);
			}
		}
	}

	@Override
	public void setDefaults() {
		name = "anonymous";
		admin = false;
		singlePatientView = false;
		blockedAttributes.clear();
	}

	@Override
	public Element toXml() {
		Element e = new Element(getXmlTag());
		Element ee;
		
		ee = new Element("name");
		ee.setText(name);
		e.addContent(ee);
		
		ee = new Element("admin");
		ee.setText(""+ admin);
		e.addContent(ee);
		
		ee = new Element("single-patient-view");
		ee.setText(""+ singlePatientView);
		e.addContent(ee);
		
		ee = new Element("blocked-attributes");
		e.addContent(ee);
		for(AttributeConfig ac : blockedAttributes){
			ee.addContent(ac.toXml());
		}
		
		return e;
	}

	public String getName(){
		return name;
	}
	public void setName(String name){
		this.name = name;
	}
	
	public boolean isAdmin(){
		return admin;
	}
	public void setAdmin(boolean admin){
		this.admin = admin;
	}
	
	public boolean isSinglePatientView(){
		return singlePatientView;
	}
	public void setSinglePatientView(boolean singlePatientView){
		this.singlePatientView = singlePatientView;
	}
	
	public List<AttributeConfig> getBlockedAttributes(){
		return blockedAttributes;
	}
}
