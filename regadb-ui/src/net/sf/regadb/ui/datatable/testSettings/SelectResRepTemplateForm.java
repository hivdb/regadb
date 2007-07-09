package net.sf.regadb.ui.datatable.testSettings;

import net.sf.regadb.db.ResistanceInterpretationTemplate;
import net.sf.regadb.ui.framework.forms.IForm;
import net.sf.regadb.ui.framework.forms.fields.IFormField;
import net.sf.regadb.ui.framework.widgets.datatable.DataTable;
import net.sf.witty.wt.WContainerWidget;
import net.sf.witty.wt.WGroupBox;

public class SelectResRepTemplateForm extends WGroupBox implements IForm
{
    private DataTable<ResistanceInterpretationTemplate> dataTable_;
    private IResRepTemplateDataTable dataTableI_;
    
    public SelectResRepTemplateForm()
    {
        super(tr("form.resistance.report.template.selectResRepTemplateForm"));
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
        dataTableI_ = new IResRepTemplateDataTable();
        dataTable_ = new DataTable<ResistanceInterpretationTemplate>(dataTableI_, 10);
        addWidget(dataTable_);
    }
}
