package net.sf.regadb.ui.tree.items.query;

import net.sf.regadb.db.QueryDefinition;
import net.sf.regadb.ui.framework.forms.IForm;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.ObjectForm;
import net.sf.regadb.ui.framework.tree.TreeMenuNode;
import net.sf.regadb.ui.tree.ObjectTreeNode;
import eu.webtoolkit.jwt.WString;

public class QueryDefinitionNavigation extends ObjectTreeNode<QueryDefinition>{

	public QueryDefinitionNavigation(TreeMenuNode parent) {
		super("query.definition", parent);
	}

	@Override
	protected IForm createSelectionForm() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getArgument(QueryDefinition type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected ObjectForm<QueryDefinition> createForm(WString name,
			InteractionState interactionState, QueryDefinition selectedObject) {
		// TODO Auto-generated method stub
		return null;
	}

}
