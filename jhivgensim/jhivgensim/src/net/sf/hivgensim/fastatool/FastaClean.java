package net.sf.hivgensim.fastatool;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

/*
 * WARNING: difference with old fastaclean: old PR only starting point is checked!
 * 
 */
public class FastaClean extends FastaTool{
	
	PrintStream out;
	SelectionWindow[] windows;
	
	public FastaClean(String inputFilename, String outputFilename, SelectionWindow[] windows) throws FileNotFoundException{
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
		boolean seqIsClean = true;
		for(SelectionWindow sw : windows){
			if(fs.getSequence().substring(sw.getStartCheck(), sw.getStopCheck()).contains("-")){				
				seqIsClean = false;					
			}
		}		
		if(seqIsClean){
			out.println(fs.getId() + "\n" + fs.getSequence());
			out.flush();
		}		
	}
}
