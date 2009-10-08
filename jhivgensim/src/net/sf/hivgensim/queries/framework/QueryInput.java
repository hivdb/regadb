package net.sf.hivgensim.queries.framework;

import net.sf.regadb.db.Patient;

public abstract class QueryInput extends PreQuery<Patient> {
	
	public QueryInput() {
		super(null);
	}
	
	protected QueryInput(IQuery<Patient> nextQuery) {
		super(nextQuery);
	}
	
	public abstract void run();
	
	public void setNextQuery(IQuery<Patient> nextQuery){
		super.setNextQuery(nextQuery);
	}
}
