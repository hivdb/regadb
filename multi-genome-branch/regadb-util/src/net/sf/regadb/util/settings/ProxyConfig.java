package net.sf.regadb.util.settings;

import java.util.ArrayList;

import org.jdom.Element;

public class ProxyConfig implements IConfigParser {
	public static class ProxyServer{
		private String host, port;
		
		public ProxyServer(String host, String port){
			this.host = host;
			this.port = port;
		}
		
		public String getHost(){
			return host;
		}
		public String getPort(){
			return port;
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
			ProxyServer ps = new ProxyServer(ee.getChildTextTrim("url"),ee.getChildTextTrim("port"));
			proxyList.add(ps);
		}
	}

	public void setDefaults() {
		proxyList.clear();
	}

	public Element toXml() {
		Element r = new Element(getXmlTag());
		
		for(ProxyServer ps : proxyList){
			Element e = new Element("proxy");
			r.addContent(e);
			
			Element ee = new Element("url");
			ee.setText(ps.getHost());
			e.addContent(ee);
			
			ee = new Element("port");
			ee.setText(ps.getPort());
			e.addContent(ee);
		}
		
		return r;
	}

	public ArrayList<ProxyServer> getProxyList(){
		return proxyList;
	}
	
    public void setProxySettings(ProxyServer proxy){
    	String host = proxy.getHost();
    	String port = proxy.getPort();
        if(host != null && host.length() > 0){
            System.setProperty("http.proxyHost", proxy.getHost());
            if(port != null && port.length() > 0)
            	System.setProperty("http.proxyPort", proxy.getPort());
        }
    }
    
	public void initProxySettings(){
        if(proxyList.size()==1)
            setProxySettings(proxyList.get(0));
    }
}
