package net.sf.regadb.ui.form.singlePatient;

import java.util.ArrayList;
import java.util.List;

import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.fields.ComboBox;
import net.sf.regadb.ui.framework.forms.fields.FieldType;
import net.sf.regadb.ui.framework.forms.fields.TextField;
import net.sf.regadb.ui.framework.widgets.table.TableHeader;
import net.sf.regadb.util.pair.Pair;
import net.sf.witty.wt.core.utils.WHorizontalAlignment;
import net.sf.witty.wt.i8n.WMessage;
import net.sf.witty.wt.widgets.SignalListener;
import net.sf.witty.wt.widgets.WCheckBox;
import net.sf.witty.wt.widgets.WContainerWidget;
import net.sf.witty.wt.widgets.WGroupBox;
import net.sf.witty.wt.widgets.WPushButton;
import net.sf.witty.wt.widgets.WTable;
import net.sf.witty.wt.widgets.WTableCell;
import net.sf.witty.wt.widgets.event.WMouseEvent;

public class DrugSelectionForm <DrugType, TherapyType> extends WGroupBox
{
	private ArrayList<DataComboMessage<DrugType>> comboItems_;
	
	private WMessage dailyDosageHeader_;
	
	private TherapyForm therapyForm_;
	
	private WPushButton _addDrugButton;
	private WPushButton _removeDrugsButton;
	private WTable _table;
	
	
	public DrugSelectionForm(TherapyForm therapyForm, WMessage title, ArrayList<DataComboMessage<DrugType>> comboItems, WMessage dailyDosageHeader)
	{
		super(title);
		comboItems_ = comboItems;
		
		dailyDosageHeader_ = dailyDosageHeader;
		
		therapyForm_ = therapyForm;
		
		init();
	}
	
	public void init()
	{
		//buttons
		WContainerWidget buttonContainer = new WContainerWidget();
		buttonContainer.setContentAlignment(WHorizontalAlignment.AlignRight);
		_addDrugButton = new WPushButton(tr("form.therapy.drugSelectionForm.addDrug"), buttonContainer);
		_addDrugButton.clicked.addListener(new SignalListener<WMouseEvent>()
				{
					public void notify(WMouseEvent a)
					{
						addDrugRowToTable(null);
					}
				});
		_removeDrugsButton = new WPushButton(tr("form.therapy.drugSelectionForm.removeDrugs"), buttonContainer);
		_removeDrugsButton.clicked.addListener(new SignalListener<WMouseEvent>()
				{
					public void notify(WMouseEvent a)
					{
						removeDrugsAction();
					}
				});
		if(therapyForm_.getInteractionState()==InteractionState.Viewing)
		{
			_addDrugButton.setEnabled(false);
			_removeDrugsButton.setEnabled(false);
		}
		addWidget(buttonContainer);
		
		//table
		_table = new WTable();
		_table.putElementAt(0, 1, new TableHeader(tr("form.therapy.drugSelectionForm.tableHeader.drug")));
		_table.putElementAt(0, 2, new TableHeader(dailyDosageHeader_));
		addWidget(_table);
	}
	
	public void fillData(ArrayList<Pair<DataComboMessage<DrugType>,Double>> results)
	{
		if(results!=null)
		{
			for(Pair<DataComboMessage<DrugType>, Double> drugPair : results)
			{
				addDrugRowToTable(drugPair);
			}
		}
	}
	
	private void removeDrugsAction()
	{
		if(_table.numRows()==1)
			return;
		
		ArrayList<WTableCell> rowIndexesToBeDeleted = new ArrayList<WTableCell>();
		
		for(int i = 1; i<_table.numRows(); i++)
		{
			if(_table.elementAt(i, 0).children().get(0) instanceof WCheckBox)
			{
				if(((WCheckBox)_table.elementAt(i, 0).children().get(0)).isChecked())
				{
					rowIndexesToBeDeleted.add(_table.elementAt(i, 0));
				}
			}
		}
		
		for(WTableCell tableCell : rowIndexesToBeDeleted)
		{
			_table.deleteRow(tableCell.row());
		}
	}
	
	private void addDrugRowToTable(Pair<DataComboMessage<DrugType>, Double> drugPair)
	{
		ComboBox combo;
		TextField tf;
		WCheckBox cb;
		
		int rowIndex = _table.numRows();
		
		if(therapyForm_.getInteractionState()==InteractionState.Adding||therapyForm_.getInteractionState()==InteractionState.Editing)
		{
			cb = new WCheckBox();
			cb.setChecked(false);
			_table.putElementAt(rowIndex, 0, cb);
		}
		
		combo = new ComboBox(therapyForm_.getInteractionState(), therapyForm_);
		for(DataComboMessage<DrugType> drug : comboItems_)
		{
			combo.addItem(drug);
		}
		if(drugPair!=null)
		{
			combo.selectItem(drugPair.getKey());
		}
		_table.putElementAt(rowIndex, 1, combo );
		
		tf = new TextField(therapyForm_.getInteractionState(), therapyForm_, FieldType.DOUBLE);
		if(drugPair!=null)
		{
			if(drugPair.getValue()!=null)
			{
				tf.setText(drugPair.getValue().toString());
			}
		}
		_table.putElementAt(rowIndex, 2, tf);
	}
	
	public List<Pair<DrugType, Double>> getData()
	{
		List<Pair<DrugType, Double>> dataList = new ArrayList<Pair<DrugType, Double>>();
		
		DrugType drug;
		Double dosage;
		for(int i = 1; i<_table.numRows(); i++)
		{
			if(_table.elementAt(i, 1).children().get(0) instanceof ComboBox)
			{
				drug = ((DataComboMessage<DrugType>)((ComboBox)_table.elementAt(i, 1).children().get(0)).currentText()).getValue();
				try
				{
					dosage = Double.parseDouble(((TextField)_table.elementAt(i, 2).children().get(0)).text());
				}
				catch(NumberFormatException nfe)
				{
					dosage = null;
				}
				dataList.add(new Pair<DrugType, Double>(drug, dosage));
			}
		}
		
		return dataList;
	}
}
