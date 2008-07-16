package net.sf.regadb.service.wts;

import java.util.Collection;

import net.sf.regadb.analysis.BlastAnalysis;
import net.sf.regadb.db.AnalysisStatus;
import net.sf.regadb.db.Genome;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.session.Login;
import net.sf.regadb.service.IAnalysis;

public class WtsBlastAnalysis extends BlastAnalysis implements IAnalysis {
    private String user_;
    private int seq_ii_;
    
    private Login login;
    
    public WtsBlastAnalysis(NtSequence ntSequence, String uid, int waitDelay)
    {
        seq_ii_ = ntSequence.getNtSequenceIi();
        setWaitDelay(waitDelay);
    }
    
    public WtsBlastAnalysis(NtSequence ntSequence, String uid)
    {
        this(ntSequence, uid, 5000);
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
        setNtSequence(getNtSequence(sessionSafeLogin));
        launch();
    }
    
    public NtSequence getNtSequence(Login sessionSafeLogin){
        setLogin(sessionSafeLogin);
        
        Transaction t = sessionSafeLogin.createTransaction();
        NtSequence ntseq = t.getSequence(seq_ii_);
        t.commit();
        
        return ntseq;
    }
    
    @SuppressWarnings("unchecked")
    protected Collection<Genome> getAllGenomes(){
        Transaction t = getLogin().createTransaction();
        return t.getGenomes();
    }

    public void pause()
    {
        
    }

    public Long removeFromLogging()
    {
        return 10000L;
    }

    protected void setLogin(Login login) {
        this.login = login;
    }

    protected Login getLogin() {
        return login;
    }
}
