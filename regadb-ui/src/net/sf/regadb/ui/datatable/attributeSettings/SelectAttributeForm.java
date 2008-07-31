package net.sf.regadb.ui.datatable.attributeSettings;

import net.sf.regadb.db.Attribute;
import net.sf.regadb.ui.framework.forms.SelectForm;
import net.sf.regadb.ui.framework.widgets.datatable.DataTable;

public class SelectAttributeForm extends SelectForm
{
    private DataTable<Attribute> dataTable_;
    private IAttributeDataTable dataTableI_;
    
    public SelectAttributeForm()
    {
        super(tr("attribute.form"));
        init();
    }

    public void init() 
    {
        dataTableI_ = new IAttributeDataTable();
        dataTable_ = new DataTable<Attribute>(dataTableI_, 10);
        addWidget(dataTable_);
    }
}
