package net.sf.hivgensim.fastatool;

import java.io.FileNotFoundException;

import net.sf.hivgensim.preprocessing.SelectionWindow;

/*
 * WARNING: difference with old fastaclean: old PR only starting point is checked!
 * 
 */
public class FastaClean extends FastaTool{
	
	SelectionWindow[] windows;
	
	public FastaClean(String inputFilename, String outputFilename, SelectionWindow[] windows) throws FileNotFoundException{
		super(inputFilename,outputFilename);
		this.windows = windows;
	}
	
	@Override
	protected void beforeProcessing() {
		
	}

	@Override
	protected void afterProcessing() {
		getOut().close();
	}
	
	@Override
	protected void processSequence(FastaSequence fs) {
		boolean seqIsClean = true;
		for(SelectionWindow sw : windows){
			if(fs.getSequence().substring(sw.getStartCheck(), sw.getStopCheck()).contains("-")){				
				seqIsClean = false;					
			}
		}		
		if(seqIsClean){
			getOut().println(fs.getId() + "\n" + fs.getSequence());			
		}		
	}
}
