package net.sf.hivgensim.queries.output;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import net.sf.hivgensim.queries.framework.DefaultQueryOutput;
import net.sf.regadb.db.NtSequence;

public class SequencesToCsv extends DefaultQueryOutput<NtSequence> {

	public SequencesToCsv(File file) throws FileNotFoundException{
		super(new PrintStream(file));
	}

	public void process(NtSequence seq) {
			getOut().println(seq.getLabel()+","+seq.getNucleotides());
	}
}
