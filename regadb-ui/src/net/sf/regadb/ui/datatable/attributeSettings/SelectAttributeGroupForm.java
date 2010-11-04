package net.sf.regadb.ui.datatable.attributeSettings;

import net.sf.regadb.db.AttributeGroup;
import net.sf.regadb.ui.framework.forms.SelectForm;
import net.sf.regadb.ui.framework.widgets.datatable.DataTable;
import net.sf.regadb.ui.tree.ObjectTreeNode;

public class SelectAttributeGroupForm extends SelectForm<AttributeGroup>
{
    public SelectAttributeGroupForm(ObjectTreeNode<AttributeGroup> node)
    {
        super(tr("form.attributes.attributeGroups.selectAttributeGroupForm"),node);
    }

    public DataTable<AttributeGroup> createDataTable() 
    {
        return new DataTable<AttributeGroup>(new IAttributeGroupDataTable(this), 10);
    }
}
