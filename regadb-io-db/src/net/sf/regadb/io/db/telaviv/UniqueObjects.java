package net.sf.regadb.io.db.telaviv;

import java.util.HashMap;
import java.util.Map;

import net.sf.regadb.db.Patient;

public abstract class UniqueObjects<T> {

	Map<String,T> uniques = new HashMap<String,T>();
	
	public T exists(Patient p, T t){
    	String key = getHashKey(p,t);
    	T u = uniques.get(key);
    	if(u == null){
    		uniques.put(key,t);
    	}
    	return u;
	}
	
	public void remove(Patient p, T t){
		uniques.remove(getHashKey(p,t));
	}
	
	public void add(Patient p, T t){
		uniques.put(getHashKey(p,t),t);
	}
	
	protected String getHashKey(Patient p, T t){
		return p.getPatientId()+":"+ getHashKey(t);
	}
	
	protected abstract String getHashKey(T t);
}
