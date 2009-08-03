package net.sf.regadb.ui.form.query.querytool.widgets;

import java.util.HashSet;

import eu.webtoolkit.jwt.WWidget;

/**
 * Can be used to replace the default WT css class mechanism
 * This class allows the assigning of multiple css classes
 * to a signle widget
 * @author fromba0
 */
public class CssClasses {

	private WWidget widget;
	private HashSet<String> styleClasses;
	
	/**
	 * Enable multiple css classes for the given widget
	 * The widget's styleClass attribute will be parsed
	 * as a space separated list of classes
	 * @param widget
	 */
	public CssClasses(WWidget widget) {
		this.widget = widget;
		styleClasses = new HashSet<String>();
		addStyle(widget.getStyleClass());
	}
	
	/**
	 * add the given style class to the widget
	 * multiple classes can be separated by spaces
	 * duplicate classes will be ignored
	 * @param style a space separated list of classes
	 */
	public void addStyle(String style) {
		if (style != null) {
			String[] styles = style.split(" ");
			for (String s : styles) {
				styleClasses.add(s);
			}
			updateStyle();
		}
	}
	
	/**
	 * remove the given style class from the widget
	 * multiple classes can be separated by spaces
	 * @param style a space separated list of classes
	 */
	public void removeStyle(String style) {
		String[] styles = style.split(" ");
		for (String s : styles) {
			styleClasses.remove(s);
		}
		updateStyle();
	}
	
	/** 
	 * remove all css classes from the widget
	 */
	public void clearStyle() {
		styleClasses.clear();
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
