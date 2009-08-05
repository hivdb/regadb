package net.sf.regadb.ui.framework.widgets.datatable;

import eu.webtoolkit.jwt.WInteractWidget;
import eu.webtoolkit.jwt.WWidget;

public class FilterTools
{
	public static DataTable findDataTable(WInteractWidget w)
	{
		WWidget parent = null;
		
		while(!(parent instanceof DataTable))
		{
			if(parent == null)
			{
				parent = w.getParent();
			}
			else
			{
				parent = parent.getParent();
			}
		}
		
		return ((DataTable)parent);
	}
}
