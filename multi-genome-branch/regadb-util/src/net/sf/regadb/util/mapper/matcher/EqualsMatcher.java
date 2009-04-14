package net.sf.regadb.util.mapper.matcher;

import org.jdom.Element;

public class EqualsMatcher implements Matcher{
    private String reference;
    
    public boolean matches(String s){
        return getReference().equals(s);
    }
    
    public String getReference(){
        return reference;
    }
    public void setReference(String reference){
        this.reference = reference;
    }

    public void parseXml(Element e) {
        setReference(e.getTextTrim());
    }
}
