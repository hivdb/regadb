package net.sf.regadb.ui.form.query.custom;

import eu.webtoolkit.jwt.WWidget;

public abstract class BasicParameter implements Parameter {
	private String description;
	private boolean mandatory;
	
	public BasicParameter(String description, boolean mandatory){
		setDescription(description);
		setMandatory(mandatory);
	}

	public abstract WWidget getWidget();
	
	public String getDescription(){
		return description;
	}
	public void setDescription(String description){
		this.description = description;
	}
	
	public boolean isMandatory(){
		return mandatory;
	}
	public void setMandatory(boolean mandatory){
		this.mandatory = mandatory;
	}
}
