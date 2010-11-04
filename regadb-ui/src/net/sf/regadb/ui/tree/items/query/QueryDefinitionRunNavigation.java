package net.sf.regadb.ui.tree.items.query;

import java.util.EnumSet;

import net.sf.regadb.db.QueryDefinitionRun;
import net.sf.regadb.ui.datatable.query.SelectQueryDefinitionRunForm;
import net.sf.regadb.ui.form.query.QueryDefinitionRunForm;
import net.sf.regadb.ui.framework.forms.IForm;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.ObjectForm;
import net.sf.regadb.ui.framework.tree.TreeMenuNode;
import net.sf.regadb.ui.tree.ObjectTreeNode;
import eu.webtoolkit.jwt.WString;

public class QueryDefinitionRunNavigation extends ObjectTreeNode<QueryDefinitionRun>{

	public QueryDefinitionRunNavigation(TreeMenuNode parent) {
		super("query.definition.run", parent,
				EnumSet.of(InteractionState.Viewing, InteractionState.Deleting));
	}

	@Override
	protected IForm createSelectionForm() {
		return new SelectQueryDefinitionRunForm(this);
	}

	@Override
	public String getArgument(QueryDefinitionRun type) {
		return type.getName();
	}

	@Override
	protected ObjectForm<QueryDefinitionRun> createForm(WString name,
			InteractionState interactionState, QueryDefinitionRun selectedObject) {
		return new QueryDefinitionRunForm(name, interactionState, this, selectedObject);
	}
}
