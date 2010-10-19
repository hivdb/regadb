package net.sf.regadb.ui.framework.tree;

import net.sf.regadb.ui.framework.RegaDBMain;
import eu.webtoolkit.jwt.WTreeNode;

public class RootMenuNode extends TreeMenuNode {
	
	public RootMenuNode(String intlText, WTreeNode root) {
		super(tr(intlText), root);
	}
	
	@Override
	public boolean isDisabled() {
		return (RegaDBMain.getApp().getLogin() == null);
	}
	
	public void doAction(){
		
	}
}
