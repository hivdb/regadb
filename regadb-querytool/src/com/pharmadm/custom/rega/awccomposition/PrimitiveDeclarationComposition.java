package com.pharmadm.custom.rega.awccomposition;

import java.util.List;

import com.pharmadm.custom.rega.queryeditor.AtomicWhereClause;
import com.pharmadm.custom.rega.queryeditor.CompositionBehaviour;
import com.pharmadm.custom.rega.queryeditor.ConfigurableWord;
import com.pharmadm.custom.rega.queryeditor.OutputVariable;
import com.pharmadm.custom.rega.queryeditor.constant.Constant;

public class PrimitiveDeclarationComposition extends CompositionBehaviour{
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
		clause.getVisualizationClauseList().getWords().get(1) instanceof OutputVariable &&
		clause.getOutputVariables().iterator().next().getObject().isPrimitive() &&
		clause.getVisualizationClauseList().getWords().get(3) instanceof Constant;			
	}

	@Override
	public List<ConfigurableWord> getKeyWords(AtomicWhereClause clause) {
		return clause.getVisualizationClauseList().getWords().subList(1, 2);
	}
}
