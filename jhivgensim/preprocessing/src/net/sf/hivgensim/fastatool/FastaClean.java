package net.sf.hivgensim.fastatool;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

/*
 * WARNING: difference with old fastaclean: old PR only starting point is checked!
 * 
 */
public class FastaClean {
	
	SelectionWindow[] windows;
	
	public FastaClean(SelectionWindow[] windows){
		this.windows = windows;
	}
	
	public void clean(String inputFileName, String outputFileName) throws FileNotFoundException{
		FastaScanner scanner = new FastaScanner(new File(inputFileName));
		PrintStream out = new PrintStream(new FileOutputStream(outputFileName));
		
		FastaSequence fs = null;
		while(scanner.hasNextSequence()){
			fs = scanner.nextSequence();
			out.println(getOutputFor(fs.getId(), fs.getSequence()));
			out.flush();
		}		
		out.close();
	}
	
	private String getOutputFor(String seqId, String nucleotides){
		boolean seqIsClean = true;
		for(SelectionWindow sw : windows){
			if(nucleotides.substring(sw.getStartCheck(), sw.getStopCheck()).contains("-")){				
				seqIsClean = false;					
			}
		}		
		if(seqIsClean){
			return seqId+"\n"+nucleotides;			
		}
		return "";
	}
}
