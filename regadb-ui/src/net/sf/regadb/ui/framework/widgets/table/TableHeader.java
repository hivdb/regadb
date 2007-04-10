package net.sf.regadb.ui.framework.widgets.table;

import net.sf.witty.wt.WTableCell;
import net.sf.witty.wt.WText;
import net.sf.witty.wt.i8n.WMessage;

public class TableHeader extends WText
{
	public TableHeader(WMessage title, WTableCell cell)
	{
		super(title, cell);
		setStyleClass("table-header");
	}
	
	public TableHeader(WMessage title)
	{
		this(title, null);
	}
}
