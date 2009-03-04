package net.sf.hivgensim.queries.output;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.List;

import net.sf.hivgensim.queries.framework.DefaultQueryOutput;
import net.sf.hivgensim.queries.framework.SequencePair;

public class SequencePairsToCsv extends DefaultQueryOutput<SequencePair>{

	public SequencePairsToCsv(File f) throws FileNotFoundException {
		super(new PrintStream(f));
	}

	protected void generateOutput(List<SequencePair> pairs) {
		for(SequencePair pair : pairs) {
			this.getOut().println(pair.getSeq1().getLabel()+","+pair.getSeq2().getLabel());		
		}
	}
}
