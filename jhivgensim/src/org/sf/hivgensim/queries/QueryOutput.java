package org.sf.hivgensim.queries;

import java.io.File;

public abstract class QueryOutput<T> {
	
	protected File file;
	protected Query<T> query;
	
	protected QueryOutput(){
		
	}
	
	public QueryOutput(QueryImpl<T> query, File file){
		this.file = file;
		this.query = query;
	}
	
	public abstract void generateOutput();

}
