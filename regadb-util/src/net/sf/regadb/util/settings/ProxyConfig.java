package net.sf.regadb.util.settings;

import java.util.ArrayList;

import net.sf.regadb.util.settings.ProxyConfig.ProxyServer.Type;

import org.jdom.Comment;
import org.jdom.Element;

public class ProxyConfig extends ConfigParser {
	public static class ProxyServer{
		public static enum Type {HTTP,SOCKS};
		
		private String host, port;
		private String user, password;
		private Type type;
		
		public ProxyServer(String host, String port){
			this(host,port,Type.HTTP);
		}
		
		public ProxyServer(String host, String port, Type type){
			this(host, port, null, null, type);
		}
		
		public ProxyServer(String host, String port, String user, String password, Type type){
			this.host = host;
			this.port = port;
		    this.user = user;
		    this.password = password;
		    this.type = type;
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
		public Type getType(){
			return type;
		}
	}

	private ArrayList<ProxyServer> proxyList = new ArrayList<ProxyServer>();
	
	public ProxyConfig(){
		super("proxies");
	}
	
	public void parseXml(RegaDBSettings settings, Element e) {
		for(Object o : e.getChildren()){
			Element ee = (Element)o;
			ProxyServer ps;
			
			String host = ee.getChildTextTrim("host");
			String port = ee.getChildTextTrim("port");
			String user = ee.getChildTextTrim("user");
			String pass = ee.getChildTextTrim("password");
			
			ProxyServer.Type type = ProxyServer.Type.HTTP;
			if("socks".equals(ee.getAttributeValue("type")))
				type = ProxyServer.Type.SOCKS;
			
			if(user == null)
			    ps = new ProxyServer(host, port, type);
			else
			    ps = new ProxyServer(host, port, user, pass, type);
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
			
			if(ps.getType() == Type.SOCKS)
				e.setAttribute("type", "socks");
			else
				e.setAttribute("type", "http");
			
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
        	if(proxy.getType() == ProxyServer.Type.HTTP){
                System.setProperty("http.proxyHost", proxy.getHost());
                
                if(proxy.getPort() != null)
                	System.setProperty("http.proxyPort", proxy.getPort());

                if(proxy.getUser() != null)
                    System.setProperty("http.proxyUser", proxy.getUser());
                
                if(proxy.getPassword() != null)
                    System.setProperty("http.proxyPassword", proxy.getPassword());
        	}
        	else if(proxy.getType() == ProxyServer.Type.SOCKS){
        		System.setProperty("socksProxyHost", proxy.getHost());
        		
        		if(proxy.getPort() != null)
        			System.setProperty("socksProxyPort", proxy.getPort());

        		if(proxy.getUser() != null)
        			System.setProperty("java.net.socks.username", proxy.getUser());

        		if(proxy.getPassword() != null)
        			System.setProperty("java.net.socks.password", proxy.getPassword());
        	}
        }
    }
    
	public void initProxySettings(){
        if(proxyList.size()==1)
            setProxySettings(proxyList.get(0));
    }
}
