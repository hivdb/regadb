package net.sf.regadb.ui.form.query.querytool.dialog;

import java.util.List;

import com.pharmadm.custom.rega.queryeditor.AtomicWhereClause;
import com.pharmadm.custom.rega.queryeditor.QueryContext;
import com.pharmadm.custom.rega.queryeditor.port.DatabaseManager;

import net.sf.regadb.ui.form.query.querytool.awceditor.WComposedAWCSelectorPanel;
import net.sf.regadb.ui.form.query.querytool.buttons.ModifyClauseButtonPanel;
import net.sf.regadb.ui.form.query.querytool.tree.QueryTreeNode;
import net.sf.regadb.ui.form.query.querytool.widgets.WButtonPanel;
import net.sf.regadb.ui.form.query.querytool.widgets.WDialog;

public class ModifyClauseDialog extends WDialog {
	
	public ModifyClauseDialog(QueryTreeNode node, QueryContext context, AtomicWhereClause clause) {
		super(tr("form.query.querytool.dialog.modify"));
		
        WComposedAWCSelectorPanel atomPanel =  null;
        List<AtomicWhereClause> clauses = DatabaseManager.getInstance().getAWCCatalog().getSimilarClauses(clause);
        for (AtomicWhereClause similarClause : clauses) {
        	boolean makeSelected = clause.getHash().equals(similarClause.getHash());
        	if (atomPanel == null) {
        		atomPanel = new WComposedAWCSelectorPanel(context, makeSelected ?clause : similarClause);        		
        	}
        	else {
        		atomPanel.addAtomicWhereClause(makeSelected ?clause : similarClause, makeSelected);
        	}
        }
        atomPanel.getRadioButtons().get(0).hide();
        atomPanel.getRadioButtons().get(0).setChecked(true);

		getContentPanel().addWidget(atomPanel);
        
        WButtonPanel buttonPanel =  new ModifyClauseButtonPanel(node, atomPanel);
		setButtonPanel(buttonPanel);
	}
}
