package net.sf.regadb.util.mapper.matcher;


public class CaseInsensitiveEqualsMatcher extends EqualsMatcher{

    public void setReference(String reference){
        super.setReference(reference.toLowerCase());
    }
    
    public boolean matches(String s){
        return super.matches(s.toLowerCase());
    }
}
