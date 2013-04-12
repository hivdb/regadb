package net.sf.regadb.service.wts;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Date;

import net.sf.regadb.db.AnalysisStatus;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.db.session.Login;
import net.sf.regadb.io.importXML.ResistanceInterpretationParser;
import net.sf.regadb.service.AnalysisThread;
import net.sf.regadb.service.IAnalysis;
import net.sf.regadb.service.wts.ServiceException.InvalidResultException;

import org.xml.sax.InputSource;

public class ResistanceInterpretationAnalysis implements IAnalysis
{
    private Integer vi_ii_;
    private Integer test_ii_;

    private Date startTime_;
    private Date endTime_;
    private String user_;
    
    public ResistanceInterpretationAnalysis(ViralIsolate isolate, Test test, String uid)
    {
        vi_ii_ = isolate.getViralIsolateIi();
        test_ii_ = test.getTestIi();
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

    public void launch(Login sessionSafeLogin) throws ServiceException
    {
        startTime_ = new Date(System.currentTimeMillis());
        
        Transaction t = sessionSafeLogin.createTransaction();
        
        ViralIsolate vi = t.getViralIsolate(vi_ii_);
        Test test = t.getTest(test_ii_);
        
        t.commit();
        
        byte [] result = ViralIsolateAnalysisHelper.runMutlist(vi, test, 500);
        
        t = sessionSafeLogin.createTransaction();
        t.clear();
        
        synchronized(AnalysisThread.mutex_)
        {
            vi = t.getViralIsolate(vi_ii_);
            test = t.getTest(test_ii_);
            
            final ViralIsolate isolate = vi;
            final Test test_final = test;
            final Transaction t_final = t;
            ResistanceInterpretationParser inp = new ResistanceInterpretationParser()
            {
                @Override
                public void completeScore(String drug, int level, double gss, String description, char sir, ArrayList<String> mutations, String remarks) 
                {
                    TestResult resistanceInterpretation = new TestResult();
                    resistanceInterpretation.setViralIsolate(isolate);
                    resistanceInterpretation.setDrugGeneric(t_final.getDrugGeneric(drug));
                    resistanceInterpretation.setValue(gss+"");
                    resistanceInterpretation.setTestDate(new Date(System.currentTimeMillis()));
                    resistanceInterpretation.setTest(test_final);
                    resistanceInterpretation.setPatient(isolate.getPatient());
                    
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
                    
                    isolate.getTestResults().add(resistanceInterpretation);
                }
            };
            try 
            {
                inp.parse(new InputSource(new ByteArrayInputStream(result)));
                t.update(vi);
                t.commit();
            } 
            catch (Exception e) 
            {
                e.printStackTrace();
                t.rollback();
                t.clearCache();
                
                throw new InvalidResultException("", "", e.getMessage());
            }
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
