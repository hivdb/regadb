package net.sf.regadb.analysis.functions;

import java.util.Arrays;

import net.sf.regadb.db.AaInsertion;
import net.sf.regadb.db.AaMutation;
import net.sf.regadb.db.AaSequence;
import net.sf.regadb.db.compare.AaInsertionComparator;
import net.sf.regadb.db.compare.AaMutationComparator;

import org.biojava.bio.seq.DNATools;
import org.biojava.bio.seq.RNATools;
import org.biojava.bio.symbol.SymbolList;

public class AaSequenceHelper 
{
    public static AaMutation[] getSortedAaMutationArray(AaSequence aaseq)
    {
        AaMutation[] mutations = new AaMutation[aaseq.getAaMutations().size()];
        aaseq.getAaMutations().toArray(mutations);
        Arrays.sort(mutations, new AaMutationComparator());
        return mutations;
    }
    
    public static AaInsertion[] getSortedAaInsertionArray(AaSequence aaseq)
    {
        AaInsertion[] insertions = new AaInsertion[aaseq.getAaInsertions().size()];
        aaseq.getAaInsertions().toArray(insertions);
        Arrays.sort(insertions, new AaInsertionComparator());
        return insertions;
    }
    
    public static String getAminoAcid(String nt)
    {
        if(nt.charAt(0)==' ')
            return "   ";
        else if(nt.charAt(0)=='-')
            return " - ";
        
        try
        {
            SymbolList symL = DNATools.createDNA(nt.toString());
            symL = DNATools.toRNA(symL);
            symL = RNATools.translate(symL);
            
            return " " + symL.seqString().charAt(0) + " ";
        }
        catch(Exception e)
        {
            return " $ ";
        }
    }
}
