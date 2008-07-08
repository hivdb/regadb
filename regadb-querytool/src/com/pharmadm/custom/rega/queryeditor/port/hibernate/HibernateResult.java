package com.pharmadm.custom.rega.queryeditor.port.hibernate;

import org.hibernate.ScrollableResults;
import org.hibernate.type.Type;

import com.pharmadm.custom.rega.queryeditor.port.QueryResult;

public class HibernateResult implements QueryResult {

	private ScrollableResults results;
	private String[] columnNames;
	private Type[] classNames;

	public HibernateResult(ScrollableResults results, String[] columnNames, Type[] classNames) {
		this.results = results;
		this.columnNames = columnNames;
		this.classNames = classNames;
		results.next();
	}
	
	
	public void close() {
		results.close();
	}

	public Object get(int row, int column) {
		results.setRowNumber(row);
		return results.get(column);

	}

	public int size() {
		results.last();
		return results.getRowNumber() + 1;
	}

	public String getColumnClassName(int index) {
		if (classNames != null) {
			Type t = classNames[index];
			return t.getReturnedClass().getName();
		}
		return "";
	}

	public int getColumnCount() {
		System.err.println(results);
		Object[] o = results.get();
		if (o == null) {
			return 0;
		}
		return o.length;
	}

	public String getColumnName(int index) {
		if (columnNames != null) {
			return columnNames[index];
		}
		return "";
	}

	public Object[] get() {
		Object[] res =  results.get();
		results.next();
		return res;
	}
}
