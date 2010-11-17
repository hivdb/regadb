package net.sf.regadb.util.settings;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Comment;
import org.jdom.Element;

public class AccessPolicyConfig extends ConfigParser {
	public static enum AccessMode{STANDALONE, INTEGRATED};
	
	private Map<String, Role> roles = new HashMap<String, Role>();
	private AccessMode accessMode;
	
	public AccessPolicyConfig(){
		super("access-policies");
	}

	public void parseXml(RegaDBSettings settings, Element e) {
		String s = e.getChildTextTrim("access-mode");
		try{
			if(s != null)
				accessMode = AccessMode.valueOf(s);
		}catch(Exception ex){
			System.err.println("Invalid access-mode: "+ s);
		}
		
		for(Object o : e.getChild("roles").getChildren()){
			Element ee = (Element)o;
			Role r = new Role();
			r.parseXml(settings, ee);
			roles.put(r.getName(), r);
		}
	}

	public void setDefaults() {
		accessMode = AccessMode.STANDALONE;
		
		roles.clear();
		
		Role r = new Role();
		r.setName("admin");
		r.setAdmin(true);
		addRole(r);
	}
	
	public void addRole(Role r){
		roles.put(r.getName(),r);
	}
	
	public Map<String, Role> getRoles() {
		return roles;
	}
	
	public Role getRole(String name){
		return roles.get(name);
	}

	public List<AttributeConfig> getBlockedAttributes(String role) {
		return getRole(role).getBlockedAttributes();
	}
	
	public AccessMode getAccessMode(){
		return accessMode;
	}
	public void setAccessMode(AccessMode accessMode){
		this.accessMode = accessMode;
	}

	public Element toXml() {
	    Element r = new Element(getXmlTag());
        Element e;
	    
	    r.addContent(new Comment("RegaDB can be used in STANDALONE (default) or INTEGRATED mode. " +
	    		"Integrated mode bypasses the login-form through the URL, i.e. to log in with 'user1', " +
	    		"access the application with the following URL: RegaDB?uid=user1")); 
        e = new Element("access-mode");
        e.setText(""+accessMode);
        r.addContent(e);
        
        r.addContent(new Comment("Each user is assigned to one role."));
	    e = new Element("roles");
	    r.addContent(e);
	    for(Role role : roles.values())
	        e.addContent(role.toXml());
	    
		return r;
	}
}
