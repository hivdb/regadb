package net.sf.regadb.ui.framework.widgets.formtable;

import net.sf.regadb.ui.framework.forms.fields.IFormField;
import net.sf.regadb.ui.framework.forms.fields.Label;
import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WTable;
import eu.webtoolkit.jwt.WWidget;

public class FormTable extends WTable {
	public FormTable(WContainerWidget parent) {
		super(parent);
		setStyleClass("datatable");
	}
	
    public int addLineToTable(Label label, IFormField field)
    {
        int numRows = getRowCount();
        getElementAt(numRows, 0).setStyleClass("form-label-area");
        putElementAt(numRows, 0, label);
        putElementAt(numRows, 1, field.getWidget());
        getElementAt(numRows, 1).setStyleClass("form-value-area");
        label.setBuddy(field);
        return numRows;
    }
    
    public int addLineToTable(int row, Label label, IFormField field)
    {
    	insertRow(row);
        getElementAt(row, 0).setStyleClass("form-label-area");
        putElementAt(row, 0, label);
        putElementAt(row, 1, field.getWidget());
        getElementAt(row, 1).setStyleClass("form-value-area");
        label.setBuddy(field);
        return row;
    }
    
    public int addLineToTable(WWidget... widgets)
    {
        int numRows = getRowCount();
        getElementAt(numRows, 0).setStyleClass("form-label-area");
        for(int i=0;i<widgets.length;++i) {
            putElementAt(numRows, i, widgets[i]);
            if(i > 0)
            	getElementAt(numRows, i).setStyleClass("form-value-area");
        }
        return numRows;
    }
    
    public void putElementAt(int row, int col, WWidget widget) {
    	if (col == 0) {
    		getElementAt(row, col).setStyleClass("form-label-area");
    	}
    	super.getElementAt(row, col).addWidget(widget);
    }

}
