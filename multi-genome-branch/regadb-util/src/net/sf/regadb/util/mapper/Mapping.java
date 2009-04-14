package net.sf.regadb.util.mapper;

import java.util.HashMap;

import net.sf.regadb.util.mapper.matcher.Matcher;
import net.sf.regadb.util.mapper.matcher.MatcherFactory;

import org.jdom.Element;

public abstract class Mapping {
    private Matcher matcher;
    
    final public void parseXml(Element e){
        matcher = MatcherFactory.getInstance().createMatcher(e.getChild("match"));
        parseMapping(e);
    }
    
    protected abstract void parseMapping(Element e);
    
    protected boolean matches(String s){
        return matcher.matches(s);
    }
    
    
    @SuppressWarnings("serial")
    public static class TKVMap<T,V> extends HashMap<String,V>{
        public T t;
        
        public TKVMap(T t){
            this.t = t;
        }
    }
}
