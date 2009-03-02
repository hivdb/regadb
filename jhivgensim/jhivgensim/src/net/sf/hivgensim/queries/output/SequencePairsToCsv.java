package net.sf.hivgensim.queries.output;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import net.sf.hivgensim.queries.framework.Query;
import net.sf.hivgensim.queries.framework.QueryOutput;
import net.sf.hivgensim.queries.framework.SequencePair;

public class SequencePairsToCsv extends QueryOutput<SequencePair>{

	public SequencePairsToCsv(File file) {
		super(file);
	}

	@Override
	public void generateOutput(Query<SequencePair> query) {
		try {
			PrintStream out = new PrintStream(new FileOutputStream(file));
			for(SequencePair pair : query.getOutputList()){
				out.println(pair.getSeq1().getLabel()+","+pair.getSeq2().getLabel());
			}
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}

}
