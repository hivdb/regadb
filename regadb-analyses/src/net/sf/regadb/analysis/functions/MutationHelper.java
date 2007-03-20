package net.sf.regadb.analysis.functions;

import net.sf.regadb.db.AaMutInsertion;
import net.sf.regadb.db.AaSequence;

public class MutationHelper 
{
    public static String getSynonymousMutations(AaSequence aaseq) 
    {
        String temp = "";
        for (AaMutInsertion mut : AaMutInsertion.getSortedMutInsertionList(aaseq)) 
        {
            if (mut.isSilent()) 
            {
                temp = addMutation(temp, mut.filterCodon(mut.getMutation().getNtReferenceCodon()),
                        mut.getPosition(), mut.filterCodon(mut.getMutation().getNtMutationCodon()));
            }
        }

        return temp;
    }

    public static String getNonSynonymousMutations(AaSequence aaseq) 
    { 
        String result = "";
        
        for (AaMutInsertion aaMutation : AaMutInsertion.getSortedMutInsertionList(aaseq)) 
        {
            if (aaMutation.isInsertion()) 
            {
                result = addMutation(result, "(", aaMutation.getPosition(),
                       ")i" + aaMutation.getAaMutationString());
            } 
            else if (!aaMutation.isSilent()) 
            {
                result = addMutation(result, aaMutation.getAaReferenceString(),
                        aaMutation.getPosition(),
                        aaMutation.getAaMutationString());
            }
        }
        
        return result;
    }
    
    private static String addMutation(String result, String from, Integer pos, String to) 
    {
        if (result.length() > 0)
            result += " ";
        result += from + pos + to;
        return result;
    }
 }
