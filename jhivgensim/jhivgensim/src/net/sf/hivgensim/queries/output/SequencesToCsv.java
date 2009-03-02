package net.sf.hivgensim.queries.output;

import java.io.File;

import net.sf.hivgensim.queries.framework.QueryOutput;
import net.sf.regadb.db.NtSequence;

public class SequencesToCsv extends QueryOutput<NtSequence> {

	public SequencesToCsv(File file){
		super(file);
	}

	public void generateOutput(NtSequence seq) {

		out.println(seq.getLabel()+","+seq.getNucleotides());

	}

}
