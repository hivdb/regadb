package net.sf.regadb.util.settings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Element;

public class AccessPolicyConfig implements IConfigParser {
	private List<String> roles = new ArrayList<String>();
	private Map<String, List<String>> blockedAttributes = new HashMap<String, List<String>>();
	private List<String> admins = new ArrayList<String>();
	
	
	public AccessPolicyConfig(){
	    setDefaults();
	}
	
	public String getXmlTag() {
		return "access-policies";
	}

	public void parseXml(RegaDBSettings settings, Element e) {
		List rolesE = e.getChild("roles").getChildren();
		List blockedAttributesE = e.getChild("blocked-attributes").getChildren();
		List adminsE = e.getChild("admins").getChildren();
		
		for(Object r : rolesE) {
			Element rr = (Element)r;
			roles.add(rr.getTextTrim());
		}
		
		String role;
		String attribute;
		for(Object ba : blockedAttributesE) {
			Element baa = (Element)ba;
			role = baa.getChildTextTrim("role");
			attribute = baa.getChildTextTrim("attribute");
			if(blockedAttributes.get(role)==null) {
				blockedAttributes.put(role, new ArrayList<String>());
			}
			blockedAttributes.get(role).add(attribute);
		}
		
		for(Object a : adminsE) {
			Element aa = (Element)a;
			admins.add(aa.getTextTrim());
		}
	}

	public void setDefaults() {
		roles.clear();
		admins.clear();
		blockedAttributes.clear();
		
		roles.add("admin");
		admins.add("admin");
	}
	
	public List<String> getRoles() {
		return roles;
	}

	public Map<String, List<String>> getBlockedAttributes() {
		return blockedAttributes;
	}

	public List<String> getAdmins() {
		return admins;
	}
	
	public boolean isAdmin(String userName) {
		return admins.contains(userName);
	}

	public Element toXml() {
	    Element r = new Element(getXmlTag());
        Element e;
	    
	    e = new Element("roles");
	    r.addContent(e);
	    for(String role : roles){
	        Element ee = new Element("role");
	        ee.setText(role);
	        e.addContent(ee);
	    }
	    
	    e = new Element("blocked-attributes");
	    r.addContent(e);
	    for(Map.Entry<String, List<String>> ba : blockedAttributes.entrySet()){
	        for(String attr : ba.getValue()){
    	        Element ee = new Element("blocked-attribute");

    	        Element eee = new Element("role");
    	        eee.setText(ba.getKey());
    	        ee.addContent(eee);
                
    	        eee = new Element("attribute");
                eee.setText(attr);
                ee.addContent(eee);
                
                e.addContent(ee);
	        }
	    }
	    
	    e = new Element("admins");
	    r.addContent(e);
	    for(String role : admins){
	        Element ee = new Element("role");
	        ee.setText(role);
	        e.addContent(ee);
	    }
	    
		return r;
	}
}
