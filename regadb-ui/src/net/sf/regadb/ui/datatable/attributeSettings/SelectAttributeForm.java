package net.sf.regadb.ui.datatable.attributeSettings;

import net.sf.regadb.db.Attribute;
import net.sf.regadb.ui.framework.forms.SelectForm;
import net.sf.regadb.ui.framework.widgets.datatable.DataTable;
import net.sf.regadb.ui.tree.ObjectTreeNode;

public class SelectAttributeForm extends SelectForm<Attribute>
{
    private DataTable<Attribute> dataTable_;
    private IAttributeDataTable dataTableI_;
    
    public SelectAttributeForm(ObjectTreeNode<Attribute> node)
    {
        super(tr("form.attributes.attribute.selectAttributeForm"), node);
        init();
    }

    public void init() 
    {
        dataTableI_ = new IAttributeDataTable(this);
        dataTable_ = new DataTable<Attribute>(dataTableI_, 10);
        addWidget(dataTable_);
    }
}
