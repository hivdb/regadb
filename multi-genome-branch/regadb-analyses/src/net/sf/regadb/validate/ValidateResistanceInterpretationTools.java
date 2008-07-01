package net.sf.regadb.validate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.db.login.DisabledUserException;
import net.sf.regadb.db.login.WrongPasswordException;
import net.sf.regadb.db.login.WrongUidException;
import net.sf.regadb.db.session.Login;
import net.sf.regadb.io.importXML.ResistanceInterpretationParser;
import net.sf.regadb.service.stanford.StanfordResistanceInterpretation;
import net.sf.regadb.service.wts.ViralIsolateAnalysisHelper;
import net.sf.regadb.service.wts.util.Utils;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class ValidateResistanceInterpretationTools {
    public static void main(String [] args) {
        Login login = null;
        try {
            login = Login.authenticate("test", "test");
        } catch (WrongUidException e) {
            e.printStackTrace();
        } catch (WrongPasswordException e) {
            e.printStackTrace();
        } catch (DisabledUserException e) {
            e.printStackTrace();
        }
        
        System.setProperty("http.proxyHost", "www-proxy");
        System.setProperty("http.proxyPort", "3128");
        
        Transaction t = login.createTransaction();
        List<Test> tests = Utils.getResistanceTests();
        
        ValidateResistanceInterpretationTools vrit = new ValidateResistanceInterpretationTools();
        StanfordResistanceInterpretation sri = new StanfordResistanceInterpretation();
        
        HashMap<String, DrugGeneric> drugGenerics = new HashMap<String, DrugGeneric>();
        for(DrugGeneric dg : t.getGenericDrugs()) {
            drugGenerics.put(dg.getGenericId(), dg);
        }
        int count = 0;
        ArrayList<ViralIsolate> vis = new ArrayList<ViralIsolate>();
        
        for(Patient p : t.getPatients())
        {
            for(ViralIsolate vi : p.getViralIsolates())
            {
                List<TestResult> testResultsStanford = null;
                try {
                    testResultsStanford = sri.calculate(vi, tests.get(0), drugGenerics);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                List<TestResult> testResultsRegaDB = vrit.runRegaDBResistanceInterpretation(vi, tests.get(0), drugGenerics);
                boolean result = vrit.compareTestResults(testResultsStanford, testResultsRegaDB, vi, tests.get(0));
                if(result)
                    System.err.print(".");
                else
                    count++;
            }
        }
        
        t.clear();
        t.commit();

        System.err.println("Counted " + count + " discordances");
    }
    
    class RI implements Comparable<RI> {
        public String drug_;
        public String sir_;
        
        public RI(String drug, String sir) {
            drug_ = drug;
            sir_ = sir;
        }
        
        public int compareTo(RI ri) {
            return drug_.compareTo(ri.drug_);
        }

        @Override
        public boolean equals(Object obj) {
            if(obj instanceof RI) {
                if(((RI)obj).drug_.equals(drug_) && ((RI)obj).sir_.equals(sir_))
                    return true;
                else
                    return false;
            }
            else return false;
        }
    }
    
    public boolean compareTestResults(List<TestResult> results_a, List<TestResult> results_b, ViralIsolate vi, Test test) {
        ArrayList<RI> a = new ArrayList<RI>();
        ArrayList<RI> b = new ArrayList<RI>();
        for(TestResult tr : results_a) {
            a.add(new RI(tr.getDrugGeneric().getGenericId(), tr.getValue()));
        }
        for(TestResult tr : results_b) {
            b.add(new RI(tr.getDrugGeneric().getGenericId(), tr.getValue()));
        }
        Collections.sort(a);
        Collections.sort(b);
        if(a.size()!=b.size()) {
            System.err.println("\nDiscordance in vi/test: " + vi.getViralIsolateIi() + "/" + test.getDescription() + " - Result lists do not have the same size");
            return false;
        }
        for(int i = 0; i < a.size(); i++) {
            if(!a.get(i).equals(b.get(i))) {
                System.err.println("\nDiscordance in vi/test: " + vi.getViralIsolateIi() + "/" + test.getDescription());
                return false;
            }
        }
        return true;
    }
    
    //temporary; make viral isolate analysis more flexible
    public List<TestResult> runRegaDBResistanceInterpretation(ViralIsolate vi, Test test, final HashMap<String, DrugGeneric> drugGenerics) {
        byte [] result = ViralIsolateAnalysisHelper.run(vi, test, 50);
        final List<TestResult> testResults = new ArrayList<TestResult>();
        ResistanceInterpretationParser inp = new ResistanceInterpretationParser()
        {
            @Override
            public void completeScore(String drug, int level, double gss, String description, char sir, ArrayList<String> mutations, String remarks) 
            {
                TestResult resistanceInterpretation = new TestResult();
                resistanceInterpretation.setDrugGeneric(drugGenerics.get(drug));
                resistanceInterpretation.setValue(gss+"");
                resistanceInterpretation.setTestDate(new Date(System.currentTimeMillis()));
                testResults.add(resistanceInterpretation);
            }
        };
        try 
        {
            inp.parse(new InputSource(new ByteArrayInputStream(result)));
        } 
        catch (SAXException e) 
        {
            e.printStackTrace();
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
        return testResults;
    }
}
