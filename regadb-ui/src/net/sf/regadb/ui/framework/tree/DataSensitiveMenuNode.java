package net.sf.regadb.ui.framework.tree;

import net.sf.regadb.db.Dataset;
import net.sf.regadb.db.session.Login;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.action.ITreeAction;
import eu.webtoolkit.jwt.WTreeNode;

public class DataSensitiveMenuNode extends TreeMenuNode {

	public DataSensitiveMenuNode(String intlText, WTreeNode root) {
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
	public boolean isDisabled() {
		Login login = RegaDBMain.getApp().getLogin();
        if(login!=null)
        {
            Dataset ds = RegaDBMain.getApp().getTree().getTreeContent().datasetSelected.getSelectedItem();
            
            if( ds != null )
            {
                return !ds.getSettingsUser().getUid().equals(login.getUid());
            }
            else
            {
                return true;
            }
        }
        else
        {
            return true;
        }
	}

}
