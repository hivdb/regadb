package net.sf.regadb.util.mapper.matcher;

import java.util.ArrayList;

import org.jdom.Element;

@SuppressWarnings("serial")
public abstract class CompoundMatcher extends ArrayList<Matcher> implements Matcher{
    
    public void parseXml(Element e) {
        for(Object o : e.getChildren()){
            Matcher m = MatcherFactory.getInstance().createMatcher((Element)o);
            add(m);
        }
    }

}
