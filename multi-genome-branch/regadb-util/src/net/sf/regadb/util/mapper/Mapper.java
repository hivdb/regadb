package net.sf.regadb.util.mapper;

import java.util.Map;

public interface Mapper<T extends Mapping> {
    
    public T get(Map<String,String> variables);

}
