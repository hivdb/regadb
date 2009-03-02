package net.sf.hivgensim.queries.framework;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

public abstract class QueryOutput<T> {
	
	protected PrintStream out;
		
	public QueryOutput(File file){
		try {
			out = new PrintStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}		
	}
	
	public void generateOutput(Query<T> query){
		for(T t: query.getOutputList()){
			generateOutput(t);
			out.flush();
		}
		out.close();
	}
	
	protected abstract void generateOutput(T t);

}
