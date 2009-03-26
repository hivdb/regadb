package net.sf.hivgensim.services;

import java.io.File;

public class BnLearner extends AbstractService {

	public BnLearner() {
		super("bnlearner");
	}
	
	public void run(
			String vdFilename,
			String idtFilename,
			String strFilename
			){		
		addUpload("mut_treated.vd",new File(vdFilename));
		addUpload("mut_treated.idt",new File(idtFilename));
		addDownload("mut_treated.str",new File(strFilename));
		super.run();
	}
	
}
