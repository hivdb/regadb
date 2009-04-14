package net.sf.regadb.util.mapper;

import java.util.ArrayList;
import java.util.List;

public class DefaultMapper<T extends Mapping> implements Mapper<T>{
    private List<T> mappings = new ArrayList<T>();
    
    public void add(T mapping){
        mappings.add(mapping);
    }

    public T get(String description) {
        for(T mapping : mappings){
            if(mapping.matches(description))
                return mapping;
        }
        return null;
    }
}
