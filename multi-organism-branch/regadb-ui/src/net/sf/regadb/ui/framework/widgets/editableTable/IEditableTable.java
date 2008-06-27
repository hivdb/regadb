package net.sf.regadb.ui.framework.widgets.editableTable;

import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.witty.wt.WWidget;

public interface IEditableTable<DataType>
{
    public WWidget[] getWidgets(DataType type);
    public void changeData(DataType type, WWidget[] widgets);
    public void addData(WWidget[] widgets);
    public void deleteData(DataType type);
    public String[] getTableHeaders();
    public WWidget[] addRow();
    public WWidget[] fixAddRow(WWidget[] widgets);
    public InteractionState getInteractionState();
    public void flush();
}
