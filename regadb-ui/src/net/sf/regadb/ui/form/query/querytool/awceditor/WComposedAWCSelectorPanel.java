package net.sf.regadb.ui.form.query.querytool.awceditor;

import java.util.List;

import net.sf.regadb.ui.form.query.querytool.configurers.WComposedVisualizationComponentFactory;

import com.pharmadm.custom.rega.queryeditor.AtomicWhereClause;
import com.pharmadm.custom.rega.queryeditor.ConfigurableWord;
import com.pharmadm.custom.rega.queryeditor.QueryContext;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.ComposedWordConfigurer;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.VisualizationComponentFactory;

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
		AtomicWhereClause firstClause = editPanel.getClause();
		return checkSignature(firstClause, clause, select);
	}
	
	private boolean checkSignature(AtomicWhereClause clause1, AtomicWhereClause clause2, boolean makeSelected) {
		if (clause1.getCompositionBehaviour().getClass().equals(clause2.getCompositionBehaviour().getClass()) &&
			clause1.getCompositionBehaviour().canCompose(clause1, clause2)) {

			VisualizationComponentFactory factory = editPanel.getEditor().getVisualizationComponentFactory();
			if (composedWordConfigurer == null) {
				List<ConfigurableWord> words = clause1.getCompositionBehaviour().getComposableWords(clause1);
				composedWordConfigurer = new WComposedVisualizationComponentFactory().createWord(clause1.getCompositionBehaviour(), factory.createComponents(words));
				getEditorPanel().createComposedWord(words, composedWordConfigurer);
			}

			List<ConfigurableWord> words2 = clause2.getCompositionBehaviour().getComposableWords(clause2);
			getEditorPanel().composeWord(factory.createComponents(words2), new WAtomicWhereClauseEditor(context, clause2), makeSelected);
			return true;
		}
		return false;
	}
}
