package net.sf.regadb.service.align;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.biojava.bio.symbol.IllegalSymbolException;

import net.sf.regadb.align.Aligner;
import net.sf.regadb.align.local.LocalAlignmentService;
import net.sf.regadb.db.AaSequence;
import net.sf.regadb.db.AnalysisStatus;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Protein;
import net.sf.regadb.db.SettingsUser;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.session.Login;
import net.sf.regadb.service.IAnalysis;

public class AlignmentAnalysis implements IAnalysis
{
    private int seqIi_;
    private NtSequence ntSeq_;
    Map<String, Protein> proteins_;
    private Date startTime_;
    private Date endTime_;
    private Login login_;
    
    public AlignmentAnalysis(NtSequence ntseq, Map<String, Protein> proteins, Login login)
    {
        seqIi_ = ntseq.getNtSequenceIi();
        ntSeq_ = ntseq;
        proteins_ = proteins;
        login_ = login;
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

    public SettingsUser getUser() 
    {
        return login_.getUserSettings_();
    }

    public void kill() 
    {
        
    }

    public void launch() 
    {
        startTime_ = new Date(System.currentTimeMillis());
        
        Aligner aligner = new Aligner(new LocalAlignmentService(), proteins_);
        
        List<AaSequence> aaSeqs = null;
        try 
        {
            aaSeqs = aligner.alignHiv(ntSeq_);
        } 
        catch (IllegalSymbolException e)
        {
            e.printStackTrace();
        }
                
        Transaction t = login_.createTransaction();
        NtSequence ntseq = t.getSequence(seqIi_);
        if(aaSeqs!=null)
        {
            for(AaSequence aaseq : aaSeqs)
            {
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
        return 1000L;
    }
}
