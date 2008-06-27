package net.sf.regadb.ui.datatable.testSettings;

import net.sf.regadb.db.ResistanceInterpretationTemplate;
import net.sf.regadb.ui.framework.forms.SelectForm;
import net.sf.regadb.ui.framework.widgets.datatable.DataTable;

public class SelectResRepTemplateForm extends SelectForm
{
    private DataTable<ResistanceInterpretationTemplate> dataTable_;
    private IResRepTemplateDataTable dataTableI_;
    
    public SelectResRepTemplateForm()
    {
        super(tr("form.resistance.report.template.selectResRepTemplateForm"));
        init();
    }

    public void init() 
    {
        dataTableI_ = new IResRepTemplateDataTable();
        dataTable_ = new DataTable<ResistanceInterpretationTemplate>(dataTableI_, 10);
        addWidget(dataTable_);
    }
}
