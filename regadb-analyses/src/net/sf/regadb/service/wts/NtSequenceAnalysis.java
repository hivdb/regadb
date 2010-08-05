package net.sf.regadb.service.wts;

import net.sf.regadb.db.AnalysisStatus;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.session.Login;
import net.sf.regadb.service.IAnalysis;

public abstract class NtSequenceAnalysis extends AbstractService implements IAnalysis{
    private String user_;
    
    private int ntsequence_ii;
    private NtSequence ntSequence=null;
    
    private Login login = null;
    
    public NtSequenceAnalysis(NtSequence ntSequence, String uid, int waitDelay)
    {
        this(ntSequence);
        setWaitDelay(waitDelay);
    }
    
    public NtSequenceAnalysis(NtSequence ntSequence, String uid)
    {
        this(ntSequence, uid, 5000);
    }
    
    public NtSequenceAnalysis(NtSequence ntSequence){
        setNtSequence(ntSequence);
    }
    
    protected String toFasta(NtSequence ntSequence){
        return '>' + ntSequence.getLabel() + '\n' + ntSequence.getNucleotides();
    }
    
    public void setNtSequence(NtSequence ntSequence) {
        this.ntSequence = ntSequence;
        if(ntSequence != null && ntSequence.getNtSequenceIi() != null)
            setNtSequenceIi(ntSequence.getNtSequenceIi());
    }

    public NtSequence getNtSequence() {
        return ntSequence;
    }
    
    public NtSequence refreshNtSequence(Transaction t){
        if(t != null)
            setNtSequence(t.getSequence(getNtSequenceIi()));
        return getNtSequence();
    }

    public Transaction createTransaction() {
        if(getLogin() != null)
            return getLogin().createTransaction();
        return null;
    }
    public void destroyTransaction(Transaction t){
        if(t != null){
            t.commit();
            setNtSequence(null);
        }
    }
    public void clearTransaction(Transaction t){
        if(t != null)
            t.clear();
    }
    
    public void setLogin(Login login) {
        this.login = login;
    }

    public Login getLogin() {
        return login;
    }

    protected void setNtSequenceIi(int sequence_ii) {
        this.ntsequence_ii = sequence_ii;
    }

    protected int getNtSequenceIi() {
        return ntsequence_ii;
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

    public void launch(Login sessionSafeLogin) throws ServiceException{
        setLogin(sessionSafeLogin);
        launch();
    }
}
