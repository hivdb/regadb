package net.sf.hivgensim.queries.framework;

public abstract class Query<InputType,OutputType> extends PreQuery<OutputType> implements IQuery<InputType>{

	protected Query(IQuery<OutputType> nextQuery) {
		super(nextQuery);		
	}
	
	public abstract void process(InputType input);
	
	public void close(){
		System.err.println("Qclose");
		getNextQuery().close();
	}

}
