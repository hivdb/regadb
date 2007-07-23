package net.sf.regadb.ui.datatable.attributeSettings;

import net.sf.regadb.db.AttributeGroup;
import net.sf.regadb.ui.framework.forms.SelectForm;
import net.sf.regadb.ui.framework.widgets.datatable.DataTable;

public class SelectAttributeGroupForm extends SelectForm
{
    private DataTable<AttributeGroup> dataTable_;
    private IAttributeGroupDataTable dataTableI_;
    
    public SelectAttributeGroupForm()
    {
        super(tr("form.attributes.attributeGroups.selectAttributeGroupForm"));
        init();
    }

    public void init() 
    {
        dataTableI_ = new IAttributeGroupDataTable();
        dataTable_ = new DataTable<AttributeGroup>(dataTableI_, 10);
        addWidget(dataTable_);
    }
}
