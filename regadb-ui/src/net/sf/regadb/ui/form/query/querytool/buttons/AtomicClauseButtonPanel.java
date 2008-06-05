package net.sf.regadb.ui.form.query.querytool.buttons;

import com.pharmadm.custom.rega.queryeditor.AtomicWhereClause;

import net.sf.regadb.ui.form.query.querytool.dialog.ModifyClauseDialog;
import net.sf.regadb.ui.form.query.querytool.tree.QueryTreeNode;
import net.sf.regadb.ui.form.query.querytool.widgets.WButtonPanel;
import net.sf.regadb.ui.form.query.querytool.widgets.WDialog;
import net.sf.witty.wt.SignalListener;
import net.sf.witty.wt.WMouseEvent;
import net.sf.witty.wt.WPushButton;

public class AtomicClauseButtonPanel extends WButtonPanel {
	private QueryTreeNode node;

	public AtomicClauseButtonPanel(QueryTreeNode node) {
		super(Style.Flat);
		getStyleClasses().addStyle("treeitempanel");
		this.node = node;
		init();
	}
	
	private void init() {
		WPushButton modifyButton_ = new WPushButton(tr("form.query.querytool.pushbutton.modify"));
		addButton(modifyButton_);
		modifyButton_.clicked.addListener(new SignalListener<WMouseEvent>() {
			public void notify(WMouseEvent a) {
				node.getQueryApp().getQueryContext().setContextClause(node.getParentNode().getClause());
				WDialog editDialog = new ModifyClauseDialog(node, node.getQueryApp().getQueryContext(), (AtomicWhereClause) node.getClause());
				node.showDialog(editDialog);
			}
		});
	}
}
