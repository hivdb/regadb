package net.sf.regadb.align.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
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
		long start = System.currentTimeMillis();

		RegaDBSettings.createInstance();

		final String output = "--output=";
		boolean nt = true;
		for (String a : args) {
			if (a.startsWith(output)) {
				String outputFormat = a.substring(a.indexOf(output) + output.length());
				if (outputFormat.equals("aa"))
					nt = false;
				else if (outputFormat.equals("nt"))
					nt = true;
				else {
					System.err.println("Wrong output format! Exiting!");
					System.exit(0);
				}
			}
		}
		
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
                    
                    System.out.println("sequence=" + seq.getName());
                    List<AaSequence> result = aligner.align(ntseq, g);
                    
                    Collections.sort(result, new Comparator<AaSequence>(){
						@Override
						public int compare(AaSequence a1, AaSequence a2) {
							return a1.getProtein().getAbbreviation().
								compareTo(a2.getProtein().getAbbreviation());
						}
                    });
                    
                	for (AaSequence aaseq : result) {
                    	Map<Short, String> aaMutations = new HashMap<Short, String>();
                    	System.out.print("protein=" + aaseq.getProtein().getAbbreviation() + ",");
                    	System.out.print("start=" + aaseq.getFirstAaPos() + ",");
                    	System.out.print("end=" + aaseq.getLastAaPos() + ",");
                    	System.out.print("mutations=");
                    	for(AaMutation aamut : aaseq.getAaMutations()) {
                    		String mut;
                    		if (nt)
                    			mut = aamut.getNtReferenceCodon().toUpperCase() + aamut.getId().getMutationPosition() + aamut.getNtMutationCodon().toUpperCase();
                    		else 
                    			mut = aamut.getAaReference().toUpperCase() + aamut.getId().getMutationPosition() + aamut.getAaMutation().toUpperCase();
                    		aaMutations.put(aamut.getId().getMutationPosition(), mut);
                    	}
                    	
                    	Map<Short, Map<Short, String>> aaInsertions = new HashMap<Short, Map<Short, String>>();
                    	for(AaInsertion aains : aaseq.getAaInsertions()) {
                    		Map<Short, String> insertions = aaInsertions.get(aains.getId().getInsertionPosition());
                    		if (insertions == null) { 
                    			insertions = new HashMap<Short, String>();
                    			aaInsertions.put(aains.getId().getInsertionPosition(), insertions);
                    		}
                    		String mut;
                    		if (nt)
                    			mut = "---" + aains.getId().getInsertionPosition() + aains.getNtInsertionCodon().toUpperCase();
                    		else
                    			mut = "-" + aains.getId().getInsertionPosition() + aains.getAaInsertion().toUpperCase();
                    		insertions.put(aains.getId().getInsertionOrder(), mut);
                    	}
            
                    	String toPrint = "";
                    	for (int i = 0; i <= aaseq.getProtein().getStopPosition(); i++) {
                    		if (aaMutations.get((short)i) != null) {
                    			toPrint += aaMutations.get((short)i)  + " ";
                    		}
                    		Map<Short, String> insertions = aaInsertions.get((short)i);
                    		if (insertions != null) {
                    			SortedSet<Short> sortedInsertions = new TreeSet<Short>(insertions.keySet());
                    			for (Short ins : sortedInsertions) {
                    				toPrint += insertions.get(ins) + " ";
                    			}
                    		}
                    	}
                    	if (toPrint.length() > 0) {
	                    	System.out.print(toPrint.substring(0,toPrint.length() - 1));
	                    	System.out.print("\n");
                    	}
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
        
        long end = System.currentTimeMillis();
        System.err.println("time=" + (end-start));
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
