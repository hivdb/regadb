package org.sf.hivgensim.queries;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import net.sf.regadb.db.Patient;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.ViralIsolate;

public class ToMutationTable extends QueryOutput<Patient> {

	public ToMutationTable(QueryImpl<Patient> query, File file){
		super(query,file);
	}

	@Override
	public void generateOutput() {
		//old mutationtable code should be converted and come here
		try {
			PrintStream out = new PrintStream(new FileOutputStream(file));
			for(Patient p : query.getOutputList()){
				for(ViralIsolate vi : p.getViralIsolates()){
					for(NtSequence seq : vi.getNtSequences()){
						out.println(p.getPatientId()+","+seq.getNucleotides());
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
