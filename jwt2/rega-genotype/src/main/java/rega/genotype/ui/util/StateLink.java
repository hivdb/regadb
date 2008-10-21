package rega.genotype.ui.util;

import eu.webtoolkit.jwt.Signal1;
import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WMouseEvent;
import eu.webtoolkit.jwt.WString;
import eu.webtoolkit.jwt.WText;

public abstract class StateLink extends WText {

	public StateLink(WString ws, WContainerWidget parent) {
		super(ws, parent);
		
		text().arg("");
		
		this.setStyleClass("non-link");
		
		this.clicked.addListener(this, new Signal1.Listener<WMouseEvent>(){
			public void trigger(WMouseEvent a) {
				String value = text().args().get(0);
				if(!value.equals("")) {
					clickAction(value);
				}
			}
		});
	}
	
	public void setVarValue(String value) {
		text().args().clear();
		text().args().add(value);
		
		text().refresh();
		
		if(value.equals("")) {
			this.setStyleClass("non-link");
		} else {
			this.setStyleClass("link");
		}
		
		refresh();
	}
	
	public abstract void clickAction(String value);
}
