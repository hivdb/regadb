package net.sf.regadb.ui.form.singlePatient.chart;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.IForm;
import net.sf.regadb.ui.framework.forms.fields.IFormField;
import net.sf.witty.wt.WContainerWidget;
import net.sf.witty.wt.WGroupBox;
import net.sf.witty.wt.WImage;
import net.sf.witty.wt.WResource;
import net.sf.witty.wt.i8n.WMessage;

public class PatientChartForm extends WGroupBox implements IForm 
{
	public PatientChartForm(Patient p)
	{
		super(tr("chart.form"));
		
		Transaction t = RegaDBMain.getApp().createTransaction();
		t.attach(p);
		final PatientChart chartDrawer = new PatientChart(p, t.getSettingsUser());
        
        WImage chartImage = new WImage(new WResource() {

            @Override
            public String resourceMimeType() {
                return "image/png";
            }

            @Override
            protected void streamResourceData(OutputStream stream) {
                try {
                    chartDrawer.writePngChart(800, stream);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            
        }, this);

		t.commit();
	}
	
	public void addFormField(IFormField field)
	{

	}

	public WContainerWidget getWContainer()
	{
		return this;
	}

    public WMessage leaveForm() {
        return null;
    }
}
