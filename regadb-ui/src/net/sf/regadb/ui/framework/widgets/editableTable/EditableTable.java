package net.sf.regadb.ui.framework.widgets.editableTable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sf.regadb.ui.framework.forms.IForm;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.fields.FormField;
import net.sf.regadb.ui.framework.forms.fields.IFormField;
import net.sf.regadb.ui.framework.forms.fields.TextField;
import net.sf.regadb.ui.framework.widgets.UIUtils;
import net.sf.regadb.ui.framework.widgets.table.TableHeader;
import eu.webtoolkit.jwt.Signal;
import eu.webtoolkit.jwt.Signal1;
import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WLength;
import eu.webtoolkit.jwt.WMouseEvent;
import eu.webtoolkit.jwt.WPushButton;
import eu.webtoolkit.jwt.WString;
import eu.webtoolkit.jwt.WTable;
import eu.webtoolkit.jwt.WTableCell;
import eu.webtoolkit.jwt.WWidget;

public class EditableTable<DataType> extends WContainerWidget
{
    
    private WTable itemTable_;
    
    private IEditableTable<DataType> editableList_;
    
    private List<DataType> items_;
    
    private ArrayList<DataType> itemList_ = new ArrayList<DataType>();
    private ArrayList<DataType> removedItemList_ = new ArrayList<DataType>();
    
    private Signal.Listener addListener;
        
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
    	addListener = new Signal.Listener() {
			@Override
			public void trigger() {
				addAction();
			}
		};
    	
        //item table
        itemTable_ = new WTable(this);
        itemTable_.setStyleClass("datatable datatable-grid");

        
        int headerPosition = 0;
        for(String header : editableList_.getTableHeaders())
        {
            itemTable_.getElementAt(0, headerPosition).addWidget(new TableHeader(tr(header)));
            itemTable_.getElementAt(0, headerPosition).setStyleClass("column-title");
            itemTable_.getElementAt(0, headerPosition).resize(new WLength(editableList_.getColumnWidths()[headerPosition], WLength.Unit.Percentage), new WLength());
            headerPosition++;
        }
        
        for(DataType item : items_)
        {
            addItem(item);
        }
        
        if(editableList_.getInteractionState()==InteractionState.Adding || editableList_.getInteractionState()==InteractionState.Editing)
        {
            addLine(editableList_.addRow(), true);
            itemTable_.getElementAt(0, headerPosition).setStyleClass("column-action");            
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
                if(lineToAdd) {
                	((IFormField)w).setConfirmAction(addListener);
                }
            }
        }
        
        int colIndex = 0;
        final int rowNum = itemTable_.getRowCount();

        for(WWidget widget : widgets)
        {
            itemTable_.getElementAt(rowNum, colIndex).clear();
            itemTable_.getElementAt(rowNum, colIndex).addWidget(widget);
            colIndex++;
        }
        
        if(lineToAdd)
        {
            WPushButton addButton = new WPushButton(tr("editableDataTable.button.addItem"));
            itemTable_.getElementAt(rowNum, colIndex).addWidget(addButton);
            itemTable_.getElementAt(rowNum, colIndex).setStyleClass("column-action");            
            addButton.clicked().addListener(this, new Signal1.Listener<WMouseEvent>()
                    {
                        public void trigger(WMouseEvent a) 
                        {
                            addAction();
                        }
                    });
        }
        else if (editableList_.getInteractionState()==InteractionState.Adding || editableList_.getInteractionState()==InteractionState.Editing) {
			final DataType toRemove = itemList_.get(rowNum - 1);
        	RemoveButton removeButton_ = new RemoveButton(tr("editableDataTable.button.removeItem"), toRemove);
            itemTable_.getElementAt(rowNum, colIndex).addWidget(removeButton_);
            itemTable_.getElementAt(rowNum, colIndex).setStyleClass("column-action");            
        }
    }
    
    private void addAction() {
        WWidget[] widgets = getWidgets(itemTable_.getRowCount()-1);
        widgets = editableList_.fixAddRow(widgets);
        if(widgets!=null)
        {
            itemTable_.deleteRow(itemTable_.getRowCount()-1);
            itemList_.add(null);
            addLine(widgets, false);
            
            addLine(editableList_.addRow(), true);
        }
        else
        {
        	UIUtils.showWarningMessageBox(this, tr("editableTable.add.warning.couldNotAdd"));
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
        for(int i =0 ; i < itemTable_.getColumnCount()-1 && processedFields<size; i++)
        {
            widgets[i] = itemTable_.getElementAt(row, i).getChildren().get(0);
            processedFields++;
        }
        
        return widgets;
    }
    
    public ArrayList<WWidget> getAllWidgets(int column)
    {
        ArrayList<WWidget> widgets = new ArrayList<WWidget>();
        
        boolean ignoreLastLine = itemTable_.getColumnCount()>(editableList_.getTableHeaders().length);
        
        for(int i = 1; i < itemTable_.getRowCount(); i++)
        {
            widgets.add(itemTable_.getElementAt(i, column).getChildren().get(0));
        }
        
        if(ignoreLastLine)
        {
            widgets.remove(widgets.size()-1);
        }
        
        return widgets;
    }
    
    public void removeAllRows()
    {
        for(int i = 1; i < itemTable_.getRowCount()-1; i++)
        {
            ((RemoveButton)itemTable_.getElementAt(i, itemTable_.getColumnCount()-1).getChildren().get(0)).remove();
        }
    }
    
    public void refreshAddRow()
    {
        if((editableList_.getInteractionState()==InteractionState.Adding || editableList_.getInteractionState()==InteractionState.Editing))
        {
            itemTable_.deleteRow(itemTable_.getRowCount()-1);
            
            addLine(editableList_.addRow(), true);
        }
    }
    
    public WString warnDuplicatesAndBlanks(int column)
    {
        Set<String> uniqueNominalValues = new HashSet<String>();
        ArrayList<String> duplicates = new ArrayList<String>();
        for(WWidget widget : getAllWidgets(column))
        {
        	String text = ((TextField)widget).text();
        	if (text.equals("")) {
        		return tr("editableTable.add.warning.blanks");
        	}
        	
            if(!uniqueNominalValues.add(text))
            {
                duplicates.add(((TextField)widget).text());
            }
        }
        
        if(duplicates.size()>0)
            return tr("editableTable.add.warning.duplicates");
        else
            return null;
    }
    
    private class RemoveButton extends WPushButton {
    	private DataType toRemove;
    	public RemoveButton(WString message, DataType toRemove) {
    		super(message);
    		this.toRemove = toRemove;
    	
    		this.clicked().addListener(this, new Signal1.Listener<WMouseEvent>() {
                public void trigger(WMouseEvent a) {
                	removeRow();
                }
            });
    	}
    	
    	private void removeRow() {
    		if(canRemove(RemoveButton.this.toRemove)){
	    		int row =  ((WTableCell)RemoveButton.this.getParent()).getRow();
	    		
	    		WWidget[] widgets = getWidgets(row);
	    		for(WWidget ww : widgets) {
	    			if(ww instanceof IFormField) {
	    				IForm form = ((IFormField)ww).getForm();
	    				if(form!=null)
	    					form.removeFormField((IFormField)ww);
	    			}
	    		}
	    		
	            itemTable_.deleteRow(row);
	            removedItemList_.add(RemoveButton.this.toRemove);
	            itemList_.remove(RemoveButton.this.toRemove);
    		}
    		else{
    			UIUtils.showWarningMessageBox(this, tr("form.delete.restriction"));
    		}
    	}
    }
    
    public boolean canRemove(DataType toRemove){
    	return true;
    }
}
