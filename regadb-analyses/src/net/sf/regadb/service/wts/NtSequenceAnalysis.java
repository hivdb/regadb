package net.sf.regadb.service.wts;

import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.Date;

import net.sf.regadb.db.AnalysisStatus;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.session.Login;
import net.sf.regadb.service.IAnalysis;
import net.sf.wts.client.WtsClient;

public class NtSequenceAnalysis implements IAnalysis
{
    private int seqIi_;
    private Date startTime_;
    private Date endTime_;
    private String user_;
    
    private Test test_;
    Patient patient_;
    
    private WtsClient client_;
    private int waitDelay_;
    
    public NtSequenceAnalysis(Integer ntseq, Patient p, Test test, String uid, int waitDelay)
    {
        seqIi_ = ntseq;
        
        patient_ = p;
        test_ = test;
        
        client_ = new WtsClient(test.getAnalysis().getUrl());
        
        waitDelay_ = waitDelay;
    }
    
    public NtSequenceAnalysis(Integer ntseq, Patient p, Test test, String uid)
    {
        this(ntseq, p, test, uid, 5000);
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
        
        NtSequence ntseq = t.getSequence(seqIi_);
        
        t.commit();
        
        String input = '>' + ntseq.getLabel() + '\n' + ntseq.getNucleotides();
        
        String challenge;
        String ticket = null;
        try 
        {
            challenge = client_.getChallenge(test_.getAnalysis().getAccount());
            ticket = client_.login(test_.getAnalysis().getAccount(), challenge, test_.getAnalysis().getPassword(), test_.getAnalysis().getServiceName());

        
        client_.upload(ticket, test_.getAnalysis().getServiceName(), test_.getAnalysis().getBaseoutputfile(), input.getBytes());
        
        client_.start(ticket, test_.getAnalysis().getServiceName());
        
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
            if(client_.monitorStatus(ticket, test_.getAnalysis().getServiceName()).startsWith("ENDED"))
            {
                finished = true;
            }
        }
        
        byte [] resultArray = client_.download(ticket, test_.getAnalysis().getServiceName(), test_.getAnalysis().getBaseoutputfile());
        
        t = sessionSafeLogin.createTransaction();

        t.attach(test_);
        Patient p = t.getPatient(patient_.getPatientIi());
        
        TestResult testResult = p.createTestResult(test_);
        
        testResult.setNtSequence(ntseq);
        testResult.setValue(new String(resultArray));
        
        t.save(testResult);
        
        t.commit();
        
        client_.closeSession(ticket, test_.getAnalysis().getServiceName());
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
