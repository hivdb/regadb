package net.sf.hivgensim.queries;

import java.util.List;

import net.sf.hivgensim.queries.framework.IQuery;
import net.sf.hivgensim.queries.framework.Query;
import net.sf.regadb.db.NtSequence;

public class RemoveSequencesFromLongitudinalPair extends Query<NtSequence,NtSequence> {

	private List<String> pairs;

	public RemoveSequencesFromLongitudinalPair(List<String> pairs, IQuery<NtSequence> nextQuery) {
		super(nextQuery);
		this.pairs = pairs;
	}

	@Override
	public void process(NtSequence input) {
		if(!pairs.contains(input.getNtSequenceIi())){
			getNextQuery().process(input);
		}		
	}

}
