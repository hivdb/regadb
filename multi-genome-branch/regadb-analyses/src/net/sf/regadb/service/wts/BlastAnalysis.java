package net.sf.regadb.service.wts;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.sf.regadb.db.Genome;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.io.importXML.ImportGenomes;
import net.sf.regadb.util.settings.RegaDBSettings;

public class BlastAnalysis extends NtSequenceAnalysis{
    
    private static Map<String, Genome> genomeMap=null;
    
    private Genome genome=null;
    
    public BlastAnalysis(NtSequence ntSequence){
        super(ntSequence);
    }
    
    public BlastAnalysis(NtSequence ntSequence, String uid) {
        super(ntSequence, uid);
    }

    protected void init(){
        setService("regadb-blast");
        getInputs().put("nt_sequence", toFasta(refreshNtSequence()));
        getOutputs().put("species", null);
    }
    
    protected void processResults()
    {
        setGenome(getGenome(getOutputs().get("species")));
    }
    
    protected void setGenome(Genome genome_) {
        this.genome = genome_;
    }

    public Genome getGenome() {
        return genome;
    }

    public Genome getGenome(String blastResult){
        if(blastResult.contains("K03455"))
            return getGenomeMap().get("HIV-1");
        
        return null;
    }
    
    synchronized public Map<String, Genome> getGenomeMap(){
        if(genomeMap == null){
            genomeMap = createGenomeMap();
        }
        return genomeMap;
    }
    
    private Map<String, Genome> createGenomeMap(){
        Map<String, Genome> map = new HashMap<String, Genome>();
        
        for(Genome g : getAllGenomes())
            map.put(g.getOrganismName(), g);
        return map;
    }
    
    protected Collection<Genome> getAllGenomes(){
        Transaction t = getTransaction();
        if(t == null){
            RegaDBSettings.getInstance().initProxySettings();
            
            FileProvider fp = new FileProvider();
            Collection<Genome> genomes = null;
            File genomesFile = null;
            try {
                genomesFile = File.createTempFile("genomes", "xml");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            try 
            {
                fp.getFile("regadb-genomes", "genomes.xml", genomesFile);
            }
            catch (RemoteException e) 
            {
                e.printStackTrace();
            }
            final ImportGenomes imp = new ImportGenomes();
            genomes = imp.importFromXml(genomesFile);
    
            return genomes;
        }
        else{
            return t.getGenomes();
        }
    }
}
