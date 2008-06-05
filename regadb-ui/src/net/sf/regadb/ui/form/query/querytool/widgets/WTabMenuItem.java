package net.sf.regadb.ui.form.query.querytool.widgets;

import net.sf.witty.wt.WContainerWidget;
import net.sf.witty.wt.WInteractWidget;
import net.sf.witty.wt.WWidget;
import net.sf.witty.wt.i8n.WMessage;
import net.sf.witty.wt.widgets.extra.WMenuItem;
import net.sf.witty.wt.widgets.extra.WMenuLoadPolicy;

public class WTabMenuItem extends WMenuItem {

	private boolean wrapped;
	private WContainerWidget iw;
	public WTabMenuItem(WMessage text, WWidget contents, WMenuLoadPolicy policy) {
		super(text, contents, policy);
		wrapped = text.literal();
	}
	
	public WInteractWidget itemWidget() {
		WInteractWidget w = super.itemWidget();
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
	}
	

}
