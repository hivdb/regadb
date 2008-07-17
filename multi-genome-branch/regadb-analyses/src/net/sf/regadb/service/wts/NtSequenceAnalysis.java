package net.sf.regadb.service.wts;

import net.sf.regadb.db.AnalysisStatus;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.session.Login;
import net.sf.regadb.service.IAnalysis;

public abstract class NtSequenceAnalysis extends AbstractService implements IAnalysis{
    private String user_;
    
    private NtSequence ntSequence=null;
    
    
    private Transaction transaction = null;
    
    public NtSequenceAnalysis(NtSequence ntSequence, String uid, int waitDelay)
    {
        this(ntSequence);
        setWaitDelay(waitDelay);
    }
    
    public NtSequenceAnalysis(NtSequence ntSequence, String uid)
    {
        this(ntSequence, uid, 5000);
    }
    
    public NtSequenceAnalysis(NtSequence ntSequence, Test test){
        setNtSequence(ntSequence);
    }
    
    public NtSequenceAnalysis(NtSequence ntSequence){
        setNtSequence(ntSequence);
    }

    protected abstract void init();
    protected abstract void processResults(); 
    
    protected String toFasta(NtSequence ntSequence){
        return '>' + ntSequence.getLabel() + '\n' + ntSequence.getNucleotides();
    }
    
    public void setNtSequence(NtSequence ntSequence) {
        this.ntSequence = ntSequence;
    }

    public NtSequence getNtSequence() {
        return ntSequence;
    }

    
    
    public NtSequence refreshNtSequence(){
        Transaction t = getTransaction();
        if(t != null)
            setNtSequence(t.getSequence(getNtSequence().getNtSequenceIi()));
        return getNtSequence();
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    //IAnalysis methods
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

    public void pause()
    {
        
    }

    public Long removeFromLogging()
    {
        return 10000L;
    }

    public void launch(Login sessionSafeLogin) {
        setTransaction(sessionSafeLogin.createTransaction());
        launch();
    }
}
