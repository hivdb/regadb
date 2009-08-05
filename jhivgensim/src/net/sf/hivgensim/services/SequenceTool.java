package net.sf.hivgensim.services;

import java.io.File;

public class SequenceTool extends AbstractService{
	
	public SequenceTool(){
		super("sequencetool-align");
	}
	
	public void run(String referenceFileName, String sequencesFileName, String outputFileName){
		addUpload("reference.fasta",new File(referenceFileName));
		addUpload("sequences.fasta",new File(sequencesFileName));
		addDownload("aligned.sequences.fasta",new File(outputFileName));
		run();
	}	
}
