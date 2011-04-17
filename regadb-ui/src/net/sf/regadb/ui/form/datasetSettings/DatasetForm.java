package net.sf.regadb.ui.form.datasetSettings;

import java.util.Date;
import java.util.List;
import java.util.Set;

import net.sf.regadb.db.Dataset;
import net.sf.regadb.db.DatasetAccess;
import net.sf.regadb.db.DatasetAccessId;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Privileges;
import net.sf.regadb.db.SettingsUser;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.FormWidget;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.fields.DateField;
import net.sf.regadb.ui.framework.forms.fields.Label;
import net.sf.regadb.ui.framework.forms.fields.TextField;
import net.sf.regadb.ui.framework.widgets.UIUtils;
import net.sf.regadb.ui.framework.widgets.formtable.FormTable;
import net.sf.regadb.util.settings.RegaDBSettings;
import eu.webtoolkit.jwt.Signal1;
import eu.webtoolkit.jwt.StandardButton;
import eu.webtoolkit.jwt.WGroupBox;
import eu.webtoolkit.jwt.WMessageBox;
import eu.webtoolkit.jwt.WMouseEvent;
import eu.webtoolkit.jwt.WPushButton;
import eu.webtoolkit.jwt.WString;

public class DatasetForm extends FormWidget 
{
	private Dataset dataset_;
	//dataset group
	private WGroupBox datasetGroup_;
	private FormTable mainGroupTable_;
	
	private Label descriptionL;
	private TextField descriptionTF;
	private Label creationDateL;
	private DateField creationDateDF;
	private Label closedDateL;
	private DateField closedDateDF;
	private Label revisionL;
	private TextField revisionTF;
	
	public DatasetForm(InteractionState interactionState, WString formName,  Dataset dataset) 
	{
		super(formName, interactionState);
		dataset_ = dataset;
		init();
		fillData();
	}
	
	public void init()
    {
	    datasetGroup_ = new WGroupBox(tr("form.datasetForm.editView.general"), this);
	    mainGroupTable_=new FormTable(datasetGroup_);
		descriptionL = new Label(tr("form.datasetForm.editView.description"));
		descriptionTF = new TextField(getInteractionState(), this);
		descriptionTF.setMandatory(true);
		mainGroupTable_.addLineToTable(descriptionL, descriptionTF);
		if (getInteractionState() != InteractionState.Adding && getInteractionState() != InteractionState.Editing)
		{
			creationDateL = new Label(tr("form.datasetForm.editView.creationDate")) ;
			creationDateDF = new DateField(InteractionState.Viewing, this, RegaDBSettings.getInstance().getDateFormat());
			mainGroupTable_.addLineToTable(creationDateL, creationDateDF);
			closedDateL = new Label(tr("form.datasetForm.editView.closedDate"));
			closedDateDF = new DateField(InteractionState.Viewing, this, RegaDBSettings.getInstance().getDateFormat());
			mainGroupTable_.addLineToTable(closedDateL, closedDateDF);
			revisionL = new Label(tr("form.datasetForm.editView.revision"));
			revisionTF = new TextField(InteractionState.Viewing, this);
			mainGroupTable_.addLineToTable(revisionL, revisionTF);
		}
		
        WPushButton deletePatients = new WPushButton(tr("form.dataset.editView.deletePatientsButton"));
        deletePatients.setHidden(getInteractionState() != InteractionState.Deleting);
        deletePatients.clicked().addListener(this, new Signal1.Listener<WMouseEvent>(){
			
			public void trigger(WMouseEvent me) {
				final WMessageBox box = UIUtils.createYesNoMessageBox(DatasetForm.this, tr("form.dataset.editView.deletePatients"));
				box.show();
				box.buttonClicked().addListener(DatasetForm.this, new Signal1.Listener<StandardButton>(){
					public void trigger(StandardButton sb) {
						if (sb == StandardButton.Yes) {
							Transaction t = RegaDBMain.getApp().createTransaction();
							List<Patient> patients = t.getPatients(dataset_);
							for (Patient p : patients) {
								t.delete(p);
							}
							t.commit();
						}
						
						box.hide();
					}
				});
			}
        });
        getExtraButtons().add(deletePatients);
	    addControlButtons();
    }
    
    private void fillData()
    {        
    	if(getInteractionState()==InteractionState.Adding)
        {
			dataset_=new Dataset();
        }
    	else
        {
    		Transaction t = RegaDBMain.getApp().createTransaction();
    		t.attach(dataset_);
    		descriptionTF.setText(dataset_.getDescription());
    		if (getInteractionState() != InteractionState.Adding && getInteractionState() != InteractionState.Editing)
    		{
        	creationDateDF.setDate(dataset_.getCreationDate());
        	closedDateDF.setDate(dataset_.getClosedDate());
        	revisionTF.setText(String.valueOf(dataset_.getRevision()));
    		}
        	t.commit();
    	}
	}
    
	@Override
	public void saveData() 
	{
		Transaction t = RegaDBMain.getApp().createTransaction();
        if(!(getInteractionState()==InteractionState.Adding))
        {
            t.attach(dataset_);
        }
        
        SettingsUser user_ = t.getSettingsUser(RegaDBMain.getApp().getLogin().getUid());
        dataset_.setDescription(descriptionTF.text());
        dataset_.setRevision(1);
        dataset_.setCreationDate(new Date(System.currentTimeMillis()));
        dataset_.setSettingsUser(user_);
        
        if(getInteractionState()==InteractionState.Adding){
        	DatasetAccess da = new DatasetAccess(new DatasetAccessId(user_, dataset_), Privileges.READWRITE.getValue(),user_.getUid());
        	user_.getDatasetAccesses().add(da);
        	dataset_.getDatasetAccesses().add(da);
        }
        
        update(dataset_, t);
        update(user_, t);
        
        t.commit();
        
        RegaDBMain.getApp().getTree().getTreeContent().datasetSelected.setSelectedItem(dataset_);
        redirectToView(RegaDBMain.getApp().getTree().getTreeContent().datasetSelected, RegaDBMain.getApp().getTree().getTreeContent().datasetView);
	}

	@Override
	public void cancel() 
	{
        if(getInteractionState()==InteractionState.Adding)
        {
            redirectToSelect(RegaDBMain.getApp().getTree().getTreeContent().datasets, RegaDBMain.getApp().getTree().getTreeContent().datasetSelect);
        }
        else
        {
            redirectToView(RegaDBMain.getApp().getTree().getTreeContent().datasetSelected, RegaDBMain.getApp().getTree().getTreeContent().datasetView);
        } 
	}

	@Override
	public WString deleteObject() 
	{
    	Set<DatasetAccess> datasetAccess = dataset_.getDatasetAccesses();
    	
    	if (datasetAccess.size() <= 1) {
    		Transaction t = RegaDBMain.getApp().createTransaction();
    		
    		final List<Patient> patients = t.getPatients(dataset_);
    		if (patients.size() > 0) {
    			return tr("form.delete.dataset.containsPatients");
    		}
    		
    	    try
    	    {
    	    	for (DatasetAccess da : datasetAccess) {
    	    		da.getId().getSettingsUser().getDatasetAccesses().remove(da);
    	    		da.getId().getDataset().getDatasetAccesses().remove(da);
    	    		
    	    		t.delete(da);
    	    	}
    	    	
    	    	t.delete(dataset_);
    	        
    	        t.commit();
    	        
    	        return null;
    	    }
            catch(Exception e)
            {
            	e.printStackTrace();
            	t.clear();
            	t.rollback();
            	
            	return tr("form.delete.restriction");
            }
    	}
    	else {
    		return tr("form.delete.restriction");
    	}
	}

	@Override
	public void redirectAfterDelete() 
	{
        RegaDBMain.getApp().getTree().getTreeContent().datasetSelect.selectNode();
        RegaDBMain.getApp().getTree().getTreeContent().datasetSelected.setSelectedItem(null);
	}
}
