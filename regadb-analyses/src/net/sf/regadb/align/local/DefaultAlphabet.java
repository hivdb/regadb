package net.sf.regadb.align.local;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class DefaultAlphabet<S extends Symbol> implements IAlphabet<S>{
    private ArrayList<S> intrep = new ArrayList<S>();
    private HashMap<String, S> strrep = new HashMap<String, S>();
    
    public void addSymbol(S s){
        s.setInt(intrep.size());
        
        intrep.add(s);
        strrep.put(s.toString(),s);
    }
    
    public S get(int i){
        return intrep.get(i);
    }
    public S get(String s){
        return strrep.get(s);
    }
    
    public int size(){
        return intrep.size();
    }    
}