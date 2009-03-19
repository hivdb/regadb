package net.sf.hivgensim.fastatool;

import java.io.FileNotFoundException;

import net.sf.hivgensim.preprocessing.SelectionWindow;

public class FastaRegion extends FastaTool{

	SelectionWindow[] windows;
	
	public FastaRegion(String inputFilename, String outputFilename, SelectionWindow[] windows) throws FileNotFoundException{
		super(inputFilename,outputFilename);
		this.windows = windows;
	}
	
	@Override
	protected void beforeProcessing() {
		
	}

	@Override
	protected void afterProcessing() {
		
	}

	@Override
	protected void processSequence(FastaSequence fs) {
		String regionSelection = "";
		for(SelectionWindow sw : windows){
			regionSelection += fs.getSequence().substring(sw.getStartCheck(), sw.getStopCheck());
		}
		getOut().println(fs.getId()+"\n"+regionSelection);
		getOut().flush();
	}
}
