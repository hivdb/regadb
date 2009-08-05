package net.sf.regadb.util.mapper.matcher;

import java.util.Map;

import net.sf.regadb.util.mapper.XmlMapper.MapperParseException;

import org.jdom.Element;

public abstract class Matcher {
    private boolean inverse=false;

    public final boolean matches(Map<String,String> variables){
        return inverse ? !matchesCondition(variables) : matchesCondition(variables);
    }
    protected abstract boolean matchesCondition(Map<String,String> variables);
    
    public void parse(Element e) throws MapperParseException {
        setInverse("true".equals(e.getAttributeValue("inverse")));
        parseCondition(e);
    }
    protected abstract void parseCondition(Element e) throws MapperParseException;
    
    public boolean isInverse(){
        return inverse;
    }
    public void setInverse(boolean inverse){
        this.inverse = inverse;
    }
}
