package net.sf.regadb.util.xml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Stack;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * @author simbre1
 * 
 * Parses a file using SAX, but has the possibility to construct a (partial) DOM tree as well.
 */
public abstract class MixedXMLParser extends DefaultHandler {
	private StringBuilder values = new StringBuilder();
	private Stack<String> path = new Stack<String>();
	
	private boolean stop = false;
	private boolean dom = false;
	
	private Element root = null;
	private Stack<Element> tree = new Stack<Element>();
	private Document doc = new Document();
	
    @Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if(!stopped()) {
			path.push(qName);
			
			startElement(qName, attributes);
			
			if(constructDomTree()){
				Element e = new Element(qName);
				for(int i = 0; i < attributes.getLength(); ++i)
					e.setAttribute(attributes.getQName(i), attributes.getValue(i));
				
				if(root == null){
					tree.clear();
					root = e;
					tree.push(e);
					doc.setRootElement(root);
				}
				else{
					tree.peek().addContent(e);
					tree.push(e);
				}
			}
		}
    }

	@Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
    	if(!stopped()) {
    		String text = values.toString().trim();
    		if(constructDomTree()){
    			if(text.length() > 0)
    				tree.peek().setText(text);
    		}
    		
	    	endElement(qName, text);
	    	
	    	values.delete(0, values.length());
	    	path.pop();
	    	
	    	if(constructDomTree()){
	    		tree.pop();
	    	}
    	}
    }
    
	@Override
    public void characters(char[] ch, int start, int length) throws SAXException {
    	if(!stopped()) {
    		values.append(new String(ch, start, length));
    	}
    }
	
	public Element getElement(String xpath){
		Object o = getObject(xpath);
		if(o != null && o instanceof Element)
			return (Element)o;
		else
			return null;
	}
	
	private Object getObject(String xpath){
		if(root != null){
			try {
				XPath x = XPath.newInstance(xpath);
				return x.selectSingleNode(doc);
			} catch (JDOMException e) {
				return null;
			}
		} else {
			return null;
		}
	}
	
	public String getPath(){
    	StringBuilder sb = new StringBuilder("/");
    	boolean first = true;
    	for(String e : path){
    		if(first)
    			first = false;
    		else
    			sb.append('/');
    		sb.append(e);
    	}
    	return sb.toString();
	}
	    
    private void parse(InputSource source)  throws SAXException, IOException {
    	XMLReader xmlReader = XMLReaderFactory.createXMLReader();
        xmlReader.setContentHandler(this);
        xmlReader.setErrorHandler(this);
        try {
        	startFile();
        	xmlReader.parse(source);
        	endFile();
        } catch (SAXParseException spe) {
        	if(!spe.getMessage().equals("XML document structures must start and end within the same entity."))
        		spe.printStackTrace();
        }
    }

	public void parse(File xml) {
    	if(xml.exists()) {
	    	try {
				parse(new InputSource(new FileReader(xml)));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    }
	
	public void stop() {
		stop = true;
	}

	public boolean stopped(){
		return stop;
	}
	
	public void constructDomTree(boolean createDomTree){
		dom = createDomTree;
	}
	
	public boolean constructDomTree(){
		return dom;
	}
	
	public void destructDomTree(){
		root = null;
		tree.clear();
	}
	
	public Element getRoot(){
		return root;
	}
	
    protected abstract void startElement(String name, Attributes attributes);
    protected abstract void endElement(String qName, String text);

    protected abstract void startFile();
	protected abstract void endFile();
}
