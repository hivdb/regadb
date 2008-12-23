package net.sf.regadb.ui.framework.widgets.datatable;

import net.sf.regadb.ui.framework.widgets.MyComboBox;
import eu.webtoolkit.jwt.Signal1;

public class FilterOperatorCombo extends MyComboBox
{
	private FilterTF filterTF_;
	
	public FilterOperatorCombo(FilterTF filterTF)
	{
		super();
		filterTF_ = filterTF;
		
		addItem(tr("datatable.filter.combo.noFilter"));
		
		activated.addListener(this, new Signal1.Listener<Integer>()
		{
			public void trigger(Integer i)
			{
				//disable if the first element is selected
				filterTF_.setEnabled(i!=0);
				
				if(!(filterTF_.isEnabled() && "".equals(filterTF_.text())))
				{
					FilterTools.findDataTable(filterTF_).applyFilter();
				}
			}
		});
	}
}