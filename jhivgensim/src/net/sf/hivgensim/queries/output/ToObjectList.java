package net.sf.hivgensim.queries.output;

import java.util.ArrayList;
import java.util.List;

import net.sf.hivgensim.queries.framework.QueryOutput;
import net.sf.regadb.db.NtSequence;

public class ToObjectList<DataType> extends QueryOutput<DataType,List<DataType>> {

	public ToObjectList() {
		super(new ArrayList<DataType>());
	}

	public List<DataType> getList(){
		return getOut();
	}
	
	public void process(DataType input) {
		getOut().add(input);
	}

	public void close() {		
	}

}
