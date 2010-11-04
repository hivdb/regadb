package net.sf.regadb.ui.tree.items.query;

import net.sf.regadb.db.QueryDefinition;
import net.sf.regadb.ui.datatable.query.SelectQueryDefinitionForm;
import net.sf.regadb.ui.form.query.QueryDefinitionForm;
import net.sf.regadb.ui.form.query.QueryDefinitionRunForm;
import net.sf.regadb.ui.framework.forms.IForm;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.ObjectForm;
import net.sf.regadb.ui.framework.tree.TreeMenuNode;
import net.sf.regadb.ui.tree.FormNavigationNode;
import net.sf.regadb.ui.tree.ObjectTreeNode;
import eu.webtoolkit.jwt.WString;

public class QueryDefinitionNavigation extends ObjectTreeNode<QueryDefinition>{

	public QueryDefinitionNavigation(TreeMenuNode parent, final QueryDefinitionRunNavigation runNode) {
		super("query.definition", parent);
		
		new FormNavigationNode(WString.tr("menu.query.definition.selected.run"), getSelectedItemNavigationNode()){
			public IForm createForm(){
                return new QueryDefinitionRunForm(WString.tr("form.query.definition.run.add"),
                		runNode,
                		QueryDefinitionNavigation.this.getSelectedItem());
			}
		};
	}

	@Override
	protected IForm createSelectionForm() {
		return new SelectQueryDefinitionForm(this);
	}

	@Override
	public String getArgument(QueryDefinition type) {
		return type.getName();
	}

	@Override
	protected ObjectForm<QueryDefinition> createForm(WString name,
			InteractionState interactionState, QueryDefinition selectedObject) {
		return new QueryDefinitionForm(name, interactionState, this, selectedObject);
	}

}
