package net.sf.regadb.service.wts;

import java.io.File;
import java.net.MalformedURLException;
import java.rmi.RemoteException;

import net.sf.regadb.db.AnalysisData;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.ViralIsolate;
import net.sf.wts.client.WtsClient;

public class ViralIsolateAnalysisHelper
{
    public static byte[] run(ViralIsolate vi, Test test, int waitDelay)
    {
        return runInternal(null, vi, test, waitDelay);
    }
    
    public static void run(File resultFile, ViralIsolate vi, Test test, int waitDelay)
    {
        runInternal(resultFile, vi, test, waitDelay);
    }
    
    private static byte[] runInternal(File resultFile, ViralIsolate vi_, Test test_, int waitDelay_)
    {
        WtsClient client = new WtsClient(test_.getAnalysis().getUrl());
        
        byte[] result = null;
        String input = "";
        for(NtSequence ntseq : vi_.getNtSequences())
        {
            input += '>' + ntseq.getLabel() + '\n' + ntseq.getNucleotides() + '\n';
        }
        
        String challenge;
        String ticket = null;
        try 
        {
        challenge = client.getChallenge(test_.getAnalysis().getAccount());
        ticket = client.login(test_.getAnalysis().getAccount(), challenge, test_.getAnalysis().getPassword(), test_.getAnalysis().getServiceName());
 
        
        client.upload(ticket, test_.getAnalysis().getServiceName(), test_.getAnalysis().getBaseinputfile(), input.getBytes());
        
        for(AnalysisData ad : test_.getAnalysis().getAnalysisDatas())
        {
            client.upload(ticket, test_.getAnalysis().getServiceName(), ad.getName(), ad.getData());
        }
        
        client.start(ticket, test_.getAnalysis().getServiceName());
        
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
            if(client.monitorStatus(ticket, test_.getAnalysis().getServiceName()).startsWith("ENDED"))
            {
                finished = true;
            }
        }
        
        if(resultFile!=null)
            client.download(ticket, test_.getAnalysis().getServiceName(), test_.getAnalysis().getBaseoutputfile(), resultFile);
        else
            result = client.download(ticket, test_.getAnalysis().getServiceName(), test_.getAnalysis().getBaseoutputfile());
        
        client.closeSession(ticket, test_.getAnalysis().getServiceName());
        
        } 
        catch (RemoteException e1) 
        {
            e1.printStackTrace();
        } 
        catch (MalformedURLException e) 
        {
            e.printStackTrace();
        }
        
        if(resultFile!=null)
            return null;
        else
            return result;
    }
}
