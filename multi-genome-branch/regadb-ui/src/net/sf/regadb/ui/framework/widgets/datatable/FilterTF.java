package net.sf.regadb.ui.framework.widgets.datatable;

import eu.webtoolkit.jwt.Signal;
import eu.webtoolkit.jwt.Signal1;
import eu.webtoolkit.jwt.WKeyEvent;
import eu.webtoolkit.jwt.WLineEdit;
import eu.webtoolkit.jwt.WValidator;

public class FilterTF extends WLineEdit
{
	private WValidator.State initialState_;
	
	public FilterTF(WValidator validator)
	{
		super();
		
		setValidator(validator);
		
		initialState_ = validate();
		
		setStyle(true);
		
		setEnabled(false);
		
		changed.addListener(this, new Signal.Listener()
		{
			public void trigger()
			{
				inputChanged();
			}
		});
	
		keyWentUp.addListener(this, new Signal1.Listener<WKeyEvent>()
		{
			public void trigger(WKeyEvent a)
			{
				inputChanged();
			}
		});
		
		enterPressed.addListener(this, new Signal.Listener()
				{
					public void trigger() 
					{
						FilterTools.findDataTable(FilterTF.this).applyFilter();
					}
				});
	}
	
	private void inputChanged()
	{
		WValidator.State state = validate();

		if (state != initialState_)
		{
			setStyle(state == WValidator.State.Valid);

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
