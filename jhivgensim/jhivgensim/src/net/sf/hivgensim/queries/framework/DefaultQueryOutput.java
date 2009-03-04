package net.sf.hivgensim.queries.framework;

import java.io.PrintStream;
import java.util.List;

public abstract class DefaultQueryOutput<DataType> extends QueryOutput<DataType, PrintStream> {
	public DefaultQueryOutput(PrintStream out) {
		super(out);
	}
	
	public void closeOutput() {
		this.getOut().flush();
		this.getOut().close();
	}
	
	protected abstract void generateOutput(List<DataType> list);
	
	public void output(List<DataType> list) {
		generateOutput(list);
		closeOutput();
	}
}
