package net.sf.hivgensim.fastatool;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

public abstract class FastaTool {
	
	private FastaScanner scanner;
	private PrintStream out;
	
	public FastaTool(String inputFilename, String outputFilename) throws FileNotFoundException{
		scanner = new FastaScanner(new File(inputFilename));
		out = new PrintStream(new File(outputFilename));
	}
	
	protected void setInputFile(String inputFilename) throws FileNotFoundException{
		scanner = new FastaScanner(new File(inputFilename));
	}
	
	public void processFastaFile() throws FileNotFoundException{
		beforeProcessing();
		FastaSequence fs = null;
		while(scanner.hasNextSequence()){
			fs = scanner.nextSequence();
			processSequence(fs);
		}
		afterProcessing();
		out.close();
	}
	
	protected PrintStream getOut(){
		return out;
	}
	
	protected abstract void beforeProcessing();
	protected abstract void processSequence(FastaSequence fs);
	protected abstract void afterProcessing();

}
