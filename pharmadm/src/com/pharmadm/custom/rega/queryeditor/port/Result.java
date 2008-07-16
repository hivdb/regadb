package com.pharmadm.custom.rega.queryeditor.port;

public interface Result {
	public void close();
	public String getColumnName(int index);
	public String getColumnClassName(int index);

	/**
	 * gets the next row of objects
	 * @return
	 */
	public Object[] get();
}
