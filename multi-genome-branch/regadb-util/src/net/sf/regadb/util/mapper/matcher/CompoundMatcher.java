package net.sf.regadb.util.mapper.matcher;

import java.util.ArrayList;
import java.util.List;

import net.sf.regadb.util.mapper.XmlMapper.MapperParseException;

import org.jdom.Element;

public abstract class CompoundMatcher extends Matcher{
    private List<Matcher> matchers = new ArrayList<Matcher>();
    
    public void parseCondition(Element e) throws MapperParseException {
        for(Object o : e.getChildren()){
            Matcher m = MatcherFactory.getInstance().createMatcher((Element)o);
            matchers.add(m);
        }
    }

    public List<Matcher> getMatchers(){
        return matchers;
    }
}
