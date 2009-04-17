package net.sf.regadb.util.mapper.matcher;

import net.sf.regadb.util.mapper.XmlMapper.MapperParseException;

import org.jdom.Element;

public abstract class VariableMatcher extends Matcher{
    private String string;
    private String variable;
    
    public void parse(Element e) throws MapperParseException{
        super.parse(e);
        setVariable(e.getAttributeValue("var"));
    }
    
    public void parseCondition(Element e) {
        setString(e.getTextTrim());
    }
    
    public String getString(){
        return string;
    }
    public void setString(String string){
        this.string = string;
    }

    public String getVariable(){
        return variable;
    }
    public void setVariable(String variable){
        this.variable = variable;
    }
}
