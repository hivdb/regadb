package net.sf.regadb.ui.form.query.querytool.buttons;

import com.pharmadm.custom.rega.queryeditor.UniqueNameContext.AssignMode;

import net.sf.regadb.ui.form.query.querytool.awceditor.WAWCEditorPanel;
import net.sf.regadb.ui.form.query.querytool.dialog.SelectClauseDialog;
import net.sf.regadb.ui.form.query.querytool.tree.QueryTreeNode;
import net.sf.regadb.ui.form.query.querytool.widgets.WButtonPanel;
import net.sf.witty.wt.SignalListener;
import net.sf.witty.wt.WMouseEvent;
import net.sf.witty.wt.WPushButton;

public class SelectClauseButtonPanel extends WButtonPanel {
	private QueryTreeNode owner;
	private SelectClauseDialog dialog;
	private WPushButton cancelButton;
	private WPushButton okButton;
	private boolean disabled;
	
	public SelectClauseButtonPanel(QueryTreeNode owner, SelectClauseDialog dialog) {
		super(Style.Default);
		this.owner = owner;
		this.dialog = dialog;
		init();
	}
	
	private void init() {
		okButton = new WPushButton(tr("form.general.button.ok"));
		addButton(okButton);

		cancelButton = new WPushButton(tr("form.general.button.cancel"));
		addButton(cancelButton);

		okButton.clicked.addListener(new SignalListener<WMouseEvent>() {
			public void notify(WMouseEvent a) {
				if (!disabled) {
					disabled = true;
					WAWCEditorPanel panel = dialog.getSelectedClause();
					if (panel == null || panel.isUseless()) {
						cancel();
					}
					else {
						panel.applyEditings();
						owner.getParentNode().addNode(panel.getClause(), AssignMode.output);
						owner.getParentNode().removeChildNode(owner);
						owner.getQueryApp().setQueryEditable(true);
					}
				}
			}
		});
		
		cancelButton.clicked.addListener(new SignalListener<WMouseEvent>() {
			public void notify(WMouseEvent a) {
				if (!disabled) {
					disabled = true;
					cancel();
				}
			}
		});
	}
	
	public void setEnabled(boolean editable) {
		super.setEnabled(editable);
		cancelButton.setEnabled(true);
	}
	
	private void cancel() {
		owner.getQueryApp().setQueryEditable(true);
		owner.getParentNode().removeChildNode(owner);
	}
}
