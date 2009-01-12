package net.sf.regadb.analysis.functions;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

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
    
    public static String getWildtypeMutationList(AaSequence aaseq) {
        String result = "";
        
        for (AaMutInsertion aaMutation : AaMutInsertion.getSortedMutInsertionList(aaseq)) {
            if (aaMutation.isInsertion()) {
                result = addMutation(result, "(", aaMutation.getPosition(),
                       ")i" + aaMutation.getAaMutationString());
            } else if (!aaMutation.isSilent()) {
                result = addMutation(result, aaMutation.getAaReferenceString(),
                        aaMutation.getPosition(),
                        aaMutation.getAaMutationString());
            }
        }
        
        return result;
    }
    
    public static String getAaMutationDifferenceList(AaSequence s1, AaSequence s2) {
        String result = "";
        
        if (!s1.getProtein().getAbbreviation().equals(s2.getProtein().getAbbreviation()))
            return null;

        Set<AaMutInsertion> mutIns1 = AaMutInsertion.getSortedMutInsertionList(s1);
        Set<AaMutInsertion> mutIns2 = AaMutInsertion.getSortedMutInsertionList(s2);
        
        Set<Integer> posSet = new TreeSet<Integer>();
        addChangedPositions(mutIns1, posSet);
        addChangedPositions(mutIns2, posSet);

        for (Iterator<Integer> i = posSet.iterator(); i.hasNext();) {
            int position = i.next();
            
            /*
             * do not generate a difference when missing data
             */
            if (!regionIncludesPosition(s1, position)
                || !regionIncludesPosition(s2, position))
                continue;

            AaMutInsertion m1 = aaMutationAt(mutIns1, position, false);
            if(m1!=null && m1.getMutation().getAaMutation().equals(m1.getMutation().getAaReference()))
            	m1 = null;
            AaMutInsertion m2 = aaMutationAt(mutIns2, position, false);
            if(m2!=null && m2.getMutation().getAaMutation().equals(m2.getMutation().getAaReference()))
            	m2 = null;

            if ((m1 != null) || (m2 != null)) {
                if (m1 == null) {
                    result = addMutation(result,
                            m2.getAaReferenceString(), m2.getPosition(), m2.getAaMutationString());
                } else if (m2 == null) {
                    result = addMutation(result,
                            m1.getAaMutationString(), m1.getPosition(), m1.getAaReferenceString());
                } else if (!sort(m1.getAaMutationString()).equals(sort(m2.getAaMutationString()))) {
                    result = addMutation(result,
                            m1.getAaMutationString(), m1.getPosition(), m2.getAaReferenceString());
                }
            }

            AaMutInsertion i1 = aaMutationAt(mutIns1, position, true);
            AaMutInsertion i2 = aaMutationAt(mutIns2, position, true);
            
            if ((i1 != null) || (i2 != null)) {
                if (i1 == null)
                    result = addMutation(result, "+", position, "i");
                else if (i2 == null)
                    result = addMutation(result, "-", position, "i");
            }
        }
        
        return result;
    }
    
    private static void addChangedPositions(Set<AaMutInsertion> mutIns, Set<Integer> posSet) {
        for (AaMutInsertion m : mutIns) {
            if (m.isInsertion() || !m.isSilent())
                posSet.add(m.getPosition());
        }
    }
    
    private static boolean regionIncludesPosition(AaSequence seq, int position) {
        return (position >= seq.getFirstAaPos()) && (position <= seq.getLastAaPos());
    }
    
    private static AaMutInsertion aaMutationAt(Set<AaMutInsertion> mutIns, int position, boolean insertion) {
        for (AaMutInsertion m : mutIns) {
            if (m.getPosition() == position && (m.isInsertion() == insertion))
                return m;
            if (m.getPosition() > position)
                return null;
        }

        return null;
    }
    
    private static String sort(String line){
        if(line.length() == 1){
            return line;
        }
        else{
            char[] temp = line.toCharArray();

            Arrays.sort(temp);
            
            String ret = "";
            for(int i = 0; i < temp.length; i++){
                ret += temp[i];
            }

            return ret;
        }
    }
 }
