package net.sf.regadb.ui.datatable.query;

import net.sf.regadb.db.QueryDefinition;
import net.sf.regadb.ui.framework.forms.IForm;
import net.sf.regadb.ui.framework.forms.fields.IFormField;
import net.sf.regadb.ui.framework.widgets.datatable.DataTable;
import net.sf.witty.wt.WContainerWidget;
import net.sf.witty.wt.WGroupBox;

public class SelectQueryRunForm extends WGroupBox implements IForm
{
    private DataTable<QueryDefinition> dataTable_;
    private ISelectQueryRunDataTable dataTableI_;
    
    public SelectQueryRunForm()
    {
        super(tr("form.query.run.select"));
        
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
        dataTableI_ = new ISelectQueryRunDataTable();
        dataTable_ = new DataTable<QueryDefinition>(dataTableI_, 10);
        
        addWidget(dataTable_);
    }
}