package net.sf.regadb.ui.form.query.querytool.buttons;

import net.sf.regadb.ui.form.query.querytool.awceditor.WAWCSelectorPanel;
import net.sf.regadb.ui.form.query.querytool.tree.QueryTreeNode;
import net.sf.regadb.ui.form.query.querytool.widgets.WButtonPanel;
import eu.webtoolkit.jwt.Signal1;
import eu.webtoolkit.jwt.WMouseEvent;
import eu.webtoolkit.jwt.WPushButton;


public class ModifyClauseButtonPanel extends WButtonPanel {
	private QueryTreeNode node;
	private WAWCSelectorPanel editPanel;
	
	public ModifyClauseButtonPanel(QueryTreeNode node, WAWCSelectorPanel editPanel) {
		super(Style.Default);
		this.node = node;
		this.editPanel = editPanel;
		init();
	}
	
	
	private void init() {
		WPushButton okButton = new WPushButton(tr("form.general.button.ok"));
		addButton(okButton);

		WPushButton cancelButton = new WPushButton(tr("form.general.button.cancel"));
		addButton(cancelButton);

		okButton.clicked().addListener(this, new Signal1.Listener<WMouseEvent>() {
			public void trigger(WMouseEvent a) {
				editPanel.getSelectedClause().getManager().applyEditings();
				node.replaceNode(editPanel.getSelectedClause().getManager().getClause());
				node.showContent();
			}
		});
		
		cancelButton.clicked().addListener(this, new Signal1.Listener<WMouseEvent>() {
			public void trigger(WMouseEvent a) {
				node.showContent();
			}
		});
	}
}
