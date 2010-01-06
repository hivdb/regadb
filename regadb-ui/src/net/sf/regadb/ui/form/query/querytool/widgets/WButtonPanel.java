package net.sf.regadb.ui.form.query.querytool.widgets;

import java.util.ArrayList;
import java.util.List;

import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WPushButton;

/**
 * a group of buttons
 * @author fromba0
 *
 */
public class WButtonPanel extends WStyledContainerWidget{
	private Style buttonStyle;
	
	protected List<WPushButton> buttons;
	
	public enum Style {
		Flat,
		Default
	}
	
	public WButtonPanel(Style style) {
		super();
		buttons = new ArrayList<WPushButton>();
		getStyleClasses().addStyle("buttonpanel");
		
		setStyle(style);
	}
	
	public void addButton(WPushButton button) {
		buttons.add(button);
		this.addWidget(button);
	}
	
	public void addSeparator() {
		WContainerWidget sep = new WContainerWidget();
		sep.setInline(true);
		sep.setStyleClass("separator");
		this.addWidget(sep);
	}
	
	/**
	 * update state of the buttons without changing the enabled state
	 */
	public void update() {
//		setEnabled(isEnabled());
	}
	
	/**
	 *  enable or disable buttons in this panel
	 * @param enabled
	 */
	public void setEnabled(boolean enabled) {
		super.setDisabled(!enabled);
	}
	
	/** 
	 * sets the display style of this button panel
	 * @param style
	 */
	private void setStyle(Style style) {
		if (style == Style.Flat) {
			getStyleClasses().addStyle("flatbuttonpanel");
		}
		else {
			getStyleClasses().addStyle("defaultbuttonpanel");
		}
		this.buttonStyle = style;
	}
	
	public Style getStyle() {
		return buttonStyle;
	}
}
