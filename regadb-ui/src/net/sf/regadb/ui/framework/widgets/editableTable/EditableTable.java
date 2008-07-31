package net.sf.regadb.ui.framework.widgets.editableTable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.fields.FormField;
import net.sf.regadb.ui.framework.forms.fields.IFormField;
import net.sf.regadb.ui.framework.forms.fields.TextField;
import net.sf.regadb.ui.framework.widgets.messagebox.MessageBox;
import net.sf.regadb.ui.framework.widgets.table.TableHeader;
import net.sf.witty.wt.SignalListener;
import net.sf.witty.wt.WContainerWidget;
import net.sf.witty.wt.WEmptyEvent;
import net.sf.witty.wt.WMouseEvent;
import net.sf.witty.wt.WPushButton;
import net.sf.witty.wt.WTable;
import net.sf.witty.wt.WWidget;
import net.sf.witty.wt.core.utils.WLength;
import net.sf.witty.wt.core.utils.WLengthUnit;
import net.sf.witty.wt.i8n.WMessage;

public class EditableTable<DataType> extends WContainerWidget
{
    
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
        //item table
        itemTable_ = new WTable(this);
        itemTable_.setStyleClass("datatable datatable-grid");

        
        int headerPosition = 0;
        for(String header : editableList_.getTableHeaders())
        {
            itemTable_.putElementAt(0, headerPosition, new TableHeader(tr(header)));
            itemTable_.elementAt(0, headerPosition).setStyleClass("column-title");
            itemTable_.elementAt(0, headerPosition).resize(new WLength(editableList_.getColumnWidths()[headerPosition], WLengthUnit.Percentage), new WLength());
            headerPosition++;
        }
        
        for(DataType item : items_)
        {
            addItem(item);
        }
        
        if(editableList_.getInteractionState()==InteractionState.Adding || editableList_.getInteractionState()==InteractionState.Editing)
        {
            addLine(editableList_.addRow(), true);
            itemTable_.elementAt(0, headerPosition).setStyleClass("column-action");            
        }
    }
    
    private void addItem(DataType item)
    {
        itemList_.add(item);
        addLine(editableList_.getWidgets(item), item==null);
    }
    
    private void addLine(WWidget[] widgets, boolean lineToAdd)
    {
        
        for(WWidget w : widgets) {
            if(w instanceof IFormField) {
                SignalListener<WEmptyEvent> se = null;
                if(lineToAdd) {
                    se = new SignalListener<WEmptyEvent>() {
                        public void notify(WEmptyEvent a) {
                            addAction();
                        }
                    };
                }
                ((IFormField)w).setConfirmAction(se);
            }
        }
        
        int colIndex = 0;
        final int rowNum = itemTable_.numRows();

        for(WWidget widget : widgets)
        {
            widget.setParent(null);
            itemTable_.putElementAt(rowNum, colIndex, widget);
            colIndex++;
        }
        
        if(lineToAdd)
        {
            WPushButton addButton = new WPushButton(tr("general.add"));
            itemTable_.putElementAt(rowNum, colIndex, addButton);
            itemTable_.elementAt(rowNum, colIndex).setStyleClass("column-action");            
            addButton.clicked.addListener(new SignalListener<WMouseEvent>()
                    {
                        public void notify(WMouseEvent a) 
                        {
                            addAction();
                        }
                    });
        }
        else if (editableList_.getInteractionState()==InteractionState.Adding || editableList_.getInteractionState()==InteractionState.Editing) {
			final DataType toRemove = itemList_.get(rowNum - 1);
        	RemoveButton removeButton_ = new RemoveButton(tr("general.remove"), toRemove);
            itemTable_.putElementAt(rowNum, colIndex, removeButton_);
            itemTable_.elementAt(rowNum, colIndex).setStyleClass("column-action");            
        }
    }
    
    private void addAction() {
        WWidget[] widgets = getWidgets(itemTable_.numRows()-1);
        widgets = editableList_.fixAddRow(widgets);
        if(widgets!=null)
        {
            itemTable_.deleteRow(itemTable_.numRows()-1);
            itemList_.add(null);
            addLine(widgets, false);
            
            addLine(editableList_.addRow(), true);
        }
        else
        {
            MessageBox.showWarningMessage(tr("message.editableTable.couldNotAdd"));
        }
    }
    
    public boolean validate()
    {
        boolean valid = true;
        WWidget[] widgets;
        FormField f;
        for(int i = 0; i < itemList_.size(); i++)
        {
            widgets = getWidgets(i+1);
            for(WWidget w : widgets)
            {
                if(w instanceof FormField)
                {
                    f = (FormField)w;
                    if(f.getFormWidget()!=null)
                    {
                        if(!f.validate())
                        {
                            valid = false;
                        }
                    }
                }
            }
        }
        return valid;
    }
    
    public void saveData()
    {
        for(DataType type : removedItemList_)
        {
            if(type!=null)
            {
                editableList_.deleteData(type);
            }
        }
        
        editableList_.flush();
        
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
    }
    
    private WWidget[] getWidgets(int row)
    {
        int size = editableList_.getTableHeaders().length;
        WWidget[] widgets = new WWidget[size];
        
        int processedFields = 0;
        for(int i =0 ; i < itemTable_.numColumns()-1 && processedFields<size; i++)
        {
            widgets[i] = itemTable_.elementAt(row, i).children().get(0);
            processedFields++;
        }
        
        return widgets;
    }
    
    public ArrayList<WWidget> getAllWidgets(int column)
    {
        ArrayList<WWidget> widgets = new ArrayList<WWidget>();
        
        boolean ignoreLastLine = itemTable_.numColumns()>(editableList_.getTableHeaders().length);
        
        for(int i = 1; i < itemTable_.numRows(); i++)
        {
            widgets.add(itemTable_.elementAt(i, column).children().get(0));
        }
        
        if(ignoreLastLine)
        {
            widgets.remove(widgets.size()-1);
        }
        
        return widgets;
    }
    
    public void removeAllRows()
    {
        for(int i = 1; i < itemTable_.numRows()-1; i++)
        {
            ((RemoveButton)itemTable_.elementAt(i, itemTable_.numColumns()-1).children().get(0)).remove();
        }
    }
    
    public void refreshAddRow()
    {
        if((editableList_.getInteractionState()==InteractionState.Adding || editableList_.getInteractionState()==InteractionState.Editing))
        {
            itemTable_.deleteRow(itemTable_.numRows()-1);
            
            addLine(editableList_.addRow(), true);
        }
    }
    
    public WMessage removeDuplicates(int column)
    {
        Set<String> uniqueNominalValues = new HashSet<String>();
        ArrayList<String> duplicates = new ArrayList<String>();
        for(WWidget widget : getAllWidgets(column))
        {
            if(!(uniqueNominalValues.add(((TextField)widget).text())))
            {
                duplicates.add(((TextField)widget).text());
            }
        }
        
        ArrayList<WWidget> widgets = getAllWidgets(column);
        for(String d : duplicates) {
            for(int i = 0; i<widgets.size(); i++) {
                if(((TextField)widgets.get(i)).text().equals(d)) {
                    if(itemList_.get(i)==null) {
                        ((RemoveButton)itemTable_.elementAt(i+1, itemTable_.numColumns()-1).children().get(0)).remove();
                    }
                }
            }
        }
        
        if(duplicates.size()>0)
            return new WMessage("message.editableTable.duplicatesRemoved");
        else
            return null;
    }
    
    private class RemoveButton extends WPushButton {
    	private DataType toRemove;
    	public RemoveButton(WMessage message, DataType toRemove) {
    		super(message);
    		this.toRemove = toRemove;
    	
    		this.clicked.addListener(new SignalListener<WMouseEvent>() {
                public void notify(WMouseEvent a) {
                	remove();
                }
            });        	
    	}
    	
    	public void remove() {
    		int row =  itemList_.indexOf(RemoveButton.this.toRemove)+1;
    		
            itemTable_.deleteRow(row);
            removedItemList_.add(RemoveButton.this.toRemove);
            itemList_.remove(RemoveButton.this.toRemove);
    	}
    }
}
