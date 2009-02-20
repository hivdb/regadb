package net.sf.regadb.align.local;

import java.util.ArrayList;

public class SymbolSequence {
    private ArrayList<Symbol> seq;
    
    public SymbolSequence(){
        seq = new ArrayList<Symbol>();
    }
    public Symbol get(int index){
        return seq.get(index);
    }
    public void add(Symbol s){
        seq.add(s);
    }
    public int length(){
        return seq.size();
    }
}
