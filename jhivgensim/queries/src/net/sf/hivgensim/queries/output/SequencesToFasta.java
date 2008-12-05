package net.sf.hivgensim.queries.output;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import net.sf.hivgensim.queries.framework.Query;
import net.sf.hivgensim.queries.framework.QueryOutput;
import net.sf.regadb.db.NtSequence;

public class SequencesToFasta extends QueryOutput<NtSequence> {

	public SequencesToFasta(File file) {
		super(file);
	}

	@Override
	public void generateOutput(Query<NtSequence> query) {
		try {
			PrintStream out = new PrintStream(new FileOutputStream(file));
			for(NtSequence seq : query.getOutputList()){
				out.println(">"+seq.getLabel());
				out.println(seq.getNucleotides());
			}
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
}
