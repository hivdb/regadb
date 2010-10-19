package net.sf.regadb.ui.tree.items.query;

import net.sf.regadb.ui.form.query.custom.NadirQuery;
import net.sf.regadb.ui.framework.forms.IForm;
import net.sf.regadb.ui.framework.tree.TreeMenuNode;
import net.sf.regadb.ui.tree.DefaultNavigationNode;
import net.sf.regadb.ui.tree.FormNavigationNode;
import eu.webtoolkit.jwt.WString;

public class CustomQueryNavigation extends DefaultNavigationNode {

	public CustomQueryNavigation(TreeMenuNode parent) {
		super(WString.tr("menu.query.custom"), parent);
		
		new FormNavigationNode(WString.tr("form.query.custom.nadir.name"), this) {
			@Override
			public IForm createForm() {
				return new NadirQuery();
			}
		};
	}

}
