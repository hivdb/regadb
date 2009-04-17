package net.sf.regadb.util.mapper.matcher;

import java.util.Map;


public class ContainsMatcher extends VariableMatcher{
    
    public boolean matchesCondition(Map<String,String> variables) {
        return variables.get(getVariable()).contains(getString());
    }
}
