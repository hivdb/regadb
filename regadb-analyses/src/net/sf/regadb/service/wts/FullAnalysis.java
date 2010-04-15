package net.sf.regadb.service.wts;

import java.util.Date;
import java.util.List;

import net.sf.regadb.db.AnalysisStatus;
import net.sf.regadb.db.Genome;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.db.meta.Equals;
import net.sf.regadb.db.session.Login;
import net.sf.regadb.io.util.StandardObjects;
import net.sf.regadb.service.AnalysisPool;
import net.sf.regadb.service.IAnalysis;
import net.sf.regadb.service.align.AlignmentAnalysis;

public class FullAnalysis implements IAnalysis {
    private Date endTime, startTime;
    private AnalysisStatus status;
    private String user;    
    private Genome genome;
    
    private ViralIsolate viralIsolate;

    public FullAnalysis(ViralIsolate viralIsolate, Genome genome) {
        setViralIsolate(viralIsolate);
        setGenome(genome);
    }

    public Date getEndTime() {
        return endTime;
    }

    public Date getStartTime() {
        return startTime;
    }

    public AnalysisStatus getStatus() {
        return status;
    }

    public String getUser() {
        return user;
    }

    public void kill() {
    }

    public void launch(Login sessionSafeLogin) {
        setStartTime(new Date());
        
        if(getViralIsolate().getNtSequences().size() > 0 && getGenome() != null){
            Transaction t = sessionSafeLogin.createTransaction();
            Test subTypeTest = t.getTest(StandardObjects.getSubtypeTestDescription(), StandardObjects.getSubtypeTestTypeDescription());
            t.commit();
                        
            if(genome != null){
                
                for(NtSequence ntseq : getViralIsolate().getNtSequences())
                {
                    if(ntseq.getAaSequences().size()==0)
                    {
                        launchAnalysis(new AlignmentAnalysis(ntseq.getNtSequenceIi(), 
                        		sessionSafeLogin.getUid(), 
                        		genome.getOrganismName()), sessionSafeLogin);
                        launchAnalysis(new SubtypeAnalysis(ntseq,
                        		subTypeTest,
                                genome,
                                sessionSafeLogin.getUid()), 
                                sessionSafeLogin); 
                    }
                }
                
                t = sessionSafeLogin.createTransaction();
                List<Test> tests = t.getTests();
                String uid = sessionSafeLogin.getUid();
                for(Test test : tests)
                {
                    if(Equals.isSameTestType(StandardObjects.getGssTestType(genome),test.getTestType())
                    		|| Equals.isSameTestType(StandardObjects.getTDRTestType(genome),test.getTestType()))
                    {
                        if(test.getAnalysis()!=null)
                        {
                            launchAnalysis(new ResistanceInterpretationAnalysis(getViralIsolate(), test, uid), sessionSafeLogin);
                        }
                    }
                }
                t.commit();
            }
        }
        
        setEndTime(new Date());
    }
    
    protected void launchAnalysis(IAnalysis analysis, Login login) {
    	AnalysisPool.getInstance().launchAnalysis(analysis, login);
    }

    public void pause() {
    }

    public Long removeFromLogging() {
        return null;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public void setStatus(AnalysisStatus status) {
        this.status = status;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setViralIsolate(ViralIsolate viralIsolate) {
        this.viralIsolate = viralIsolate;
    }

    public ViralIsolate getViralIsolate() {
        return viralIsolate;
    }

    public void setGenome(Genome genome) {
        this.genome = genome;
    }

    public Genome getGenome() {
        return genome;
    }
}
