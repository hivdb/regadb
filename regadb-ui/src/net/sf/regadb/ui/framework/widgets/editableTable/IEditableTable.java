package net.sf.regadb.ui.framework.widgets.editableTable;

import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.witty.wt.widgets.WWidget;

public interface IEditableTable<DataType>
{
    //this method gets a null value when adding an emtpy row
    public WWidget[] getWidgets(DataType type);
    public void changeData(DataType type, WWidget[] widgets);
    public void addData(WWidget[] widgets);
    public void deleteData(DataType type);
    public String[] getTableHeaders();
    public InteractionState getInteractionState();
}
