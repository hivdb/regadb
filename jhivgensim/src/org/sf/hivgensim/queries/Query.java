package org.sf.hivgensim.queries;

import java.util.List;

public interface Query<T> {
	
	public abstract List<T> getOutputList();

}
