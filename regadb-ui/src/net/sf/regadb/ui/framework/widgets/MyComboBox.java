package net.sf.regadb.ui.framework.widgets;

import eu.webtoolkit.jwt.WComboBox;
import eu.webtoolkit.jwt.WContainerWidget;

public class MyComboBox extends WComboBox {

	public MyComboBox() {
		super();
	}

	public MyComboBox(WContainerWidget parent) {
		super(parent);
	}
	
	public void sort() {
		this.getModel().sort(0);
	}

	public void setCurrentItem(CharSequence lt) {
		for(int i = 0; i<this.getCount(); i++) {
			if(getItemText(i).getValue().equals(lt.toString()))
				this.setCurrentIndex(i);
		}
	}
	
	public void addItem(CharSequence text)  {
		super.addItem(text);
		if(getCurrentIndex()==-1) {
			setCurrentIndex(0);
		}
	}
	
    public void removeCurrentItem() {
    	if(getCurrentIndex()!=-1)
    		removeItem(getCurrentIndex());
    }
}
