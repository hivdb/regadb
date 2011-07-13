package net.sf.regadb.validate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.db.login.DisabledUserException;
import net.sf.regadb.db.login.WrongPasswordException;
import net.sf.regadb.db.login.WrongUidException;
import net.sf.regadb.db.session.Login;
import net.sf.regadb.io.importXML.ResistanceInterpretationParser;
import net.sf.regadb.io.util.StandardObjects;
import net.sf.regadb.service.wts.ViralIsolateAnalysisHelper;
import net.sf.regadb.util.settings.RegaDBSettings;

import org.hibernate.Query;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


public class CheckResistanceTests {

	public static void main(String[] args) throws SAXException, IOException {
		RegaDBSettings.createInstance();
		
		String seq = "tggccattgacagaagaaaaaataaaagcattaacagaaatttgtacagaattggaaagggaaggaaaa"+
			"atttcaaaaattggacctgaaaatccatacaacactccaatattcgccataaagaagaaaaacagtact"+
			"agatggagaaaattggtagattttagagagctcaataaaagaactcaagacttctgggaggtccaatta"+
			"ggaatacctcatcc";
		
        Login login = null;
        try {
            login = Login.authenticate(args[0], args[1]);
        } catch (WrongUidException e) {
            e.printStackTrace();
        } catch (WrongPasswordException e) {
            e.printStackTrace();
        } catch (DisabledUserException e) {
            e.printStackTrace();
        }
        
        Transaction t = login.createTransaction();
        
        t.commit();
        
        for(Test test : t.getTests()) {
            if(test.getTestType().getDescription().equals(StandardObjects.getGssDescription()) ) {
            	final Set<String> drugs = new HashSet<String>();
            	{
            	ViralIsolate vi = new ViralIsolate();
                NtSequence ntseq = new NtSequence();
                ntseq.setLabel("seq");
                ntseq.setNucleotides(seq);
                vi.getNtSequences().add(ntseq);
                byte [] result = ViralIsolateAnalysisHelper.run(vi, test, 500);
                
                ResistanceInterpretationParser inp = new ResistanceInterpretationParser()
                {
                    public void completeScore(String drug, int level, double gss, String description, char sir, ArrayList<String> mutations, String remarks) 
                    {
                    	if (!drugs.add(drug))
                    		throw new RuntimeException("Could not add drug " + drug);
                    }
                };
                inp.parse(new InputSource(new ByteArrayInputStream(result)));
            	}
            	
                Query q = t.createQuery("from ViralIsolate vi where vi.viralIsolateIi in (" +
                		"select tr.viralIsolate.viralIsolateIi from TestResult tr " +
                		"where tr.test = :test " +
                		"group by tr.viralIsolate.viralIsolateIi having count(*) != :number" +
                		")");
                q.setParameter("test", test);
                q.setParameter("number", (long)drugs.size());
                List<ViralIsolate> vis = q.list();
                for (ViralIsolate vi : vis) {
                	Set<String> dbDrugs = new HashSet<String>();
                	for (TestResult tr : vi.getTestResults()) {
                		if (tr.getTest() == test) {
                			dbDrugs.add(tr.getDrugGeneric().getGenericId());
                		}
                	}
                	if (!drugs.containsAll(dbDrugs)) {
                		System.err.println(vi.getSampleId());
                		System.err.println(drugs.size() + " - " + dbDrugs.size());
                	}
                }
            }
        }
	}

}
