package com.pharmadm.custom.rega.gui;

import java.util.List;

import com.pharmadm.custom.rega.queryeditor.AtomicWhereClause;
import com.pharmadm.custom.rega.queryeditor.ConfigurableWord;
import com.pharmadm.custom.rega.queryeditor.QueryContext;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.AtomicWhereClauseEditor;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.VisualizationComponentFactory;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.WordConfigurer;

/**
 * a composed AWC panel can combine AWCs with the same composition type
 * @author fromba0
 *
 */
public class ComposedAWCSelectorPanel extends BasicAWCSelectorPanel{

    private WordConfigurer composedWordConfigurer;
	
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
				composedWordConfigurer = clause1.getCompositionBehaviour().getWordConfigurer(factory.createComponents(words));
				getEditorPanel().createComposedWord(words, composedWordConfigurer);
			}

			List<ConfigurableWord> words2 = clause2.getCompositionBehaviour().getComposableWords(clause2);
			getEditorPanel().composeWord(factory.createComponents(words2), new AtomicWhereClauseEditor(context, clause2));
			return true;
		}
		return false;
	}
}
