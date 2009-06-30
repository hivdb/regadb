package net.sf.regadb.service.wts;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.sf.regadb.db.Genome;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.io.importXML.ImportGenomes;
import net.sf.regadb.util.settings.RegaDBSettings;

public class BlastAnalysis extends NtSequenceAnalysis{
    @SuppressWarnings("serial")
	public static class UnsupportedGenomeException extends ServiceException{
        private String blastResult;

        public UnsupportedGenomeException(String service, String url, String blastResult) {
            super(service, url);
            setBlastResult(blastResult);
        }

        public void setBlastResult(String blastResult) {
            this.blastResult = blastResult;
        }

        public String getBlastResult() {
            return blastResult;
        }
        
    }
    
    private static Map<String, Genome> genomeMap=null;
    
    private Genome genome=null;
    
    public BlastAnalysis(NtSequence ntSequence){
        super(ntSequence);
    }
    
    public BlastAnalysis(NtSequence ntSequence, String uid) {
        super(ntSequence, uid);
    }

    protected void init(){
        Transaction t = createTransaction();
        
        setService("regadb-blast");
        getInputs().put("nt_sequence", toFasta(refreshNtSequence(t)));
        getOutputs().put("species", null);
        
        destroyTransaction(t);
    }
    
    protected void processResults() throws ServiceException
    {
        String result = getOutputs().get("species");
        if(result != null && result.length() > 0){
            Genome g = getGenome(result);
            
            if(g != null)
                setGenome(g);
            else
                throw new UnsupportedGenomeException(getService(), getUrl(), result);
        }
        else
            throw new ServiceException(getService(),getUrl());
    }
    
    protected void setGenome(Genome genome_) {
        this.genome = genome_;
    }

    public Genome getGenome() {
        return genome;
    }

    public Genome getGenome(String blastResult){
        try{
            return getGenomeMap().get(blastResult.split("\n")[0].trim());
        }
        catch(Exception e){
            return null;
        }
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
        Collection<Genome> genomes = null;
        Transaction t = createTransaction();
        
        if(t == null){
            RegaDBSettings.getInstance().getProxyConfig().initProxySettings();
            
            File genomesFile = null;
            try {
                genomesFile = RegaDBWtsServer.getGenomes();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            final ImportGenomes imp = new ImportGenomes();
            genomes = imp.importFromXml(genomesFile);
        }
        else{
            genomes = t.getGenomes();
        }

        destroyTransaction(t);
        return genomes;
    }
}
