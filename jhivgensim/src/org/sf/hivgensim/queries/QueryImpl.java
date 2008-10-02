package org.sf.hivgensim.queries;

import java.util.ArrayList;
import java.util.List;

public abstract class QueryImpl<T,I> implements Query<T>{
	
	protected Query<I> inputQuery;
	protected List<T> outputList;
	protected abstract void populateOutputList();
	
	protected QueryImpl(){
		
	}	
	
	public List<T> getOutputList(){
		if(outputList == null){
			outputList = new ArrayList<T>();
			populateOutputList();
		}
		return outputList;
	}
	
	
	
}
