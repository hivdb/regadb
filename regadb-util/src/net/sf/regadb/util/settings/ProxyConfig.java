package net.sf.regadb.util.settings;

import java.util.ArrayList;

import org.jdom.Comment;
import org.jdom.Element;

public class ProxyConfig implements IConfigParser {
	public static class ProxyServer{
		private String host, port;
		private String user, password;
		
		public ProxyServer(String host, String port){
			this.host = host;
			this.port = port;
			this.user = null;
			this.password = null;
		}
		
		public ProxyServer(String host, String port, String user, String password){
		    this(host, port);
		    this.user = user;
		    this.password = password;
		}
		
		public String getHost(){
			return host;
		}
		public String getPort(){
			return port;
		}
		public String getUser(){
		    return user;
		}
		public String getPassword(){
		    return password;
		}
	}

	private ArrayList<ProxyServer> proxyList = new ArrayList<ProxyServer>();
	
	public ProxyConfig(){
		setDefaults();
	}
	
	public String getXmlTag() {
		return "proxies";
	}

	public void parseXml(RegaDBSettings settings, Element e) {
		for(Object o : e.getChildren()){
			Element ee = (Element)o;
			ProxyServer ps;
			
			String host = ee.getChildTextTrim("host");
			String port = ee.getChildTextTrim("port");
			String user = ee.getChildTextTrim("user");
			String pass = ee.getChildTextTrim("password");
			
			if(user == null)
			    ps = new ProxyServer(host, port);
			else
			    ps = new ProxyServer(host, port, user, pass);
			proxyList.add(ps);
		}
	}

	public void setDefaults() {
		proxyList.clear();
	}

	public Element toXml() {
		Element r = new Element(getXmlTag());
		
		r.addContent(new Comment("Optional list of proxies."));
		
		for(ProxyServer ps : proxyList){
			Element e = new Element("proxy");
			r.addContent(e);
			
			Element ee = new Element("host");
			ee.setText(ps.getHost());
			e.addContent(ee);
			
			ee = new Element("port");
			ee.setText(ps.getPort());
			e.addContent(ee);
			
			if(ps.getUser() != null){
			    ee = new Element("user");
			    ee.setText(ps.getUser());
			    e.addContent(ee);
			}
			
			if(ps.getPassword() != null){
                ee = new Element("password");
                ee.setText(ps.getPassword());
                e.addContent(ee);
            }
		}
		
		return r;
	}

	public ArrayList<ProxyServer> getProxyList(){
		return proxyList;
	}
	
    public void setProxySettings(ProxyServer proxy){
        if(proxy.getHost() != null){
            System.setProperty("http.proxyHost", proxy.getHost());
            
            if(proxy.getPort() != null)
            	System.setProperty("http.proxyPort", proxy.getPort());

            if(proxy.getUser() != null)
                System.setProperty("http.proxyUser", proxy.getUser());
            
            if(proxy.getPassword() != null)
                System.setProperty("http.proxyPassword", proxy.getPassword());
        }
    }
    
	public void initProxySettings(){
        if(proxyList.size()==1)
            setProxySettings(proxyList.get(0));
    }
}
