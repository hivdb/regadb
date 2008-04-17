package net.sf.regadb.ui.form.impex;

import net.sf.regadb.db.Dataset;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.ui.form.singlePatient.DataComboMessage;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.FormWidget;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.witty.wt.WComboBox;
import net.sf.witty.wt.WLabel;
import net.sf.witty.wt.WTable;
import net.sf.witty.wt.i8n.WMessage;

public class ExportForm extends FormWidget {
	private WTable table_;
	private WComboBox datasets;
	
	public ExportForm(WMessage formName, InteractionState interactionState) {
		super(formName, interactionState);
		init();
	}
	
	public void init()
    {
		table_ = new WTable();
		table_.setStyleClass("spacyTable");
		addWidget(table_);
		
		new WLabel(tr("form.impex.export.dataset"), table_.elementAt(0, 0));
		datasets = new WComboBox(table_.elementAt(0, 1));
		
		fillData();
		
		addControlButtons();
    }
	
	public void fillData() {
		Transaction t = RegaDBMain.getApp().createTransaction();
		
		// Fill in dataset combo box
		datasets.clear();
        for(Dataset ds : t.getDatasets())
        {
        	datasets.addItem(new DataComboMessage<Dataset>(ds, ds.getDescription()));
        }
        datasets.sort();
	}
	
	@Override
	public void cancel() {
		fillData();
	}
	
	@Override
	public WMessage deleteObject() {return null;}
	
	@Override
	public void redirectAfterDelete() {}
	
	@Override
	public void saveData() {
		// TODO
	}
	
	@Override
	public WMessage leaveForm() {
        return null;
    }
}
