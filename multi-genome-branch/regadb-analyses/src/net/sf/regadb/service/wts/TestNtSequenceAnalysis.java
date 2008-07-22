package net.sf.regadb.service.wts;

import java.util.Date;

import net.sf.regadb.db.Analysis;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.service.AnalysisThread;

public class TestNtSequenceAnalysis extends NtSequenceAnalysis{
    private int test_ii;
    private Test test=null;
    private TestResult testResult=null;
    
    
    public TestNtSequenceAnalysis(NtSequence ntSequence, Test test, String uid, int waitDelay)
    {
        this(ntSequence, test);
        setWaitDelay(waitDelay);
    }
    
    public TestNtSequenceAnalysis(NtSequence ntSequence, Test test, String uid)
    {
        this(ntSequence, test, uid, 5000);
    }
    
    public TestNtSequenceAnalysis(NtSequence ntSequence, Test test){
        super(ntSequence);
        setTest(test);
    }

    protected void init(){
        Transaction t = createTransaction();
        
        refreshTest(t);
        Analysis a = getTest().getAnalysis();
        
        setUrl(a.getUrl());
        setService(a.getServiceName());
        setAccount(a.getAccount());
        setPassword(a.getPassword());
        
        getInputs().put(a.getBaseinputfile(), toFasta(refreshNtSequence(t)));
        getOutputs().put(a.getBaseoutputfile(), null);
        
        destroyTransaction(t);
    }
    
    protected void processResults(){
        Transaction t = createTransaction();
        if(t != null){
            synchronized(AnalysisThread.mutex_)
            {
                t.clear();
                refreshTest(t);
                refreshNtSequence(t);
                
                createTestResult();
                
                t.save(getNtSequence());
            }
        }
        else{
            createTestResult();
        }
        destroyTransaction(t);
    }
    
    protected void createTestResult(){
        TestResult tr = new TestResult();
        tr.setNtSequence(getNtSequence());
        setPatient(tr);
        tr.setValue(getOutputs().get(getTest().getAnalysis().getBaseoutputfile()).trim());
        tr.setTestDate(new Date());
        tr.setTest(getTest());
        getNtSequence().getTestResults().add(tr);
        setTestResult(tr);
    }
    
    public void setPatient(TestResult tr){
        ViralIsolate vi = getNtSequence().getViralIsolate();
        
        if(vi != null)
            tr.setPatient(vi.getPatient());
    }
    
    public void setTest(Test test) {
        this.test = test;
        if(test != null)
            setTestIi(test.getTestIi());
    }

    public Test getTest() {
        return test;
    }

    public void setTestResult(TestResult testResult) {
        this.testResult = testResult;
    }

    public TestResult getTestResult() {
        return testResult;
    }
    
    public Test refreshTest(Transaction t){
        if(t != null)
            setTest(t.getTest(getTestIi()));
        return getTest();
    }

    protected void setTestIi(int test_ii) {
        this.test_ii = test_ii;
    }

    protected int getTestIi() {
        return test_ii;
    }
    
    public void destroyTransaction(Transaction t){
        if(t != null){
            super.destroyTransaction(t);
            setTest(null);
        }
    }
}
