package net.sf.regadb.ui.form.query.querytool.buttons;

import net.sf.regadb.ui.form.query.querytool.QueryTreeNode;
import net.sf.regadb.ui.form.query.querytool.SelectClauseDialog;
import net.sf.regadb.ui.form.query.querytool.awceditor.WAWCEditorPanel;
import net.sf.witty.wt.SignalListener;
import net.sf.witty.wt.WMouseEvent;
import net.sf.witty.wt.WPushButton;

public class SelectClauseButtonPanel extends ButtonPanel {
	private QueryTreeNode owner;
	private SelectClauseDialog dialog;
	
	public SelectClauseButtonPanel(QueryTreeNode owner, SelectClauseDialog dialog) {
		super(Style.Default);
		this.owner = owner;
		this.dialog = dialog;
		init();
	}
	
	private void init() {
		WPushButton okButton = new WPushButton(tr("form.general.button.ok"));
		addButton(okButton);

		WPushButton cancelButton = new WPushButton(tr("form.general.button.cancel"));
		addButton(cancelButton);

		okButton.clicked.addListener(new SignalListener<WMouseEvent>() {
			public void notify(WMouseEvent a) {
				WAWCEditorPanel panel = dialog.getSelectedClause();
				panel.applyEditings();
				owner.getParentNode().addNode(panel.getClause());
				owner.showRegularContent();
				owner.getParentNode().removeChildNode(owner);
			}
		});
		
		cancelButton.clicked.addListener(new SignalListener<WMouseEvent>() {
			public void notify(WMouseEvent a) {
				owner.showRegularContent();
				owner.getParentNode().removeChildNode(owner);
			}
		});
	}

}
