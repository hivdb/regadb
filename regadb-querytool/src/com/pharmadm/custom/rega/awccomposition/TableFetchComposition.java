package com.pharmadm.custom.rega.awccomposition;

import java.util.ArrayList;
import java.util.List;

import com.pharmadm.custom.rega.queryeditor.wordconfiguration.JComposedOutputVariableConfigurer;
import com.pharmadm.custom.rega.queryeditor.AtomicWhereClause;
import com.pharmadm.custom.rega.queryeditor.CompositionBehaviour;
import com.pharmadm.custom.rega.queryeditor.ConfigurableWord;
import com.pharmadm.custom.rega.queryeditor.WordConfigurer;

public class TableFetchComposition implements CompositionBehaviour {

	@Override
	public boolean canCompose(AtomicWhereClause signatureClause, AtomicWhereClause clause) {
		return matches(signatureClause) && matches(clause);
	}

	@Override
	public boolean matches(AtomicWhereClause clause) {
		return clause.getOutputVariables().size() == 1 && 
		clause.getConstants().size() == 0 &&
		clause.getInputVariables().size() == 1 &&
		clause.getOutputVariables().iterator().next().getVariableType().isTable();	
	}

	@Override
	public List<ConfigurableWord> getComposableWords(AtomicWhereClause clause) {
		List<ConfigurableWord> words = new ArrayList<ConfigurableWord>();
		words.add(clause.getOutputVariables().iterator().next());
		return words;
	}

	@Override
	public WordConfigurer getWordConfigurer(List<WordConfigurer> configurers) {
		return new JComposedOutputVariableConfigurer(configurers.get(0));
	}

}
