package net.sf.regadb.ui.datatable.attributeSettings;

import net.sf.regadb.db.Attribute;
import net.sf.regadb.ui.framework.forms.SelectForm;
import net.sf.regadb.ui.framework.widgets.datatable.DataTable;
import net.sf.regadb.ui.tree.ObjectTreeNode;

public class SelectAttributeForm extends SelectForm<Attribute>
{
    public SelectAttributeForm(ObjectTreeNode<Attribute> node)
    {
        super(tr("form.attributeSettings.attribute.selectForm"), node);
    }

    public DataTable<Attribute> createDataTable() 
    {
        return new DataTable<Attribute>(new IAttributeDataTable(this), 10);
    }
}
