package net.sf.regadb.ui.form.query.querytool;

import java.util.HashSet;

import net.sf.witty.wt.WWidget;

public class CssClasses {

	private WWidget widget;
	private HashSet<String> styleClasses;
	
	public CssClasses(WWidget widget) {
		this.widget = widget;
		styleClasses = new HashSet<String>();
		addStyle(widget.styleClass());
	}
	
	public void addStyle(String style) {
		if (style != null) {
			String[] styles = style.split(" ");
			for (String s : styles) {
				styleClasses.add(s);
			}
			updateStyle();
		}
	}
	
	public void removeStyle(String style) {
		String[] styles = style.split(" ");
		for (String s : styles) {
			styleClasses.remove(s);
		}
		updateStyle();
	}
	
	private void updateStyle() {
		String classes = "";
		for (String st : styleClasses) {
			classes += st + " ";
		}
		widget.setStyleClass(classes.trim());
	}
}
