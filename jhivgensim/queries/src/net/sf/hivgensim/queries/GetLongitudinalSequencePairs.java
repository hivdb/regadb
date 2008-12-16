package net.sf.hivgensim.queries;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import net.sf.hivgensim.queries.framework.Query;
import net.sf.hivgensim.queries.framework.QueryImpl;
import net.sf.hivgensim.queries.framework.QueryUtils;
import net.sf.hivgensim.queries.framework.SequencePair;
import net.sf.hivgensim.queries.input.FromSnapshot;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.ViralIsolate;

public class GetLongitudinalSequencePairs extends QueryImpl<SequencePair,Patient> {
	
	private String[] drugs = new String[]{};
	
	/*
	 *  if strict is true
	 *  	we get only one pair of viral isolates per patient:
	 *  	the latest naive viral isolate with the latest treated viral isolate
	 *  	(note every sequence from the first viral isolate is combined with
	 *  	 every sequence from the second viral isolate, so we still end up with
	 *  	multiple sequence pairs / patient but those will be removed when we
	 *  	select for the protein. TODO check already here for protein)
	 *  otherwise 
	 *  	we return all pairs:
	 *  	every naive with every treated sequence
	 */
	
	private boolean strict = false;
	
	protected GetLongitudinalSequencePairs(Query<Patient> inputQuery) {
		super(inputQuery);
	}
	
	public GetLongitudinalSequencePairs(Query<Patient> inputQuery,String[] drugs, boolean strict) {
		super(inputQuery);
		this.drugs = drugs;
		this.strict = strict;
	}

	@Override
	public void populateOutputList() {
		GetTreatedSequences exp = new GetTreatedSequences(inputQuery,drugs);
		GetDrugNaiveSequences nai = new GetDrugNaiveSequences(inputQuery,drugs);
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
			if(strict){
				Set<NtSequence> latestNaive = QueryUtils.getLatestNtSequence(naiFromPatient);
				Set<NtSequence> latestTreated = QueryUtils.getLatestNtSequence(expFromPatient);
				outputList.addAll(makeSequencePairs(latestNaive,latestTreated));
			}else{
				outputList.addAll(makeSequencePairs(naiFromPatient,expFromPatient));
			}			
		}
	}
	
	private Set<SequencePair> makeSequencePairs(Set<NtSequence> firstSequences, Set<NtSequence> secondSequences){
		Set<SequencePair> pairs = new HashSet<SequencePair>();
		for(NtSequence seq1 : firstSequences){
			for(NtSequence seq2 : secondSequences){
				pairs.add(new SequencePair(seq1,seq2));
			}
		}
		return pairs;
	}
	
	public static void main(String[] args){
		GetLongitudinalSequencePairs query = new GetLongitudinalSequencePairs(
				new FromSnapshot(new File("/home/gbehey0/queries/stanford.snapshot")),new String[]{"IDV"},false);
		query.populateOutputList();
	}

}
