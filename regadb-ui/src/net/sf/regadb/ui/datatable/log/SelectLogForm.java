package net.sf.regadb.ui.datatable.log;

import java.io.File;

import net.sf.regadb.ui.framework.forms.SelectForm;
import net.sf.regadb.ui.framework.widgets.datatable.DataTable;
import net.sf.regadb.ui.framework.widgets.datatable.FileDataTable;
import net.sf.regadb.ui.tree.ObjectTreeNode;

public class SelectLogForm extends SelectForm<File> {
    public SelectLogForm(ObjectTreeNode<File> node){
        super(tr("form.log.selectLogForm"),node);
    }

    protected DataTable<File> createDataTable(){
        return new FileDataTable<File>(new ILogDataTable(this), 10);
    }
}
