package net.sf.hivgensim.queries.output;

import java.io.File;

import net.sf.hivgensim.queries.framework.QueryOutput;
import net.sf.hivgensim.queries.framework.SequencePair;

public class SequencePairsToCsv extends QueryOutput<SequencePair>{

	public SequencePairsToCsv(File file) {
		super(file);
	}

	@Override
	protected void generateOutput(SequencePair pair) {
		out.println(pair.getSeq1().getLabel()+","+pair.getSeq2().getLabel());		
	}

}
