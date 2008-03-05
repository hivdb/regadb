package net.sf.regadb.service.wts;

import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.Date;

import net.sf.regadb.db.AnalysisStatus;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.session.Login;
import net.sf.regadb.service.AnalysisThread;
import net.sf.regadb.service.IAnalysis;
import net.sf.wts.client.WtsClient;

public class NtSequenceAnalysis implements IAnalysis
{
    private Integer seq_ii_;
    private Integer test_ii_;

    private Date startTime_;
    private Date endTime_;
    private String user_;
    
    private int waitDelay_;
    
    public NtSequenceAnalysis(NtSequence ntSequence, Test test, String uid, int waitDelay)
    {
        seq_ii_ = ntSequence.getNtSequenceIi();
        
        test_ii_ = test.getTestIi();
        
        waitDelay_ = waitDelay;
    }
    
    public NtSequenceAnalysis(NtSequence ntSequence, Test test, String uid)
    {
        this(ntSequence, test, uid, 5000);
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
        
        NtSequence ntseq = t.getSequence(seq_ii_);
        Test test = t.getTest(test_ii_);
        
        WtsClient client_ = new WtsClient(test.getAnalysis().getUrl());
        
        t.commit();
        
        String input = '>' + ntseq.getLabel() + '\n' + ntseq.getNucleotides();
        
        String challenge;
        String ticket = null;
        try 
        {
            challenge = client_.getChallenge(test.getAnalysis().getAccount());
            ticket = client_.login(test.getAnalysis().getAccount(), challenge, test.getAnalysis().getPassword(), test.getAnalysis().getServiceName());
        
            client_.upload(ticket, test.getAnalysis().getServiceName(), test.getAnalysis().getBaseinputfile(), input.getBytes());
            
            client_.start(ticket, test.getAnalysis().getServiceName());
            
            boolean finished = false;
            while(!finished)
            {
                try 
                {
                    Thread.sleep(waitDelay_);
                } 
                catch (InterruptedException ie) 
                {
                    ie.printStackTrace();
                }
                if(client_.monitorStatus(ticket, test.getAnalysis().getServiceName()).startsWith("ENDED"))
                {
                    finished = true;
                }
            }
            
            byte [] resultArray = client_.download(ticket, test.getAnalysis().getServiceName(), test.getAnalysis().getBaseoutputfile());
            
            t = sessionSafeLogin.createTransaction();
            
            synchronized(AnalysisThread.mutex_)
            {
            t.clear();
            ntseq = t.getSequence(seq_ii_);
            test = t.getTest(test_ii_);
            
            TestResult testResult = new TestResult(test);
            
            testResult.setNtSequence(ntseq);
            testResult.setPatient(ntseq.getViralIsolate().getPatient());
            testResult.setValue(new String(resultArray));
            ntseq.getTestResults().add(testResult);
            
            t.save(ntseq);
            
            t.commit();
            }
            
            client_.closeSession(ticket, test.getAnalysis().getServiceName());
        } 
        catch (RemoteException e1) 
        {
            e1.printStackTrace();
        } 
        catch (MalformedURLException e) 
        {
            e.printStackTrace();
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
