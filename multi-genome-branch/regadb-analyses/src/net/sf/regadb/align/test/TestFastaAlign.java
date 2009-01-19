package net.sf.regadb.align.test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import net.sf.regadb.align.Aligner;
import net.sf.regadb.align.local.LocalAlignmentService;
import net.sf.regadb.db.AaInsertion;
import net.sf.regadb.db.AaMutation;
import net.sf.regadb.db.AaSequence;
import net.sf.regadb.db.Genome;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.login.DisabledUserException;
import net.sf.regadb.db.login.WrongPasswordException;
import net.sf.regadb.db.login.WrongUidException;
import net.sf.regadb.db.session.Login;
import net.sf.regadb.service.wts.BlastAnalysis;
import net.sf.regadb.service.wts.ServiceException;

import org.biojava.bio.BioException;
import org.biojava.bio.seq.Sequence;
import org.biojavax.bio.seq.RichSequenceIterator;

public class TestFastaAlign {
	public static void main(String [] args) {
        RichSequenceIterator xna = null;
        FileReader uploadedStream = null;
        BufferedReader br = null;
        
        Aligner aligner = new Aligner(new LocalAlignmentService());
        
        try 
        {
            uploadedStream = new FileReader(args[0]);
            br = new BufferedReader(uploadedStream);
            xna = org.biojavax.bio.seq.RichSequence.IOTools.readFastaDNA(br, null);
        }
        catch (NoSuchElementException ex) 
        {
        	ex.printStackTrace();
        }
        catch (FileNotFoundException ex) 
        {
        	ex.printStackTrace();
        }
        catch(IOException ioe)
        {
        	ioe.printStackTrace();
        }
        
        Genome g = null;
        
        if(xna!=null)
        { 
            while(xna.hasNext())
            {
                try {
                    Sequence seq = xna.nextRichSequence();
                    NtSequence ntseq = new NtSequence();
                    ntseq.setNucleotides(seq.seqString());
                    
                    if(g==null) {
                    	g = getGenome(ntseq);
                    }
                    		
                    List<AaSequence> result = aligner.align(ntseq, g);
                    for(AaSequence aaseq : result) {
                    	for(AaMutation aamut : aaseq.getAaMutations()) {
                    		char[] mut = aamut.getAaMutation().toCharArray();
                    		Arrays.sort(mut);
                    		System.out.print(aaseq.getProtein().getAbbreviation() + 
                    				aamut.getId().getMutationPosition() + 
                    				new String(mut) + " ");
                    	}
                    	
                    	for(AaInsertion aains : aaseq.getAaInsertions()) {
                    		char[] ins = aains.getAaInsertion().toCharArray();
                    		Arrays.sort(ins);
                    		System.out.print(aaseq.getProtein().getAbbreviation()+
                    				aains.getId().getInsertionPosition()+aains.getId().getInsertionOrder()+
                    				new String(ins) +" ");
                    	}
                    }
                    System.out.print("\n");
                }
                catch (NoSuchElementException e) 
                {
                    System.out.println("ERROR");
                } 
                catch (BioException e) 
                {
                	System.out.println("ERROR");
                }
                catch (Exception e) 
                {
                	System.out.println("ERROR");
                }
            }
        }
        
        try 
        {
            uploadedStream.close();
            br.close();
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
	}
	
    public static Genome getGenome(NtSequence ntseq)
    {
        BlastAnalysis blastAnalysis = new BlastAnalysis(ntseq);
        try {
            blastAnalysis.launch();
        } catch (ServiceException e) {
            e.printStackTrace();
        }
        return blastAnalysis.getGenome();
    }
}
