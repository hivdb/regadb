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
    
    private ViralIsolate viralIsolate;

    public FullAnalysis(ViralIsolate viralIsolate) {
        setViralIsolate(viralIsolate);
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
        
        if(getViralIsolate().getNtSequences().size() > 0){
            Transaction t = sessionSafeLogin.createTransaction();
            Test subTypeTest = t.getTest(RegaDBWtsServer.getSubtypeTest(), RegaDBWtsServer.getSubtypeTestType());
            t.commit();
            
            BlastAnalysis blastAnalysis = new BlastAnalysis(getViralIsolate().getNtSequences().iterator().next(), sessionSafeLogin.getUid());
            blastAnalysis.launch(sessionSafeLogin);
            Genome genome = blastAnalysis.getGenome();
            
            if(genome != null){
                
                for(NtSequence ntseq : getViralIsolate().getNtSequences())
                {
                    if(ntseq.getAaSequences().size()==0)
                    {
                        AnalysisPool.getInstance().launchAnalysis(new AlignmentAnalysis(ntseq.getNtSequenceIi(), sessionSafeLogin.getUid(), genome.getOrganismName()), sessionSafeLogin);
                        AnalysisPool.getInstance().launchAnalysis(new SubtypeAnalysis(   ntseq,
                                                                                            subTypeTest,
                                                                                            genome,
                                                                                            sessionSafeLogin.getUid()), sessionSafeLogin); 
                    }
                }
                
                t = sessionSafeLogin.createTransaction();
                List<Test> tests = t.getTests();
                String uid = sessionSafeLogin.getUid();
                for(Test test : tests)
                {
                    if(Equals.isSameTestType(StandardObjects.getGssTestType(genome),test.getTestType()))
                    {
                        if(test.getAnalysis()!=null)
                        {
                            AnalysisPool.getInstance().launchAnalysis(new ResistanceInterpretationAnalysis(getViralIsolate(), test, uid), sessionSafeLogin);
                        }
                    }
                }
                t.commit();
            }
        }
        
        setEndTime(new Date());
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
}
