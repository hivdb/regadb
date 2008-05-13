package net.sf.regadb.ui.form.query.querytool.buttons;

import net.sf.regadb.ui.form.query.querytool.QueryEditorGroupBox;
import net.sf.regadb.ui.form.query.querytool.QueryTreeNode;
import net.sf.witty.wt.SignalListener;
import net.sf.witty.wt.WMouseEvent;
import net.sf.witty.wt.WPushButton;

import java.util.ArrayList;
import java.util.List;

import com.pharmadm.custom.rega.queryeditor.WhereClause;

public class EditButtonPanel extends ButtonPanel {
	private QueryEditorGroupBox owner;
	private List<QueryTreeNode> selection;
	private List<WhereClause> cursorClauses;
	private boolean enabled;
	
	private WPushButton cutButton_;
	private WPushButton copyButton_;
	private WPushButton pasteButton_;
	private WPushButton deleteButton_;
	private WPushButton unwrapButton_;
	private WPushButton wrapAndButton_;
	private WPushButton wrapOrButton_;
	private WPushButton wrapNotButton_;
	
	
	public EditButtonPanel(QueryEditorGroupBox owner) {
		super(Style.Flat);
		this.owner = owner;
		cursorClauses = new ArrayList<WhereClause>();
		selection = new ArrayList<QueryTreeNode>();
		enabled = true;
		init();
	}
	
	private void init() {
		cutButton_ = new WPushButton(tr("form.query.querytool.pushbutton.cut"));
		copyButton_ = new WPushButton(tr("form.query.querytool.pushbutton.copy"));
		pasteButton_ = new WPushButton(tr("form.query.querytool.pushbutton.paste"));
		deleteButton_ = new WPushButton(tr("form.query.querytool.pushbutton.delete"));
		unwrapButton_ = new WPushButton(tr("form.query.querytool.pushbutton.unwrap"));
		wrapAndButton_ = new WPushButton(tr("form.query.querytool.pushbutton.wrapand"));
		wrapOrButton_ = new WPushButton(tr("form.query.querytool.pushbutton.wrapor"));
		wrapNotButton_ = new WPushButton(tr("form.query.querytool.pushbutton.wrapnot"));
		
		
		copyButton_.clicked.addListener(new SignalListener<WMouseEvent>() {
			public void notify(WMouseEvent a) {
				cursorClauses = new ArrayList<WhereClause>();
				for (QueryTreeNode node : selection) {
					cursorClauses.add(node.getClause());
				}
			}
		});
		
		pasteButton_.clicked.addListener(new SignalListener<WMouseEvent>() {
			public void notify(WMouseEvent a) {
				WhereClause parentClause = selection.get(0).getClause();
                try {
	                for (WhereClause clause : cursorClauses) {
	                    if (parentClause.acceptsAdditionalChild()) {
								selection.get(0).addNode((WhereClause) clause.clone());
	                    }
	                }
				} catch (CloneNotSupportedException e) {
					e.printStackTrace();
				}
			}
		});
		
		deleteButton_.clicked.addListener(new SignalListener<WMouseEvent>() {
			public void notify(WMouseEvent a) {
				selection.get(0).getParentNode().removeAll(selection);
			}
		});
		
		cutButton_.clicked.addListener(new SignalListener<WMouseEvent>() {
			public void notify(WMouseEvent a) {
				cursorClauses = new ArrayList<WhereClause>();
				for (QueryTreeNode node : selection) {
					cursorClauses.add(node.getClause());
				}
				selection.get(0).getParentNode().removeAll(selection);
			}
		});
		
		addButton(cutButton_);
		addButton(copyButton_);
		addButton(pasteButton_);
		addSeparator();
		addButton(deleteButton_);
		addSeparator();
		addButton(unwrapButton_);
		addSeparator();
		addButton(wrapAndButton_);
		addButton(wrapOrButton_);
		addButton(wrapNotButton_);
		
	}
	
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		this.enabled = enabled;
		checkButtons(enabled);
	}
	
	private void checkButtons(boolean enabled) {
        boolean haveSameParent = haveSameParent(selection);
        boolean containsRoot = containsRootClause(selection);
        
        wrapAndButton_.setEnabled(haveSameParent && enabled);
        wrapOrButton_.setEnabled(haveSameParent && enabled);
        wrapNotButton_.setEnabled(haveSameParent && enabled);

        unwrapButton_.setEnabled(haveSameParent && firstHasGrandParent() && enabled);
        
        copyButton_.setEnabled(haveSameParent && enabled);
        cutButton_.setEnabled(haveSameParent && enabled);
        pasteButton_.setEnabled((! isFirstAtomic()) && (cursorClauses.size() > 0) && selection.size() == 1 && enabled);
        deleteButton_.setEnabled(!containsRoot && selection.size() > 0 && enabled);		
	}

	public void updateSelection() {
		selection = owner.getQueryTree().getSelection();
		System.err.println(selection.size());
		checkButtons(enabled);
	}
	
	private boolean firstHasGrandParent() {
		return selection.size() > 0 
		&& selection.get(0).getParentNode() != null
		&& selection.get(0).getParentNode().getParentNode() != null;
	}
	
	private boolean isFirstAtomic() {
		return selection.size() > 0 && selection.get(0).getClause().isAtomic();
	}
	
    private boolean haveSameParent(List<QueryTreeNode> selection) {
    	boolean same = selection.size() > 0;
    	
    	if (selection.size() > 0) {
    		QueryTreeNode parentNode = selection.get(0).getParentNode();
	    	for (QueryTreeNode node : selection) {
	    		if (node.getParentNode() == null || !node.getParentNode().equals(parentNode)) {
	    			same = false;
	    		}
	    	}
    	}

    	return same;
    }
    
    private boolean containsRootClause(List<QueryTreeNode> selection) {
    	boolean root = false;
    	
    	if (selection.size() > 0) {
	    	for (QueryTreeNode node : selection) {
	    		WhereClause clause = (WhereClause) node.getClause();
	    		if (clause.getParent() == null) {
	    			root = true;
	    		}
	    	}
    	}
    	return root;
    }			

}