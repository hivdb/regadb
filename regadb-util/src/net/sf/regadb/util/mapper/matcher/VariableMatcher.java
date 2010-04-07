package net.sf.regadb.util.mapper.matcher;

import java.util.Map;

import net.sf.regadb.util.mapper.XmlMapper.MapperParseException;

import org.jdom.Element;

public abstract class VariableMatcher extends Matcher{
	@SuppressWarnings("serial")
	public static class VariableDoesNotExistException extends MatcherException{
		public VariableDoesNotExistException(String variable){
			super("Variable does not exist: '"+ variable +"'");
		}
	}
	
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
    
    protected String getValue(Map<String,String> variables) throws VariableDoesNotExistException{
    	if(!variables.containsKey(getVariable()))
    		throw new VariableDoesNotExistException(getVariable());
    	return variables.get(getVariable());
    }
}
