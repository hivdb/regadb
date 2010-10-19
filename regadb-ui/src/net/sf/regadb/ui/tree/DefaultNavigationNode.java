package net.sf.regadb.ui.tree;

import net.sf.regadb.ui.framework.tree.TreeMenuNode;
import eu.webtoolkit.jwt.WString;
import eu.webtoolkit.jwt.WTreeNode;

public class DefaultNavigationNode extends TreeMenuNode {

	public DefaultNavigationNode(WString name, TreeMenuNode parent) {
		super(name, parent);
	}

	@Override
	public void doAction() {
		if(getChildNodes().size() > 0){
			TreeMenuNode s = null;
			
			for(WTreeNode n : getChildNodes()){
				n.setDisabled(false);
				n.refresh();
				
				if(s == null && !n.isDisabled())
					s = (TreeMenuNode)n;
			}
			
			if(s!=null)
				s.selectNode();
		}
	}

}
