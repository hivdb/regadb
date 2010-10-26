package net.sf.regadb.ui.datatable.attributeSettings;

import net.sf.regadb.db.AttributeGroup;
import net.sf.regadb.ui.framework.forms.SelectForm;
import net.sf.regadb.ui.framework.widgets.datatable.DataTable;
import net.sf.regadb.ui.tree.ObjectTreeNode;

public class SelectAttributeGroupForm extends SelectForm<AttributeGroup>
{
    private DataTable<AttributeGroup> dataTable_;
    private IAttributeGroupDataTable dataTableI_;
    
    public SelectAttributeGroupForm(ObjectTreeNode<AttributeGroup> node)
    {
        super(tr("form.attributes.attributeGroups.selectAttributeGroupForm"),node);
        init();
    }

    public void init() 
    {
        dataTableI_ = new IAttributeGroupDataTable(this);
        dataTable_ = new DataTable<AttributeGroup>(dataTableI_, 10);
        addWidget(dataTable_);
    }
}
