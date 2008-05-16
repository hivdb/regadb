package com.pharmadm.custom.rega.queryeditor.constant;

import java.io.Serializable;

public class SuggestedValuesOption implements Serializable{
	private Object option, value;
	public SuggestedValuesOption(){}
	
	public SuggestedValuesOption(Object value, Object option) {
		this.option = option;
		this.value = value;
	}
	
	public SuggestedValuesOption(Object value) {
		this.option = value;
		this.value = value;
	}
	
	public Object getOption() {
		return option;
	}
	
	public void setOption(Object option) {
		this.option = option;
	}
	
	public void setValue(Object value) {
		this.value = value;
	}
	
	public Object getValue() {
		return value;
	}

	public boolean equals(Object o) {
		SuggestedValuesOption opt = (SuggestedValuesOption) o;
		return opt.getOption().equals(option);
	}
	
	public String toString() {
		return option.toString() + "," + value.toString();
	}
}
