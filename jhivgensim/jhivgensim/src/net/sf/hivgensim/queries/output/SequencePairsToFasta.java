package net.sf.hivgensim.queries.output;

import java.io.File;

import net.sf.hivgensim.queries.framework.QueryOutput;
import net.sf.hivgensim.queries.framework.SequencePair;

public class SequencePairsToFasta extends QueryOutput<SequencePair>{

	public SequencePairsToFasta(File file) {
		super(file);
	}

	@Override
	public void generateOutput(SequencePair pair) {
		out.println(">"+pair.getSeq1().getLabel());
		out.println(pair.getSeq1().getNucleotides());
		out.println(">"+pair.getSeq2().getLabel());
		out.println(pair.getSeq2().getNucleotides());		

	}

}
