package net.sf.hivgensim.fastatool;

import java.io.File;
import java.io.FileNotFoundException;

public abstract class FastaTool {
	
	private FastaScanner scanner;
	
	public FastaTool(String inputFilename) throws FileNotFoundException{
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
	}
	
	protected abstract void beforeProcessing();
	protected abstract void processSequence(FastaSequence fs);
	protected abstract void afterProcessing();

}
