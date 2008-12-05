package net.sf.hivgensim.queries;

import java.util.HashSet;
import java.util.Set;

import net.sf.hivgensim.queries.framework.Query;
import net.sf.hivgensim.queries.framework.QueryImpl;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.ViralIsolate;

public class GetLongitudinalSequencePairs extends QueryImpl<NtSequence,Patient> {

	public GetLongitudinalSequencePairs(Query<Patient> inputQuery) {
		super(inputQuery);
	}

	@Override
	public void populateOutputList() {
		GetExperiencedSequences exp = new GetExperiencedSequences(inputQuery,new String[]{"IDV"});
		GetNaiveSequences nai = new GetNaiveSequences(inputQuery,new String[]{"PI"}); //only for IDV?
		for(Patient p : inputQuery.getOutputList()){
			Set<NtSequence> expFromPatient = new HashSet<NtSequence>();
			Set<NtSequence> naiFromPatient = new HashSet<NtSequence>();
			for(ViralIsolate vi : p.getViralIsolates()){
				for(NtSequence seq : vi.getNtSequences()){
					if(exp.getOutputList().contains(seq)){
						expFromPatient.add(seq);					
					}else if(nai.getOutputList().contains(seq)){
						naiFromPatient.add(seq);					
					}
				}
			}
			for(NtSequence seq1 : naiFromPatient){
				for(NtSequence seq2 : expFromPatient){
					System.out.println(p.getPatientId()+","+
							seq1.getViralIsolate().getSampleId()+","+seq1.getViralIsolate().getSampleDate()+","+
							seq1.getLabel()+","+	
							seq2.getViralIsolate().getSampleId()+","+seq2.getViralIsolate().getSampleDate()+","+
							seq2.getLabel());
					
				}
			}
		}


	}

}
