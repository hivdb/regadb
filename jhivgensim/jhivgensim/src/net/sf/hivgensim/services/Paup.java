package net.sf.hivgensim.services;

import java.io.File;

public class Paup extends AbstractService {
	
	public Paup(){
		super("paup");
	}
	
	public void run(String inputFilename, String outputFilename){
		addUpload("phylo.nex",new File(inputFilename));
		addDownload("tree.phy",new File(outputFilename));
		super.run();
	}

}
