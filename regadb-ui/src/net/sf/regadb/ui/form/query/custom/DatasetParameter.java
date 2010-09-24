package net.sf.regadb.ui.form.query.custom;

import java.util.List;

import net.sf.regadb.db.Dataset;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.ui.form.singlePatient.DataComboMessage;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.IForm;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.fields.ComboBox;
import eu.webtoolkit.jwt.WWidget;

public class DatasetParameter extends BasicParameter{
	
	private ComboBox<Dataset> datasets;

	public DatasetParameter(IForm form, String description, boolean mandatory) {
		super(description, mandatory);
		
		datasets = new ComboBox<Dataset>(InteractionState.Adding, form);
		init();
	}

	@Override
	public WWidget getWidget() {
		return datasets;
	}
	
	protected void init(){
		Transaction t = RegaDBMain.getApp().createTransaction();
		
		List<Dataset> dss = t.getCurrentUsersDatasets();
		for(Dataset ds : dss){
			datasets.addItem(new DataComboMessage<Dataset>(ds, ds.getDescription()));
		}
		datasets.sort();
		
		t.commit();
	}

	public Dataset getDataset(){
		return datasets.currentValue();
	}

	public boolean isValid() {
		return true;
	}
}
