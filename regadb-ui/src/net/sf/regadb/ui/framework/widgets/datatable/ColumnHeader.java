package net.sf.regadb.ui.framework.widgets.datatable;

import net.sf.witty.wt.i8n.WMessage;
import net.sf.witty.wt.widgets.WContainerWidget;
import net.sf.witty.wt.widgets.WText;

public class ColumnHeader extends WContainerWidget
{
	private WText header_;
	
	public ColumnHeader(WMessage intlName, WContainerWidget parent)
	{
		super(parent);
		
		header_ = new WText(intlName, this);
	}
}
