package net.sf.regadb.ui.datatable.testSettings;

import net.sf.regadb.db.ResistanceInterpretationTemplate;
import net.sf.regadb.ui.framework.forms.SelectForm;
import net.sf.regadb.ui.framework.widgets.datatable.DataTable;
import net.sf.regadb.ui.tree.ObjectTreeNode;

public class SelectResRepTemplateForm extends SelectForm<ResistanceInterpretationTemplate>
{
    public SelectResRepTemplateForm(ObjectTreeNode<ResistanceInterpretationTemplate> node)
    {
        super(tr("form.resistance.report.template.selectResRepTemplateForm"), node);
    }

    public DataTable<ResistanceInterpretationTemplate> createDataTable() 
    {
        return new DataTable<ResistanceInterpretationTemplate>(new IResRepTemplateDataTable(this), 10);
    }
}
