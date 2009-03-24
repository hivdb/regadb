package net.sf.hivgensim.queries.framework;


public abstract class QueryOutput<DataType, OutputType> implements IQuery<DataType>{
	private OutputType out;

	public QueryOutput(OutputType out){
		this.out = out;
	}	
	
	
	public OutputType getOut() {
		return out;
	}
}
