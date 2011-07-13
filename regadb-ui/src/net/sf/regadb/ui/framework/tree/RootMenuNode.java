package net.sf.regadb.ui.framework.tree;

import net.sf.regadb.ui.framework.RegaDBMain;
import eu.webtoolkit.jwt.WTreeNode;

public class RootMenuNode extends TreeMenuNode {
	
	public RootMenuNode() {
		super(tr("menu.root.rootItem"), null);
	}
	
	@Override
	public boolean isDisabled() {
		return (RegaDBMain.getApp().getLogin() == null);
	}
	
	public void doAction(){
		
	}

	@Override
	public boolean matchesInternalPath(String[] path, int depth) {
		return true;
	}
}
