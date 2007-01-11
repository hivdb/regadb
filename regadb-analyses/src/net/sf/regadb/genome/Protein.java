/*
 * Created on Jan 10, 2007
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package net.sf.regadb.genome;

import org.biojava.bio.seq.DNATools;
import org.biojava.bio.seq.RNATools;
import org.biojava.bio.symbol.IllegalAlphabetException;
import org.biojava.bio.symbol.SymbolList;

public class Protein {

    private OpenReadingFrame orf;
    private int firstAa;
    private int lastAa;
    private String name;
    private String description;

    public Protein(String name, String description, int firstAa, int lastAa)
    {
        this.name = name;
        this.description = description;
        this.orf = null;
        this.firstAa = firstAa;
        this.lastAa = lastAa;
    }

    public int getFirstNt() {
        return 3 * (firstAa - 1) + 1;
    }

    public int getLastNt() {
        return 3 * lastAa;
    }
    
    public int getFirstAa() {
        return firstAa;
    }

    public int getLastAa() {
        return lastAa;
    }

    public SymbolList getNtSequence() {
        return orf.getSequence().subList(getFirstNt(), getLastNt());
    }

    public SymbolList getAaSequence() {
        try {
            return RNATools.translate(DNATools.toRNA(getNtSequence()));
        } catch (IllegalAlphabetException e) {
            throw new RuntimeException(e);
        } catch (IndexOutOfBoundsException e) {
            throw new RuntimeException(e);
        }
    }

    public OpenReadingFrame getOrf() {
        return orf;
    }

    void setOpenReadingFrame(OpenReadingFrame orf) {
        this.orf = orf;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public int getAaLength() {
        // TODO Auto-generated method stub
        return lastAa - firstAa + 1;
    }

    public int posInProtein(int aa) {
        return aa - firstAa + 1;
    }
    
}
