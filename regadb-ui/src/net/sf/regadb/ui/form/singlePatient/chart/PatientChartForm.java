package net.sf.regadb.ui.form.singlePatient.chart;

import java.io.File;

import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.IForm;
import net.sf.regadb.ui.framework.forms.fields.IFormField;
import net.sf.witty.wt.widgets.WContainerWidget;
import net.sf.witty.wt.widgets.WFileResource;
import net.sf.witty.wt.widgets.WGroupBox;
import net.sf.witty.wt.widgets.WImage;

public class PatientChartForm extends WGroupBox implements IForm 
{
	//TODO remove image when leaving the form
	
	private WImage chartImage_;
	
	public PatientChartForm(Patient p)
	{
		super(tr("form.singlePatient.viewChart"));
		
		Transaction t = RegaDBMain.getApp().createTransaction();
		t.update(p);
		PatientChart chartDrawer = new PatientChart(p, t.getSettingsUser());
		File tmpFile = RegaDBMain.getApp().createTempFile("regadb-chart", ".png");
		chartDrawer.writePngChartToFile(800, tmpFile);
		chartImage_ = new WImage(new WFileResource("image/png", tmpFile.getAbsolutePath()), this);

		t.commit();
	}
	
	public void addFormField(IFormField field)
	{

	}

	public WContainerWidget getWContainer()
	{
		return this;
	}
}
