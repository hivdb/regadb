package net.sf.regadb.align.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.SortedSet;
import java.util.TreeSet;

import net.sf.regadb.align.Aligner;
import net.sf.regadb.align.local.LocalAlignmentService;
import net.sf.regadb.db.AaInsertion;
import net.sf.regadb.db.AaMutation;
import net.sf.regadb.db.AaSequence;
import net.sf.regadb.db.Genome;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.io.importXML.ImportGenomes;
import net.sf.regadb.service.wts.BlastAnalysis;
import net.sf.regadb.service.wts.RegaDBWtsServer;
import net.sf.regadb.service.wts.ServiceException;
import net.sf.regadb.util.settings.RegaDBSettings;

import org.biojava.bio.BioException;
import org.biojava.bio.seq.Sequence;
import org.biojavax.bio.seq.RichSequenceIterator;

public class TestFastaAlign {
	public static void main(String [] args) {
		RegaDBSettings.createInstance();
		
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
        if (args.length > 1) 
        	g = getGenome(args[1]);
        
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
                    
                    System.out.println("sequence:" + seq.getName());
                    List<AaSequence> result = aligner.align(ntseq, g);
                    for(AaSequence aaseq : result) {
                    	Map<Short, String> aaMutations = new HashMap<Short, String>();
                    	System.out.print("protein:" + aaseq.getProtein().getAbbreviation() + ":");
                    	for(AaMutation aamut : aaseq.getAaMutations()) {
                    		String mut = aamut.getNtReferenceCodon() + aamut.getId() + aamut.getNtMutationCodon();
                    		aaMutations.put(aamut.getId().getMutationPosition(), mut);
                    	}
                    	
                    	Map<Short, Map<Short, String>> aaInsertions = new HashMap<Short, Map<Short, String>>();
                    	for(AaInsertion aains : aaseq.getAaInsertions()) {
                    		Map<Short, String> insertions = aaInsertions.get(aains.getId().getInsertionPosition());
                    		if (insertions == null) { 
                    			insertions = new HashMap<Short, String>();
                    			aaInsertions.put(aains.getId().getInsertionPosition(), insertions);
                    		}
                    		String mut = "---" + aains.getId().getInsertionPosition() + aains.getNtInsertionCodon();
                    		insertions.put(aains.getId().getInsertionPosition(), mut);
                    	}
                    	
                    	for (int i = aaseq.getProtein().getStartPosition(); i <= aaseq.getProtein().getStopPosition(); i++) {
                    		if (aaMutations.get(i) != null) {
                    			System.out.print(aaMutations.get(i) + " ");
                    		}
                    		Map<Short, String> insertions = aaInsertions.get(i);
                    		if (insertions != null) {
                    			SortedSet<Short> sortedInsertions = new TreeSet<Short>(insertions.keySet());
                    			for (Short ins : sortedInsertions) {
                    				System.out.print(insertions.get(ins) + " ");
                    			}
                    		}
                    	}
                    	System.out.print("\n");
                    }
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
	
	public static Genome getGenome(String genome) {
		//TODO this is util code
        RegaDBSettings.getInstance().getProxyConfig().initProxySettings();
        
        File genomesFile = null;
        try {
            genomesFile = RegaDBWtsServer.getGenomes();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        final ImportGenomes imp = new ImportGenomes();
        Collection<Genome> c = imp.importFromXml(genomesFile);
        
        for (Genome g : c) {
        	if (g.getOrganismName().equals(genome.trim()))
        		return g;
        }
        
        return null;
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
