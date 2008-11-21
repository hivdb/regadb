package net.sf.hivgensim.queries.framework;

import java.io.File;

import net.sf.hivgensim.queries.GetAllSequences;
import net.sf.hivgensim.queries.GetExperiencedSequences;
import net.sf.hivgensim.queries.GetNaiveSequences;
import net.sf.hivgensim.queries.GetSequenceSummary;
import net.sf.hivgensim.queries.input.FromSnapshot;
import net.sf.hivgensim.queries.output.ToFasta;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Patient;


public class Proto {
	
	public static void main(String[] args){
		
		//AZT+3TC
		
		//naive
		//grep '>' naiveseq.fasta | sed 's/>//' | sort -g > naive.ids
//		long start = System.currentTimeMillis();
		QueryInput input = new FromSnapshot(new File("/home/gbehey0/queries/stanford.snapshot"));
//		Query<NtSequence> qn = new GetNaiveSequences(input,new String[]{"NRTI"});
//		File outputFile = new File("/home/gbehey0/stanford/queries/final/gn");
//		QueryOutput<NtSequence> output = new ToFasta(outputFile);
//		output.generateOutput(qn);
//		long stop = System.currentTimeMillis();
//		System.out.println("time: "+(stop-start)+" ms");
		
		//experienced
		//grep '>' expseq.fasta | sed 's/>//' | sort -g > exp.ids
//		start = System.currentTimeMillis();
//		Query<NtSequence> qe = new GetExperiencedSequences(input,new String[]{"AZT","3TC"});
//		QueryOutput<NtSequence> outpute = new ToFasta(new File("/home/gbehey0/stanford/queries/ge"));
//		outpute.generateOutput(qe);
//		stop = System.currentTimeMillis();
//		System.out.println("time: "+(stop-start)+" ms");

		QueryOutput<Patient> qo = new GetSequenceSummary(new File("/home/gbehey0/stanford/summaryseq"));
		qo.generateOutput(input);
//		
//		Query<NtSequence> allSequences = new GetAllSequences(input);
//		(new ToFasta(new File("/home/gbehey0/stanford/allseqs"))).generateOutput(allSequences);
		
		
	}

}
