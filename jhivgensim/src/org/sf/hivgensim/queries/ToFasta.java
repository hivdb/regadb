package org.sf.hivgensim.queries;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import net.sf.regadb.db.NtSequence;

public class ToFasta extends QueryOutput<NtSequence> {

	public ToFasta(File file) {
		super(file);
	}

	@Override
	public void generateOutput(Query<NtSequence> query) {
		try {
			PrintStream out = new PrintStream(new FileOutputStream(file));
			System.out.println(query.getOutputList().size());
			for(NtSequence seq : query.getOutputList()){
				out.println(">" + seq.getNtSequenceIi());
				out.println(seq.getNucleotides());
			}
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	public static void main(String[] args){
		ToFasta out = new ToFasta(new File("/home/gbehey0/queries/test.fasta"));
		out.generateOutput(new GetLatestSequencePerPatient(new FromDatabase("gbehey0","bla123")));
	}

}
