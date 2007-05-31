package net.sf.regadb.service.wts;

import java.io.File;
import java.rmi.RemoteException;

import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.ViralIsolate;
import net.sf.wts.client.WtsClient;

public class ViralIsolateAnalysis
{
    private Test test_;

    private WtsClient client_;
    private int waitDelay_;
    
    private ViralIsolate vi_;
    
    public ViralIsolateAnalysis(ViralIsolate vi, Test test, int waitDelay)
    {
        vi_ = vi;
        
        test_ = test;
        
        client_ = new WtsClient(test.getAnalysis().getUrl());
        
        waitDelay_ = waitDelay;
    }
    
    public ViralIsolateAnalysis(ViralIsolate vi, Test test)
    {
        this(vi, test, 5000);
    }
    
    public void run(File resultFile)
    {
        String input = "";
        for(NtSequence ntseq : vi_.getNtSequences())
        {
            input += '>' + ntseq.getLabel() + '\n' + ntseq.getNucleotides() + '\n';
        }
        
        String challenge;
        String ticket = null;
        try 
        {
            challenge = client_.getChallenge(test_.getAnalysis().getAccount());
            ticket = client_.login(test_.getAnalysis().getAccount(), challenge, test_.getAnalysis().getPassword(), test_.getAnalysis().getServiceName());
        } 
        catch (RemoteException e1) 
        {
            e1.printStackTrace();
        }
        
        client_.upload(ticket, test_.getAnalysis().getServiceName(), test_.getAnalysis().getBaseinputfile(), input.getBytes());
        
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
        
        client_.download(ticket, test_.getAnalysis().getServiceName(), test_.getAnalysis().getBaseoutputfile(), resultFile);
        
        client_.closeSession(ticket, test_.getAnalysis().getServiceName());
    }
}
