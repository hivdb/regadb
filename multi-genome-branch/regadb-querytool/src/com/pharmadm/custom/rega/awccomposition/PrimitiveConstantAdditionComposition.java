package com.pharmadm.custom.rega.awccomposition;

import java.util.List;

import com.pharmadm.custom.rega.queryeditor.AtomicWhereClause;
import com.pharmadm.custom.rega.queryeditor.CompositionBehaviour;
import com.pharmadm.custom.rega.queryeditor.ConfigurableWord;
import com.pharmadm.custom.rega.queryeditor.InputVariable;
import com.pharmadm.custom.rega.queryeditor.OutputVariable;
import com.pharmadm.custom.rega.queryeditor.constant.Constant;
import com.pharmadm.custom.rega.queryeditor.constant.OperatorConstant;

public class PrimitiveConstantAdditionComposition extends CompositionBehaviour {

	@Override
	public boolean canCompose(AtomicWhereClause signatureClause,
			AtomicWhereClause clause) {
		return matches(signatureClause) && matches(clause);
	}

	@Override
	public List<ConfigurableWord> getComposableWords(AtomicWhereClause clause) {
		return clause.getVisualizationClauseList().getWords().subList(1, 7);
	}

	@Override
	public boolean matches(AtomicWhereClause clause) {
		return clause.getVisualizationClauseList().getWords().size() == 7 &&
		clause.getVisualizationClauseList().getWords().get(1) instanceof OutputVariable &&
		clause.getOutputVariables().iterator().next().getObject().isPrimitive() &&
		clause.getVisualizationClauseList().getWords().get(3) instanceof InputVariable &&
		clause.getInputVariables().iterator().next().getObject().isPrimitive() &&
		clause.getVisualizationClauseList().getWords().get(4) instanceof OperatorConstant &&			
		clause.getVisualizationClauseList().getWords().get(5) instanceof Constant;			
	}

}
