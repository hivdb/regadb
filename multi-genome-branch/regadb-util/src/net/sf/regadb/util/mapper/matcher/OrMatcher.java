package net.sf.regadb.util.mapper.matcher;


@SuppressWarnings("serial")
public class OrMatcher extends CompoundMatcher{

    public boolean matches(String s) {
        for(Matcher m : this)
            if(m.matches(s))
                return true;
        return false;
    }

}
