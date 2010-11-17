package net.sf.regadb.util.settings;

import java.util.HashMap;
import java.util.Map;

import org.jdom.Comment;
import org.jdom.Element;

public class HibernateConfig extends ConfigParser{
	private Map<String, String> properties = new HashMap<String,String>();
	
	public HibernateConfig(){
		super("hibernate");
	}

	public Element toXml() {
		Element r = new Element(getXmlTag());
		
		r.addContent(new Comment("Hibernate database connection configuration."));
		
		for(Map.Entry<String, String> e : properties.entrySet()){
			Element ee = new Element("property");
			ee.setAttribute("name", e.getKey());
			ee.setText(e.getValue());
			r.addContent(ee);
		}
		return r;
	}

	public void parseXml(RegaDBSettings settings, Element e) {
		for(Object o : e.getChildren()){
			Element ee = (Element)o;
			properties.put(ee.getAttributeValue("name"), ee.getText());
		}
	}

	public String getProperty(String name){
		return properties.get(name);
	}
	
	public Map<String,String> getProperties(){
		return properties;
	}
	
	public void setProperty(String name, String value){
		properties.put(name,value);
	}
	
	public void setDialect(String dialect) {
		properties.put("hibernate.dialect",dialect);
	}

	public String getDialect() {
		return properties.get("hibernate.dialect");
	}

	public void setDriverClass(String driverClass) {
		properties.put("hibernate.connection.driver_class",driverClass);
	}

	public String getDriverClass() {
		return properties.get("hibernate.connection.driver_class");
	}

	public void setUrl(String url) {
		properties.put("hibernate.connection.url",url);
	}

	public String getUrl() {
		return properties.get("hibernate.connection.url");
	}

	public void setPassword(String password) {
		properties.put("hibernate.connection.password",password);
	}

	public String getPassword() {
		return properties.get("hibernate.connection.password");
	}

	public void setUsername(String username) {
		properties.put("hibernate.connection.username",username);
	}

	public String getUsername() {
		return properties.get("hibernate.connection.username");
	}

	public void setDefaults() {
		setDialect("org.hibernate.dialect.PostgreSQLDialect");
		setDriverClass("org.postgresql.Driver");
		setUrl("jdbc:postgresql://localhost:5432/regadb");
		setPassword("regadb_password");
		setUsername("regadb_user");
	}
}
