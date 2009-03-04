package net.sf.hivgensim.queries.output;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.List;

import net.sf.hivgensim.queries.framework.DefaultQueryOutput;
import net.sf.hivgensim.queries.framework.SequencePair;

public class SequencePairsToFasta extends DefaultQueryOutput<SequencePair>{

	public SequencePairsToFasta(File file) throws FileNotFoundException {
		super(new PrintStream(file));
	}

	protected void generateOutput(List<SequencePair> pairs) {
		for(SequencePair pair : pairs) {
			getOut().println(">"+pair.getSeq1().getLabel());
			getOut().println(pair.getSeq1().getNucleotides());
			getOut().println(">"+pair.getSeq2().getLabel());
			getOut().println(pair.getSeq2().getNucleotides());
		}
	}

}
