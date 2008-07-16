package com.pharmadm.custom.rega.queryeditor.port;

public interface ScrollableQueryResult extends Result{
	
	/**
	 * return true when the end of the results is reached
	 * @return
	 */
	public boolean isLast();
}
