package net.sf.regadb.db;

import java.util.SortedSet;
import java.util.TreeSet;

public class AaMutInsertion implements Comparable<AaMutInsertion>
{
    private AaMutation mutation_;
    private AaInsertion insertion_;
    
    private AaMutInsertion()
    {
        
    }
    
    public AaMutInsertion(AaMutation mutation)
    {
        mutation_ = mutation;
    }
    
    public AaMutInsertion(AaInsertion insertion)
    {
        insertion_ = insertion;
    }
    
    public int getPosition()
    {
        if(mutation_==null)
            return insertion_.getId().getPosition();
        else
            return mutation_.getId().getPosition();
    }
    
    public String getAaMutationString()
    {
        if(mutation_==null)
            return insertion_.getAaInsertion();
        else
            return mutation_.getAaMutation();
    }

    public int compareTo(AaMutInsertion a) 
    {
        int thisVal = this.getPosition();
        int anotherVal = a.getPosition();
        return (thisVal<anotherVal ? -1 : (thisVal==anotherVal ? 0 : 1));
    }
    
    public static SortedSet<AaMutInsertion> getMutInsertionList(AaSequence aaseq)
    {
        SortedSet<AaMutInsertion> set = new TreeSet<AaMutInsertion>();
        
        for(AaMutation mutation : aaseq.getAaMutations())
        {
            set.add(new AaMutInsertion(mutation));
        }
        
        for(AaInsertion insertion : aaseq.getAaInsertions())
        {
            set.add(new AaMutInsertion(insertion));
        }
        
        return set;
    }
    
    public boolean isInsertion() 
    {
        return insertion_!=null;
    }

    public boolean isSilent() 
    {
        if(mutation_!=null)
            return mutation_.getAaReference().equals(mutation_.getAaMutation());
        else
            return false;
    }
}
