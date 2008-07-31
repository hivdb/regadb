package net.sf.regadb.ui.tree.items.testSettings;

import net.sf.regadb.db.ResistanceInterpretationTemplate;
import net.sf.regadb.ui.tree.GenericSelectedItem;
import net.sf.witty.wt.widgets.extra.WTreeNode;

public class ResRepTemplateSelectedItem extends GenericSelectedItem<ResistanceInterpretationTemplate> 
{
    public ResRepTemplateSelectedItem(WTreeNode parent) 
    {
        super(parent, "report.form", "{resRepTemplateSelectedItem}");
    }

    @Override
    public String getArgument(ResistanceInterpretationTemplate type) 
    {
        return type.getName();
    }
}
