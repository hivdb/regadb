package net.sf.regadb.ui.datatable.log;

import java.io.File;

import net.sf.regadb.ui.framework.forms.SelectForm;
import net.sf.regadb.ui.framework.widgets.datatable.DataTable;
import net.sf.regadb.ui.framework.widgets.datatable.FileDataTable;
import net.sf.regadb.ui.tree.ObjectTreeNode;

public class SelectLogForm extends SelectForm<File> {
    private DataTable<File> dataTable_;
    private ILogDataTable dataTableI_;

    public SelectLogForm(ObjectTreeNode<File> node){
        super(tr("form.log.selectLogForm"),node);
        init();
    }
    

    public void init(){
        dataTableI_ = new ILogDataTable(this);
        dataTable_ = new FileDataTable<File>(dataTableI_, 10);
        addWidget(dataTable_);
    }
}
