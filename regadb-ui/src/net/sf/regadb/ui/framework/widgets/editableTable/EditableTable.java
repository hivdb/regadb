package net.sf.regadb.ui.framework.widgets.editableTable;

import java.util.ArrayList;
import java.util.List;

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
    private WPushButton removeButton_;
    
    private WTable itemTable_;
    
    private IEditableTable<DataType> editableList_;
    
    private List<DataType> items_;
    
    private ArrayList<DataType> itemList_ = new ArrayList<DataType>();
    private ArrayList<DataType> removedItemList_ = new ArrayList<DataType>();
        
    public EditableTable(WContainerWidget parent, IEditableTable<DataType> editableList, List<DataType> items)
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
        removeButton_ = new WPushButton(tr("editableDataTable.button.removeItem"), buttonContainer);
        removeButton_.clicked.addListener(new SignalListener<WMouseEvent>()
                {
                    public void notify(WMouseEvent a)
                    {
                        removeItems();
                    }
                });
        
        addWidget(buttonContainer);
        
        //item table
        itemTable_ = new WTable(this);
        int headerPosition = itemTable_.numColumns()+1;
        for(String header : editableList_.getTableHeaders())
        {
            itemTable_.putElementAt(0, headerPosition, new TableHeader(tr(header)));
            headerPosition++;
        }
        
        for(DataType item : items_)
        {
            addItem(item);
        }
        
        if(editableList_.getInteractionState()==InteractionState.Adding || editableList_.getInteractionState()==InteractionState.Editing)
        {
            addLine(editableList_.addRow(), true);
        }
        else
        {
            removeButton_.setHidden(true);
        }
    }
    
    private void removeItems()
    {
        ArrayList<Integer> indexes = new ArrayList<Integer>();
        for(int i = 1; i < itemTable_.numRows()-1; i++)
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
        addLine(editableList_.getWidgets(item), item==null);
        
        itemList_.add(item);
    }
    
    private void addLine(WWidget[] widgets, boolean lineToAdd)
    {
        int colIndex;
        int rowNum;
        
        rowNum = itemTable_.numRows();
        if((editableList_.getInteractionState()==InteractionState.Adding || editableList_.getInteractionState()==InteractionState.Editing) && !lineToAdd)
        {
            itemTable_.putElementAt(rowNum, 0, new WCheckBox());
        }
        colIndex = 1;
        for(WWidget widget : widgets)
        {
            widget.setParent(null);
            itemTable_.putElementAt(rowNum, colIndex, widget);
            colIndex++;
        }
        
        if(lineToAdd)
        {
            WPushButton addButton = new WPushButton(tr("editableDataTable.button.addItem"));
            itemTable_.putElementAt(rowNum, colIndex, addButton);
            addButton.clicked.addListener(new SignalListener<WMouseEvent>()
                    {
                        public void notify(WMouseEvent a) 
                        {
                            WWidget[] widgets = getWidgets(itemTable_.numRows()-1);
                            widgets = editableList_.fixAddRow(widgets);
                            itemTable_.deleteRow(itemTable_.numRows()-1);
                            addLine(widgets, false);
                            
                            addLine(editableList_.addRow(), true);
                            itemList_.add(null);
                        }
                    });
        }
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
        int size = editableList_.getTableHeaders().length;
        WWidget[] widgets = new WWidget[size];
        
        int processedFields = 0;
        for(int i =1 ; i < itemTable_.numColumns() && processedFields<size; i++)
        {
            widgets[i-1] = itemTable_.elementAt(row, i).children().get(0);
            processedFields++;
        }
        
        return widgets;
    }
    
    public WWidget[] getAllWidgets(int column)
    {
        WWidget[] widgets;
        if(itemTable_.numRows()==0)
        {
            widgets = new WWidget[0];
        }
        else
        {
            boolean addButton = itemTable_.elementAt(itemTable_.numRows()-1, itemTable_.numColumns()-1).children().get(0) instanceof WPushButton;
            widgets = new WWidget[itemTable_.numRows()-1-(addButton?1:0)];
            
            for(int i = 1; i < widgets.length; i++)
            {
                widgets[i-1] = itemTable_.elementAt(i, column+1).children().get(0);
            }
        }
        return widgets;
    }
}
