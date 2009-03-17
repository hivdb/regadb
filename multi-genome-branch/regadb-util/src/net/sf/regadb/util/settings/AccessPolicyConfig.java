package net.sf.regadb.util.settings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Element;

public class AccessPolicyConfig implements IConfigParser {
	public class BlockedAttribute {
		public String attributeName;
		public String groupName;		
	}
	
	private List<String> roles = new ArrayList<String>();
	private Map<String, List<BlockedAttribute>> blockedAttributes = new HashMap<String, List<BlockedAttribute>>();
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
		for(Object ba : blockedAttributesE) {
			Element baa = (Element)ba;
			role = baa.getChildTextTrim("role");
			BlockedAttribute ba_o = new BlockedAttribute();
			ba_o.attributeName = baa.getChildTextTrim("name");
			ba_o.groupName = baa.getChildTextTrim("group");
			if(blockedAttributes.get(role)==null) {
				blockedAttributes.put(role, new ArrayList<BlockedAttribute>());
			}
			blockedAttributes.get(role).add(ba_o);
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

	public Map<String, List<BlockedAttribute>> getBlockedAttributes() {
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
	    for(Map.Entry<String, List<BlockedAttribute>> ba : blockedAttributes.entrySet()){
	        for(BlockedAttribute attr : ba.getValue()){
    	        Element ee = new Element("blocked-attribute");

    	        Element eee = new Element("role");
    	        eee.setText(ba.getKey());
    	        ee.addContent(eee);
                
    	        eee = new Element("group");
                eee.setText(attr.groupName);
                ee.addContent(eee);
                
    	        eee = new Element("name");
                eee.setText(attr.attributeName);
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
