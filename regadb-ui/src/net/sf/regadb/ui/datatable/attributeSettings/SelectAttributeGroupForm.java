package net.sf.regadb.ui.datatable.attributeSettings;

import net.sf.regadb.db.AttributeGroup;
import net.sf.regadb.ui.framework.forms.IForm;
import net.sf.regadb.ui.framework.forms.fields.IFormField;
import net.sf.regadb.ui.framework.widgets.datatable.DataTable;
import net.sf.witty.wt.WContainerWidget;
import net.sf.witty.wt.WGroupBox;

public class SelectAttributeGroupForm extends WGroupBox implements IForm
{
    private DataTable<AttributeGroup> dataTable_;
    private IAttributeGroupDataTable dataTableI_;
    
    public SelectAttributeGroupForm()
    {
        super(tr("form.attributes.attributeGroups.selectAttributeGroupForm"));
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
        dataTableI_ = new IAttributeGroupDataTable();
        dataTable_ = new DataTable<AttributeGroup>(dataTableI_, 10);
        addWidget(dataTable_);
    }
}
