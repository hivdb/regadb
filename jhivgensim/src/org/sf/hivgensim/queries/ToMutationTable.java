package org.sf.hivgensim.queries;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import net.sf.regadb.db.NtSequence;

public class ToMutationTable extends QueryOutput<NtSequence> {

	public ToMutationTable(File file){
		super(file);
	}

	public void generateOutput(Query<NtSequence> query) {
		//old mutationtable code should be converted and come here
		try {
			PrintStream out = new PrintStream(new FileOutputStream(file));
			for(NtSequence seq : query.getOutputList()){
				out.println(seq.getNtSequenceIi()+","+seq.getNucleotides());
			}
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}

}