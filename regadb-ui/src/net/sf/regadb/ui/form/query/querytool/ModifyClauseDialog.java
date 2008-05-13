package net.sf.regadb.ui.form.query.querytool;

import com.pharmadm.custom.rega.queryeditor.AtomicWhereClause;
import com.pharmadm.custom.rega.queryeditor.QueryContext;
import com.pharmadm.custom.rega.queryeditor.port.DatabaseManager;

import net.sf.regadb.ui.form.query.querytool.awceditor.WComposedAWCSelectorPanel;
import net.sf.regadb.ui.form.query.querytool.buttons.ButtonPanel;
import net.sf.regadb.ui.form.query.querytool.buttons.ModifyClauseButtonPanel;
import net.sf.witty.wt.i8n.WMessage;

public class ModifyClauseDialog extends WDialog {
	
	public ModifyClauseDialog(QueryTreeNode node, QueryContext context, AtomicWhereClause clause) {
		super(new WMessage("form.query.querytool.dialog.modify"));
		
        WComposedAWCSelectorPanel atomPanel = new WComposedAWCSelectorPanel(context, clause);
        for (AtomicWhereClause similarClause : DatabaseManager.getInstance().getAWCCatalog().getSimilarClauses(clause)) {
        	atomPanel.addAtomicWhereClause(similarClause);
        }
        atomPanel.getRadioButtons().get(0).hide();
        atomPanel.getRadioButtons().get(0).setChecked(true);
		getContentArea().addWidget(atomPanel);
        
        ButtonPanel buttonPanel =  new ModifyClauseButtonPanel(node, atomPanel);
		setButtonPanel(buttonPanel);
	}
}
