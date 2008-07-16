package com.pharmadm.custom.rega.queryeditor.wordconfiguration;

import java.util.List;
import com.pharmadm.custom.rega.queryeditor.QueryContext;


import com.pharmadm.custom.rega.queryeditor.AtomicWhereClause;
import com.pharmadm.custom.rega.queryeditor.ConfigurableWord;

public class ComposedAWCManager {
	private ComposedAWCEditor editor;
	
	public ComposedAWCManager(ComposedAWCEditor editor) {
		this.editor = editor;
	}

    /** Applies changes made to all visualisation components in the componentList to the corresponding AWCWords */
    public void applyEditings() {
    	editor.applyEditings();
    }
    
    public AtomicWhereClause getClause() {
    	return editor.getSelectedEditor().getAtomicWhereClause();
    }
    
	public boolean isUseless() {
		return editor.isUseless();
	}
	
    private ConfigurationController getEditor() {
        return editor.getFirstEditor();
    }
    
    
	public ComposedWordConfigurer addAtomicWhereClause(AtomicWhereClause clause, boolean select,  ComposedWordConfigurer composedWordConfigurer, ComposedVisualizationComponentFactory composedFactory, QueryContext context, AtomicWhereClauseEditor editor) {
		AtomicWhereClause firstClause = getClause();
		return checkSignature(firstClause, clause, select, composedWordConfigurer, composedFactory, context, editor);
	}
	
	public boolean canCompose(AtomicWhereClause clause2) {
		AtomicWhereClause clause1 = getClause();
		return (clause1.getCompositionBehaviour().getClass().equals(clause2.getCompositionBehaviour().getClass()) &&
				clause1.getCompositionBehaviour().canCompose(clause1, clause2));
	}
	
	/**
	 * compose the two given clauses
	 * @param clause1 the signature clause
	 * @param clause2 the new clause
	 * @param makeSelected true to make clause2 the active clause
	 * @param composedWordConfigurer a composed word configurer
	 * @param the factory to create composed words
	 * @return the updated composed word configurer
	 */
	private ComposedWordConfigurer checkSignature(AtomicWhereClause clause1, AtomicWhereClause clause2, boolean makeSelected, ComposedWordConfigurer composedWordConfigurer, ComposedVisualizationComponentFactory composedFactory, QueryContext context, AtomicWhereClauseEditor editor) {
		VisualizationComponentFactory factory = getEditor().getVisualizationComponentFactory();	
		if (composedWordConfigurer == null) {
			List<ConfigurableWord> words = clause1.getCompositionBehaviour().getComposableWords(clause1);
			List<ConfigurableWord> keys = clause1.getCompositionBehaviour().getKeyWords(clause1);
			composedWordConfigurer = composedFactory.createWord(clause1.getCompositionBehaviour(), factory.createComponents(words), factory.createComponents(keys));
			this.editor.createComposedWord(keys, words, composedWordConfigurer);
		}

		List<ConfigurableWord> words2 = clause2.getCompositionBehaviour().getComposableWords(clause2);
		List<ConfigurableWord> keys2 = clause2.getCompositionBehaviour().getKeyWords(clause2);
		this.editor.composeWord(factory.createComponents(keys2), factory.createComponents(words2), editor, makeSelected);
		return composedWordConfigurer;
	}    
    
}
