package net.sf.hivgensim.queries.output;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;


import net.sf.hivgensim.queries.framework.Query;
import net.sf.hivgensim.queries.framework.QueryOutput;
import net.sf.regadb.db.NtSequence;

public class SequencesToCsv extends QueryOutput<NtSequence> {

	public SequencesToCsv(File file){
		super(file);
	}

	public void generateOutput(Query<NtSequence> query) {
		try {
			PrintStream out = new PrintStream(new FileOutputStream(file));
			for(NtSequence seq : query.getOutputList()){
				out.println(seq.getLabel()+","+seq.getNucleotides());
			}
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}

}
