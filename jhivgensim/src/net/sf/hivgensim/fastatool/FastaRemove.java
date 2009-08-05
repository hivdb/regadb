package net.sf.hivgensim.fastatool;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;

public class FastaRemove {
	
	private File infile;
	private File outfile;
	
	private ArrayList<String> idsToBeRemoved = new ArrayList<String>();
	
	public FastaRemove(String infilename, String infilenameToRemove, String outfilename) throws FileNotFoundException{
		this.infile = new File(infilename);
		this.outfile = new File(outfilename);
		
		FastaScanner fs = new FastaScanner(new File(infilenameToRemove));
		while(fs.hasNextSequence()){
			idsToBeRemoved.add(fs.nextSequence().getId());
		}
	}
	
	public void processFastaFile() throws FileNotFoundException{
		FastaScanner fs = new FastaScanner(infile);
		PrintStream ps = new PrintStream(outfile);
		
		while(fs.hasNextSequence()){
			FastaSequence seq = fs.nextSequence();
			if(!idsToBeRemoved.contains(seq.getId())){
				ps.println(seq);
			}
		}
		ps.flush();
		ps.close();
	}
	
	public static void main(String[] args) throws FileNotFoundException{
		if(args.length != 3){
			System.err.println("Usage: FastaRemove infile.fasta infile.toremove.fasta outfile.fasta");
			return;			
		}
		new FastaRemove(args[0],args[1],args[2]).processFastaFile();
	}

}
