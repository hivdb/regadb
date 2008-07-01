/*
 * Created on Jan 10, 2007
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package net.sf.regadb.align.local;

import java.util.HashMap;
import java.util.Map;

import org.biojava.bio.BioException;
import org.biojava.bio.alignment.SubstitutionMatrix;
import org.biojava.bio.seq.Sequence;
import org.biojava.bio.seq.impl.SimpleGappedSequence;
import org.biojava.bio.seq.impl.SimpleSequence;
import org.biojava.bio.seq.io.SymbolTokenization;
import org.biojava.bio.symbol.Alignment;
import org.biojava.bio.symbol.Edit;
import org.biojava.bio.symbol.IllegalAlphabetException;
import org.biojava.bio.symbol.IllegalSymbolException;
import org.biojava.bio.symbol.SimpleAlignment;
import org.biojava.bio.symbol.SimpleSymbolList;
import org.biojava.bio.symbol.Symbol;
import org.biojava.bio.symbol.SymbolList;
import org.biojava.utils.ChangeVetoException;

public class NeedlemanWunsch {

    private SubstitutionMatrix subMatrix;

    private double gapOpenScore;

    private double gapExtensionScore;

    public NeedlemanWunsch(double gapOpenScore, double gapExtensionScore,
            SubstitutionMatrix subMat) {
        this.gapOpenScore = gapOpenScore;
        this.gapExtensionScore = gapExtensionScore;
        this.subMatrix = subMat;
    }

    public ScoredAlignment pairwiseAlignment(Sequence seq1, Sequence seq2) {

        try {
            final int seq1Size = seq1.length();
            final int seq2Size = seq2.length();

            System.err.println("seq1Size:"+seq1Size+"seq2Size:"+seq2Size);
            double dnTable[][] = new double[seq1Size + 1][seq2Size + 1];
            int gapsLengthTable[][] = new int[seq1Size + 1][seq2Size + 1];
            // >0: horiz, <0: vert

            double edgeGapExtensionScore = 0;

            /*
             * compute table
             */
            dnTable[0][0] = 0;
            gapsLengthTable[0][0] = 0;

            for (int i = 1; i < seq1Size + 1; ++i) {
                dnTable[i][0] = dnTable[i - 1][0] + edgeGapExtensionScore;
                gapsLengthTable[i][0] = gapsLengthTable[i - 1][0] + 1;
            }

            for (int j = 1; j < seq2Size + 1; ++j) {
                dnTable[0][j] = dnTable[0][j - 1] + edgeGapExtensionScore;
                gapsLengthTable[0][j] = gapsLengthTable[0][j - 1] - 1;
            }

            for (int i = 1; i < seq1Size + 1; ++i) {
                for (int j = 1; j < seq2Size + 1; ++j) {

                    double sextend = dnTable[i - 1][j - 1]
                           + getReplaceScore(seq1.symbolAt(i), seq2.symbolAt(j));

                    double ges = (j == seq2Size) ? edgeGapExtensionScore
                            : gapExtensionScore;

                    double horizGapScore = ((gapsLengthTable[i - 1][j] > 0)
                            || (j == seq2Size) ? ges : gapOpenScore + ges);
                    double sgaphoriz = dnTable[i - 1][j] + horizGapScore;

                    ges = (i == seq1Size) ? edgeGapExtensionScore
                            : gapExtensionScore;

                    double vertGapScore = (gapsLengthTable[i][j - 1] < 0
                            || (i == seq1Size) ? ges : gapOpenScore + ges);
                    double sgapvert = dnTable[i][j - 1] + vertGapScore;

                    if ((sextend >= sgaphoriz) && (sextend >= sgapvert)) {
                        dnTable[i][j] = sextend;
                        gapsLengthTable[i][j] = 0;
                    } else {
                        if (sgaphoriz > sgapvert) {
                            dnTable[i][j] = sgaphoriz;
                            gapsLengthTable[i][j] = Math.max(0,
                                    gapsLengthTable[i - 1][j]) + 1;
                        } else {
                            dnTable[i][j] = sgapvert;
                            gapsLengthTable[i][j] = Math.min(0,
                                    gapsLengthTable[i][j - 1]) - 1;
                        }
                    }
                }
            }

            /*
             * reconstruct best solution alignment.
             */
            SimpleSymbolList syms1 = new SimpleSymbolList(seq1);
            SimpleSymbolList syms2 = new SimpleSymbolList(seq2);
            
            SymbolTokenization st = seq1.getAlphabet().getTokenization("default");
            SymbolList gap = new SimpleSymbolList(st, "-");
            
            int i = seq1Size + 1, j = seq2Size + 1;
            do {
                if (gapsLengthTable[i - 1][j - 1] == 0) {
                    --i;
                    --j;
                } else if (gapsLengthTable[i - 1][j - 1] > 0) {
                    --i;
                    syms2.edit(new Edit(j, 0, gap));
                } else {
                    --j;
                    syms1.edit(new Edit(i, 0, gap));
                }
            } while (i > 1 || j > 1);

            double score = dnTable[seq1Size][seq2Size];

            Sequence aligned1 = new SimpleGappedSequence(new SimpleSequence(
                    syms1, seq1.getURN(), seq1.getName(),
                    seq1.getAnnotation()));
            Sequence aligned2 = new SimpleGappedSequence(new SimpleSequence(
                    syms2, seq2.getURN(), seq2.getName(),
                    seq2.getAnnotation()));

            Map<String, Sequence> m = new HashMap<String, Sequence>();
            m.put(seq1.getName(), aligned1);
            m.put(seq2.getName(), aligned2);
            Alignment result = new SimpleAlignment(m);

            return new ScoredAlignment(result, score);
        } catch (IndexOutOfBoundsException e) {
            throw new RuntimeException(e);
        } catch (IllegalAlphabetException e) {
            throw new RuntimeException(e);
        } catch (ChangeVetoException e) {
            throw new RuntimeException(e);
        } catch (IllegalSymbolException e) {
            throw new RuntimeException(e);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        } catch (BioException e) {
            throw new RuntimeException(e);
        }
    }

    private double getReplaceScore(Symbol s1, Symbol s2) {
        try {
            return subMatrix.getValueAt(s1, s2);
        } catch (BioException e) {
            if (s1.getMatches().contains(s2) ||
                s2.getMatches().contains(s1))
                return 0;
            else
                return -3;
        }
    }

    public double computeScore(SymbolList seq1, SymbolList seq2) {
        double score = 0;
        int seq1GapLength = 0;
        int seq2GapLength = 0;

        boolean seq1LeadingGap = true;
        boolean seq2LeadingGap = true;

        double edgeGapExtensionScore = 0;

        for (int i = 1; i <= seq1.length(); ++i) {
            if (seq1.symbolAt(i) == seq1.getAlphabet().getGapSymbol()) {
                ++seq1GapLength;
            } else {
                if (seq1GapLength > 0)
                    if (seq1LeadingGap)
                        score -= seq1GapLength * edgeGapExtensionScore;
                    else
                        score -= getGapOpenScore() + seq1GapLength * getGapExtensionScore();

                seq1GapLength = 0;

                if (seq2.symbolAt(i) == seq2.getAlphabet().getGapSymbol()) {
                    ++seq2GapLength;
                } else {
                    if (seq2GapLength > 0)
                        if (seq2LeadingGap)
                            score -= seq2GapLength * edgeGapExtensionScore;
                        else
                            score -= getGapOpenScore() + seq2GapLength * getGapExtensionScore();

                    seq2GapLength = 0;

                    score += getReplaceScore(seq1.symbolAt(i), seq2.symbolAt(i));
                }
            }
        }

        score += seq1GapLength * edgeGapExtensionScore;
        score += seq2GapLength * edgeGapExtensionScore;

        return score;
    }

    public double getGapOpenScore() {
        return gapOpenScore;
    }

    public double getGapExtensionScore() {
        return gapExtensionScore;
    }

}
