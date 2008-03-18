package net.sf.regadb.ui.tree.items.singlePatient;

import net.sf.regadb.ui.framework.forms.action.ITreeAction;
import net.sf.regadb.ui.framework.tree.TreeMenuNode;
import net.sf.regadb.ui.tree.GenericSelectedItem;
import net.sf.witty.wt.i8n.WMessage;
import net.sf.witty.wt.widgets.extra.WTreeNode;

public class ActionItem extends TreeMenuNode
{
    private ITreeAction action_;
    
	public ActionItem(WMessage text, WTreeNode root, ITreeAction action)
	{
		super(text, root);
        action_ = action;
	}
    
    public ActionItem(WMessage text, WTreeNode root)
    {
        this(text, root, null);
    }

	@Override
	public ITreeAction getFormAction()
	{  
	    //automatically go to select if the "root" element of a category is selected
	    if(action_==null) {
	        GenericSelectedItem selectedItem = null;
	        for(TreeMenuNode node : getChildren()) {
	            if(node instanceof GenericSelectedItem) {
	                selectedItem = (GenericSelectedItem)node;
	                break;
	            }
	        }
	        final GenericSelectedItem finalSelectedItem = selectedItem;
	        if(selectedItem!=null) {
	            return new ITreeAction() {
                    public void performAction(TreeMenuNode node) {
                        if(finalSelectedItem.getSelectedItem()==null)
                            getChildren().get(0).prograSelectNode();
                        else 
                            finalSelectedItem.getChildren().get(0).prograSelectNode();
                    }
	            };
	        } else {
	            return new ITreeAction() {
	                public void performAction(TreeMenuNode node) {
	                    getChildren().get(0).prograSelectNode();
	                }
	            };
	        }
	    } else {
	        return action_;
        }
	}

	@Override
	public boolean isEnabled()
	{
		return getParent().isEnabled();
	}
}
