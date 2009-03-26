package net.sf.hivgensim.queries;

import net.sf.hivgensim.queries.framework.IQuery;
import net.sf.hivgensim.queries.framework.Query;
import net.sf.hivgensim.queries.framework.SequencePair;

public class CheckForRegion extends Query<SequencePair, SequencePair> {

	private String organism;
	private String protein;
	
	protected CheckForRegion(IQuery<SequencePair> nextQuery) {
		super(nextQuery);	
	}
	
	public CheckForRegion(String organism, String protein, IQuery<SequencePair> nextQuery){
		super(nextQuery);
		this.organism = organism;
		this.protein = protein;
	}

	@Override
	public void process(SequencePair input) {
		if(input.inRegion(organism, protein)){
			getNextQuery().process(input);
		}		
	}

}
