package net.sf.regadb.util.mapper.matcher;

import org.jdom.Element;

public interface Matcher {
    public boolean matches(String s);
    
    public void parseXml(Element e);
}
