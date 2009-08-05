package net.sf.regadb.service.wts;

import net.sf.regadb.db.Genome;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Test;

public class SubtypeAnalysis extends TestNtSequenceAnalysis{    
    private Genome genome=null;
    
    public SubtypeAnalysis(NtSequence ntSequence, Test test, Genome genome) {
        super(ntSequence, test);
        setGenome(genome);
    }

    public SubtypeAnalysis(NtSequence ntSequence, Test subtypeTest, Genome genome, String uid) {
        this(ntSequence, subtypeTest, genome);
    }

    @Override
    protected void init() {
        super.init();
        getInputs().put("species", getGenome().getOrganismName());
    }

    public void setGenome(Genome genome) {
        this.genome = genome;
    }

    public Genome getGenome() {
        return genome;
    }
}
