package net.sf.regadb.ui.tree.items.datasetSettings;

import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.action.ITreeAction;
import net.sf.regadb.ui.framework.tree.TreeMenuNode;
import net.sf.witty.wt.widgets.extra.WTreeNode;

public class DatasetAccessItem extends TreeMenuNode
{
    public DatasetAccessItem(WTreeNode root)
    {
        super(tr("datasetaccess.form"), root);
    }
    
    @Override
    public ITreeAction getFormAction()
    {
        return null;
    }

    @Override
    public boolean isEnabled()
    {
        return RegaDBMain.getApp().getLogin()!=null;
    }
}
