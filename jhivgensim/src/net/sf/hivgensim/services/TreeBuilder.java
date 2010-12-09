package net.sf.hivgensim.services;

import java.io.File;

public class TreeBuilder extends AbstractService {

	protected TreeBuilder() {
		super("regadb-tree");		
	}

	public void run(String inputFilename, String outputFilename){
		addUpload("sequences.fasta",new File(inputFilename));
		addDownload("tree.phy",new File(outputFilename));
		super.run();
	}
	
}
