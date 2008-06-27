package net.sf.regadb.io.db.telaviv;

import java.util.HashMap;
import java.util.Map;

public abstract class UniqueObjects<K,V> {

	Map<String,V> uniques = new HashMap<String,V>();
	
	public V exists(K k, V v){
    	String key = getHashKey(k,v);
    	V u = uniques.get(key);
    	if(u == null){
    		uniques.put(key,v);
    	}
    	return u;
	}
	
	public void remove(K k, V v){
		uniques.remove(getHashKey(k,v));
	}
	
	public void add(K k, V v){
		uniques.put(getHashKey(k,v),v);
	}
	
	protected abstract String getHashKey(K k, V v);
}
