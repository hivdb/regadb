package com.pharmadm.custom.rega.queryeditor.port;

public interface ScrollableQueryResult {
	public void close();
	public String getColumnName(int index);
	public String getColumnClassName(int index);
	
	/**
	 * return true when the end of the results is reached
	 * @return
	 */
	public boolean isLast();

	/**
	 * gets the next row of objects
	 * @return
	 */
	public Object[] get();
}
