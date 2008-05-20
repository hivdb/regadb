package net.sf.regadb.ui.form.query.querytool.buttons;

import java.util.ArrayList;


import net.sf.regadb.ui.form.query.querytool.tree.QueryTreeNode;
import net.sf.witty.wt.SignalListener;
import net.sf.witty.wt.WMouseEvent;
import net.sf.witty.wt.WPushButton;

import com.pharmadm.custom.rega.queryeditor.AWCPrototypeCatalog;
import com.pharmadm.custom.rega.queryeditor.AndClause;
import com.pharmadm.custom.rega.queryeditor.ComposedWhereClause;
import com.pharmadm.custom.rega.queryeditor.InclusiveOrClause;
import com.pharmadm.custom.rega.queryeditor.NotClause;
import com.pharmadm.custom.rega.queryeditor.OutputVariable;
import com.pharmadm.custom.rega.queryeditor.port.DatabaseManager;

public class NonAtomicClauseButtonPanel extends ButtonPanel {
	private QueryTreeNode node;
	Object[] clauses;
	
	public NonAtomicClauseButtonPanel(QueryTreeNode node) {
		super(Style.Flat);
		this.setStyleClass(this.styleClass() + " treeitempanel");
		this.node = node;
		AWCPrototypeCatalog catalog = DatabaseManager.getInstance().getAWCCatalog();
		clauses = catalog.getAWCPrototypes(new ArrayList<OutputVariable>()).toArray();
		init();
	}
	
	private void init() {
		WPushButton addClauseButton_ = new WPushButton(tr("form.query.querytool.pushbutton.addclause"));
		addClauseButton_.clicked.addListener(new SignalListener<WMouseEvent>() {
			public void notify(WMouseEvent a) {
				node.selectNewNode();
			}
		});
		addButton(addClauseButton_);
		addSeparator();
		
		WPushButton addAndButton_ = new WPushButton(tr("form.query.querytool.pushbutton.addand"));
		addAndButton_.clicked.addListener(new SignalListener<WMouseEvent>() {
			public void notify(WMouseEvent a) {
				node.addNode(new AndClause());
			}
		});
		addButton(addAndButton_);
		
		WPushButton addOrButton_ = new WPushButton(tr("form.query.querytool.pushbutton.addor"));
		addOrButton_.clicked.addListener(new SignalListener<WMouseEvent>() {
			public void notify(WMouseEvent a) {
				node.addNode(new InclusiveOrClause());
			}
		});
		addButton(addOrButton_);

		WPushButton addNotButton_ = new WPushButton(tr("form.query.querytool.pushbutton.addnot"));
		addNotButton_.clicked.addListener(new SignalListener<WMouseEvent>() {
			public void notify(WMouseEvent a) {
				node.addNode(new NotClause());
			}
		});
		addButton(addNotButton_);
		
//		addNotButton_.setEnabled(canAddChild());
//		addOrButton_.setEnabled(canAddChild());
//		addAndButton_.setEnabled(canAddChild());
//		addClauseButton_.setEnabled(canAddChild());
	}
	
	public void setEditable(boolean enabled) {
		super.setEditable(enabled);
		
		for (WPushButton button : buttons) {
			button.setEnabled(enabled && canAddChild());
		}
		
	}
	
	private boolean canAddChild() {
		return (!node.getClause().getAvailableAtomicClauses(DatabaseManager.getInstance().getAWCCatalog()).isEmpty() && ((ComposedWhereClause) node.getClause()).acceptsAdditionalChild());
	}
}
