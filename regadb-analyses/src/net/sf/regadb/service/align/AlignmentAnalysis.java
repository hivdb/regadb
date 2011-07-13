package net.sf.regadb.service.align;

import java.util.Date;
import java.util.List;

import net.sf.regadb.align.Aligner;
import net.sf.regadb.align.local.LocalAlignmentService;
import net.sf.regadb.db.AaSequence;
import net.sf.regadb.db.AnalysisStatus;
import net.sf.regadb.db.Genome;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Protein;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.session.Login;
import net.sf.regadb.sequencedb.SequenceDb;
import net.sf.regadb.service.AnalysisThread;
import net.sf.regadb.service.IAnalysis;

import org.biojava.bio.symbol.IllegalSymbolException;

public class AlignmentAnalysis implements IAnalysis
{
    private int seqIi_;
    private Date startTime_;
    private Date endTime_;
    private String user_;
    private String organismName_;
    private SequenceDb sequenceDb_;
    
    public AlignmentAnalysis(Integer ntseq, String user, String organismName, SequenceDb sequenceDb)
    {
        seqIi_ = ntseq;
        user_ = user;
        organismName_ = organismName;
        sequenceDb_ = sequenceDb;
    }

    public Date getEndTime() 
    {
        return endTime_;
    }

    public Date getStartTime()
    {
        return startTime_;
    }

    public AnalysisStatus getStatus() 
    {
        return null;
    }

    public String getUser() 
    {
        return user_;
    }

    public void kill() 
    {
        
    }

    public void launch(Login sessionSafeLogin)
    {
        startTime_ = new Date(System.currentTimeMillis());
        
        Transaction t = sessionSafeLogin.createTransaction();
        Genome g = t.getGenome(organismName_);
        
        Aligner aligner = new Aligner(new LocalAlignmentService());
        
        NtSequence ntseq = t.getSequence(seqIi_);
        
        t.commit();
        
        List<AaSequence> aaSeqs = null;
        try 
        {
            aaSeqs = aligner.align(ntseq, g);
        } 
        catch (IllegalSymbolException e)
        {
            e.printStackTrace();
        }
                
        System.err.println("Committing results.");
        t = sessionSafeLogin.createTransaction();
        
        synchronized(AnalysisThread.mutex_)
        {
            t.clear();
            ntseq = t.getSequence(seqIi_);
            Protein protein;
            if(aaSeqs!=null)
            {
                for(AaSequence aaseq : aaSeqs)
                {
                    protein = t.getProtein(aaseq.getProtein().getProteinIi());
                    aaseq.setProtein(protein);
                    aaseq.setNtSequence(ntseq);
                    ntseq.getAaSequences().add(aaseq);
                }
            }
            ntseq.setAligned(true);
            
            t.update(ntseq);
            t.commit();
            
            if (sequenceDb_ != null)
            	sequenceDb_.sequenceAligned(ntseq);
        }        
        endTime_ = new Date(System.currentTimeMillis());
    }

    public void pause() 
    {
        
    }

    public Long removeFromLogging()
    {
        return 10000L;
    }
}
