package com.pharmadm.custom.rega.queryeditor.port;

public interface QueryResult {
	public void close();
	public int size();
	public String getColumnName(int index);
	public String getColumnClassName(int index);
	public int getColumnCount();
	
	/**
	 * index starts at 0
	 * @param x
	 * @param y
	 * @return
	 */
	public Object get(int x, int y);
}
