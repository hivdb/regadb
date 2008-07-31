package net.sf.regadb.ui.form.query.querytool.buttons;



import net.sf.regadb.ui.form.query.querytool.tree.QueryTreeNode;
import net.sf.regadb.ui.form.query.querytool.widgets.WButtonPanel;
import net.sf.witty.wt.SignalListener;
import net.sf.witty.wt.WMouseEvent;
import net.sf.witty.wt.WPushButton;

import com.pharmadm.custom.rega.queryeditor.AndClause;
import com.pharmadm.custom.rega.queryeditor.ComposedWhereClause;
import com.pharmadm.custom.rega.queryeditor.InclusiveOrClause;
import com.pharmadm.custom.rega.queryeditor.NotClause;
import com.pharmadm.custom.rega.queryeditor.UniqueNameContext.AssignMode;

public class NonAtomicClauseButtonPanel extends WButtonPanel {
	private QueryTreeNode node;
	private WPushButton addClauseButton_;
	
	public NonAtomicClauseButtonPanel(QueryTreeNode node) {
		super(Style.Flat);
		getStyleClasses().addStyle("treeitempanel");
		this.node = node;
		init();
	}
	
	private void init() {
		addClauseButton_ = new WPushButton(tr("query.querytool.edit.addclause"));
		addClauseButton_.clicked.addListener(new SignalListener<WMouseEvent>() {
			public void notify(WMouseEvent a) {
				addClauseButton_.disable();
				node.selectNewNode();
			}
		});
		addButton(addClauseButton_);
		addSeparator();
		
		WPushButton addAndButton_ = new WPushButton(tr("query.querytool.edit.addand"));
		addAndButton_.clicked.addListener(new SignalListener<WMouseEvent>() {
			public void notify(WMouseEvent a) {
				node.addNode(new AndClause(), AssignMode.all);
			}
		});
		addButton(addAndButton_);
		
		WPushButton addOrButton_ = new WPushButton(tr("query.querytool.edit.addor"));
		addOrButton_.clicked.addListener(new SignalListener<WMouseEvent>() {
			public void notify(WMouseEvent a) {
				node.addNode(new InclusiveOrClause(), AssignMode.all);
			}
		});
		addButton(addOrButton_);

		WPushButton addNotButton_ = new WPushButton(tr("query.querytool.edit.addnot"));
		addNotButton_.clicked.addListener(new SignalListener<WMouseEvent>() {
			public void notify(WMouseEvent a) {
				node.addNode(new NotClause(), AssignMode.all);
			}
		});
		addButton(addNotButton_);
	}
	
	public void setEnabled(boolean enabled) {
		boolean addChild = canAddChild() && enabled;
		
		for (WPushButton button : buttons) {
			button.setEnabled(addChild);
		}
		
	}
	
	private boolean canAddChild() {
		// this condition removed
		// it can take an unruly amount of time if the query gets big
		// !node.getClause().getAvailableAtomicClauses(DatabaseManager.getInstance().getAWCCatalog()).isEmpty()
		return ((ComposedWhereClause) node.getClause()).acceptsAdditionalChild();
	}
}
