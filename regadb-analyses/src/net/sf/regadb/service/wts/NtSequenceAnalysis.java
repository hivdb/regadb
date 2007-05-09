package net.sf.regadb.service.wts;

import java.rmi.RemoteException;
import java.util.Date;

import net.sf.regadb.db.AnalysisStatus;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.session.Login;
import net.sf.regadb.io.exportXML.ExportToXML;
import net.sf.regadb.io.util.IOUtil;
import net.sf.regadb.service.IAnalysis;
import net.sf.wts.client.WtsClient;

import org.jdom.Element;

public class NtSequenceAnalysis implements IAnalysis
{
    private int seqIi_;
    private Date startTime_;
    private Date endTime_;
    private String user_;
    
    private String wtsAccount_;
    private String wtsPassword_;
    private String wtsService_;
    private String wtsOutputFileName_;
    private boolean fasta_;
    private Test test_;
    Patient patient_;
    
    private WtsClient client_;
    
    public NtSequenceAnalysis(Integer ntseq, Patient p, String user, String wtsAccount, String wtsPassword, String wtsService, String wtsUrl, Test test, String wtsOutputFileName, boolean fasta)
    {
        seqIi_ = ntseq;
        user_ = user;
        
        wtsAccount_ = wtsAccount;
        wtsPassword_ = wtsPassword;
        wtsService_ = wtsService;
        wtsOutputFileName_ = wtsOutputFileName;
        
        fasta_ = fasta;
        
        patient_ = p;
        test_ = test;
        
        client_ = new WtsClient(wtsUrl);
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
        
        String input;
        if(fasta_)
        {
            input = '>' + ntseq.getLabel() + '\n' + ntseq.getNucleotides();
        }
        else
        {    
            ExportToXML export = new ExportToXML();
            Element e = new Element("NtSequence");
            export.writeNtSequence(ntseq, e);
            input = IOUtil.getStringFromDoc(e);
        }
        
        String challenge;
        String ticket = null;
        try 
        {
            challenge = client_.getChallenge(wtsAccount_);
            ticket = client_.login(wtsAccount_, challenge, wtsPassword_, wtsService_);
        } 
        catch (RemoteException e1) 
        {
            e1.printStackTrace();
        }
        
        client_.upload(ticket, wtsService_, "nt_sequence", input.getBytes());
        
        client_.start(ticket, wtsService_);
        
        boolean finished = false;
        while(!finished)
        {
            try 
            {
                Thread.sleep(5000);
            } 
            catch (InterruptedException ie) 
            {
                ie.printStackTrace();
            }
            if(client_.monitorStatus(ticket, wtsService_).startsWith("ENDED"))
            {
                finished = true;
            }
        }
        
        byte [] resultArray = client_.download(ticket, wtsService_, wtsOutputFileName_);
        
        t = sessionSafeLogin.createTransaction();

        t.attach(test_);
        Patient p = t.getPatient(patient_.getPatientIi());
        
        TestResult testResult = p.createTestResult(test_);
        
        testResult.setNtSequence(ntseq);
        testResult.setValue(new String(resultArray));
        
        t.save(testResult);
        
        t.commit();
        
        client_.closeSession(ticket, wtsService_);
                        
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
