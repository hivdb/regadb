package net.sf.regadb.db.compare;

import java.util.Comparator;

import net.sf.regadb.db.DrugGeneric;

public class DrugGenericComparator implements Comparator<DrugGeneric>
{
    public int compare(DrugGeneric o1, DrugGeneric o2) 
    {
        return o1.getGenericName().compareTo(o2.getGenericName());
    }
}
