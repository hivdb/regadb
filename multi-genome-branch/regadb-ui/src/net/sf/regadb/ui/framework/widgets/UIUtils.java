package net.sf.regadb.ui.framework.widgets;

import java.util.EnumSet;

import eu.webtoolkit.jwt.Icon;
import eu.webtoolkit.jwt.Signal;
import eu.webtoolkit.jwt.Signal1;
import eu.webtoolkit.jwt.StandardButton;
import eu.webtoolkit.jwt.WMessageBox;
import eu.webtoolkit.jwt.WObject;
import eu.webtoolkit.jwt.WString;
import eu.webtoolkit.jwt.WTimer;

/**
 * Collection of UI Utils.
 * 
 * @author plibin0
 */
public class UIUtils {
	/**
	 * Show a warning message box with text.
	 * 
	 * @param receiver
	 * @param text
	 */
	public static void showWarningMessageBox(WObject receiver, WString text) {
        final WMessageBox box = new WMessageBox(WMessageBox.tr("datatable.message.warning"), text, Icon.Warning, EnumSet.of(StandardButton.Ok));
        box.show();
        box.buttonClicked().addListener(box, new Signal1.Listener<StandardButton>(){
        	public void trigger(StandardButton e1){
        		box.remove();
        	}
        });
	}
	
	public static String keyOrValue(WString s) {
		if(s.literal()) {
			return s.value();
		} else {
			return s.key();
		}
	}
	
	public static WMessageBox createYesNoMessageBox(WObject receiver, WString text) {
        final WMessageBox box = new WMessageBox(WMessageBox.tr("datatable.message.warning"), text, Icon.Warning, EnumSet.of(StandardButton.Yes, StandardButton.No));
        return box;
	}

	public static void singleShot(WObject receiver, int msec, Signal.Listener listener) {
		WTimer timer = new WTimer();
		timer.setInterval(msec);
		timer.setSingleShot(true);
		timer.timeout().addListener(receiver, listener);
		timer.start();
	}
}
