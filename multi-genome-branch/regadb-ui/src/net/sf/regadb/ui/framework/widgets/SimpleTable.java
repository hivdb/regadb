package net.sf.regadb.ui.framework.widgets;

import net.sf.regadb.ui.framework.widgets.table.TableHeader;
import net.sf.witty.wt.WContainerWidget;
import net.sf.witty.wt.WTable;
import net.sf.witty.wt.WWidget;
import net.sf.witty.wt.core.utils.WLength;
import net.sf.witty.wt.core.utils.WLengthUnit;
import net.sf.witty.wt.i8n.WMessage;

public class SimpleTable extends WTable{
	int currentRow;
	
	public SimpleTable(WContainerWidget parent) {
		super(parent);
    	setStyleClass("datatable datatable-grid");
    	currentRow = 1;
	}
	public void setHeaders(WMessage... titles) {
        int row = 0;
        int col = 0;
        for (WMessage title : titles) {
        	putElementAt(row, col, new TableHeader(title));
        	elementAt(row, col).setStyleClass("column-title");
        	col++;
        }
	}
	
	public void setWidths(int... widths) {
		int col = 0;
		for (int i : widths) {
			elementAt(0, col).resize(new WLength(i, WLengthUnit.Percentage), new WLength());
			col++;
		}
	}
	
	public void addRow(WWidget... widgets) {
		int col = 0;
		for (WWidget widget : widgets) {
			putElementAt(currentRow, col, widget);
			col++;
		}
		currentRow++;
	}
	
	/**
	 * make all columns equally width
	 */
	public void distributeWidths() {
    	int[] widths = new int[numColumns()];
    	for (int i = 0 ; i < numColumns() - 1; i++) {
    		widths[i] = (int) Math.floor(100/numColumns());
    	}
    	widths[numColumns() - 1] = 100 - ((int) Math.floor(100/numColumns())) * (numColumns()-1);	
    	setWidths(widths);
	}
}
