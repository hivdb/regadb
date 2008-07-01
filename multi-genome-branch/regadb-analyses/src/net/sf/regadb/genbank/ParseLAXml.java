package net.sf.regadb.genbank;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

public class ParseLAXml {
	public static void main(String [] args) {
        try {
        	SAXBuilder builder = new SAXBuilder();
        	
            Document doc = builder.build("/home/plibin0/projects/genbank/HXB2.xml");
            Element root = doc.getRootElement();

            final int startLine = 2;
            
            List<Element> elList = new ArrayList<Element>();
            findElementRecursively(root, "pre", elList);
            
            Element pre = ((Element)elList.get(0));
            String [] lines = pre.getValue().split("\n");
            for(int i = startLine; i<lines.length; i++) {
            	String [] words = lines[i].split(" ");
            	try {
            		int offset = Integer.parseInt(words[words.length-1]);
            		//System.err.println(lines[i]);
            		i += processAnnotations(lines, i, offset);
            	} catch(NumberFormatException nfe) {
            		
            	}
            }
            
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	private static int processAnnotations(String [] lines, int pos, int offset) {
		int jump = 0;
		List<String> annotationLines = new ArrayList<String>();
		for(int i = pos + 1; i <lines.length; i++) {
        	String [] words = lines[i].split(" ");
        	try {
        		Integer.parseInt(words[words.length-1]);
        		break;
        	} catch(NumberFormatException nfe) {
        		annotationLines.add(lines[i]);
        		//System.err.println(lines[i]);
        		jump++;
        	}
		}
		
		for(int i = 0; i<annotationLines.size(); i++) {
			String l = annotationLines.get(i);
			int i1 = l.indexOf('\\');
			if(i1!=-1) {
				p(i1, getAnnotationBack(l, i1), offset);
			}
			int i2 = l.indexOf('/');
			if(i2!=-1) {
				p(i2, getAnnotationFront(l, i2), offset);
			}
			int i3 = l.indexOf('<');
			if(i3!=-1) {
				p(i3, getAnnotationBack(l, i3), offset);
			}
			int i4 = l.indexOf('>');
			if(i4!=-1) {
				p(i4, getAnnotationFront(l, i4), offset);
			}
		}
		
		return jump;
	}
	
	public static void p(int start, String s, int offset) {
		System.err.println((offset+start) + "\n" + s.trim());
	}
	
	private static String locationId = "\\/<>";
	
	public static String getAnnotationBack(String l, int start) {
		for(int i = start-1; i>0; i--) {
			if(locationId.contains(l.charAt(i)+"")) {
				return l.substring(i, start);
			}
		}
		
		return l.substring(0, start);
	}
	
	public static  String getAnnotationFront(String l, int start) {
		for(int i = start+1; i<l.length(); i++) {
			if(locationId.contains(l.charAt(i)+"")) {
				return l.substring(start+1, i);
			}
		}
		return l.substring(start+1, l.length());
	}
	
    private static void findElementRecursively(Element parent, String name, List<Element> elementList) {
        for(Object o : parent.getChildren()) {
            Element e = (Element)o;
            if(e.getName().trim().equals(name)) {
                elementList.add(e.getParentElement());
            } else {
                findElementRecursively(e, name, elementList);
            }
        }
    }
	
}
