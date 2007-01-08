package net.sf.regadb.ui.framework.widgets.datatable;

import net.sf.witty.event.SignalListener;
import net.sf.witty.wt.widgets.WComboBox;

public class FilterOperatorCombo extends WComboBox
{
	private FilterTF filterTF_;
	
	public FilterOperatorCombo(FilterTF filterTF)
	{
		filterTF_ = filterTF;
		
		addItem(tr("datatable.filter.combo.noFilter"));
		
		activated.addListener(new SignalListener<Integer>()
		{
			public void notify(Integer i)
			{
				//disable if the first element is selected
				filterTF_.setEnabled(i!=0);
			}
		});
	}
}
