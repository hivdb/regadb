package net.sf.regadb.util.mapper.matcher;

import java.util.Map;


public class OrMatcher extends CompoundMatcher{

    public boolean matchesCondition(Map<String,String> variables) throws MatcherException{
        for(Matcher m : getMatchers())
            if(m.matches(variables))
                return true;
        return false;
    }

}
