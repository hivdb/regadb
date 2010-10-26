package net.sf.regadb.ui.tree.items.query;

import net.sf.regadb.db.QueryDefinition;
import net.sf.regadb.ui.datatable.query.SelectQueryToolQueryForm;
import net.sf.regadb.ui.form.query.querytool.QueryToolForm;
import net.sf.regadb.ui.framework.forms.IForm;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.ObjectForm;
import net.sf.regadb.ui.framework.tree.TreeMenuNode;
import net.sf.regadb.ui.tree.ObjectTreeNode;
import net.sf.regadb.util.string.StringUtils;
import eu.webtoolkit.jwt.WString;

public class QueryToolNavigation extends ObjectTreeNode<QueryDefinition> {

	public QueryToolNavigation(TreeMenuNode parent) {
		super("query.querytool", parent);
	}

	@Override
	protected IForm createSelectionForm() {
		return new SelectQueryToolQueryForm(this);
	}

	@Override
	public String getArgument(QueryDefinition type) {
		return StringUtils.trimToLength(type.getName(), 10);
	}

	@Override
	protected ObjectForm<QueryDefinition> createForm(WString name,
			InteractionState interactionState, QueryDefinition selectedObject) {
		return new QueryToolForm(name, interactionState, QueryToolNavigation.this, selectedObject);
	}
}
