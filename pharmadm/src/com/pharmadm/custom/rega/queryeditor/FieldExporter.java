package com.pharmadm.custom.rega.queryeditor;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public abstract class FieldExporter implements Serializable {
	private String variableName;
	private String[] columns;
	private boolean[] selected;
	
	public FieldExporter(String variableName){
		this.variableName = variableName;
	}
	
	public String getVariableName(){
		return variableName;
	}
	
	public String[] getColumns(){
		return columns;
	}
	
	public void setColumns(String columns[]){
		this.columns = columns;
		selected = new boolean[columns.length];
	}
	
	public String getColumn(int i){
		return columns[i];
	}

	public void setColumn(int i, String name){
		columns[i] = name;
	}
	
	public boolean isSelected(int i){
		return selected[i];
	}
	
	public void setSelected(int i, boolean select){
		selected[i] = select;
	}
	
	public boolean toggleSelected(int i){
		selected[i] = !selected[i];
		return selected[i];
	}
	
	public abstract String getValue(Object o, int i);
	
	public String[] getValues(Object o){
		String[] values = new String[getColumns().length];
		for(int i = 0; i < values.length; ++i){
			values[i] = getValue(o,i);
		}
		return values;
	}
	
	public Collection<String> getSelectedColumns(){
		List<String> cols = new LinkedList<String>();
		for(int i=0; i<getColumns().length; ++i){
			if(isSelected(i))
				cols.add(getColumn(i));
		}
		
		return cols;
	}
	
	public Collection<String> getSelectedValues(Object o){
		List<String> values = new LinkedList<String>();
		for(int i=0; i<getColumns().length; ++i){
			if(isSelected(i))
				values.add(getValue(o,i));
		}
		
		return values;
	}
}
