/*
 * Created on Jan 5, 2007
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package net.sf.regadb.align.local;

import org.biojava.bio.symbol.Alignment;

public class ScoredAlignment {
    private Alignment alignment;
    private double score;
    
    public ScoredAlignment(Alignment alignment, double score) {
        this.alignment = alignment;
        this.score = score;
    }

    public Alignment getAlignment() {
        return alignment;
    }

    public double getScore() {
        return score;
    }
}
