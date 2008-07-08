package com.pharmadm.custom.rega.queryeditor.port.hibernate;

import org.hibernate.ScrollableResults;
import org.hibernate.type.Type;

import com.pharmadm.custom.rega.queryeditor.port.ScrollableQueryResult;

public class HibernateScrollableResult implements ScrollableQueryResult {

	private ScrollableResults results;
	private String[] columnNames;
	private Type[] classNames;

	public HibernateScrollableResult(ScrollableResults results, String[] columnNames, Type[] classNames) {
		this.results = results;
		this.columnNames = columnNames;
		this.classNames = classNames;
		results.next();
	}
	
	
	public void close() {
		results.close();
	}

	public String getColumnClassName(int index) {
		if (classNames != null) {
			Type t = classNames[index];
			return t.getReturnedClass().getName();
		}
		return "";
	}

	public String getColumnName(int index) {
		if (columnNames != null) {
			return columnNames[index];
		}
		return "";
	}


	@Override
	public Object[] get() {
		Object[] res = results.get();
		results.next();
		return res;
	}


	@Override
	public boolean isLast() {
		return results.get() == null;
	}
}
