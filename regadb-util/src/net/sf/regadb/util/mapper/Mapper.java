package net.sf.regadb.util.mapper;

import java.util.Map;

import net.sf.regadb.util.mapper.matcher.Matcher.MatcherException;

public interface Mapper<T extends Mapping> {
    
    public T get(Map<String,String> variables) throws MatcherException;

}
