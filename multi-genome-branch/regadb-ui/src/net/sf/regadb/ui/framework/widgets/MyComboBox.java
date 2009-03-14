package net.sf.regadb.ui.framework.widgets;

import eu.webtoolkit.jwt.WComboBox;
import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WString;

public class MyComboBox extends WComboBox {

	public MyComboBox() {
		super();
	}

	public MyComboBox(WContainerWidget parent) {
		super(parent);
	}
	
	public void sort() {
		this.model().sort(0);
	}

	public void setCurrentItem(WString lt) {
		for(int i = 0; i<this.count(); i++) {
			if(itemText(i).equals(lt))
				this.setCurrentIndex(i);
		}
	}
	
	public void addItem(WString text)  {
		super.addItem(text);
		if(currentIndex()==-1) {
			setCurrentIndex(0);
		}
	}
}
