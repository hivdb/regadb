package net.sf.regadb.service.stanford;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import net.sf.regadb.csv.Table;
import net.sf.regadb.db.AaInsertion;
import net.sf.regadb.db.AaMutation;
import net.sf.regadb.db.AaSequence;
import net.sf.regadb.db.AnalysisData;
import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.service.wts.RegaDBWtsServer;
import net.sf.wts.client.WtsClient;

public class StanfordResistanceInterpretation 
{
    public void calculate(final File algorithmFile, ViralIsolate vi, final File resultFile) {
        WtsClient client = new WtsClient(RegaDBWtsServer.url_);
        try {
            String challenge = client.getChallenge("public");
            String serviceName = "stanford-hiv-resist";
            String sessionTicket = client.login("public", challenge, "public", serviceName);
            client.upload(sessionTicket, serviceName, "mutations", getMutationList(vi).getBytes());
            client.upload(sessionTicket, serviceName, "asi_rules", algorithmFile);
            client.start(sessionTicket, serviceName);
            while(true) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                }
                if(client.monitorStatus(sessionTicket, serviceName).startsWith("ENDED")) {
                    break;
                }
            }
            client.download(sessionTicket, serviceName, "interpretation", resultFile);
            client.closeSession(sessionTicket, serviceName);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
    
    public List<TestResult> calculate(ViralIsolate vi, Test test, final HashMap<String, DrugGeneric> drugGenerics) throws IOException {
        File resultFile = File.createTempFile("stanford_score_result_file", "txt");
        File algorithmFile = File.createTempFile("algo", "xml");
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(algorithmFile)); 
        bos.write(((AnalysisData)test.getAnalysis().getAnalysisDatas().toArray()[0]).getData());
        bos.flush();
        bos.close();
        calculate(algorithmFile, vi, resultFile);
        List<TestResult> testResults = interpreteResultFile(resultFile, drugGenerics);
        algorithmFile.delete();
        resultFile.delete();
        return testResults;
    }
    
    public ArrayList<TestResult> interpreteResultFile(File resultFile, final HashMap<String, DrugGeneric> drugGenerics) throws FileNotFoundException {
        Table scoreTable = new Table(new BufferedInputStream(new FileInputStream(resultFile)), false);

        ArrayList<TestResult> resistanceResults = new ArrayList<TestResult>();

        for (int j = 1; j < scoreTable.numRows(); ++j) {
            for (int i = 1; i < scoreTable.numColumns();) {
                String drug = scoreTable.valueAt(i, 0);
                String sir = scoreTable.valueAt(i+2, j);
                String muts = scoreTable.valueAt(i+3, j);
                muts = muts.replaceAll(";", " ");
                i += 4;
                
                if (drug.equals("ATV_RTV"))
                    drug = "ATV/r";
                else if (drug.equals("SQV_RTV"))
                    drug = "SQV/r";
                else if (drug.equals("APV/FPV_RTV"))
                    drug = "FPV/r";
                else if (drug.equals("fAPV/r"))
                    drug = "FPV/r";
                else if (drug.equals("fAPV"))
                    drug = "FPV";
                else if (drug.equals("LPV"))
                    drug = "LPV/r";
                
                DrugGeneric d = drugGenerics.get(drug);
                if (d != null) {
                    TestResult tr = new TestResult();
                    double gss;
                    switch(sir.charAt(0)) {
                        case 'R' : gss = 0.0;
                        break;
                        case 'I' : gss = 0.5;
                        break;
                        case 'S' : gss = 1.0;
                        break;
                        default : gss = -1;
                    }
                    tr.setValue(gss+"");
                    tr.setDrugGeneric(d);
                    tr.setTestDate(new Date(System.currentTimeMillis()));
                    resistanceResults.add(tr);
                }
            }
        }
        return resistanceResults;
    }
    
    private String getMutationList(ViralIsolate vi) {
        AaSequence pro = null;
        AaSequence rt = null;
        
        for(NtSequence ntseq : vi.getNtSequences())
        {
            for(AaSequence aaseq : ntseq.getAaSequences())
            {
                if(aaseq.getProtein().getAbbreviation().toUpperCase().equals("PRO"))
                    pro = aaseq;
                else if(aaseq.getProtein().getAbbreviation().toUpperCase().equals("RT"))
                    rt = aaseq;
            }
        }
        
        String result = vi.getViralIsolateIi() + " {";

        if(rt!=null)
        {
            result += getHivdbMutationList(rt); 
        }
        
        result += "} {";

        if(pro!=null)
        {
            result += getHivdbMutationList(pro);
        }
        
        result += "}";

        return result;
    }
    
    private String getHivdbMutationList(AaSequence aaseq) {
        String result = "";
        
        for (AaMutation mut : aaseq.getAaMutations()) {
                //also handle deletions (adds a d)
                if(mut.getAaMutation()!=null)
                result += " " + mut.getId().getMutationPosition() + (mut.getAaMutation().equals("-")?"d":mut.getAaMutation());
        }
        
        for(AaInsertion ins : aaseq.getAaInsertions()) {
            result += " " + ins.getId().getInsertionPosition() + "i";
        }

        return result;
    }
}
