package net.sf.regadb.ui.framework.widgets.datatable;

import net.sf.witty.wt.SignalListener;
import net.sf.witty.wt.WEmptyEvent;
import net.sf.witty.wt.WKeyEvent;
import net.sf.witty.wt.WLineEdit;
import net.sf.witty.wt.validation.WValidator;
import net.sf.witty.wt.validation.WValidatorState;

public class FilterTF extends WLineEdit
{
	private WValidatorState initialState_;
	
	public FilterTF(WValidator validator)
	{
		super();
		
		setValidator(validator);
		
		initialState_ = validate();
		
		setStyle(true);
		
		setEnabled(false);
		
		changed.addListener(new SignalListener<WEmptyEvent>()
		{
			public void notify(WEmptyEvent a)
			{
				inputChanged();
			}
		});
	
		keyWentUp.addListener(new SignalListener<WKeyEvent>()
		{
			public void notify(WKeyEvent a)
			{
				inputChanged();
			}
		});
		
		enterPressed.addListener(new SignalListener<WEmptyEvent>()
				{
					public void notify(WEmptyEvent a) 
					{
						FilterTools.findDataTable(FilterTF.this).applyFilter();
					}
				});
	}
	
	private void inputChanged()
	{
		WValidatorState state = validate();

		if (state != initialState_)
		{
			setStyle(state == WValidatorState.Valid);

			initialState_ = state;
		}
	}
	
	public void setStyle(boolean valid)
	{
		if(valid)
			setStyleClass("filter-field textfield edit-valid");
		else
			setStyleClass("filter-field textfield edit-invalid");
	}
}
