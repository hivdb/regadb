package net.sf.hivgensim.queries.output;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.List;

import net.sf.hivgensim.queries.framework.DefaultQueryOutput;
import net.sf.regadb.db.NtSequence;

public class SequencesToCsv extends DefaultQueryOutput<NtSequence> {

	public SequencesToCsv(File file) throws FileNotFoundException{
		super(new PrintStream(file));
	}

	protected void generateOutput(List<NtSequence> seqs) {
		for(NtSequence seq : seqs) {
			getOut().println(seq.getLabel()+","+seq.getNucleotides());
		}
	}
}
