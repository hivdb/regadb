/*
 * Created on Jan 10, 2007
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package net.sf.regadb.align;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.biojava.bio.BioException;
import org.biojava.bio.seq.DNATools;
import org.biojava.bio.seq.ProteinTools;
import org.biojava.bio.seq.RNATools;
import org.biojava.bio.seq.io.SymbolTokenization;
import org.biojava.bio.symbol.Alphabet;
import org.biojava.bio.symbol.FiniteAlphabet;
import org.biojava.bio.symbol.IllegalAlphabetException;
import org.biojava.bio.symbol.IllegalSymbolException;
import org.biojava.bio.symbol.SimpleSymbolList;
import org.biojava.bio.symbol.Symbol;
import org.biojava.bio.symbol.SymbolList;
import org.biojava.utils.ChangeVetoException;

public class Mutation {
    private int aaPos;
    private int insIndex;

    private SymbolList refCodon;
    private SymbolList targetCodon;

    private Symbol      refAminoAcid;
    private Set<Symbol> targetAminoAcids;

    public int getInsIndex() {
        return insIndex;
    }
    public int getAaPos() {
        return aaPos;
    }
    public Symbol getRefAminoAcid() {
        return refAminoAcid;
    }
    public SymbolList getRefCodon() {
        return refCodon;
    }
    public Set<Symbol> getTargetAminoAcids() {
        return targetAminoAcids;
    }
    public SymbolList getTargetCodon() {
        return targetCodon;
    }
    
    public Mutation(int aaPos, SymbolList refCodon, SymbolList targetCodon) {
        try {
            this.aaPos = aaPos;
            this.insIndex = -1;
            this.refCodon = refCodon;
            this.refAminoAcid = RNATools.translate(DNATools.toRNA(refCodon)).symbolAt(1);
            this.targetCodon = targetCodon;
            this.targetAminoAcids = translateCodon(targetCodon);
        } catch (IndexOutOfBoundsException e) {
            throw new RuntimeException(e);
        } catch (IllegalAlphabetException e) {
            throw new RuntimeException(e);
        }
    }

    public Mutation(int aaPos, int insIndex, SymbolList targetCodon) {
        this.aaPos = aaPos;
        this.insIndex = insIndex;
        this.refCodon = null;
        this.refAminoAcid = null;
        this.targetCodon = targetCodon;
        this.targetAminoAcids = translateCodon(targetCodon);
    }

    @Override
    public String toString() {
        try {
            SymbolTokenization aatok = ProteinTools.getAlphabet().getTokenization("token");
            SymbolTokenization nttok = DNATools.getDNA().getTokenization("token");
            
            String result;
            if (insIndex != -1)
                result = String.valueOf(aaPos) + "ins" + (insIndex + 1) + asString(targetAminoAcids);
            else
                result = aatok.tokenizeSymbol(refAminoAcid) + String.valueOf(aaPos) + asString(targetAminoAcids);

            result += " (";
            if (refCodon != null)
                result += nttok.tokenizeSymbolList(refCodon) + "->";
            result += nttok.tokenizeSymbolList(targetCodon) + ")";
            
            return result;
        } catch (IllegalSymbolException e) {
            throw new RuntimeException(e);
        } catch (IllegalAlphabetException e) {
            throw new RuntimeException(e);
        } catch (BioException e) {
            throw new RuntimeException(e);
        }
    }
    
    public String aaToString()
    {
        return asString(targetAminoAcids);
    }
    
    private static String asString(Set<Symbol> targetAminoAcids) {
        try {
            SymbolTokenization aatok = ProteinTools.getTAlphabet().getTokenization("token");
            
            String result = new String();

            for (Symbol s:targetAminoAcids) {
                result += aatok.tokenizeSymbol(s);
            }
            
            return result;
        } catch (IllegalSymbolException e) {
            throw new RuntimeException(e);
        } catch (BioException e) {
            throw new RuntimeException(e);
        }
    }

    
    private static Set<Symbol> translateCodon(SymbolList codon) {
        try {
            HashSet<Symbol> result = new HashSet<Symbol>();
            
            ArrayList<SymbolList> nonAmbiguous = listAmbiguity(codon);

            for (SymbolList c:nonAmbiguous) {
                SymbolList aa = RNATools.translate(DNATools.toRNA(c));

                result.add(aa.symbolAt(1));
            }
            
            return result;
       } catch (IllegalAlphabetException e) {
            throw new RuntimeException(e);
        } catch (IndexOutOfBoundsException e) {
            throw new RuntimeException(e);
        } catch (IllegalSymbolException e) {
            throw new RuntimeException(e);
        } catch (ChangeVetoException e) {
            throw new RuntimeException(e);
        }
    }

    private static ArrayList<SymbolList> listAmbiguity(SymbolList seq)
        throws IllegalSymbolException, ChangeVetoException {
        
        ArrayList<SymbolList> result = new ArrayList<SymbolList>();
        FiniteAlphabet[] alfabets = new FiniteAlphabet[seq.length()];

        for (int i = 1; i <= seq.length(); ++i) {
            FiniteAlphabet a = (FiniteAlphabet) seq.symbolAt(i).getMatches();
            if (a.contains(RNATools.u()))
                a.removeSymbol(RNATools.u());

            alfabets[i - 1] = a;
        }

        Symbol[] generated = new Symbol[seq.length()];
        generateAllSequences(alfabets, 0, generated, result, seq.getAlphabet());
        
        return result;
    }

    private static void generateAllSequences(FiniteAlphabet[] alfabets, int i,
                                             Symbol[] generated, ArrayList<SymbolList> result,
                                             Alphabet alfa) {
        Iterator it = alfabets[i].iterator();
        
        while (it.hasNext()) {
            generated[i] = (Symbol) it.next();

            if (i == alfabets.length - 1) {
                Symbol[] copyGenerated = new Symbol[generated.length];
                for (int j = 0; j < copyGenerated.length; ++j)
                    copyGenerated[j] = generated[j];
                SymbolList list = new SimpleSymbolList(copyGenerated, alfabets.length, alfa);

                result.add(list);
            } else {
                generateAllSequences(alfabets, i + 1, generated, result, alfa);
            }
        }
    }
}
