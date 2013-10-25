package net.sf.regadb.service.wts;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import net.sf.regadb.db.AaInsertion;
import net.sf.regadb.db.AaMutation;
import net.sf.regadb.db.AaSequence;
import net.sf.regadb.db.AnalysisData;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.io.importXML.ResistanceInterpretationParser;
import net.sf.regadb.service.wts.client.WtsClientFactory;
import net.sf.regadb.util.settings.RegaDBSettings;
import net.sf.wts.client.IWtsClient;
import net.sf.wts.client.WtsClient;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

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
        IWtsClient client = WtsClientFactory.getWtsClient(
        		RegaDBSettings.getInstance().getInstituteConfig().getWtsUrl(test_.getAnalysis().getUrl()));
        
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
    
    public static String toFasta(ViralIsolate vi){
    	StringBuilder sb = new StringBuilder();
    	
    	for(NtSequence nt : vi.getNtSequences())
			sb.append('>').append(nt.getLabel().replace(' ','_').replace(',','-')).append('\n')
				.append(nt.getNucleotides()).append('\n');
    	
    	return sb.toString();
    }
    
    public static byte[] runMutlist(Collection<ViralIsolate> vis, Test test_, int waitDelay_)
    {
    	File resultFile = null; 
    	
        IWtsClient client = WtsClientFactory.getWtsClient(
        		RegaDBSettings.getInstance().getInstituteConfig().getWtsUrl(test_.getAnalysis().getUrl()));
        
        byte[] result = null;
        StringBuilder input = new StringBuilder();
        for(ViralIsolate vi_ : vis)
        {
	        for(NtSequence ntseq : vi_.getNtSequences())
	        {
	        	for(AaSequence aa : ntseq.getAaSequences()){
	        		input.append(aa.getProtein().getAbbreviation()).append(',');
	        		input.append(aa.getFirstAaPos()).append('-').append(aa.getLastAaPos());
	        		input.append(":");
	        		
	        		boolean first = true;
	        		
	        		for(AaMutation m : aa.getAaMutations()){
	        			if(first)
	        				first = false;
	        			else
	            			input.append(",");
	        			
	        			input.append(m.getAaReference() == null || m.getAaReference().length() == 0 ?
	        					"-" : m.getAaReference());
	        			input.append(m.getId().getMutationPosition());
	        			input.append(m.getAaMutation() == null || m.getAaReference().length() == 0 ?
	        					"-" : m.getAaMutation());
	        		}
	        		
	        		for(AaInsertion i : aa.getAaInsertions()){
	        			if(first)
	        				first = false;
	        			else
	            			input.append(",");
	        			
	        			input.append("i");
	        			input.append(i.getId().getInsertionPosition());
	        			input.append(i.getAaInsertion());
	        		}
	        		
	        		input.append(";");
	        	}
	        }
        }
        
        String challenge;
        String ticket = null;
        try 
        {
        challenge = client.getChallenge(test_.getAnalysis().getAccount());
        String serviceName = test_.getAnalysis().getServiceName();
        ticket = client.login(test_.getAnalysis().getAccount(), challenge, test_.getAnalysis().getPassword(), serviceName);
        
        client.upload(ticket, serviceName, test_.getAnalysis().getBaseinputfile(), input.toString().getBytes());
        
        for(AnalysisData ad : test_.getAnalysis().getAnalysisDatas())
        {
            client.upload(ticket, serviceName, ad.getName(), ad.getData());
        }
        
        client.start(ticket, serviceName);
        
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
            if(client.monitorStatus(ticket, serviceName).startsWith("ENDED"))
            {
                finished = true;
            }
        }
        
        if(resultFile!=null)
            client.download(ticket, serviceName, test_.getAnalysis().getBaseoutputfile(), resultFile);
        else
            result = client.download(ticket, serviceName, test_.getAnalysis().getBaseoutputfile());
        
        client.closeSession(ticket, serviceName);
        
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
    
    public static byte[] runMutlist(ViralIsolate vi_, Test test_, int waitDelay_)
    {
    	List<ViralIsolate> vis = new ArrayList<ViralIsolate>(1);
    	vis.add(vi_);
    	return runMutlist(vis, test_, waitDelay_);
    }
    
    public static List<TestResult> runViralIsolateResistanceTest(final Transaction t, final ViralIsolate isolate, final Test test) throws SAXException, IOException{
    	final List<TestResult> testResults = new ArrayList<TestResult>();
    	
        byte[] result = ViralIsolateAnalysisHelper.runMutlist(isolate, test, 200);
    	
    	ResistanceInterpretationParser inp = new ResistanceInterpretationParser()
        {
            @Override
            public void completeScore(String drug, int level, double gss, String description, char sir, ArrayList<String> mutations, String remarks) 
            {
                TestResult resistanceInterpretation = new TestResult();
                resistanceInterpretation.setDrugGeneric(t.getDrugGeneric(drug));
                resistanceInterpretation.setValue(gss+"");
                resistanceInterpretation.setTestDate(new Date(System.currentTimeMillis()));
                resistanceInterpretation.setTest(test);
                
                StringBuffer data = new StringBuffer();
                data.append("<interpretation><score><drug>");
                data.append(drug);
                data.append("</drug><level>");
                data.append(level);
                data.append("</level><description>");
                data.append(description);
                data.append("</description><sir>");
                data.append(sir);
                data.append("</sir><gss>");
                data.append(gss);
                data.append("</gss><mutations>");
                int size = mutations.size();
                for(int i = 0; i<size; i++)
                {
                    data.append(mutations.get(i));
                    if(i!=size-1)
                        data.append(' ');
                }
                data.append("</mutations><remarks>");
                data.append(remarks);
                data.append("</remarks></score></interpretation>");
                resistanceInterpretation.setData(data.toString().getBytes());
                
                testResults.add(resistanceInterpretation);
            }
        };
        
        inp.parse(new InputSource(new ByteArrayInputStream(result)));
        return testResults;
    }
}
