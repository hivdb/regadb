package com.pharmadm.custom.rega.gui.awceditor;

import java.util.List;

import com.pharmadm.custom.rega.gui.configurers.JComposedVisualizationComponentFactory;
import com.pharmadm.custom.rega.queryeditor.AtomicWhereClause;
import com.pharmadm.custom.rega.queryeditor.ConfigurableWord;
import com.pharmadm.custom.rega.queryeditor.QueryContext;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.ComposedWordConfigurer;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.VisualizationComponentFactory;

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
		AtomicWhereClause firstClause = editPanel.getClause();
		return checkSignature(firstClause, clause);
	}
	
	private boolean checkSignature(AtomicWhereClause clause1, AtomicWhereClause clause2) {
		if (clause1.getCompositionBehaviour().getClass().equals(clause2.getCompositionBehaviour().getClass()) &&
			clause1.getCompositionBehaviour().canCompose(clause1, clause2)) {

			VisualizationComponentFactory factory = editPanel.getEditor().getVisualizationComponentFactory();
			if (composedWordConfigurer == null) {
				List<ConfigurableWord> words = clause1.getCompositionBehaviour().getComposableWords(clause1);
				List<ConfigurableWord> keys = clause1.getCompositionBehaviour().getKeyWords(clause1);
				composedWordConfigurer = new JComposedVisualizationComponentFactory().createWord(clause1.getCompositionBehaviour(), factory.createComponents(words), factory.createComponents(keys));
				getEditorPanel().createComposedWord(keys, words, composedWordConfigurer);
			}

			List<ConfigurableWord> words2 = clause2.getCompositionBehaviour().getComposableWords(clause2);
			List<ConfigurableWord> keys2 = clause2.getCompositionBehaviour().getKeyWords(clause2);
			getEditorPanel().composeWord(factory.createComponents(keys2), factory.createComponents(words2), new JAtomicWhereClauseEditor(context, clause2), false);
			return true;
		}
		return false;
	}
}
