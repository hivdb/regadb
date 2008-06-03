package net.sf.regadb.install.wizard;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.sf.regadb.util.pair.Pair;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

public class GlobalConf {
	private static final long serialVersionUID = 561374770071044439L;
	private static GlobalConf instance_ = null;
	private Element root = null;
	private ArrayList<ChangeListener> listeners_ = new ArrayList<ChangeListener>();
	
	private GlobalConf() {}
	
	public Document setFile(String fileName) {
		return setFile(new File(fileName));
	}
	public Document setFile(File file) {
		SAXBuilder builder = new SAXBuilder();
		Document doc = null;
        
        try {
            doc = builder.build(file);
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        if ( doc != null ) {
        	root = doc.getRootElement();
        }
        
        fireEvent();
        
        return doc;
	}
	
	private synchronized void fireEvent() {
		for(ChangeListener ccl : listeners_) {
			ccl.changed();
		}
	}
	
	public void addListener(ChangeListener ccl) {
		listeners_.add(ccl);
	}
	
	@SuppressWarnings("unchecked")
	public String getProperty(String key) {
		if ( root != null ) {
			for(Element e : (List<Element>)root.getChildren("property")) {
				if ( e.getAttribute("name").getValue().equals(key) ) {
					return e.getValue();
				}
			}
	//		System.err.println("Property not found: " + key);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<Pair<String, String>> getProxies() {
		List<Pair<String, String>> proxies = new ArrayList<Pair<String, String>>();
		for(Element e : (List<Element>)root.getChildren("proxy")) {
			List<Element> prop = (List<Element>)e.getChildren("property");
			String proxy = prop.get(0).getValue();
			String port = prop.get(1).getValue();
			proxies.add(new Pair(proxy, port));
		}
		return proxies;
	}
	
	public static GlobalConf getInstance() {
		if ( instance_ == null ) {
			instance_ = new GlobalConf();
		}
		return instance_;
	}
}
