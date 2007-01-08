package net.sf.regadb.ui.framework.widgets.datatable;

import net.sf.witty.event.SignalListener;
import net.sf.witty.wt.validation.WValidator;
import net.sf.witty.wt.validation.WValidatorState;
import net.sf.witty.wt.widgets.WLineEdit;
import net.sf.witty.wt.widgets.event.WEmptyEvent;
import net.sf.witty.wt.widgets.event.WKeyEvent;

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
			setStyleClass("filterTF-valid-input");
		else
			setStyleClass("filterTF-invalid-input");
	}
}
