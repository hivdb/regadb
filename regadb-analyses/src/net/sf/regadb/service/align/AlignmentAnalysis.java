package net.sf.regadb.service.align;

import java.util.Date;
import java.util.List;

import net.sf.regadb.align.Aligner;
import net.sf.regadb.align.local.LocalAlignmentService;
import net.sf.regadb.db.AaSequence;
import net.sf.regadb.db.AnalysisStatus;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.session.Login;
import net.sf.regadb.service.IAnalysis;

import org.biojava.bio.symbol.IllegalSymbolException;

public class AlignmentAnalysis implements IAnalysis
{
    private int seqIi_;
    private Date startTime_;
    private Date endTime_;
    private String user_;
    
    public AlignmentAnalysis(Integer ntseq, String user)
    {
        seqIi_ = ntseq;
        user_ = user;
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
        
        Aligner aligner = new Aligner(new LocalAlignmentService(), t.getProteinMap());
        
        NtSequence ntseq = t.getSequence(seqIi_);
        
        t.commit();
        
        List<AaSequence> aaSeqs = null;
        try 
        {
            aaSeqs = aligner.alignHiv(ntseq);
        } 
        catch (IllegalSymbolException e)
        {
            e.printStackTrace();
        }
                
        System.err.println("Committing results.");
        t = sessionSafeLogin.createTransaction();
        ntseq = t.getSequence(seqIi_);
        
        if(aaSeqs!=null)
        {
            for(AaSequence aaseq : aaSeqs)
            {
                aaseq.setNtSequence(ntseq);
                ntseq.getAaSequences().add(aaseq);
            }
        }
        
        t.update(ntseq);
        t.commit();
                
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
