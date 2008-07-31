package net.sf.regadb.ui.form.log;

import java.io.File;

import net.sf.regadb.ui.tree.GenericSelectedItem;
import net.sf.witty.wt.widgets.extra.WTreeNode;

public class LogSelectedItem extends GenericSelectedItem<File>
{
    public LogSelectedItem(WTreeNode parent) 
    {
        super(parent, "menu.log.logSelectedItem", "{logSelectedItem}");
    }

    @Override
    public String getArgument(File type) 
    {
        return type.getName();
    }
}
