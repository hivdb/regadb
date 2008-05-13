package com.pharmadm.custom.rega.awccomposition;

import java.util.ArrayList;
import java.util.List;

import com.pharmadm.custom.rega.queryeditor.AtomicWhereClause;
import com.pharmadm.custom.rega.queryeditor.CompositionBehaviour;
import com.pharmadm.custom.rega.queryeditor.ConfigurableWord;


public class CustomAttributeComposition extends CompositionBehaviour {

	public boolean canCompose(AtomicWhereClause signatureClause, AtomicWhereClause clause) {
		return matches(signatureClause) && matches(clause);
	}

	public boolean matches(AtomicWhereClause clause) {
		return clause.getOutputVariables().size() == 1 && 
		clause.getConstants().size() == 2 &&
		clause.getInputVariables().size() == 1 &&
		(clause.getFromVariables().size() >= 0);	
	}

	public List<ConfigurableWord> getComposableWords(AtomicWhereClause clause) {
		List<ConfigurableWord> words = new ArrayList<ConfigurableWord>();
		words.add(clause.getOutputVariables().iterator().next());
		words.addAll(clause.getConstants());
		return words;
	}
}
