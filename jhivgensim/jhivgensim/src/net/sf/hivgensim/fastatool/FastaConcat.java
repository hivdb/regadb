package net.sf.hivgensim.fastatool;

import java.io.FileNotFoundException;

public class FastaConcat extends FastaTool{
	
	private String naiveInputFilename;
	private String treatedInputFilename;
	private boolean treated = false;
	
	public FastaConcat(String naiveInputFilename, String treatedInputFilename, String outputFilename) throws FileNotFoundException{
		super(naiveInputFilename,outputFilename);
		this.naiveInputFilename = naiveInputFilename;
		this.treatedInputFilename = treatedInputFilename;
	}

	@Override
	protected void afterProcessing() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void beforeProcessing() {
		// TODO Auto-generated method stub
		
	}
	
	public void processFastaFile() throws FileNotFoundException{
		setInputFile(naiveInputFilename);
		super.processFastaFile();
		treated = true;
		setInputFile(treatedInputFilename);
		super.processFastaFile();
	}
	
	public void close(){
		if(treated){
			super.close();
		}
	}

	@Override
	protected void processSequence(FastaSequence fs) {
		if(treated){
			getOut().println(fs.getId().replace(">", ">T"));			
		}else{
			getOut().println(fs.getId().replace(">", ">N"));			
		}
		getOut().println(fs.getSequence());		
	}
	
	
	
	


}
