package net.sf.regadb.util.mapper;

public interface Mapper<T extends Mapping> {
    
    public T get(String description);

}
