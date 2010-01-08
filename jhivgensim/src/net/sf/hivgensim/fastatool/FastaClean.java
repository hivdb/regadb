package net.sf.hivgensim.fastatool;

import java.io.FileNotFoundException;

/*
 * WARNING: difference with old fastaclean: old PR only starting point is checked!
 * 
 */
public class FastaClean extends FastaTool{
	
	private int startNt;
	private int stopNt;
	
	public FastaClean(String inputFilename, String outputFilename, int start, int stop) throws FileNotFoundException{
		super(inputFilename,outputFilename);
		this.startNt = (start-1)*3;
		this.stopNt = stop*3;
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
		if(fs.getSequence().substring(startNt,stopNt).contains("-")){				
				seqIsClean = false;					
		}				
		if(seqIsClean){
			getOut().println(fs.getId() + "\n" + fs.getSequence());			
		}		
	}
	
	public static void main(String[] args) throws NumberFormatException, FileNotFoundException{
		if(args.length != 4){
			System.err.println("Usage: fastaclean in.fasta out.fasta startAA stopAA");
			System.exit(1);
		}
		FastaClean fc = new FastaClean(args[0],args[1],Integer.parseInt(args[2]),Integer.parseInt(args[3]));
		fc.processFastaFile();
	}
}
