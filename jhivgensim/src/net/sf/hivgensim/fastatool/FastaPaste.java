package net.sf.hivgensim.fastatool;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

public class FastaPaste {
	
	private String firstInput;
	private String secondInput;
	private String output;
	
	public FastaPaste(String firstInput, String secondInput, String output){
		this.firstInput = firstInput;
		this.secondInput = secondInput;
		this.output = output;
	}
	
	public void processFastaFile() throws FileNotFoundException{
		FastaScanner first = new FastaScanner(new File(firstInput));
		FastaScanner second = new FastaScanner(new File(secondInput));
		PrintStream out = new PrintStream(new File(output));
		
		FastaSequence seq1;
		FastaSequence seq2;
		
		while(first.hasNextSequence()){
			if(!second.hasNextSequence()){
				throw new Error("more sequences in first than in second fasta-file");
			}
			seq1 = first.nextSequence();
			seq2 = second.nextSequence();
			out.println(seq1.getId());
			out.println(seq1.getSequence()+seq2.getSequence());			
		}
		if(second.hasNextSequence()){
			throw new Error("more sequences in second than in first fasta-file");
		}
		out.flush();
		out.close();
	}
	
	public static void main(String[] args) throws FileNotFoundException{
		if(args.length != 3){
			System.err.println("Usage: merge first.fasta second.fasta out.fasta");
			return;
		}
		new FastaPaste(args[0],args[1],args[2]).processFastaFile();		
	}

}
