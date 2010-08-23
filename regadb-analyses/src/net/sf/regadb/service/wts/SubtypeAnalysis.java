package net.sf.regadb.service.wts;

import net.sf.regadb.db.Genome;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Test;
import net.sf.regadb.util.settings.RegaDBSettings;

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
    
    @Override
    public String getUrl(){
    	if(RegaDBSettings.getInstance().getInstituteConfig().getUseWtsUrlForSubtyping())
    		return RegaDBSettings.getInstance().getInstituteConfig().getWtsUrl();
    	else
    		return super.getUrl();
    }
}
