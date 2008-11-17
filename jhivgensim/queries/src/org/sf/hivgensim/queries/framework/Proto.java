package org.sf.hivgensim.queries.framework;

import java.io.File;

import org.sf.hivgensim.queries.GetLatestSequencePerPatient;
import org.sf.hivgensim.queries.GetNaivePatients;
import org.sf.hivgensim.queries.GetPatientsFromDataset;
import org.sf.hivgensim.queries.input.FromDatabase;
import org.sf.hivgensim.queries.output.ToFasta;

import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Patient;

public class Proto {
	
	public static void main(String[] args){
		long start = System.currentTimeMillis();
		QueryInput input = new FromDatabase("gbehey0","bla123"); 
//		QueryInput input = new FromSnapshot(new File("/home/gbehey0/queries/auto.snapshot"));
		Query<Patient> q0 = new GetPatientsFromDataset(input, "stanfordB");
		Query<Patient> q1 = new GetNaivePatients(q0,new String[]{"PI"});
		Query<NtSequence> q2 = new GetLatestSequencePerPatient(q1);
//		Query<NtSequence> q3 = new GetAllSequences(input);
		File outputFile = new File("/home/gbehey0/queries/result");
		QueryOutput<NtSequence> output = new ToFasta(outputFile);
		output.generateOutput(q2);
		long stop = System.currentTimeMillis();
		System.out.println("time"+(stop-start));
		

	}

}
