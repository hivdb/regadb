package net.sf.regadb.ui.form.importTool;

import eu.webtoolkit.jwt.Signal1;
import eu.webtoolkit.jwt.WBreak;
import eu.webtoolkit.jwt.WDialog;
import eu.webtoolkit.jwt.WMouseEvent;
import eu.webtoolkit.jwt.WPushButton;
import eu.webtoolkit.jwt.WString;
import eu.webtoolkit.jwt.WText;

public class DetailsBox extends WDialog {
	private DetailsForm form;
	private WText error;

	public DetailsBox(DetailsForm form) {
		super(form.getTitle());
		this.form = form;
		getContents().addWidget(form);
		new WBreak(getContents());
		error = new WText(getContents());
		new WBreak(getContents());
        WPushButton ok = new WPushButton(tr("form.importTool.details.box.ok"), getContents());
        ok.clicked().addListener(this, new Signal1.Listener<WMouseEvent>() {
			public void trigger(WMouseEvent arg) {
				if(handleError()) {
					DetailsBox.this.form.save();
					hide();
				}
			}
		});
        WPushButton cancel = new WPushButton(tr("form.importTool.details.box.cancel"), getContents());
        cancel.clicked().addListener(this, new Signal1.Listener<WMouseEvent>() {
			public void trigger(WMouseEvent arg) {
				hide();
			}
		});
	}
	
	private boolean handleError() {
		WString errorMessage = form.validate();
		if (errorMessage == null) {
			error.setHidden(true);
			return true;
		} else {
			error.setText(errorMessage);
			error.setHidden(false);
			return false;
		}
	}
	
	public DetailsForm getForm() {
		return form;
	}
}
