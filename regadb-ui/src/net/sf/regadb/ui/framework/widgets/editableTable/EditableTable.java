package net.sf.regadb.ui.framework.widgets.editableTable;

import java.util.ArrayList;

import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.widgets.table.TableHeader;
import net.sf.witty.wt.core.utils.WHorizontalAlignment;
import net.sf.witty.wt.widgets.SignalListener;
import net.sf.witty.wt.widgets.WCheckBox;
import net.sf.witty.wt.widgets.WContainerWidget;
import net.sf.witty.wt.widgets.WPushButton;
import net.sf.witty.wt.widgets.WTable;
import net.sf.witty.wt.widgets.WWidget;
import net.sf.witty.wt.widgets.event.WMouseEvent;

public class EditableTable<DataType> extends WContainerWidget
{
    private WPushButton addButton_;
    private WPushButton removeButton_;
    
    private WTable itemTable_;
    
    private IEditableTable<DataType> editableList_;
    
    private ArrayList<DataType> items_;
    
    private ArrayList<DataType> itemList_ = new ArrayList<DataType>();
    
    public EditableTable(WContainerWidget parent, IEditableTable<DataType> editableList, ArrayList<DataType> items)
    {
        super(parent);
        editableList_ = editableList;
        items_ = items;
        init();
    }
    
    public void init()
    {
        //buttons
        WContainerWidget buttonContainer = new WContainerWidget(this);
        buttonContainer.setContentAlignment(WHorizontalAlignment.AlignRight);
        addButton_ = new WPushButton(tr("editableDataTable.button.addItem"), buttonContainer);
        addButton_.clicked.addListener(new SignalListener<WMouseEvent>()
                {
                    public void notify(WMouseEvent a)
                    {
                        addItem(null);
                    }
                });
        removeButton_ = new WPushButton(tr("editableDataTable.button.removeItem"), buttonContainer);
        removeButton_.clicked.addListener(new SignalListener<WMouseEvent>()
                {
                    public void notify(WMouseEvent a)
                    {
                        removeItems();
                    }
                });
        
        if(editableList_.getInteractionState()==InteractionState.Viewing)
        {
            addButton_.setHidden(true);
            removeButton_.setHidden(true);
        }
        
        addWidget(buttonContainer);
        
        //item table
        itemTable_ = new WTable(this);
        for(String header : editableList_.getTableHeaders())
        {
            itemTable_.putElementAt(0, itemTable_.numColumns()+1, new TableHeader(tr(header)));
        }
        
        for(DataType item : items_)
        {
            addItem(item);
        }
    }
    
    private void removeItems()
    {
        ArrayList<Integer> indexes = new ArrayList<Integer>();
        for(int i = 1; i < itemTable_.numRows(); i++)
        {
            if(((WCheckBox)itemTable_.elementAt(i, 0).children().get(0)).isChecked())
            {
                indexes.add(i-1);
            }
        }
        
        int amountOfRowsAlreadyDeleted = 0;
        for(Integer index : indexes)
        {
            itemTable_.deleteRow(index+1-amountOfRowsAlreadyDeleted);
            itemList_.remove(index-amountOfRowsAlreadyDeleted);
            amountOfRowsAlreadyDeleted++;
        }
    }
    
    private void addItem(DataType item)
    {
        int colIndex;
        int rowNum;
        
        rowNum = itemTable_.numRows();
        if(editableList_.getInteractionState()==InteractionState.Adding || editableList_.getInteractionState()==InteractionState.Editing)
        {
            itemTable_.putElementAt(rowNum, 0, new WCheckBox());
        }
        colIndex = 1;
        for(WWidget widget : editableList_.getWidgets(item))
        {
            itemTable_.putElementAt(rowNum, colIndex, widget);
            colIndex++;
        }
        itemList_.add(item);
    }
}
