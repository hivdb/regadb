package net.sf.regadb.ui.tree.items.importTool;

import net.sf.regadb.ui.form.importTool.data.ImportDefinition;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.tree.GenericSelectedItem;
import eu.webtoolkit.jwt.WTreeNode;

public class ImportToolSelectedItem extends GenericSelectedItem<ImportDefinition> {
	public ImportToolSelectedItem(WTreeNode parent)
	{
		super(parent, "menu.importTool.selected");
	}
	
	@Override
	public String getArgument(ImportDefinition type) {
		return RegaDBMain.getApp().getTree().getTreeContent().importToolSelected.getSelectedItem().getDescription();
	}
}
