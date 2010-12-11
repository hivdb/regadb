package net.sf.regadb.ui.form.singlePatient;

import eu.webtoolkit.jwt.WContainerWidget;

public abstract class TabForm extends WContainerWidget {
	
	private boolean initialized = false;

	public TabForm(){
		super();
	}
	
	protected abstract void initialize();
	
	public boolean isInitialized(){
		return initialized;
	}
	
	@Override
	public void show(){
		if(!initialized){
			initialize();
			initialized = true;
		}
		super.show();
	}
}
