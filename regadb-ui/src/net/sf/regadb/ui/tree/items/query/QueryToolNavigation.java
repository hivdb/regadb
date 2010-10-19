package net.sf.regadb.ui.tree.items.query;

import net.sf.regadb.db.QueryDefinition;
import net.sf.regadb.ui.datatable.query.SelectQueryToolQueryForm;
import net.sf.regadb.ui.form.query.querytool.QueryToolForm;
import net.sf.regadb.ui.framework.forms.IForm;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.tree.TreeMenuNode;
import net.sf.regadb.ui.tree.ObjectTreeNode;
import net.sf.regadb.util.string.StringUtils;

public class QueryToolNavigation extends ObjectTreeNode<QueryDefinition> {

	public QueryToolNavigation(TreeMenuNode parent) {
		super("query.querytool", parent);
	}

	@Override
	protected IForm createForm(InteractionState interactionState) {
		return new QueryToolForm(getFormResource(getStateName(interactionState)),interactionState);
	}

	@Override
	protected IForm createSelectionForm() {
		return new SelectQueryToolQueryForm();
	}

	@Override
	public String getArgument(QueryDefinition type) {
		return StringUtils.trimToLength(type.getName(), 10);
	}
}
