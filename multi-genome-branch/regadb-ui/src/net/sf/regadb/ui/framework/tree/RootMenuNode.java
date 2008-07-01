package net.sf.regadb.ui.framework.tree;

import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.action.ITreeAction;
import net.sf.witty.wt.widgets.extra.WTreeNode;

public class RootMenuNode extends TreeMenuNode {
	
	public RootMenuNode(String intlText, WTreeNode root) {
		super(tr(intlText), root);
	}
	
	@Override
	public ITreeAction getFormAction() {
		return new ITreeAction()
		{
			public void performAction(TreeMenuNode node)
			{
				// Empty ITreeAction needed for tree node to expand at click
			}
		};
	}
	
	@Override
	public boolean isEnabled() {
		return (RegaDBMain.getApp().getLogin() != null);
	}
	
}
