package net.sf.regadb.ui.form.query.querytool.widgets;

import net.sf.witty.wt.WContainerWidget;

public class WStyledContainerWidget extends WContainerWidget implements StyledWidget {
	private CssClasses style = new CssClasses(this);

	public CssClasses getStyleClasses() {
		return style;
	}
	
	public WStyledContainerWidget(WContainerWidget parent) {
		super(parent);
	}

	public WStyledContainerWidget() {
	}

}
