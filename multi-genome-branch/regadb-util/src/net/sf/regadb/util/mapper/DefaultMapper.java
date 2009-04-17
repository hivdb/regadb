package net.sf.regadb.util.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DefaultMapper<T extends Mapping> implements Mapper<T>{
    private List<T> mappings = new ArrayList<T>();
    
    public void add(T mapping){
        mappings.add(mapping);
    }

    public T get(Map<String,String> variables) {
        for(T mapping : mappings){
            if(mapping.matches(variables))
                return mapping;
        }
        return null;
    }
}
