package com.pharmadm.custom.rega.queryeditor.port;

public interface QueryStatement {
	public void close();
	public void cancel();
	public void setFetchSize(int size);
	public QueryResult executeQuery(String query);
	public boolean exists();
}
