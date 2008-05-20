package net.sf.regadb.ui.form.query.querytool.buttons;

import com.pharmadm.custom.rega.queryeditor.AtomicWhereClause;

import net.sf.regadb.ui.form.query.querytool.dialog.ModifyClauseDialog;
import net.sf.regadb.ui.form.query.querytool.dialog.WDialog;
import net.sf.regadb.ui.form.query.querytool.tree.QueryTreeNode;
import net.sf.witty.wt.SignalListener;
import net.sf.witty.wt.WMouseEvent;
import net.sf.witty.wt.WPushButton;

public class AtomicClauseButtonPanel extends ButtonPanel {
	private QueryTreeNode node;

	public AtomicClauseButtonPanel(QueryTreeNode node) {
		super(Style.Flat);
		this.setStyleClass(this.styleClass() + " treeitempanel");
		this.node = node;
		init();
	}
	
	private void init() {
		WPushButton modifyButton_ = new WPushButton(tr("form.query.querytool.pushbutton.modify"));
		addButton(modifyButton_);
		modifyButton_.clicked.addListener(new SignalListener<WMouseEvent>() {
			public void notify(WMouseEvent a) {
				node.getEditorModel().setContextClause(node.getParentNode().getClause());
				WDialog editDialog = new ModifyClauseDialog(node, node.getEditorModel().getQueryContext(), (AtomicWhereClause) node.getClause());
				node.showDialog(editDialog);
			}
		});
	}
}
