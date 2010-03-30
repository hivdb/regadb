package net.sf.regadb.util.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.regadb.util.mapper.matcher.Matcher.MatcherException;

public class DefaultMapper<T extends Mapping> implements Mapper<T>{
    private List<T> mappings = new ArrayList<T>();
    
    public void add(T mapping){
        mappings.add(mapping);
    }

    public T get(Map<String,String> variables) throws MatcherException{
        for(T mapping : mappings){
            if(mapping.matches(variables))
                return mapping;
        }
        return null;
    }
}
