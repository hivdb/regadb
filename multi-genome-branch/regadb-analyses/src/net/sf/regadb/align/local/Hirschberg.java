package net.sf.regadb.align.local;

import org.biojava.bio.seq.DNATools;
import org.biojava.bio.seq.Sequence;
import org.biojava.bio.symbol.IllegalSymbolException;

public class Hirschberg {

    public static void main(String args[]){
        try {
            Sequence s1 = DNATools.createDNASequence(args[0], "s1");
            Sequence s2 = DNATools.createDNASequence(args[1], "s2");
            
            Hirschberg.align(s1, s2);
            
        } catch (IllegalSymbolException e) {
            e.printStackTrace();
        }
    }
    
    public static void align(Sequence s1, Sequence s2){
        int n = s1.length();
        int m = s2.length();
        
        for(int i = 0; i<n; ++i){
            
        }
    }
}
