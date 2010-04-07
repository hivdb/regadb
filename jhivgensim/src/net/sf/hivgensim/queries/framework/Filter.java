package net.sf.hivgensim.queries.framework;

import java.util.ArrayList;

public class Filter<Type> extends Query<Type,Type> {
	
	public interface FilterCondition<Type> {		
		public boolean matchesCondition(Type input);		
	}
	
	private ArrayList<FilterCondition<Type>> filters = new ArrayList<FilterCondition<Type>>();
	
	public Filter(FilterCondition<Type> filter, IQuery<Type> nextQuery) {
		super(nextQuery);
		filters.add(filter);
	}
	
	public void addFilter(FilterCondition<Type> filter){
		filters.add(filter);
	}
	
	public void process(Type input) {
		for(FilterCondition<Type> f : filters){
			if(!f.matchesCondition(input)){
				return;
			}		
		}
		getNextQuery().process(input);		
	}	
	
}
