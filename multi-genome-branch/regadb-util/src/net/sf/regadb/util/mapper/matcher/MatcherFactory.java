package net.sf.regadb.util.mapper.matcher;


import net.sf.regadb.util.mapper.XmlMapper.MapperParseException;

import org.jdom.Element;

public class MatcherFactory {
    @SuppressWarnings("serial")
    public static class InvalidMatcherTypeException extends MapperParseException{
        public InvalidMatcherTypeException(String type){
            super(type);
        }
    }
    
    private static MatcherFactory factory = null;
    
    public static MatcherFactory getInstance(){
        if(factory == null)
            factory = new MatcherFactory();
        
        return factory;
    }
    
    public Matcher createMatcher(Element e) throws MapperParseException{
        Matcher m = createMatcher(e.getAttributeValue("type"));
        m.parse(e);
        return m;
    }
    
    public Matcher createMatcher(String type) throws InvalidMatcherTypeException{
        if(type.equals("equals"))
            return new EqualsMatcher();
        
        if(type.equals("iequals"))
            return new CaseInsensitiveEqualsMatcher();
            
        if(type.equals("or"))
            return new OrMatcher();
        
        if(type.equals("and"))
            return new AndMatcher();
        
        if(type.equals("number"))
            return new NumberMatcher();
        
        if(type.equals("contains"))
            return new ContainsMatcher();
        
        throw new InvalidMatcherTypeException("type: '"+ type +"'");
    }
}
