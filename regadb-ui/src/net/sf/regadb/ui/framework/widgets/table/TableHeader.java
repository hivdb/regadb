package net.sf.regadb.ui.framework.widgets.table;

import net.sf.witty.wt.i8n.WMessage;
import net.sf.witty.wt.widgets.WTableCell;
import net.sf.witty.wt.widgets.WText;

public class TableHeader extends WText
{
	public TableHeader(WMessage title, WTableCell cell)
	{
		super(title, cell);
		setStyleClass("table-header");
	}
	
	public TableHeader(WMessage title)
	{
		super(title, null);
	}
}
