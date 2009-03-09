package net.sf.hivgensim.services;

import java.io.File;

public class Estimate extends AbstractService{

	public Estimate(){
		super("estimate");
	}

	public void run(
			String mutTreatedFilename, 
			String naiveFastaFilename,
			String mutTreatedIdtFilename,
			String mutTreatedStrFilename,
			String mutTreatedVdFilename,
			String wildtypesFilename,
			String doublepositionsFilename,
			String mutagenesisFilename,
			String weightsFilename,
			String bestCftOutFilename,
			String estimateDiagOutFilename
	){

		addUpload("mut_treated.csv", new File(mutTreatedFilename));
		addUpload("naive.fasta", new File(naiveFastaFilename));
		addUpload("mut_treated.idt", new File(mutTreatedIdtFilename));
		addUpload("mut_treated.str", new File(mutTreatedStrFilename));
		addUpload("mut_treated.vd", new File(mutTreatedVdFilename));
		addUpload("wildtypes", new File(wildtypesFilename));
		addUpload("doublepositions", new File(doublepositionsFilename));
		addUpload("mutagenesis", new File(mutagenesisFilename));
		addUpload("weights.csv", new File(weightsFilename));

		addDownload("best.cft", new File(bestCftOutFilename));
		addDownload("estimate.diag", new File(estimateDiagOutFilename));
		
		super.run();
	}
}
