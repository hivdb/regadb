package net.sf.regadb.util.mapper.matcher;

import java.util.Map;


public class EqualsMatcher extends VariableMatcher{
    
    public boolean matchesCondition(Map<String,String> variables){
        return getString().equals(variables.get(getVariable()));
    }
}
