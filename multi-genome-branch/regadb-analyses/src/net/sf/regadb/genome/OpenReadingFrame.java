/*
 * Created on Jan 10, 2007
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package net.sf.regadb.genome;

import java.util.Map;

import org.biojava.bio.seq.Sequence;

public class OpenReadingFrame {

    private Genome genome;
    private String name;
    private Map<String, Protein> proteins;
    private Sequence sequence;
 
    public OpenReadingFrame(Genome genome, String name, Sequence sequence, Map<String, Protein> proteins)
    {
        this.genome = genome;
        this.name = name;
        this.sequence = sequence;
        this.proteins = proteins;
        
        for (String k:proteins.keySet()) {
            proteins.get(k).setOpenReadingFrame(this);
        }
    }

    public Genome getGenome() {
        return genome;
    }

    public String getName() {
        return name;
    }

    public Map<String, Protein> getProteins() {
        return proteins;
    }

    public Protein getProtein(String name) {
        return proteins.get(name);
    }

    public Sequence getSequence() {
        return sequence;
    }
}
