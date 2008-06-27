package net.sf.regadb.db.compare;

import java.util.Comparator;

import net.sf.regadb.db.AaInsertion;

public class AaInsertionComparator implements Comparator<AaInsertion>
{

    public int compare(AaInsertion o1, AaInsertion o2) 
    {
        Short pos1 = o1.getId().getInsertionPosition();
        Short pos2 = o2.getId().getInsertionPosition();
        
        if(pos1.equals(pos2))
        {
            Short ord1 = o1.getId().getInsertionOrder();
            Short ord2 = o2.getId().getInsertionOrder();
            
            return ord1.compareTo(ord2);
        }
        else
        {
            return pos1.compareTo(pos2);
        }
    }

}
