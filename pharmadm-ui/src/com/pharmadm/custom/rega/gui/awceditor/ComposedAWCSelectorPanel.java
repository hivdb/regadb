package com.pharmadm.custom.rega.gui.awceditor;


import com.pharmadm.custom.rega.gui.configurers.JComposedVisualizationComponentFactory;
import com.pharmadm.custom.rega.queryeditor.AtomicWhereClause;
import com.pharmadm.custom.rega.queryeditor.QueryContext;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.ComposedWordConfigurer;

/**
 * a composed AWC panel can combine AWCs with the same composition type
 * @author fromba0
 *
 */
public class ComposedAWCSelectorPanel extends BasicAWCSelectorPanel{

    private ComposedWordConfigurer composedWordConfigurer;
	
	public ComposedAWCSelectorPanel(QueryContext context, AtomicWhereClause clause) {
		super(context, clause);
		composedWordConfigurer = null;
	}
	
	@Override
	public boolean addAtomicWhereClause(AtomicWhereClause clause) {
		boolean composable = editPanel.getManager().canCompose(clause);
		if (composable) {
			composedWordConfigurer =  editPanel.getManager().addAtomicWhereClause(clause, false, composedWordConfigurer, new JComposedVisualizationComponentFactory(), context, new JAtomicWhereClauseEditor(context, clause));
		}
		return composable;
	}
}
