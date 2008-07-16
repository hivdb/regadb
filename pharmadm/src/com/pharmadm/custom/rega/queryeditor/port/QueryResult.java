package com.pharmadm.custom.rega.queryeditor.port;

public interface QueryResult extends Result{
	public int size();
	public int getColumnCount();
	
	/**
	 * index starts at 0
	 * @param x
	 * @param y
	 * @return
	 */
	public Object get(int x, int y);

	/**
	 * gets the next row of objects
	 * @return
	 */
	public Object[] get();
}
