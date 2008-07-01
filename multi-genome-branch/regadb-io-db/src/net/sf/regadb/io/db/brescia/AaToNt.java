package net.sf.regadb.io.db.brescia;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import net.sf.regadb.align.Mutation;

import org.biojava.bio.seq.DNATools;
import org.biojava.bio.seq.Sequence;
import org.biojava.bio.symbol.IllegalSymbolException;

public class AaToNt 
{
    public static String findCodon(String aminoAcid, Set<String> codons)
    {
        char [] aminoAcidCharArray = aminoAcid.toCharArray();
        Arrays.sort(aminoAcidCharArray);
        
        char [] aaTempCA;
        
        Sequence s = null;
        
        for(String c : codons)
        {
            try 
            {
                s = DNATools.createDNASequence(c, "test");
            } 
            catch (IllegalSymbolException e) 
            {
                e.printStackTrace();
            }
            Mutation m1 = new Mutation(0,0,s);
            aaTempCA = m1.aaToString().toCharArray();
            Arrays.sort(aaTempCA);
            if(new String(aaTempCA).equals(new String(aminoAcidCharArray)))
            {
                return c;
            }
        }
        
        return null;
    }
    
    public static Set<String> getMixedPopulationCodonTable()
    {
        return createTable("ACTGMRWSYKVHDBX");
    }
    
    public static Set<String> getSimpleCodonTable()
    {
        return createTable("ACTG");
    }
    
    private static Set<String> createTable(String nt) 
    {
        char[] chars = nt.toCharArray();
       
        Set<String> combinations = new HashSet<String>();
       
        for (char a : chars) 
        {
            for (char b : chars) 
            {
                for (char c : chars) 
                {                               
                    combinations.add("" + a + b + c);
                }
            }
        }
        return combinations;
    }
    
    public static void main(String [] args)
    {
        Set<String> nt  = getMixedPopulationCodonTable();
        String s = findCodon("NMK", nt);
        System.err.println(s);
        String s1 = findCodon("NMKI", nt);
        System.err.println(s1);
    }
}
