package net.sf.hivgensim.queries.output;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;

import net.sf.hivgensim.queries.framework.DefaultQueryOutput;
import net.sf.regadb.db.NtSequence;

public class SequencesToCsv extends DefaultQueryOutput<NtSequence> {
	
	private SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
	
	public SequencesToCsv(File file) throws FileNotFoundException{
		super(new PrintStream(file));
	}

	public void process(NtSequence seq) {
			getOut().println(seq.getNtSequenceIi()+","+sdf.format(seq.getViralIsolate().getSampleDate()));
	}
}
