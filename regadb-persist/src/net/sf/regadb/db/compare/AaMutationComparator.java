package net.sf.regadb.db.compare;

import java.util.Comparator;

import net.sf.regadb.db.AaMutation;

public class AaMutationComparator implements Comparator<AaMutation>
{
    public int compare(AaMutation o1, AaMutation o2) 
    {
        Short a = o1.getId().getMutationPosition();
        Short b = o2.getId().getMutationPosition();
        
        return a.compareTo(b);
    }
}
