package net.sf.regadb.util.mapper.matcher;

import java.util.Map;


public class CaseInsensitiveEqualsMatcher extends VariableMatcher{

    public void setString(String string){
        super.setString(string.toLowerCase());
    }
    
    public boolean matchesCondition(Map<String,String> variables) throws MatcherException{
        return getString().equals(getValue(variables).toLowerCase());
    }
}
