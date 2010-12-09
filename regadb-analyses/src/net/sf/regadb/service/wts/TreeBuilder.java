package net.sf.regadb.service.wts;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import net.sf.regadb.util.settings.RegaDBSettings;

public class TreeBuilder extends AbstractService {

	private String sequences;
	private String tree;

	public TreeBuilder(String sequences) {
		this.sequences = sequences;
	}

	protected void init() {
		setService("regadb-tree");
		getInputs().put("sequences.fasta", sequences);
		getOutputs().put("tree.phy", "");
	}

	protected void processResults() throws ServiceException {
		tree = getOutputs().get("tree.phy");
		System.out.println(tree);
	}

	public static void main(String[] args) throws ServiceException, FileNotFoundException {
		if(args.length != 1) {
			System.err.println("TreeBuilder aligned_sequences.fasta");
			System.exit(1);
		}
		RegaDBSettings.createInstance();
		Scanner s = new Scanner(new File(args[0]));
		StringBuilder sb = new StringBuilder();
		while(s.hasNextLine()) {
			sb.append(s.nextLine()+"\n");
		}		
		new TreeBuilder(sb.toString()).launch();
	}

}
