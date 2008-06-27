package com.pharmadm.custom.rega.queryeditor.port.hibernate;

import java.util.List;

import org.hibernate.type.Type;

import com.pharmadm.custom.rega.queryeditor.port.QueryResult;

public class HibernateResult implements QueryResult {

	private List<Object> list;
	private String[] columnNames;
	private Type[] classNames;
	
	public HibernateResult(List<Object> list, String[] columnNames, Type[] classNames) {
		this.list = list;
		this.columnNames = columnNames;
		this.classNames = classNames;
	}
	
	public void close() {}

	public Object get(int row, int column) {
		if (list.size() > 0) {
			if (list.get(0) instanceof Object[]) {
				return ((Object[]) list.get(row))[column];
			}
			else {
				return list.get(row);
			}
		}
		return null;
	}

	public int size() {
		return list.size();
	}

	public String getColumnClassName(int index) {
		if (classNames != null) {
			Type t = classNames[index];
			return t.getReturnedClass().getName();
		}
		return "";
	}

	public int getColumnCount() {
		if (list.size() > 0) {
			if (list.get(0) instanceof Object[]) {
				return ((Object[]) list.get(0)).length;
			}
			else {
				return 1;
			}
		}
		return 0;
	}

	public String getColumnName(int index) {
		if (columnNames != null) {
			return columnNames[index];
		}
		return "";
	}
}
