package net.sf.regadb.ui.form.importTool;

import net.sf.regadb.ui.form.importTool.data.Rule;
import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WString;

public abstract class DetailsForm extends WContainerWidget {
	private WString title;
	
	public DetailsForm(WString title) {
		this.title = title;
	}
	
	public WString getTitle() {
		return title;
	}
	
	public abstract WString validate();
	
	public abstract void save(Rule rule);
	
	public abstract void save();
	
	public abstract void init();
}
