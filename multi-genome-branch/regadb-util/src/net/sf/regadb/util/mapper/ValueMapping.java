package net.sf.regadb.util.mapper;

import org.jdom.Element;

public class ValueMapping extends Mapping{
    private String value;
    
    @Override
    protected void parseMapping(Element e) {
        setValue(e.getAttributeValue("value"));
    }
    
    public String getValue(){
        return value;
    }
    public void setValue(String value){
        this.value = value;
    }
    
    public String toString(){
        return value;
    }
}
