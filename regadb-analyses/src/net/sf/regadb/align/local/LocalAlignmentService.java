/*
 * Created on Jan 5, 2007
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package net.sf.regadb.align.local;

import java.util.NoSuchElementException;

import net.sf.regadb.align.AlignmentResult;
import net.sf.regadb.align.AlignmentService;
import net.sf.regadb.align.Mutation;

import org.biojava.bio.BioException;
import org.biojava.bio.seq.Sequence;
import org.biojava.bio.seq.impl.SimpleSequence;
import org.biojava.bio.symbol.Alignment;
import org.biojava.bio.symbol.Alphabet;
import org.biojava.bio.symbol.Edit;
import org.biojava.bio.symbol.IllegalSymbolException;
import org.biojava.bio.symbol.SimpleSymbolList;
import org.biojava.bio.symbol.Symbol;
import org.biojava.bio.symbol.SymbolList;
import org.biojava.utils.ChangeVetoException;

public class LocalAlignmentService implements AlignmentService {
    
    private CodonAlign codonAligner;

    public LocalAlignmentService() {
        this.codonAligner = new CodonAlign();
    }

    private static Sequence removeGaps(Sequence seq) {
        SymbolList syms = new SimpleSymbolList(seq);
        Alphabet alfa = syms.getAlphabet();
        try {
            for (int i = 1; i <= syms.length(); ++i) {
                Symbol s = syms.symbolAt(i);
                   
                if (s == alfa.getGapSymbol()) {
                    syms.edit(new Edit(i, 1, SymbolList.EMPTY_LIST));
                }
            }
            
        } catch (ChangeVetoException e) {
            throw new RuntimeException(e);
        } catch (BioException e) {
            throw new RuntimeException(e);
        }

        return new SimpleSequence(syms, seq.getURN(), seq.getName(), seq.getAnnotation());
     }
    
    public AlignmentResult alignTo(Sequence target, Sequence ref)
    {
        try {
            
            target = removeGaps(target);
            
            Alignment alignment = codonAligner.compute(ref, target, 5);
            SymbolList alignedRef = alignment.symbolListForLabel("ref");
            SymbolList alignedTarget = alignment.symbolListForLabel("target");

            AlignmentResult result = new AlignmentResult();

            int ins = -1;

            int firstNt = firstNonGap(alignedTarget);
            int lastNt = lastNonGap(alignedTarget);
            result.setFirstAa((firstNt - 1) / 3 + 1); // stupid !! BioJava idiots !!
            result.setLastAa((lastNt - 1) / 3 + 1);   // stupid !! BioJava idiots !!
            
            int firstRefAa = result.getFirstAa();
            int lastRefAa = result.getLastAa();
            if((firstNt-1) % 3 != 0)
            	++firstRefAa;
            if((lastNt-1) % 3 != 0)
            	--lastRefAa;
            

            int refAaPos = result.getFirstAa() - 1;

            for (int i = result.getFirstAa(); i <= result.getLastAa(); ++i) {
                int codonStart = (i - 1) * 3 + 1;
                int codonEnd = i * 3;
                SymbolList refCodon = alignedRef.subList(codonStart, codonEnd);
                SymbolList targetCodon = alignedTarget.subList(codonStart, codonEnd);

                if (refCodon.seqString().equals("---")){
                    ++ins;
                    --lastRefAa;
                }
                else {
                    ins = -1;
                    ++refAaPos;
                }

                if (!refCodon.equals(targetCodon)) {
                    if (ins >= 0)
                        result.addMutation(new Mutation(refAaPos, ins, targetCodon));
                    else {
                        result.addMutation(new Mutation(refAaPos, refCodon, targetCodon));
                    }
                }
            }
            
            result.setFirstRefAa(firstRefAa);
            result.setLastRefAa(lastRefAa);
            
            return result;
        } catch (NoSuchElementException e) {
            throw new RuntimeException(e);
        } catch (AlignmentException e) {
            return null;
        } catch (IllegalSymbolException e) {
            throw new RuntimeException(e);
        }        
    }

    private int lastNonGap(SymbolList alignedTarget) {
        for (int j = alignedTarget.length(); j >= 1; --j) {
            if (alignedTarget.symbolAt(j) != alignedTarget.getAlphabet().getGapSymbol())
                return j;
        }
        return -1;
    }
    
    private int firstNonGap(SymbolList alignedTarget) {
        for (int j = 1; j <= alignedTarget.length(); ++j) {
            if (alignedTarget.symbolAt(j) != alignedTarget.getAlphabet().getGapSymbol())
                return j;
        }
        return alignedTarget.length() + 1;
    }
}
