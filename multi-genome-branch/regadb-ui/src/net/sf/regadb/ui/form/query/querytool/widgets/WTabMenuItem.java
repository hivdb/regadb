package net.sf.regadb.ui.form.query.querytool.widgets;

import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WInteractWidget;
import eu.webtoolkit.jwt.WMenuItem;
import eu.webtoolkit.jwt.WString;
import eu.webtoolkit.jwt.WWidget;

public class WTabMenuItem extends WMenuItem {

	private boolean wrapped;
	private WContainerWidget iw;
	public WTabMenuItem(WString text, WWidget contents, LoadPolicy policy) {
		super(text, contents, policy);
		wrapped = text.literal();
	}
	
	public WInteractWidget itemWidget() {
		if(super.itemWidget() instanceof WInteractWidget) {
			WInteractWidget w = (WInteractWidget)super.itemWidget();
			if (wrapped) {
				return w;
			}
			else {
				if (iw == null) {
					iw = new WContainerWidget();
					iw.setInline(true);
					iw.addWidget(w);
				}
				return iw;
			}
		} else {
			return null;
		}
	}
}
