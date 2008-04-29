package com.pharmadm.custom.rega.awccomposition;

import java.util.ArrayList;
import java.util.List;

import com.pharmadm.custom.rega.queryeditor.AtomicWhereClause;
import com.pharmadm.custom.rega.queryeditor.CompositionBehaviour;
import com.pharmadm.custom.rega.queryeditor.ConfigurableWord;
import com.pharmadm.custom.rega.queryeditor.WordConfigurer;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.JAttributeConfigurer;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.JCombinedConfigurer;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.JComposedOutputVariableConfigurer;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.JOutputVariableConfigurer;


public class CustomAttributeComposition implements CompositionBehaviour {

	@Override
	public boolean canCompose(AtomicWhereClause signatureClause, AtomicWhereClause clause) {
		return matches(signatureClause) && matches(clause);
	}

	@Override
	public boolean matches(AtomicWhereClause clause) {
		return clause.getOutputVariables().size() == 1 && 
		clause.getConstants().size() == 2 &&
		clause.getInputVariables().size() == 1 &&
		(clause.getFromVariables().size() >= 0);	
	}

	@Override
	public List<ConfigurableWord> getComposableWords(AtomicWhereClause clause) {
		List<ConfigurableWord> words = new ArrayList<ConfigurableWord>();
		words.add(clause.getOutputVariables().iterator().next());
		words.addAll(clause.getConstants());
		return words;
	}

	@Override
	public WordConfigurer getWordConfigurer(List<WordConfigurer> configurers) {
		JCombinedConfigurer constants = new JCombinedConfigurer(configurers.subList(1, 3));
		JComposedOutputVariableConfigurer ovar = new JComposedOutputVariableConfigurer((JOutputVariableConfigurer) configurers.get(0));
		return new JAttributeConfigurer(ovar, constants);
	}

}
