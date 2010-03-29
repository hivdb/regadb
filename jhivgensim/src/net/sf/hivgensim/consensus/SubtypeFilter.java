package net.sf.hivgensim.consensus;

import net.sf.hivgensim.queries.framework.IQuery;
import net.sf.hivgensim.queries.framework.Query;
import net.sf.hivgensim.queries.framework.utils.NtSequenceUtils;
import net.sf.regadb.db.NtSequence;

public class SubtypeFilter extends Query<NtSequence, NtSequence> {

	private String subtype = "HIV-1 Subtype B";
	
	protected SubtypeFilter(IQuery<NtSequence> nextQuery) {
		super(nextQuery);
	}

	@Override
	public void process(NtSequence input) {
		if(NtSequenceUtils.getSubtype(input).equals(subtype)){
			getNextQuery().process(input);
		}		
	}

}
