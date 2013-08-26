package net.sf.regadb.db.tools;

import java.util.Arrays;

import net.sf.regadb.db.AaInsertion;
import net.sf.regadb.db.AaMutation;
import net.sf.regadb.db.AaSequence;
import net.sf.regadb.db.compare.AaInsertionComparator;
import net.sf.regadb.db.compare.AaMutationComparator;

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
    
    private static char codonTable[][][] = {
    		  { { 'K' /* AAA */,
    		      'N' /* AAC */,
    		      'K' /* AAG */,
    		      'N' /* AAT */
    		    },
    		    { 'T' /* ACA */,
    		      'T' /* ACC */,
    		      'T' /* ACG */,
    		      'T' /* ACT */
    		    },
    		    { 'R' /* AGA */,
    		      'S' /* AGC */,
    		      'R' /* AGG */,
    		      'S' /* AGT */
    		    },
    		    { 'I' /* ATA */,
    		      'I' /* ATC */,
    		      'M' /* ATG */,
    		      'I' /* ATT */
    		    }
    		  },
    		  { { 'Q' /* CAA */,
    		      'H' /* CAC */,
    		      'Q' /* CAG */,
    		      'H' /* CAT */
    		    },
    		    { 'P' /* CCA */,
    		      'P' /* CCC */,
    		      'P' /* CCG */,
    		      'P' /* CCT */
    		    },
    		    { 'R' /* CGA */,
    		      'R' /* CGC */,
    		      'R' /* CGG */,
    		      'R' /* CGT */
    		    },
    		    { 'L' /* CTA */,
    		      'L' /* CTC */,
    		      'L' /* CTG */,
    		      'L' /* CTT */
    		    }
    		  },
    		  { { 'E' /* GAA */,
    		      'D' /* GAC */,
    		      'E' /* GAG */,
    		      'D' /* GAT */
    		    },
    		    { 'A' /* GCA */,
    		      'A' /* GCC */,
    		      'A' /* GCG */,
    		      'A' /* GCT */
    		    },
    		    { 'G' /* GGA */,
    		      'G' /* GGC */,
    		      'G' /* GGG */,
    		      'G' /* GGT */
    		    },
    		    { 'V' /* GTA */,
    		      'V' /* GTC */,
    		      'V' /* GTG */,
    		      'V' /* GTT */
    		    }
    		  },
    		  { { '*' /* TAA */,
    		      'Y' /* TAC */,
    		      '*' /* TAG */,
    		      'Y' /* TAT */
    		    },
    		    { 'S' /* TCA */,
    		      'S' /* TCC */,
    		      'S' /* TCG */,
    		      'S' /* TCT */
    		    },
    		    { '*' /* TGA */,
    		      'C' /* TGC */,
    		      'W' /* TGG */,
    		      'C' /* TGT */
    		    },
    		    { 'L' /* TTA */,
    		      'F' /* TTC */,
    		      'L' /* TTG */,
    		      'F' /* TTT */
    		    }
    		  }};
    
    private static boolean ambiguous(char c) {
    	c = Character.toLowerCase(c);
    	
    	return c != 'a' && c != 'c' && c != 't' && c != 'g';
    }
    
    private static int ntIndex(char c) {
    	c = Character.toLowerCase(c);
    	
    	if (c == 'a') 
    		return 0;
    	else if (c == 'c') 
    		return 1;
    	else if (c == 'g') 
    		return 2;
    	else if (c == 't') 
    		return 3;
    	else 
    		return -1;
    }
    
    public static char getAminoAcid(String nt)
    {
        if(nt.charAt(0)==' ')
            return ' ';
        else if(nt.charAt(0)=='-')
            return '-';
        
        if (ambiguous(nt.charAt(0)) 
        		|| ambiguous(nt.charAt(1)) 
        		|| ambiguous(nt.charAt(2)))
        		return 'X';

        return codonTable[ntIndex(nt.charAt(0))][ntIndex(nt.charAt(1))][ntIndex(nt.charAt(2))];
    }
}
