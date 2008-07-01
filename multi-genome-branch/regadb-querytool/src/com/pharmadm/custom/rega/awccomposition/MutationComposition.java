package com.pharmadm.custom.rega.awccomposition;

import java.util.List;

import com.pharmadm.custom.rega.queryeditor.AtomicWhereClause;
import com.pharmadm.custom.rega.queryeditor.CompositionBehaviour;
import com.pharmadm.custom.rega.queryeditor.ConfigurableWord;
import com.pharmadm.custom.rega.queryeditor.FixedString;
import com.pharmadm.custom.rega.queryeditor.InputVariable;
import com.pharmadm.custom.rega.queryeditor.constant.MutationConstant;

public class MutationComposition extends CompositionBehaviour {

	@Override
	public boolean canCompose(AtomicWhereClause signatureClause,
			AtomicWhereClause clause) {
		return matches(signatureClause) && matches(clause);
	}

	@Override
	public List<ConfigurableWord> getComposableWords(AtomicWhereClause clause) {
		return clause.getVisualizationClauseList().getWords().subList(2, 4);
	}

	@Override
	public boolean matches(AtomicWhereClause clause) {
		return clause.getVisualizationClauseList().getWords().size() == 4 &&
		clause.getVisualizationClauseList().getWords().get(1) instanceof InputVariable &&
		clause.getInputVariables().iterator().next().getObject().isTable() &&
		clause.getVisualizationClauseList().getWords().get(2) instanceof FixedString &&				
		clause.getVisualizationClauseList().getWords().get(3) instanceof MutationConstant;
	}

}
