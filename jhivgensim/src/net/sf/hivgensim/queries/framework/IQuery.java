package net.sf.hivgensim.queries.framework;

public interface IQuery<InputType> {
	
	public abstract void process(InputType input);
	public abstract void close();

}
