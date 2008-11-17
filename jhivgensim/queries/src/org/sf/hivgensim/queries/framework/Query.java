package org.sf.hivgensim.queries.framework;

import java.util.List;

public interface Query<T> {
	
	public abstract List<T> getOutputList();

}
