package net.sf.regadb.ui.framework.widgets;

import java.util.Arrays;
import java.util.List;

import net.sf.regadb.ui.framework.widgets.table.TableHeader;
import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WLength;
import eu.webtoolkit.jwt.WTable;
import eu.webtoolkit.jwt.WTableRow;
import eu.webtoolkit.jwt.WWidget;

public class SimpleTable extends WTable{
	int maxColumns = 0;
	int headerColumns = 0;
	
	public SimpleTable(WContainerWidget parent) {
		super(parent);
    	setStyleClass("datatable datatable-grid");
	}
	
	public void setHeaders(CharSequence... titles) {
		setHeaders(Arrays.asList(titles));
	}
	
	public void setHeaders(List<CharSequence> titles) {
        int row = 0;
        headerColumns = 0;
        for (CharSequence title : titles) {
        	getElementAt(row, headerColumns).addWidget(new TableHeader(title));
        	getElementAt(row, headerColumns).setStyleClass("column-title");
        	headerColumns++;
        }
        maxColumns = Math.max(maxColumns, headerColumns);
	}
	
	public void setWidths(int... widths) {
		int col = 0;
		for (int i : widths) {
			getElementAt(0, col).resize(new WLength(i, WLength.Unit.Percentage), new WLength());
			col++;
		}
	}
	
	public void addRow(WWidget ... widgets) {
		addRow(Arrays.asList(widgets));
	}
	
	public WTableRow addRow(List<WWidget> widgets) {
		int col = 0;
		int row = getRowCount();
		for (WWidget widget : widgets) {
			getElementAt(row, col).addWidget(widget);
			col++;
		}
		WTableRow tableRow = this.getRowAt(row);
		
		maxColumns = Math.max(maxColumns, col);
		
		return tableRow;
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
	    	getElementAt(0,headerColumns-1).setColumnSpan(colspan);
	    }
	}
}
