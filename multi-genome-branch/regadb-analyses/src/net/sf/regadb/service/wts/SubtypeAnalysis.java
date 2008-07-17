package net.sf.regadb.service.wts;

import java.util.HashMap;
import java.util.Map;

import net.sf.regadb.db.Genome;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Test;

public class SubtypeAnalysis extends TestNtSequenceAnalysis{
    private static Map<String, String> species = null;
    
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
        getInputs().put("species", getSpecies(getGenome()));
    }

    public void setGenome(Genome genome) {
        this.genome = genome;
    }

    public Genome getGenome() {
        return genome;
    }
    
    synchronized public static String getSpecies(Genome genome){
        if(species == null)
            species = createSpeciesMap();
        return species.get(genome.getOrganismName());
    }
    
    private static Map<String, String> createSpeciesMap(){
        Map<String, String> map = new HashMap<String, String>();
        
        map.put("HIV-1", "hiv");
        
        return map;
    }
}
