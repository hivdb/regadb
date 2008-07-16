package net.sf.regadb.analysis;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.sf.regadb.db.Genome;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.io.importXML.ImportGenomes;
import net.sf.regadb.service.wts.FileProvider;
import net.sf.regadb.service.wts.RegaDBWtsServer;
import net.sf.regadb.util.settings.RegaDBSettings;
import net.sf.wts.client.WtsClient;

public class BlastAnalysis {
    
    private static Map<String, Genome> genomeMap=null;
    
    private Date startTime;
    private Date endTime;
    private int waitDelay;

    private NtSequence ntSequence=null;
    private Genome genome=null;
    
    private String account = "public";
    private String password = "public";
    private String service = "regadb-blast";
    private String output = "species";
    
    public BlastAnalysis(){
        
    }
    
    public BlastAnalysis(NtSequence ntSequence){
        setNtSequence(ntSequence);
    }

    public void launch()
    {
        setStartTime(new Date());
        
        WtsClient client_ = new WtsClient(RegaDBWtsServer.url_);
        
        NtSequence ntseq = getNtSequence();
        
        String input = '>' + ntseq.getLabel() + '\n' + ntseq.getNucleotides();
        
        String challenge;
        String ticket = null;

        try 
        {
            challenge = client_.getChallenge(getAccount());
            ticket = client_.login(getAccount(), challenge, getPassword(), getService());
        
            client_.upload(ticket, getService(), "nt_sequence", input.getBytes());
            
            client_.start(ticket, getService());
            
            boolean finished = false;
            while(!finished)
            {
                try 
                {
                    Thread.sleep(getWaitDelay());
                } 
                catch (InterruptedException ie) 
                {
                    ie.printStackTrace();
                }
                if(client_.monitorStatus(ticket, getService()).startsWith("ENDED"))
                {
                    finished = true;
                }
            }
            
            byte [] resultArray = client_.download(ticket, getService(), getOutput());
            setGenome(getGenome(new String(resultArray)));
            
            client_.closeSession(ticket, getService());
        } 
        catch (RemoteException e1) 
        {
            e1.printStackTrace();
        } 
        catch (MalformedURLException e) 
        {
            e.printStackTrace();
        }
                        
        setEndTime(new Date());
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

    public void setAccount(String account) {
        this.account = account;
    }

    public String getAccount() {
        return account;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getService() {
        return service;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getOutput() {
        return output;
    }

    public void setNtSequence(NtSequence ntSequence) {
        this.ntSequence = ntSequence;
    }

    public NtSequence getNtSequence() {
        return ntSequence;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setWaitDelay(int waitDelay) {
        this.waitDelay = waitDelay;
    }

    public int getWaitDelay() {
        return waitDelay;
    }
}
