package net.sf.regadb.ui.form.query.querytool.buttons;

import net.sf.regadb.ui.form.query.querytool.dialog.ModifyClauseDialog;
import net.sf.regadb.ui.form.query.querytool.tree.QueryTreeNode;
import net.sf.regadb.ui.form.query.querytool.widgets.MyDialog;
import net.sf.regadb.ui.form.query.querytool.widgets.WButtonPanel;

import com.pharmadm.custom.rega.queryeditor.AtomicWhereClause;

import eu.webtoolkit.jwt.Signal1;
import eu.webtoolkit.jwt.WMouseEvent;
import eu.webtoolkit.jwt.WPushButton;

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
		modifyButton_.clicked.addListener(this, new Signal1.Listener<WMouseEvent>() {
			public void trigger(WMouseEvent a) {
				node.getQueryApp().getQueryContext().setContextClause(node.getParentNode().getClause());
				MyDialog editDialog = new ModifyClauseDialog(node, node.getQueryApp().getQueryContext(), (AtomicWhereClause) node.getClause());
				node.showDialog(editDialog);
			}
		});
	}
}
