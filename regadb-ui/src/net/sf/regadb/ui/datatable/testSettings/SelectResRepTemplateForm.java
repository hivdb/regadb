package net.sf.regadb.ui.datatable.testSettings;

import net.sf.regadb.db.ResistanceInterpretationTemplate;
import net.sf.regadb.ui.framework.forms.SelectForm;
import net.sf.regadb.ui.framework.widgets.datatable.DataTable;
import net.sf.regadb.ui.tree.ObjectTreeNode;

public class SelectResRepTemplateForm extends SelectForm<ResistanceInterpretationTemplate>
{
    private DataTable<ResistanceInterpretationTemplate> dataTable_;
    private IResRepTemplateDataTable dataTableI_;
    
    public SelectResRepTemplateForm(ObjectTreeNode<ResistanceInterpretationTemplate> node)
    {
        super(tr("form.resistance.report.template.selectResRepTemplateForm"), node);
        init();
    }

    public void init() 
    {
        dataTableI_ = new IResRepTemplateDataTable(this);
        dataTable_ = new DataTable<ResistanceInterpretationTemplate>(dataTableI_, 10);
        addWidget(dataTable_);
    }
}
