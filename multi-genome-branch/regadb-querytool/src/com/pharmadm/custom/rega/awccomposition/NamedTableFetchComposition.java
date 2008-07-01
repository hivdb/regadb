package com.pharmadm.custom.rega.awccomposition;

import java.util.List;

import com.pharmadm.custom.rega.queryeditor.AtomicWhereClause;
import com.pharmadm.custom.rega.queryeditor.CompositionBehaviour;
import com.pharmadm.custom.rega.queryeditor.ConfigurableWord;
import com.pharmadm.custom.rega.queryeditor.FixedString;
import com.pharmadm.custom.rega.queryeditor.OutputVariable;
import com.pharmadm.custom.rega.queryeditor.constant.Constant;
import com.pharmadm.custom.rega.queryeditor.constant.OperatorConstant;

/**
 * table as a variable but with a given name
 * @author fromba0
 *
 */
public class NamedTableFetchComposition extends CompositionBehaviour {

	@Override
	public boolean canCompose(AtomicWhereClause signatureClause,
			AtomicWhereClause clause) {
		return matches(signatureClause) && matches(clause);
	}

	@Override
	public List<ConfigurableWord> getComposableWords(AtomicWhereClause clause) {
		return clause.getVisualizationClauseList().getWords().subList(1, 5);
	}

	@Override
	public boolean matches(AtomicWhereClause clause) {
		return clause.getVisualizationClauseList().getWords().size() == 5 &&
		clause.getVisualizationClauseList().getWords().get(1) instanceof OutputVariable &&
		clause.getOutputVariables().iterator().next().getObject().isTable() &&
		clause.getVisualizationClauseList().getWords().get(2) instanceof FixedString &&				
		clause.getVisualizationClauseList().getWords().get(3) instanceof OperatorConstant &&				
		clause.getVisualizationClauseList().getWords().get(4) instanceof Constant;			
	}
}
