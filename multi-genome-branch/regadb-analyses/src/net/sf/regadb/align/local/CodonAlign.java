/*
 * Created on Jan 5, 2007
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package net.sf.regadb.align.local;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import org.biojava.bio.BioException;
import org.biojava.bio.BioRuntimeException;
import org.biojava.bio.alignment.SubstitutionMatrix;
import org.biojava.bio.seq.DNATools;
import org.biojava.bio.seq.ProteinTools;
import org.biojava.bio.seq.RNATools;
import org.biojava.bio.seq.Sequence;
import org.biojava.bio.seq.SequenceTools;
import org.biojava.bio.seq.impl.SimpleSequence;
import org.biojava.bio.symbol.Alignment;
import org.biojava.bio.symbol.Edit;
import org.biojava.bio.symbol.IllegalAlphabetException;
import org.biojava.bio.symbol.IllegalSymbolException;
import org.biojava.bio.symbol.SimpleAlignment;
import org.biojava.bio.symbol.SimpleSymbolList;
import org.biojava.bio.symbol.SymbolList;
import org.biojava.utils.ChangeVetoException;

public class CodonAlign {

    static private String blosum30 = "   A  R  N  D  C  Q  E  G  H  I  L  K  M  F  P  S  T  W  Y  V  B  Z  X  *\n"
            + "A  4 -1  0  0 -3  1  0  0 -2  0 -1  0  1 -2 -1  1  1 -5 -4  1  0  0  0 -7\n"
            + "R -1  8 -2 -1 -2  3 -1 -2 -1 -3 -2  1  0 -1 -1 -1 -3  0  0 -1 -2  0 -1 -7\n"
            + "N  0 -2  8  1 -1 -1 -1  0 -1  0 -2  0  0 -1 -3  0  1 -7 -4 -2  4 -1  0 -7\n"
            + "D  0 -1  1  9 -3 -1  1 -1 -2 -4 -1  0 -3 -5 -1  0 -1 -4 -1 -2  5  0 -1 -7\n"
            + "C -3 -2 -1 -3 17 -2  1 -4 -5 -2  0 -3 -2 -3 -3 -2 -2 -2 -6 -2 -2  0 -2 -7\n"
            + "Q  1  3 -1 -1 -2  8  2 -2  0 -2 -2  0 -1 -3  0 -1  0 -1 -1 -3 -1  4  0 -7\n"
            + "E  0 -1 -1  1  1  2  6 -2  0 -3 -1  2 -1 -4  1  0 -2 -1 -2 -3  0  5 -1 -7\n"
            + "G  0 -2  0 -1 -4 -2 -2  8 -3 -1 -2 -1 -2 -3 -1  0 -2  1 -3 -3  0 -2 -1 -7\n"
            + "H -2 -1 -1 -2 -5  0  0 -3 14 -2 -1 -2  2 -3  1 -1 -2 -5  0 -3 -2  0 -1 -7\n"
            + "I  0 -3  0 -4 -2 -2 -3 -1 -2  6  2 -2  1  0 -3 -1  0 -3 -1  4 -2 -3  0 -7\n"
            + "L -1 -2 -2 -1  0 -2 -1 -2 -1  2  4 -2  2  2 -3 -2  0 -2  3  1 -1 -1  0 -7\n"
            + "K  0  1  0  0 -3  0  2 -1 -2 -2 -2  4  2 -1  1  0 -1 -2 -1 -2  0  1  0 -7\n"
            + "M  1  0  0 -3 -2 -1 -1 -2  2  1  2  2  6 -2 -4 -2  0 -3 -1  0 -2 -1  0 -7\n"
            + "F -2 -1 -1 -5 -3 -3 -4 -3 -3  0  2 -1 -2 10 -4 -1 -2  1  3  1 -3 -4 -1 -7\n"
            + "P -1 -1 -3 -1 -3  0  1 -1  1 -3 -3  1 -4 -4 11 -1  0 -3 -2 -4 -2  0 -1 -7\n"
            + "S  1 -1  0  0 -2 -1  0  0 -1 -1 -2  0 -2 -1 -1  4  2 -3 -2 -1  0 -1  0 -7\n"
            + "T  1 -3  1 -1 -2  0 -2 -2 -2  0  0 -1  0 -2  0  2  5 -5 -1  1  0 -1  0 -7\n"
            + "W -5  0 -7 -4 -2 -1 -1  1 -5 -3 -2 -2 -3  1 -3 -3 -5 20  5 -3 -5 -1 -2 -7\n"
            + "Y -4  0 -4 -1 -6 -1 -2 -3  0 -1  3 -1 -1  3 -2 -2 -1  5  9  1 -3 -2 -1 -7\n"
            + "V  1 -1 -2 -2 -2 -3 -3 -3 -3  4  1 -2  0  1 -4 -1  1 -3  1  5 -2 -3  0 -7\n"
            + "B  0 -2  4  5 -2 -1  0  0 -2 -2 -1  0 -2 -3 -2  0  0 -5 -3 -2  5  0 -1 -7\n"
            + "Z  0  0 -1  0  0  4  5 -2  0 -3 -1  1 -1 -4  0 -1 -1 -1 -2 -3  0  4  0 -7\n"
            + "X  0 -1  0 -1 -2  0 -1 -1 -1  0  0  0  0 -1 -1  0  0 -2 -1  0 -1  0 -1 -7\n"
            + "* -7 -7 -7 -7 -7 -7 -7 -7 -7 -7 -7 -7 -7 -7 -7 -7 -7 -7 -7 -7 -7 -7 -7  1\n";

    static private String nuc4_4 = "    A   T   G   C   S   W   R   Y   K   M   B   V   H   D   N\n"
            + "A   5  -4  -4  -4  -4   1   1  -4  -4   1  -4  -1  -1  -1  -2\n"
            + "T  -4   5  -4  -4  -4   1  -4   1   1  -4  -1  -4  -1  -1  -2\n"
            + "G  -4  -4   5  -4   1  -4   1  -4   1  -4  -1  -1  -4  -1  -2\n"
            + "C  -4  -4  -4   5   1  -4  -4   1  -4   1  -1  -1  -1  -4  -2\n"
            + "S  -4  -4   1   1  -1  -4  -2  -2  -2  -2  -1  -1  -3  -3  -1\n"
            + "W   1   1  -4  -4  -4  -1  -2  -2  -2  -2  -3  -3  -1  -1  -1\n"
            + "R   1  -4   1  -4  -2  -2  -1  -4  -2  -2  -3  -1  -3  -1  -1\n"
            + "Y  -4   1  -4   1  -2  -2  -4  -1  -2  -2  -1  -3  -1  -3  -1\n"
            + "K  -4   1   1  -4  -2  -2  -2  -2  -1  -4  -1  -3  -3  -1  -1\n"
            + "M   1  -4  -4   1  -2  -2  -2  -2  -4  -1  -3  -1  -1  -3  -1\n"
            + "B  -4  -1  -1  -1  -1  -3  -3  -1  -1  -3  -1  -2  -2  -2  -1\n"
            + "V  -1  -4  -1  -1  -1  -3  -1  -3  -3  -1  -2  -1  -2  -2  -1\n"
            + "H  -1  -1  -4  -1  -3  -1  -3  -1  -3  -1  -2  -2  -1  -2  -1\n"
            + "D  -1  -1  -1  -4  -3  -1  -1  -3  -1  -3  -2  -2  -2  -1  -1\n"
            + "N  -2  -2  -2  -2  -1  -1  -1  -1  -1  -1  -1  -1  -1  -1  -1\n";

    static private SubstitutionMatrix nuc4_4matrix = null;
    static private SubstitutionMatrix blosum30matrix = null;
    private int minNtScore;
        
    private NeedlemanWunsch aaNeedleman;

    private NeedlemanWunsch ntNeedleman;

    public CodonAlign() {
        this.aaNeedleman = new NeedlemanWunsch(-10, -3.3, blosum30matrix);
        this.ntNeedleman = new NeedlemanWunsch(-10, -3.3, nuc4_4matrix);
        this.minNtScore = 200;
    }

    public Alignment compute(Sequence ref, Sequence target, int maxFrameShifts)
            throws AlignmentException, IllegalSymbolException {
        try {
            ScoredAlignment ntAlignment = ntNeedleman.pairwiseAlignment(ref, target);

            if (ntAlignment.getScore() < minNtScore)
                throw new AlignmentException("Nucleotide alignment score below threshold");

            Sequence refAA = SequenceTools.createSequence(RNATools
                    .translate(DNATools.toRNA(ref)), "refAA", "refAA", null);

            int bestFrameShift = -1;
            ScoredAlignment bestAlignment = null;

            for (int i = 0; i < 3; ++i) {
                int last = i + ((target.length() - i) / 3) * 3;

                Sequence targetAA = SequenceTools.createSequence(RNATools
                        .translate(DNATools.toRNA(target.subList(i + 1, last))),
                        "targetAA", "targetAA", null);

                ScoredAlignment alignment = aaNeedleman.pairwiseAlignment(refAA, targetAA);

                if (bestAlignment == null || alignment.getScore() > bestAlignment.getScore()) {
                    bestFrameShift = i;
                    bestAlignment = alignment;
                }
            }

            Alignment aaAlignment = bestAlignment.getAlignment();
            
            ScoredAlignment ntCodonAlignment = alignLikeAA(ref, target, bestFrameShift,
                    aaAlignment.symbolListForLabel("refAA"),
                    aaAlignment.symbolListForLabel("targetAA"));

            System.err.println("Scores: " + ntAlignment.getScore() + " " + ntCodonAlignment.getScore());
            
            if (ntAlignment.getScore() - ntCodonAlignment.getScore() > 50) {
                /*
                 * a possible frameshift
                 */
                boolean fixed = false;

                SymbolList targetFixed = new SimpleSymbolList(target);

                if (maxFrameShifts > 0) {
                    /*
                     * try to fix: walk through the nucleotide alignment, and find
                     * an "isolated" gap that is not of size multiple of 3.
                     */
                    final int BOUNDARY = 10;
                    int seq2pos = 0;
                    int refGapStart = 0;
                    int targetGapStart = 0;

                    SymbolList refNtAligned = ntAlignment.getAlignment().symbolListForLabel(ref.getName());
                    SymbolList targetNtAligned = ntAlignment.getAlignment().symbolListForLabel(target.getName());

                    for (int i = 1; i <= ntAlignment.getAlignment().length(); ++i) {
                        if (refNtAligned.symbolAt(i)
                                == refNtAligned.getAlphabet().getGapSymbol()) {
                            if (refGapStart == -1)
                                refGapStart = i;
                        } else {
                            if (refGapStart > 0) {
                                int refGapStop = i;

                                if ((refGapStop - refGapStart) % 3 != 0) {
                                    /*
                                     * check it is isolated: no gaps in either
                                     * sequence around this gap
                                     */
                                    if (haveGaps(refNtAligned, refGapStart
                                            - BOUNDARY, refGapStart)
                                            || haveGaps(refNtAligned, refGapStop,
                                                    refGapStop + BOUNDARY)
                                            || haveGaps(targetNtAligned,
                                                    refGapStart - BOUNDARY,
                                                    refGapStart)
                                            || haveGaps(targetNtAligned,
                                                    refGapStop, refGapStop
                                                            + BOUNDARY)) {
                                        /*
                                         * not isolated: skip this gap.
                                         */
                                    } else {
                                        /*
                                         * fix it !
                                         */
                                        for (int j = 0; j < 3 - (refGapStop - refGapStart) % 3; ++j) {
                                            targetFixed.edit(new Edit(seq2pos, 0, DNATools.createDNA("n"))); 
                                        }

                                        fixed = true;
                                        break;
                                    }
                                }
                            }

                            refGapStart = -1;
                        }

                        if (targetNtAligned.symbolAt(i) == targetNtAligned
                                .getAlphabet().getGapSymbol()) {
                            if (targetGapStart == -1)
                                targetGapStart = i;
                        } else {
                            if (targetGapStart > 0) {
                                int targetGapStop = i;

                                if ((targetGapStop - targetGapStart) % 3 > 0) {
                                    /*
                                     * check it is isolated: no gaps in either
                                     * sequence around this gap
                                     */
                                    if (haveGaps(refNtAligned, targetGapStart
                                            - BOUNDARY, targetGapStart)
                                            || haveGaps(refNtAligned,
                                                    targetGapStop, targetGapStop
                                                            + BOUNDARY)
                                            || haveGaps(targetNtAligned,
                                                    targetGapStart - BOUNDARY,
                                                    targetGapStart)
                                            || haveGaps(targetNtAligned,
                                                    targetGapStop, targetGapStop
                                                            + BOUNDARY)) {
                                        /*
                                         * not isolated: skip this gap.
                                         */
                                    } else {
                                        /*
                                         * fix it !
                                         */
                                        // target.insert(target.begin() + seq2pos,
                                        // (targetGapStop - targetGapStart) % 3,
                                        // Nucleotide::N);
                                        for (int j = 0; j < (targetGapStop - targetGapStart) % 3; ++j) {
                                            targetFixed.edit(new Edit(seq2pos, 0, DNATools.createDNA("n"))); 
                                        }

                                        fixed = true;
                                        break;
                                    }
                                }
                            }

                            targetGapStart = -1;
                            ++seq2pos;
                        }
                    }
                }

                if (!fixed) {
                    throw new AlignmentException("Could not align!");
                } else {
                    return compute(ref, new SimpleSequence(targetFixed, target.getURN(), target.getName(),
                                   target.getAnnotation()), maxFrameShifts - 1);
                }
            } else {
                return ntCodonAlignment.getAlignment();
            }
        } catch (IndexOutOfBoundsException e) {
            throw new RuntimeException(e);
        } catch (BioRuntimeException e) {
            throw new RuntimeException(e);
        } catch (NoSuchElementException e) {
            throw new RuntimeException(e);
        } catch (ChangeVetoException e) {
            throw new RuntimeException(e);
        } catch (IllegalAlphabetException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean haveGaps(SymbolList seq, int from, int to) {
        for (int i = from; i < to; ++i)
            if (i > 0 && i <= seq.length())
                if (seq.symbolAt(i) == seq.getAlphabet().getGapSymbol())
                    return true;

          return false;
    }

    private ScoredAlignment alignLikeAA(SymbolList seq1, SymbolList seq2,
            int ORF, SymbolList seqAA1, SymbolList seqAA2)
            throws IndexOutOfBoundsException, IllegalAlphabetException,
            ChangeVetoException, IllegalSymbolException {
        seq1 = new SimpleSymbolList(seq1);
        
        SymbolList seq2ORFLead = ORF == 0 ? null : seq2.subList(1, ORF);
        seq2 = new SimpleSymbolList(seq2.subList(ORF + 1, seq2.length()));

        int aaLength = seq2.length() / 3;

        SymbolList seq2ORFEnd = (aaLength * 3 == seq2.length()) ? null : seq2.subList(aaLength * 3 + 1, seq2.length());
        seq2 = new SimpleSymbolList(seq2.subList(1, aaLength * 3));

        
        int firstNonGap = -1;
        int lastNonGap = -1;

        for (int i = 1; i <= seqAA1.length(); ++i) {
            if (seqAA1.symbolAt(i) == seqAA1.getAlphabet().getGapSymbol()) {
                seq1.edit(new Edit((i - 1) * 3 + 1, 0, DNATools.createDNA("---")));
            }

            if (seqAA2.symbolAt(i) == seqAA2.getAlphabet().getGapSymbol()) {
                seq2.edit(new Edit((i - 1) * 3 + 1, 0, DNATools.createDNA("---")));
            } else {
                if (firstNonGap == -1)
                    firstNonGap = (i - 1) * 3 + 1;
                lastNonGap = i * 3;
            }
        }

        if (seq2ORFLead != null)
            for (int i = 1; i <= seq2ORFLead.length(); ++i)
                if ((firstNonGap - seq2ORFLead.length() + i) >= 1)
                    seq2.edit(new Edit(firstNonGap - seq2ORFLead.length() + i,
                            seq2ORFLead.getAlphabet(), seq2ORFLead.symbolAt(i)));

        if (seq2ORFEnd != null)
            for (int i = 1; i <= seq2ORFEnd.length(); ++i)
                if (lastNonGap + i <= seq2.length())
                    seq2.edit(new Edit(lastNonGap + i, seq2ORFEnd.getAlphabet(),
                            seq2ORFEnd.symbolAt(i)));

        Map<String, SymbolList> alignment = new HashMap<String, SymbolList>();
        alignment.put("ref", seq1);
        alignment.put("target", seq2);

        return new ScoredAlignment(new SimpleAlignment(alignment), ntNeedleman.computeScore(seq1, seq2));
    }

    {
        try {
            nuc4_4matrix =         
                new SubstitutionMatrix(DNATools.getDNA(), nuc4_4, "NUC4.4");

            blosum30matrix =
                new SubstitutionMatrix(ProteinTools.getTAlphabet(), blosum30, "BLOSUM30");
        } catch (BioException e) {
            e.printStackTrace();
        }
    }
    
    @SuppressWarnings("unchecked")
    public static String toString(Alignment alignment){
        StringBuilder sb = new StringBuilder();
        
        java.util.Iterator<SymbolList> iter = alignment.symbolListIterator();
        while(iter.hasNext()){
            SymbolList sl = iter.next();
            String s = sl.seqString();
            for(int i = 0; i<s.length(); ++i){
                if((i % 3) == 0)
                    sb.append(' ');
                sb.append(s.charAt(i));
            }
            sb.append('\n');
        }
        
        return sb.toString();
    }
}
