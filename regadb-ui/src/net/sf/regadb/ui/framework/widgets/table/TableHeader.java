package net.sf.regadb.ui.framework.widgets.table;

import eu.webtoolkit.jwt.WTableCell;
import eu.webtoolkit.jwt.WText;

public class TableHeader extends WText
{
	public TableHeader(CharSequence title, WTableCell cell)
	{
		super(title, cell);
		setStyleClass("table-header");
	}
	
	public TableHeader(CharSequence title)
	{
		this(title, null);
	}
}
