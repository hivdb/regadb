package net.sf.hivgensim.fastatool;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

public class FastaRegion {

	SelectionWindow[] windows;

	public FastaRegion(SelectionWindow[] windows){
		this.windows = windows;
	}

	public void getRegion(String inputFileName, String outputFileName) throws FileNotFoundException{
		FastaScanner scanner = new FastaScanner(new File(inputFileName));
		PrintStream out = new PrintStream(new FileOutputStream(outputFileName));
		
		FastaSequence fs = null;
		while(scanner.hasNextSequence()){
			fs = scanner.nextSequence();
			out.println(getRegionFor(fs.getId(), fs.getSequence()));
			out.flush();
		}		
		out.close();
	}

	private String getRegionFor(String seqId, String nucleotides){
		String regionSelection = "";
		for(SelectionWindow sw : windows){
			regionSelection += nucleotides.substring(sw.getStartCheck(), sw.getStopCheck());
		}
		return seqId+"\n"+regionSelection;
	}
}
