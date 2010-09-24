package net.sf.regadb.ui.form.query.custom;

import eu.webtoolkit.jwt.WWidget;

public interface Parameter {
	public abstract WWidget getWidget();
	public String getDescription();
	public boolean isMandatory();
	public boolean isValid();
}
