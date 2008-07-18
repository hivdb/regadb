package net.sf.regadb.ui.form.query.querytool.awceditor;


import net.sf.regadb.ui.form.query.querytool.configurers.WComposedVisualizationComponentFactory;

import com.pharmadm.custom.rega.queryeditor.AtomicWhereClause;
import com.pharmadm.custom.rega.queryeditor.QueryContext;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.ComposedWordConfigurer;

public class WComposedAWCSelectorPanel extends WBasicAWCSelectorPanel {
    private ComposedWordConfigurer composedWordConfigurer;
	
	public WComposedAWCSelectorPanel(QueryContext context, AtomicWhereClause clause) {
		super(context, clause);
		composedWordConfigurer = null;
	}
	
	@Override
	public boolean addAtomicWhereClause(AtomicWhereClause clause) {
		return addAtomicWhereClause(clause, false);
	}
	
	public boolean addAtomicWhereClause(AtomicWhereClause clause, boolean select) {
		boolean composable = editPanel.getManager().canCompose(clause);
		if (composable) {
			composedWordConfigurer = editPanel.getManager().addAtomicWhereClause(clause, select, composedWordConfigurer, new WComposedVisualizationComponentFactory(), context, new WAtomicWhereClauseEditor(context, clause));
		}
		return composable;
	}
}
