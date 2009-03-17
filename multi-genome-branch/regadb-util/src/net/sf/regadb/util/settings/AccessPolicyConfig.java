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
		//TODO
		return null;
	}
}
