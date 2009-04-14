package net.sf.hivgensim.queries;

import java.util.List;

import net.sf.hivgensim.queries.framework.IQuery;
import net.sf.hivgensim.queries.framework.Query;
import net.sf.hivgensim.queries.framework.SequencePair;
import net.sf.regadb.db.NtSequence;

public class RemoveSequencesFromLongitudinalPair extends Query<NtSequence,NtSequence> {

	private List<SequencePair> pairs;
	
	public RemoveSequencesFromLongitudinalPair(List<SequencePair> pairs, IQuery<NtSequence> nextQuery) {
		super(nextQuery);
		this.pairs = pairs;
	}

	@Override
	public void process(NtSequence input) {
		for(SequencePair p : pairs){
			if(p.getSeq1().getNtSequenceIi() == input.getNtSequenceIi() 
				|| p.getSeq2().getNtSequenceIi() == input.getNtSequenceIi()){
				continue;
			}
			getNextQuery().process(input);
		}
		
	}

}
