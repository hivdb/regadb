package net.sf.regadb.util.mapper.matcher;

@SuppressWarnings("serial")
public class AndMatcher extends CompoundMatcher{

    public boolean matches(String s) {
        for(Matcher m : this)
            if(!m.matches(s))
                return false;
        return true;
    }

}
