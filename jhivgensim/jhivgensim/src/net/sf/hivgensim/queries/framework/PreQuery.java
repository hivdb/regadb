package net.sf.hivgensim.queries.framework;

public abstract class PreQuery<OutputType>{
	
	private IQuery<OutputType> nextQuery;
	
	protected PreQuery(IQuery<OutputType> nextQuery){
		setNextQuery(nextQuery);
	}

	protected IQuery<OutputType> getNextQuery() {
		return nextQuery;
	}

	protected void setNextQuery(IQuery<OutputType> nextQuery) {
		this.nextQuery = nextQuery;
	}
	
	
	
	
	
	
	
}
