package net.sf.regadb.ui.form.impex;

import java.io.File;
import java.io.FileOutputStream;

import net.sf.regadb.db.Dataset;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.io.exportXML.ExportToXMLOutputStream;
import net.sf.regadb.ui.form.singlePatient.DataComboMessage;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.FormWidget;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.fields.ComboBox;
import net.sf.regadb.ui.framework.forms.fields.Label;
import net.sf.regadb.ui.framework.widgets.formtable.FormTable;
import net.sf.regadb.util.hibernate.HibernateFilterConstraint;
import net.sf.witty.wt.SignalListener;
import net.sf.witty.wt.WAnchor;
import net.sf.witty.wt.WFileResource;
import net.sf.witty.wt.WMouseEvent;
import net.sf.witty.wt.WPushButton;
import net.sf.witty.wt.WWidget;
import net.sf.witty.wt.i8n.WMessage;

import org.hibernate.Query;

public class ExportForm extends FormWidget {
	private FormTable table_;
	private ComboBox<Dataset> datasets;
	private File exportFile;
	
	public ExportForm(WMessage formName, InteractionState interactionState) {
		super(formName, interactionState);
		init();
	}
	
	public void init() {
		table_ = new FormTable(this);
		
		Label datasetsL = new Label(tr("form.impex.export.dataset"));;
		datasets = new ComboBox<Dataset>(InteractionState.Editing, this);
		fillData();
		table_.addLineToTable(datasetsL, datasets);
		
		
		WPushButton export = new WPushButton(tr("form.impex.export.title"));
		Label exportL = new Label(tr("form.impex.export.title"));
		WWidget[] widgets = {exportL, export};
		table_.addLineToTable(widgets);
		
		export.clicked.addListener(new SignalListener<WMouseEvent>() {
			public void notify(WMouseEvent a) {
				try{
					Dataset ds = datasets.currentValue();

					deleteExportFile();
					exportFile = RegaDBMain.getApp().createTempFile(ds.getDescription() + "_export", "xml");
					FileOutputStream fout = new FileOutputStream(exportFile);

					ExportToXMLOutputStream xmlout = new ExportToXMLOutputStream(fout);


					Transaction t = RegaDBMain.getApp().getLogin().createTransaction();
					
					HibernateFilterConstraint hfc = new HibernateFilterConstraint();
					hfc.setClause(" dataset.description = :description ");
					hfc.addArgument("description", ds.getDescription());
					long n = t.getPatientCount(hfc);
					int maxResults = 100;
					
					xmlout.start();
					for(int i=0; i < n; i+=maxResults){
						t.commit();
						t.clearCache();
						t = RegaDBMain.getApp().getLogin().createTransaction();

						Query q = t.getPatientsQuery(ds);
						q.setReadOnly(true);
						q.setFirstResult(i);
						q.setMaxResults(maxResults);

						xmlout.write(q.list());
					}
					xmlout.stop();
					
					table_.elementAt(0, 2).clear();
					
					new WAnchor(new WFileResource("text/txt", exportFile.getAbsolutePath()),
					new WMessage(ds.getDescription() + "_export.xml", true),
					table_.elementAt(0, 2)).setStyleClass("link");
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		});
		
		addControlButtons();
    }
	
	public void fillData() {
		Transaction t = RegaDBMain.getApp().createTransaction();
		
		// Fill in dataset combo box
//		datasets.clearItems();
        for(Dataset ds : t.getDatasets())
        {
        	datasets.addItem(new DataComboMessage<Dataset>(ds, ds.getDescription()));
        }
        datasets.sort();
        datasets.selectIndex(0);
	}
	
	private void deleteExportFile() {
		if ( exportFile != null ) exportFile.delete();
	}
	
	@Override
	public void cancel() {}
	
	@Override
	public WMessage deleteObject() {return null;}
	
	@Override
	public void redirectAfterDelete() {}
	
	@Override
	public void saveData() {}
	
	@Override
	public WMessage leaveForm() {
		deleteExportFile();
		return null;
	}
}
