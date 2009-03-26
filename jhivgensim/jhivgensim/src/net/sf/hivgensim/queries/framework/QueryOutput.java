package net.sf.hivgensim.queries.framework;

import net.sf.hivgensim.queries.output.ToObjectList;


public abstract class QueryOutput<DataType, OutputType> implements IQuery<DataType>{
	private OutputType out;

	public QueryOutput(OutputType out){
		this.out = out;
	}	
	
	public void output(ToObjectList<DataType> list){
		for(DataType t : list.getList()){
			process(t);
		}
		close();
	}
	
	public OutputType getOut() {
		return out;
	}
}
