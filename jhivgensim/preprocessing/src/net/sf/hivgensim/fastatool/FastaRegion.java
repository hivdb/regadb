package net.sf.hivgensim.fastatool;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

public class FastaRegion extends FastaTool{

	PrintStream out;
	SelectionWindow[] windows;
	
	public FastaRegion(String inputFilename, String outputFilename, SelectionWindow[] windows) throws FileNotFoundException{
		super(inputFilename);
		this.out = new PrintStream(new FileOutputStream(outputFilename));
		this.windows = windows;
	}
	
	@Override
	protected void beforeProcessing() {
		
	}

	@Override
	protected void afterProcessing() {
		out.close();
	}

	

	@Override
	protected void processSequence(FastaSequence fs) {
		String regionSelection = "";
		for(SelectionWindow sw : windows){
			regionSelection += fs.getSequence().substring(sw.getStartCheck(), sw.getStopCheck());
		}
		out.println(fs.getId()+"\n"+regionSelection);
		out.flush();
	}
}
