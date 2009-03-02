package net.sf.hivgensim.queries.output;

import java.io.File;

import net.sf.hivgensim.queries.framework.QueryOutput;
import net.sf.regadb.db.NtSequence;

public class SequencesToFasta extends QueryOutput<NtSequence> {

	public SequencesToFasta(File file) {
		super(file);
	}

	@Override
	public void generateOutput(NtSequence seq) {
		out.println(">"+seq.getLabel());
		out.println(seq.getNucleotides());

	}
}
