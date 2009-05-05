package net.sf.hivgensim.queries;

import net.sf.hivgensim.queries.framework.IQuery;
import net.sf.hivgensim.queries.framework.Query;

public class BasicSampler<T> extends Query<T,T>{
	
	public float probability;
	
	public BasicSampler(float prob, IQuery<T> nextQuery) {
		super(nextQuery);
		this.probability = prob;
	}
	

	@Override
	public void process(T input) {
		if(Math.random() < probability){
			getNextQuery().process(input);
		}		
	}
	
	

}
