package net.sf.regadb.ui.datatable.log;

import java.io.File;

import net.sf.regadb.ui.framework.forms.SelectForm;
import net.sf.regadb.ui.framework.widgets.datatable.DataTable;
import net.sf.regadb.ui.framework.widgets.datatable.FileDataTable;

public class SelectLogForm extends SelectForm {
    private DataTable<File> dataTable_;
    private ILogDataTable dataTableI_;

    public SelectLogForm(){
        super(tr("log.form"));
        init();
    }
    

    public void init(){
        dataTableI_ = new ILogDataTable();
        dataTable_ = new FileDataTable<File>(dataTableI_, 10);
        addWidget(dataTable_);
    }
}
