package net.sf.regadb.ui.datatable.testSettings;

import net.sf.regadb.db.TestType;
import net.sf.regadb.ui.framework.forms.SelectForm;
import net.sf.regadb.ui.framework.widgets.datatable.DataTable;
import net.sf.regadb.ui.tree.ObjectTreeNode;

public class SelectTestTypeForm extends SelectForm<TestType>
{
    public SelectTestTypeForm(ObjectTreeNode<TestType> node)
    {
        super(tr("form.testSettings.testType.selectForm"),node);
    }

    public DataTable<TestType> createDataTable()
    {
        return new DataTable<TestType>(new ITestTypeDataTable(this), 10);
    }
}
