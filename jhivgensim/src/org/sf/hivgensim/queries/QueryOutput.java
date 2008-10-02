package org.sf.hivgensim.queries;

import java.io.File;

public abstract class QueryOutput<T> {
	
	protected File file;
		
	protected QueryOutput(){
		
	}
	
	public QueryOutput(File file){
		this.file = file;		
	}
	
	public abstract void generateOutput(Query<T> query);

}
