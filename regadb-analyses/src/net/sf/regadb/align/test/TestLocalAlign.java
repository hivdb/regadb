/*
 * Created on Jan 10, 2007
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package net.sf.regadb.align.test;

import java.util.Date;
import java.util.List;

import net.sf.regadb.align.Aligner;
import net.sf.regadb.align.local.LocalAlignmentService;
import net.sf.regadb.db.AaSequence;
import net.sf.regadb.db.Genome;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.db.login.DisabledUserException;
import net.sf.regadb.db.login.WrongPasswordException;
import net.sf.regadb.db.login.WrongUidException;
import net.sf.regadb.db.session.Login;

import org.biojava.bio.symbol.IllegalSymbolException;

class TestLocalAlign {
    private Login login;
    private Aligner aligner;
    private NtSequence seq;
    private List<AaSequence> result;

    TestLocalAlign(String uid, String passwd)
        throws WrongUidException, WrongPasswordException {
        
        try
		{
			login = Login.authenticate(uid, passwd);
		}
		catch (WrongUidException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (WrongPasswordException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
        catch (DisabledUserException e) 
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        if (login == null) {
            throw new RuntimeException("Could not login with given username/password.");
        }
    }

    TestLocalAlign() {
        login = null;

        aligner = new Aligner(new LocalAlignmentService());
    }

    void testCreateAligner() {
        Transaction t = login.createTransaction();

        aligner = new Aligner(new LocalAlignmentService());
        
        t.commit();
    }
    
    void testAlign() {
        seq = new NtSequence();
        seq.setNucleotides("CCTCAAATcACTCTTTGGCAGCGACCCCTTGTCTCAATAAAAGTAGGGGG"
+ "CCAGACAAAGGAGGCTCTCTTAGACACAGGAGCAGATGATACAGTATTAG"
+ "AAGAAACAGTAGAG-CTGCCAGGAAGATGGAAACCAAAAATGATAGGAGG"
+ "AATTGGAGGTTTTATCAAAGTAAGACAGTATGATCAAATACTCATAGAGA"
+ "TTTGTGGAAAAAAGGCTATAGGTACAGTATtAGTAGGACCTACACCTGTC"
+ "AACAtAATtGGAAGAAATATGTTGACTCAGCTTGGATGCACACtAAAyTK"
+ "TCCAATWRSCMCCATTGAAACTGTACCAGTAAAATTAAAGCCAGGAATGG"
+ "ACGGYCCAAAGGTTAAACAATGGCCATTGACAGAAGARAARATAAAAGCA"
+ "TTAACAGAAaTTTGTGAGGAAATGGARAAGGAAGGAAAAATTACAAAAAT"
+ "TGGGCCTGAAAATCCATATAACACTCCAGTATTTGCCATAAAAAAGAAGG"
+ "ACAGTACTAAGTGGAGAAAATTGGTAGATTTCAGGGAACTTAATAAAAGA"
+ "ACTCAAGACTTTTGGGAAGTACAATTAGGAATACCACACCCTGCAGGGTT"
+ "AAAAAAGAAAAARTCAGTGACAGTACTGGATGTGGGGGATGCATATTTTT"
+ "CAGTKCCTTTAGAtGAAAATTTCAGAAAATATACTGCATTCACCATACCT"
+ "AGTATAAACAATGAAACACCAGGGATTAGATATCAATATAATGTrCTYCC"
+ "ACAGGGATGGAAAGGATCACCAGCAATATTCCAGAGTAGCATGACAAAAA"
+ "TCTTagAGCcCTTTAGgGCACAAAATCCAGACATAGTCATCTATCAATAT"
+ "ATGGATGACTTGTATGTAGGATCTGACTTAGAGATAGGGCAACATAGAGC"
+ "AAAAATAGAGAAGCTAAGrGACCATCTATTAArGTGGGGATTTACCACAC"
+ "CAGATAAGAAACATCAGAAAGAACCCCCATTYCTTTGGATGGGGTATGAA"
+ "CTCCATCCTGACAAATGGACAGTACAGCCTATACAGCTGCCAGATAAGGA"
+ "AAGCTGGACTGTCAATGATATACAGAAGTTAGTGGGAAAATTAAATTGGG"
+ "CAAGTCAGATTTACCCAGGAATTAAAGTAAAGCAACTTTGtAAACTCCTT"
+ "AGGGGGACCAArGCACtAACAGACAtAGtACCACtAACtGAtGAAGCAGA"
+ "ATtagAACtGgcaGAGAACAGGGAAATTCTAAAAGAACCAGTACATGGAG"
+ "TATATTATGACCCATCAAAAGAGTTRATAGCTGAAATACAGAAACAGGGG"
+ "GATGAC");
        
        try {
            Transaction t = login.createTransaction();
            Genome g = t.getGenome("HIV-1");
            
            result = aligner.align(seq, g);
            for (AaSequence aas:result) {
                System.err.println("protein: " + aas.getProtein().getFullName());
                System.err.println("region: " + aas.getFirstAaPos() + " - " + aas.getLastAaPos());
                System.err.println("mutations: " + aas.getAaMutations().size());
                System.err.println("insertions: " + aas.getAaInsertions().size());
            }
        } catch (IllegalSymbolException e) {
            e.printStackTrace();
        }   
    }


    private void testCommit() {
        Transaction t = login.createTransaction();

        Patient p = t.getPatient(t.getDataset("TEST"), "12312");

        ViralIsolate vi = p.createViralIsolate();
        vi.setSampleDate(new Date());
        vi.setSampleId("Flupke");
        
        vi.getNtSequences().add(seq);
        seq.setViralIsolate(vi);

        t.update(p);
        
        t.commit();
    }

    public static void main(String[] args) throws WrongUidException, WrongPasswordException {
        
        if (false) {
            TestLocalAlign test = new TestLocalAlign("kdforc0", "Vitabis1");

            test.testCreateAligner();
            test.testAlign();
            test.testCommit();
        } else {
            TestLocalAlign test = new TestLocalAlign();
            test.testAlign();           
        }
    }
}
