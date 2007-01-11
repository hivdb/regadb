/*
 * Created on Jan 10, 2007
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package net.sf.regadb.align.test;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.biojava.bio.symbol.IllegalSymbolException;

import net.sf.regadb.align.Aligner;
import net.sf.regadb.align.local.LocalAlignmentService;
import net.sf.regadb.db.AaSequence;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Protein;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.db.session.Login;

class TestLocalAlign {
    private Login login;
    private Aligner aligner;
    private NtSequence seq;
    private List<AaSequence> result;

    TestLocalAlign(String uid, String passwd) {
        login = Login.authenticate(uid, passwd);
        
        if (login == null) {
            throw new RuntimeException("Could not login with given username/password.");
        }
    }
 
    void testCreateAligner() {
        Transaction t = login.createTransaction();

        Map<String, Protein> proteins = t.getProteinMap();
        aligner = new Aligner(new LocalAlignmentService(), proteins);
        
        t.commit();
    }
    
    void testAlign() {
        seq = new NtSequence();
        seq.setNucleotides("CTAAGTTTGCCAGGAAAATGGAAACCAAAAATGATAGGAGGAATT"
                + "GGAGGTTTTAtcAAAGTAAAAcaGTAtGATCAGGtATCCRTAGAGATCTG"
                + "tGGACAtAAAGCTATAGGTACAGTGcTagTAGGACCTACaccTGTCAACA"
                + "nTAATTGGAAGAAATCTGTTGACTCAGATTGGTTGCACTTTAAATTTTCCC"
                + "ATtAgTCCTATTGAAACTGTACCAGTAAAATTAAAGCCAGGAATGGATGG"
                + "CCCAAAAGTTAAACAATGGCCATTGACAGAAGAAAAAATAAAAGCATTAG"
                + "TAGAAATTTGTACAGAAATGGAAAAGGAAGGGAAAATTTCAAAAATTGGG"
                + "CCTGAAAATCCATACAATACTCCAG");
        
        try {
            result = aligner.alignHiv(seq);
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

        t.save(p);
        
        t.commit();
    }

    public static void main(String[] args) {        
        TestLocalAlign test = new TestLocalAlign("kdforc0", "Vitabis1");

        test.testCreateAligner();
        test.testAlign();
        test.testCommit();
    }
}
