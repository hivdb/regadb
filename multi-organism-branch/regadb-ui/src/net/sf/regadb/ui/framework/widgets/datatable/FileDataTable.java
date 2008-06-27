package net.sf.regadb.ui.framework.widgets.datatable;

import java.io.File;

public class FileDataTable<DataType> extends DataTable<DataType> {
    public FileDataTable(IDataTable<DataType> dataTableInterface, int amountOfPageRows){
        super(dataTableInterface,amountOfPageRows);
    }
    
    @Override
    public boolean stillExists(Object obj) {
        return ((File)obj).exists();
    }
}
