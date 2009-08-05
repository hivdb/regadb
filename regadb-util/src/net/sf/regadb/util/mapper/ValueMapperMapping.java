package net.sf.regadb.util.mapper;

import java.util.Map;

import net.sf.regadb.util.mapper.XmlMapper.MapperParseException;

import org.jdom.Element;

public class ValueMapperMapping extends Mapping implements Mapper<ValueMapping>{
    private DefaultMapper<ValueMapping> valueMapper = new DefaultMapper<ValueMapping>();
    
    private String constant;
    private String variable;

    @Override
    protected void parseMapping(Element e) throws MapperParseException {
        Element ee = e.getChild("values");
        if(ee != null){
            setConstant(ee.getAttributeValue("const"));
            setVariable(ee.getAttributeValue("var"));
            
            for(Object o : ee.getChildren()){
                Element eee = (Element)o;
                ValueMapping vm = new ValueMapping();
                vm.parseXml(eee);
                valueMapper.add(vm);
            }
        }
    }

    public ValueMapping get(Map<String, String> variables) {
        return valueMapper.get(variables);
    }
    
    public String getValue(Map<String, String> variables){
        ValueMapping vm = get(variables);
        
        if(vm != null)
            return vm.getValue(variables);
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
}
