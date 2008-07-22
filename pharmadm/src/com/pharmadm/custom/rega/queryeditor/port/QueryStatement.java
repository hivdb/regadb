package com.pharmadm.custom.rega.queryeditor.port;

import java.util.HashMap;

public interface QueryStatement {
	public void close();
	public void cancel();
	public void setFetchSize(int size);
	/**
	 * execute as a scrollable read only query
	 * this implementation is extremely fast but has limited features
	 * @param query
	 * @return
	 */
	public ScrollableQueryResult executeScrollableQuery(String query, HashMap<String, Object>  preparedConstantMap);
	
	/**
	 * Present a feature rich result but
	 * possibly slower than executeScrollableQuery
	 * @param query
	 * @return
	 */
	public QueryResult executeQuery(String query, HashMap<String, Object>  preparedConstantMap);
	public boolean exists();
}
