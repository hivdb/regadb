package net.sf.regadb.ui.tree;

import net.sf.regadb.ui.framework.tree.TreeMenuNode;
import eu.webtoolkit.jwt.WString;

public abstract class SelectedItemNavigationNode<Type> extends DefaultNavigationNode{

	public SelectedItemNavigationNode(WString name, TreeMenuNode parent) {
		super(name, parent);
	}

	public abstract Type getSelectedItem(); 
	
	public boolean isDisabled(){
		return super.isDisabled() || getSelectedItem() == null;
	}
}
