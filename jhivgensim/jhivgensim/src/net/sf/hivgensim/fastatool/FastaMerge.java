package net.sf.hivgensim.fastatool;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

public class FastaMerge{
	
	private String naiveInputFilename;
	private String treatedInputFilename;
	private String outputFilename;
	
	public FastaMerge(String naiveInputFilename, String treatedInputFilename, String outputFilename) throws FileNotFoundException{
		this.naiveInputFilename = naiveInputFilename;
		this.treatedInputFilename = treatedInputFilename;
		this.outputFilename = outputFilename;		
	}
		
	public void processFastaFile() throws FileNotFoundException{
		FastaScanner naive = new FastaScanner(new File(naiveInputFilename));
		FastaScanner treated = new FastaScanner(new File(treatedInputFilename));
		PrintStream out = new PrintStream(new File(outputFilename));
		
		FastaSequence nseq;
		FastaSequence tseq;
		
		while(naive.hasNextSequence()){
			if(!treated.hasNextSequence()){
				throw new Error("more naive than treated sequences");
			}
			nseq = naive.nextSequence();
			tseq = treated.nextSequence();
			out.println(nseq.getId());
			out.println(nseq.getSequence());
			out.println(tseq.getId());
			out.println(tseq.getSequence());
		}
		if(treated.hasNextSequence()){
			throw new Error("more treated than naive sequences");
		}
		out.flush();
		out.close();
	}
	
	public static void main(String[] args) throws FileNotFoundException{
		if(args.length != 3){
			System.err.println("Usage: merge naive.fasta treated.fasta out.fasta");
			return;
		}
		new FastaMerge(args[0],args[1],args[2]).processFastaFile();		
	}

}
