package net.sf.hivgensim.queries.framework;

import java.util.List;

public abstract class QueryOutput<DataType, OutputType> {
	private OutputType out;

	public QueryOutput(OutputType out){
		this.out = out;
	}
	
	protected abstract void generateOutput(List<DataType> query);
	
	protected abstract void closeOutput();
	
	public OutputType getOut() {
		return out;
	}
}
