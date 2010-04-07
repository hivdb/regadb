package net.sf.regadb.util.mapper;

import java.util.HashMap;
import java.util.Map;

import net.sf.regadb.util.mapper.XmlMapper.MapperParseException;
import net.sf.regadb.util.mapper.matcher.Matcher;
import net.sf.regadb.util.mapper.matcher.MatcherFactory;
import net.sf.regadb.util.mapper.matcher.Matcher.MatcherException;

import org.jdom.Element;

public abstract class Mapping {
    private Matcher matcher;
    
    final public void parseXml(Element e) throws MapperParseException{
        Element ee = e.getChild("match");
        if(ee != null)
            matcher = MatcherFactory.getInstance().createMatcher(ee);
        parseMapping(e);
    }
    
    protected abstract void parseMapping(Element e) throws MapperParseException;
    
    protected boolean matches(Map<String,String> variables) throws MatcherException{
        return matcher.matches(variables);
    }
    
    
    @SuppressWarnings("serial")
    public static class TKVMap<T,V> extends HashMap<String,V>{
        public T t;
        
        public TKVMap(T t){
            this.t = t;
        }
    }
}
