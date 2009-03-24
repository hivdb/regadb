package net.sf.hivgensim.queries.framework;

import java.io.PrintStream;

public abstract class DefaultQueryOutput<DataType> extends QueryOutput<DataType, PrintStream> {
	
	public DefaultQueryOutput(PrintStream out) {
		super(out);
	}
	
	public void close() {
		this.getOut().flush();
		this.getOut().close();
	}
}
