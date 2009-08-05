package net.sf.regadb.ui.form.query.querytool.buttons;

import net.sf.regadb.ui.form.query.querytool.QueryToolForm;
import net.sf.regadb.ui.form.query.querytool.widgets.WButtonPanel;
import eu.webtoolkit.jwt.Signal1;
import eu.webtoolkit.jwt.WMouseEvent;
import eu.webtoolkit.jwt.WPushButton;


public class RunButtonPanel extends WButtonPanel {

	
	public RunButtonPanel(QueryToolForm mainForm) {
		super(Style.Default);
		init(mainForm);
	}
	
	private void init(final QueryToolForm mainForm) {
		WPushButton runButton = new WPushButton(tr("form.query.querytool.pushbutton.run"));
		runButton.clicked().addListener(this, new Signal1.Listener<WMouseEvent>() {
			public void trigger(WMouseEvent a) {
				mainForm.runQuery();
			}
        });  
		addButton(runButton);
		getStyleClasses().addStyle("runbutton");
		
	}

}
