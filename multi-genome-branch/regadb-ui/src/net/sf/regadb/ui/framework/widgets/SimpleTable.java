package net.sf.regadb.ui.framework.widgets;

import net.sf.regadb.ui.framework.widgets.table.TableHeader;
import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WLength;
import eu.webtoolkit.jwt.WString;
import eu.webtoolkit.jwt.WTable;
import eu.webtoolkit.jwt.WWidget;

public class SimpleTable extends WTable{
	int currentRow;
	int maxColumns = 0;
	int headerColumns = 0;
	
	public SimpleTable(WContainerWidget parent) {
		super(parent);
    	setStyleClass("datatable datatable-grid");
    	currentRow = 1;
	}
	public void setHeaders(WString... titles) {
        int row = 0;
        headerColumns = 0;
        for (WString title : titles) {
        	elementAt(row, headerColumns).addWidget(new TableHeader(title));
        	elementAt(row, headerColumns).setStyleClass("column-title");
        	headerColumns++;
        }
        maxColumns = Math.max(maxColumns, headerColumns);
	}
	
	public void setWidths(int... widths) {
		int col = 0;
		for (int i : widths) {
			elementAt(0, col).resize(new WLength(i, WLength.Unit.Percentage), new WLength());
			col++;
		}
	}
	
	public void addRow(WWidget... widgets) {
		int col = 0;
		for (WWidget widget : widgets) {
			elementAt(currentRow, col).addWidget(widget);
			col++;
		}
		currentRow++;
		
		maxColumns = Math.max(maxColumns, col);
	}
	
	/**
	 * make all columns equally width
	 */
	public void distributeWidths() {
    	int[] widths = new int[maxColumns];
    	for (int i = 0 ; i < maxColumns - 1; i++) {
    		widths[i] = (int) Math.floor(100/maxColumns);
    	}
    	widths[maxColumns - 1] = 100 - ((int) Math.floor(100/maxColumns)) * (maxColumns-1);	
    	setWidths(widths);
	}
	
	public void spanHeaders(){
	    int colspan = maxColumns - headerColumns +1;
	    if(colspan > 1){
	        elementAt(0,headerColumns-1).setColumnSpan(colspan);
	    }
	}
}
