package net.sf.regadb.service.wts;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import net.sf.regadb.util.settings.RegaDBSettings;

public class NucleotideAlignment extends AbstractService {

	private String sequences;
	private String organism;
	private double signalThreshold;
	
	private String alignedSequences;
	
	public NucleotideAlignment(String sequences, String organism, double signalThreshold) {
		this.sequences = sequences;
		this.organism = organism;
		this.signalThreshold = signalThreshold;
	}

	protected void init() {
		setService("regadb-nt-align");
		getInputs().put("nt_sequences", sequences);
		getInputs().put("organism_name", organism);
		getInputs().put("min_signal", ""+signalThreshold);
		getOutputs().put("aligned_sequences", "");
	}

	protected void processResults() throws ServiceException {
		alignedSequences = getOutputs().get("aligned_sequences");		
	}
	
	public String getAlignedSequences() {
		return alignedSequences;
	}
	
	public static void main(String[] args) throws ServiceException, FileNotFoundException {
		if(args.length != 3) {
			System.err.println("TreeBuilder sequences.fasta organism signalThreshold");
			System.exit(1);
		}
		RegaDBSettings.createInstance();
		Scanner s = new Scanner(new File(args[0]));
		StringBuilder sb = new StringBuilder();
		while(s.hasNextLine()) {
			sb.append(s.nextLine()+"\n");
		}
		String organism = args[1];
		String signalThreshold = args[2];
		
		NucleotideAlignment test = new NucleotideAlignment(sb.toString(), organism, Double.parseDouble(signalThreshold));
		test.launch();
		new TreeBuilder(test.alignedSequences).launch();
	}

}
