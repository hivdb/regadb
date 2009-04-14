package net.sf.regadb.util.mapper.matcher;

import net.sf.regadb.util.mapper.CaseInsensitiveEqualsMatcher;

import org.jdom.Element;

public class MatcherFactory {
    private static MatcherFactory factory = null;
    
    public static MatcherFactory getInstance(){
        if(factory == null)
            factory = new MatcherFactory();
        
        return factory;
    }
    
    public Matcher createMatcher(Element e){
        Matcher m = createMatcher(e.getAttributeValue("type"));
        m.parseXml(e);
        return m;
    }
    
    public Matcher createMatcher(String type){
        if(type.equals("equals"))
            return new EqualsMatcher();
        
        if(type.equals("iequals"))
            return new CaseInsensitiveEqualsMatcher();
            
        if(type.equals("or"))
            return new OrMatcher();
        
        if(type.equals("and"))
            return new AndMatcher();
        
        if(type.equals("not"))
            return new NotMatcher();
        
        return null;
    }
}
