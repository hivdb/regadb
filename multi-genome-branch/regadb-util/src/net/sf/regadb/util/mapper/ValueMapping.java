package net.sf.regadb.util.mapper;

import java.util.Map;

import org.jdom.Element;

public class ValueMapping extends Mapping{
    private String constant;
    private String variable;
    
    @Override
    protected void parseMapping(Element e) {
        setConstant(e.getAttributeValue("const"));
        setVariable(e.getAttributeValue("var"));
    }
    
    public String getValue(Map<String,String> variables){
        if(getConstant() != null)
            return getConstant();
        if(getVariable() != null)
            return variables.get(getVariable());
        return null;
    }

    public String getConstant(){
        return constant;
    }
    public void setConstant(String constant){
        this.constant = constant;
    }
    
    public String getVariable(){
        return variable;
    }
    public void setVariable(String variable){
        this.variable = variable;
    }
    
    public String toString(){
        if(getConstant() != null)
            return getConstant();
        if(getVariable() != null)
            return getVariable();
        return "";
    }
}
