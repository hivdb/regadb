package net.sf.regadb.util.mapper;

import net.sf.regadb.util.mapper.XmlMapper.MapperParseException;

import org.jdom.Element;

public class AttributeMapping extends ValueMapperMapping{

    private String name;
    private String group;
    
    @Override
    protected void parseMapping(Element e) throws MapperParseException {
        super.parseMapping(e);
        
        setName(e.getAttributeValue("name"));
        setGroup(e.getAttributeValue("group"));
    }

    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }
    
    public String getGroup(){
        return group;
    }
    public void setGroup(String group){
        this.group = group;
    }
}
