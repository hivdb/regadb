package net.sf.hivgensim.queries.framework;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

public abstract class ObjectQueryOutput<DataType> extends QueryOutput<DataType,ObjectOutputStream> {

	
	public ObjectQueryOutput(ObjectOutputStream out) {
		super(out);	
	}

	@Override
	protected void closeOutput() {
		try {
			getOut().flush();
			getOut().close();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}

	@Override
	protected abstract void generateOutput(List<DataType> query);
}
