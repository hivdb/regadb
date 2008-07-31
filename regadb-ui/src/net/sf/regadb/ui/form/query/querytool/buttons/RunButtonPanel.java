package net.sf.regadb.ui.form.query.querytool.buttons;

import net.sf.regadb.ui.form.query.querytool.QueryToolForm;
import net.sf.regadb.ui.form.query.querytool.widgets.WButtonPanel;
import net.sf.witty.wt.SignalListener;
import net.sf.witty.wt.WMouseEvent;
import net.sf.witty.wt.WPushButton;


public class RunButtonPanel extends WButtonPanel {

	
	public RunButtonPanel(QueryToolForm mainForm) {
		super(Style.Default);
		init(mainForm);
	}
	
	private void init(final QueryToolForm mainForm) {
		WPushButton runButton = new WPushButton(tr("form.query.querytool.pushbutton.run"));
		runButton.clicked.addListener(new SignalListener<WMouseEvent>() {
			public void notify(WMouseEvent a) {
				mainForm.runQuery();
			}
        });  
		addButton(runButton);
		getStyleClasses().addStyle("runbutton");
		
	}

}
