package net.sf.hivgensim.queries.framework;

import net.sf.regadb.db.Patient;

public abstract class QueryInput extends PreQuery<Patient> {

	protected QueryInput(IQuery<Patient> nextQuery) {
		super(nextQuery);
	}
	
	public abstract void run();

}
