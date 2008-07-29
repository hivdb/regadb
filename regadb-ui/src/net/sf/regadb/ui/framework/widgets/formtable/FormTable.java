package net.sf.regadb.ui.framework.widgets.formtable;

import net.sf.regadb.ui.framework.forms.fields.IFormField;
import net.sf.regadb.ui.framework.forms.fields.Label;
import net.sf.witty.wt.WContainerWidget;
import net.sf.witty.wt.WTable;
import net.sf.witty.wt.WWidget;

public class FormTable extends WTable {
	public FormTable(WContainerWidget parent) {
		super(parent);
		setStyleClass("datatable");
	}
	
    public int addLineToTable(Label label, IFormField field)
    {
        int numRows = numRows();
        elementAt(numRows, 0).setStyleClass("form-label-area");
        putElementAt(numRows, 0, label);
        putElementAt(numRows, 1, field.getWidget());
        label.setBuddy(field);
        return numRows;
    }
    
    public int addLineToTable(WWidget... widgets)
    {
        int numRows = numRows();
        elementAt(numRows, 0).setStyleClass("form-label-area");
        for(int i=0;i<widgets.length;++i) {
            putElementAt(numRows, i, widgets[i]);
        }
        return numRows;
    }
    
    public void putElementAt(int row, int col, WWidget widget) {
    	if (col == 0) {
    		elementAt(row, col).setStyleClass("form-label-area");
    	}
    	super.putElementAt(row, col, widget);
    }

}
