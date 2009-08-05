package net.sf.regadb.align.local;

public class Symbol {
    private int i;
    private String s;
    
    public Symbol(String s){
        this.s = s;
    }
    public Symbol(int i, String s){
        this.i = i;
        this.s = s;
    }
    
    public void setInt(int i){
        this.i = i;
    }    
    public int toInt(){
        return i;
    }
    
    public void setString(String s){
        this.s = s;
    }
    public String toString(){
        return s;
    }
}
