package net.sf.regadb.align.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
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
import net.sf.regadb.align.view.UIVisualizeAaSequence;
import net.sf.regadb.analysis.functions.MutationHelper;
import net.sf.regadb.db.AaInsertion;
import net.sf.regadb.db.AaMutation;
import net.sf.regadb.db.AaSequence;
import net.sf.regadb.db.Genome;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.io.importXML.ImportGenomes;
import net.sf.regadb.service.wts.BlastAnalysis;
import net.sf.regadb.service.wts.RegaDBWtsServer;
import net.sf.regadb.service.wts.ServiceException;
import net.sf.regadb.util.args.Arguments;
import net.sf.regadb.util.args.PositionalArgument;
import net.sf.regadb.util.args.ValueArgument;
import net.sf.regadb.util.settings.RegaDBSettings;

import org.biojava.bio.BioException;
import org.biojava.bio.seq.Sequence;
import org.biojavax.bio.seq.RichSequenceIterator;

public class TestFastaAlign {
	public enum OutputType {
		Aa,
		Nt,
		Ui;
	}
	public static void main(String [] args) throws FileNotFoundException {
		Arguments as = new Arguments();
		ValueArgument outputType = as.addValueArgument("type", "aa|nt|ui", false);
		ValueArgument genome = as.addValueArgument("genome", "HIV-1|HIV-2A|HIV-2B|HCV|HTLV-1", false);
		PositionalArgument inputFile = as.addPositionalArgument("fasta", true);
		PositionalArgument outputFile = as.addPositionalArgument("output-file", false);
		
		if(!as.handle(args))
			return;
		
		long start = System.currentTimeMillis();

		RegaDBSettings.createInstance();

		OutputType ot = OutputType.Nt;
		if(outputType.isSet()){
			if (outputType.getValue().equals("aa"))
				ot = OutputType.Aa;
			else if (outputType.getValue().equals("nt"))
				ot = OutputType.Nt;
			else if (outputType.getValue().equals("ui"))
				ot = OutputType.Ui;
			else {
				System.err.println("Wrong output format! Exiting!");
				return;
			}
		}
		
		PrintStream out = outputFile.isSet() ? new PrintStream(new FileOutputStream(new File(outputFile.getValue()))) : System.out;
		
        RichSequenceIterator xna = null;
        FileReader uploadedStream = null;
        BufferedReader br = null;
        
        Aligner aligner = new Aligner(new LocalAlignmentService());
        
        try 
        {
            uploadedStream = new FileReader(inputFile.getValue());
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
        
        Genome g = null;
        if (genome.isSet()) 
        	g = getGenome(genome.getValue());
        
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
                    
                    out.println("sequence=" + seq.getName());
                    System.err.println(">"+ seq.getName());
                    List<AaSequence> tmpResult = aligner.align(ntseq, g);

                    //remove the AaSequences where startpos == endpos
                    List<AaSequence> result = new ArrayList<AaSequence>();
                    for (AaSequence aaseq : tmpResult) {
                    	if (aaseq.getFirstAaPos() != aaseq.getLastAaPos())
                    		result.add(aaseq);
                    }
                    
                    Collections.sort(result, new Comparator<AaSequence>(){
						
                    	public int compare(AaSequence a1, AaSequence a2) {
							return a1.getProtein().getAbbreviation().compareTo(a2.getProtein().getAbbreviation());
						}
                    });
                    
                	for (AaSequence aaseq : result) {
                		if (ot == OutputType.Ui) {
                			out.println(new UIVisualizeAaSequence().getAlignmentView(aaseq));
                			out.println(MutationHelper.getSynonymousMutations(aaseq));
                			out.println(MutationHelper.getNonSynonymousMutations(aaseq));
                		} else {
	                    	Map<Short, String> aaMutations = new HashMap<Short, String>();
	                    	out.print("protein=" + aaseq.getProtein().getAbbreviation() + ",");
	                    	out.print("start=" + aaseq.getFirstAaPos() + ",");
	                    	out.print("end=" + aaseq.getLastAaPos() + ",");
	                    	out.print("mutations=");
	                    	for(AaMutation aamut : aaseq.getAaMutations()) {
	                    		String mut;
	                    		if (ot == OutputType.Nt)
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
	                    		if (ot == OutputType.Nt)
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
		                    	out.print(toPrint.substring(0,toPrint.length() - 1));	                    	
	                    	}
	                    	out.print("\n");
                		}
                    }
                }
                catch (NoSuchElementException e) 
                {
                	e.printStackTrace();
                    out.println("ERROR");
                } 
                catch (BioException e) 
                {
                	e.printStackTrace();
                	out.println("ERROR");
                }
                catch (Exception e) 
                {
                	e.printStackTrace();
                	out.println("ERROR");
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
