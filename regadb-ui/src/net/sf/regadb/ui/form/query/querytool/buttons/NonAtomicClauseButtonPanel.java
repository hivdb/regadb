package net.sf.regadb.ui.form.query.querytool.buttons;



import net.sf.regadb.ui.form.query.querytool.tree.QueryTreeNode;
import net.sf.regadb.ui.form.query.querytool.widgets.WButtonPanel;

import com.pharmadm.custom.rega.queryeditor.AndClause;
import com.pharmadm.custom.rega.queryeditor.ComposedWhereClause;
import com.pharmadm.custom.rega.queryeditor.InclusiveOrClause;
import com.pharmadm.custom.rega.queryeditor.NotClause;
import com.pharmadm.custom.rega.queryeditor.UniqueNameContext.AssignMode;

import eu.webtoolkit.jwt.Signal1;
import eu.webtoolkit.jwt.WMouseEvent;
import eu.webtoolkit.jwt.WPushButton;

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
		addClauseButton_ = new WPushButton(tr("form.query.querytool.pushbutton.addclause"));
		addClauseButton_.clicked().addListener(this, new Signal1.Listener<WMouseEvent>() {
			public void trigger(WMouseEvent a) {
				node.selectNewNode();
			}
		});
		addButton(addClauseButton_);
		addSeparator();
		
		WPushButton addAndButton_ = new WPushButton(tr("form.query.querytool.pushbutton.addand"));
		addAndButton_.clicked().addListener(this, new Signal1.Listener<WMouseEvent>() {
			public void trigger(WMouseEvent a) {
				node.addNode(new AndClause(), AssignMode.all);
			}
		});
		addButton(addAndButton_);
		
		WPushButton addOrButton_ = new WPushButton(tr("form.query.querytool.pushbutton.addor"));
		addOrButton_.clicked().addListener(this, new Signal1.Listener<WMouseEvent>() {
			public void trigger(WMouseEvent a) {
				node.addNode(new InclusiveOrClause(), AssignMode.all);
			}
		});
		addButton(addOrButton_);

		WPushButton addNotButton_ = new WPushButton(tr("form.query.querytool.pushbutton.addnot"));
		addNotButton_.clicked().addListener(this, new Signal1.Listener<WMouseEvent>() {
			public void trigger(WMouseEvent a) {
				node.addNode(new NotClause(), AssignMode.all);
			}
		});
		addButton(addNotButton_);
	}
	
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled && canAddChild());
	}
	
	private boolean canAddChild() {
		// this condition removed
		// it can take an unruly amount of time if the query gets big
		// !node.getClause().getAvailableAtomicClauses(DatabaseManager.getInstance().getAWCCatalog()).isEmpty()
		return ((ComposedWhereClause) node.getClause()).acceptsAdditionalChild();
	}
}
