package net.sf.regadb.ui.form.singlePatient.chart;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.IForm;
import net.sf.regadb.ui.framework.forms.fields.IFormField;
import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WGroupBox;
import eu.webtoolkit.jwt.WImage;
import eu.webtoolkit.jwt.WResource;
import eu.webtoolkit.jwt.WString;

public class PatientChartForm extends WGroupBox implements IForm 
{
	public PatientChartForm(Patient p)
	{
		super(tr("form.singlePatient.viewChart"));
		
		Transaction t = RegaDBMain.getApp().createTransaction();
		t.attach(p);
		final PatientChart chartDrawer = new PatientChart(p, t.getSettingsUser());
        
        WImage chartImage = new WImage(new WResource() {

            @Override
            public String resourceMimeType() {
                return "image/png";
            }

            @Override
            protected boolean streamResourceData(OutputStream stream, HashMap<String, String> arguments) throws IOException {
                try {
                    chartDrawer.writePngChart(800, stream);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
            }
            
        }, lt("Patient Chart"), this);

		t.commit();
	}
	
	public void addFormField(IFormField field)
	{

	}

	public WContainerWidget getWContainer()
	{
		return this;
	}

    public WString leaveForm() {
        return null;
    }
}
