package net.sf.regadb.ui.framework.widgets.editableTable;

import java.util.ArrayList;

import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.widgets.table.TableHeader;
import net.sf.witty.wt.SignalListener;
import net.sf.witty.wt.WCheckBox;
import net.sf.witty.wt.WContainerWidget;
import net.sf.witty.wt.WMouseEvent;
import net.sf.witty.wt.WPushButton;
import net.sf.witty.wt.WTable;
import net.sf.witty.wt.WWidget;
import net.sf.witty.wt.core.utils.WHorizontalAlignment;

public class EditableTable<DataType> extends WContainerWidget
{
    private WPushButton addButton_;
    private WPushButton removeButton_;
    
    private WTable itemTable_;
    
    private IEditableTable<DataType> editableList_;
    
    private ArrayList<DataType> items_;
    
    private ArrayList<DataType> itemList_ = new ArrayList<DataType>();
    private ArrayList<DataType> removedItemList_ = new ArrayList<DataType>();
        
    public EditableTable(WContainerWidget parent, IEditableTable<DataType> editableList, ArrayList<DataType> items)
    {
        super(parent);
        this.setStyleClass("editable-table");
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
            removedItemList_.add(itemList_.get(index-amountOfRowsAlreadyDeleted));
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
    
    public void saveData()
    {
        DataType dt;
        for(int i = 0; i < itemList_.size(); i++)
        {
            dt = itemList_.get(i);
            if(dt==null)
            {
                editableList_.addData(getWidgets(i+1));
            }
            else
            {
                editableList_.changeData(dt, getWidgets(i+1));
            }
        }
        
        for(DataType type : removedItemList_)
        {
            editableList_.deleteData(type);
        }
    }
    
    private WWidget[] getWidgets(int row)
    {
        WWidget[] widgets = new WWidget[itemTable_.numColumns()-1];
        
        for(int i = 1; i < itemTable_.numColumns(); i++)
        {
            widgets[i-1] = itemTable_.elementAt(row, i).children().get(0);
        }
        
        return widgets;
    }
}
