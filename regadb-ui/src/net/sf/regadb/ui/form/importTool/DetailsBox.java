package net.sf.regadb.ui.form.importTool;

import eu.webtoolkit.jwt.Signal1;
import eu.webtoolkit.jwt.WBreak;
import eu.webtoolkit.jwt.WDialog;
import eu.webtoolkit.jwt.WMouseEvent;
import eu.webtoolkit.jwt.WPushButton;
import eu.webtoolkit.jwt.WString;

public class DetailsBox extends WDialog {
	private DetailsForm form;

	public DetailsBox(DetailsForm form) {
		super(form.getTitle());
		this.form = form;
		getContents().addWidget(form);
		new WBreak(getContents());
        WPushButton ok = new WPushButton(tr("form.importTool.details.box.ok"), getContents());
        WPushButton cancel = new WPushButton(tr("form.importTool.details.box.cancel"), getContents());
        ok.clicked().addListener(this, new Signal1.Listener<WMouseEvent>() {
			public void trigger(WMouseEvent arg) {
				remove();
			}
		});
        cancel.clicked().addListener(this, new Signal1.Listener<WMouseEvent>() {
			public void trigger(WMouseEvent arg) {
				remove();
			}
		});
        this.show();
	}
	
	public DetailsForm getForm() {
		return form;
	}
}
