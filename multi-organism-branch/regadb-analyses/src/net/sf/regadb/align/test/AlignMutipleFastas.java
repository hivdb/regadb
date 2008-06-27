package net.sf.regadb.align.test;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.biojava.bio.symbol.IllegalSymbolException;

import net.sf.regadb.align.Aligner;
import net.sf.regadb.align.local.LocalAlignmentService;
import net.sf.regadb.analysis.functions.FastaHelper;
import net.sf.regadb.analysis.functions.FastaRead;
import net.sf.regadb.db.AaSequence;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Protein;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.login.DisabledUserException;
import net.sf.regadb.db.login.WrongPasswordException;
import net.sf.regadb.db.login.WrongUidException;
import net.sf.regadb.db.session.Login;

public class AlignMutipleFastas
{
    public static void main(String [] args)
    {
        Login login = null;
        try
        {
            login = Login.authenticate("kdforc0","Vitabis1");
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
            e.printStackTrace();
        }
        
        if (login == null) {
            throw new RuntimeException("Could not login with given username/password.");
        }
    
    
        File dir = new File("/home/plibin0/Desktop/fasta/Seqs_Pieter/");
        String [] files = dir.list();
        
        File a;
        for(String fn : files)
        {
            a = new File(dir.getAbsolutePath()+File.separatorChar+fn);
            if(!a.isDirectory())
            {
                FastaRead read = FastaHelper.readFastaFile(a, false);
                System.err.println(read.xna_);
                testAlign(read.xna_, login);
            }
        }
    }
    
    static void testAlign(String nt, Login login) {
        NtSequence seq = new NtSequence();
        seq.setNucleotides(nt);

        Transaction t = login.createTransaction();

        Map<String, Protein> proteins = t.getProteinMap();
        Aligner aligner = new Aligner(new LocalAlignmentService(), proteins);
        
        t.commit();
        
        try {
            List<AaSequence> result = aligner.alignHiv(seq);
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
}
