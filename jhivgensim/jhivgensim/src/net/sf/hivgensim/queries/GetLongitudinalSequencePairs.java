package net.sf.hivgensim.queries;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import net.sf.hivgensim.queries.framework.IQuery;
import net.sf.hivgensim.queries.framework.Query;
import net.sf.hivgensim.queries.framework.QueryUtils;
import net.sf.hivgensim.queries.framework.SequencePair;
import net.sf.hivgensim.queries.output.ToObjectList;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Patient;

public class GetLongitudinalSequencePairs extends Query<Patient,SequencePair> {

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

	protected GetLongitudinalSequencePairs(IQuery<SequencePair> nextQuery) {
		super(nextQuery);
	}

	public GetLongitudinalSequencePairs(String[] drugs, boolean strict, IQuery<SequencePair> nextQuery) {
		super(nextQuery);
		this.drugs = drugs;
		this.strict = strict;
	}

	@Override
	public void process(Patient p) {
		ToObjectList<NtSequence> treated = new ToObjectList<NtSequence>();
		new GetTreatedSequences(drugs,treated).process(p);
		ToObjectList<NtSequence> naive = new ToObjectList<NtSequence>();
		new GetDrugNaiveSequences(drugs,naive).process(p);

		if(strict){
			Set<NtSequence> latestNaive = QueryUtils.getLatestNtSequences(naive.getList());
			Set<NtSequence> latestTreated = QueryUtils.getLatestNtSequences(treated.getList());
			if(latestNaive == null || latestTreated == null){		
				return;
			}
			for(SequencePair pair : makeSequencePairs(p,latestNaive,latestTreated)){
				getNextQuery().process(pair);
			}
		}else{
			for(SequencePair pair : makeSequencePairs(p,naive.getList(),treated.getList())){
				getNextQuery().process(pair);
			}
		}
	}

	private Set<SequencePair> makeSequencePairs(Patient p, Collection<NtSequence> firstSequences, Collection<NtSequence> secondSequences){
		Set<SequencePair> pairs = new HashSet<SequencePair>();
		for(NtSequence seq1 : firstSequences){
			for(NtSequence seq2 : secondSequences){
				pairs.add(new SequencePair(p,seq1,seq2,QueryUtils.therapyRegimenInBetween(p, seq1, seq2)));				
			}
		}
		return pairs;
	}
}
