package net.sf.regadb.ui.datatable.attribute;

import net.sf.regadb.db.Attribute;
import net.sf.regadb.ui.framework.forms.IForm;
import net.sf.regadb.ui.framework.forms.fields.IFormField;
import net.sf.regadb.ui.framework.widgets.datatable.DataTable;
import net.sf.witty.wt.widgets.WContainerWidget;
import net.sf.witty.wt.widgets.WGroupBox;

public class SelectAttributeForm extends WGroupBox implements IForm
{
    private DataTable<Attribute> dataTable_;
    private IAttributeDataTable dataTableI_;
    
    public SelectAttributeForm()
    {
        super(tr("form.attributes.attribute.selectAttributeForm"));
        init();
    }
    
    public void addFormField(IFormField field)
    {
        
    }

    public WContainerWidget getWContainer()
    {
        return this;
    }

    public void init() 
    {
        dataTableI_ = new IAttributeDataTable();
        dataTable_ = new DataTable<Attribute>(dataTableI_, 10);
        addWidget(dataTable_);
    }
}
