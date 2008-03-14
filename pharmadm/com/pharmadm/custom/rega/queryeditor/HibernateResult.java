package com.pharmadm.custom.rega.queryeditor;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.type.Type;

public class HibernateResult implements QueryResult {

	private List list;
	private String[] columnNames;
	private Type[] classNames;
	
	public HibernateResult(List list, String[] columnNames, Type[] classNames) {
		this.list = list;
		this.columnNames = columnNames;
		this.classNames = classNames;
	}
	
	@Override
	public void close() {}

	@Override
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

	@Override
	public int size() {
		return list.size();
	}

	@Override
	public String getColumnClassName(int index) {
		if (columnNames != null) {
			Type t = classNames[index];
			return t.getClass().getName();
		}
		return "";
	}

	@Override
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

	@Override
	public String getColumnName(int index) {
		if (columnNames != null) {
			return columnNames[index];
		}
		return "";
	}
}
