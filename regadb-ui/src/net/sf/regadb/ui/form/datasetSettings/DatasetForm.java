package net.sf.regadb.ui.form.datasetSettings;

import java.util.Date;

import net.sf.regadb.db.Dataset;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.FormWidget;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.fields.DateField;
import net.sf.regadb.ui.framework.forms.fields.Label;
import net.sf.regadb.ui.framework.forms.fields.TextField;
import net.sf.witty.wt.WGroupBox;
import net.sf.witty.wt.WTable;
import net.sf.witty.wt.i8n.WMessage;

public class DatasetForm extends FormWidget 
{
	private Dataset dataset_;
	//dataset group
	private WGroupBox datasetGroup_;
	private WTable mainGroupTable_;
	
	private Label descriptionL;
	private TextField descriptionTF;
	private Label creationDateL;
	private DateField creationDateDF;
	private Label closedDateL;
	private DateField closedDateDF;
	private Label revisionL;
	private TextField revisionTF;
	
	public DatasetForm(InteractionState interactionState, WMessage formName,  Dataset dataset) 
	{
		super(formName, interactionState);
		dataset_ = dataset;
		init();
		fillData();
	}
	
	public void init()
    {
	    datasetGroup_ = new WGroupBox(tr("form.datasetForm.editView.general"), this);
	    mainGroupTable_=new WTable(datasetGroup_);
		descriptionL = new Label(tr("form.datasetForm.editView.description"));
		descriptionTF = new TextField(getInteractionState(), this);
		descriptionTF.setMandatory(true);
		addLineToTable(mainGroupTable_, descriptionL, descriptionTF);
		if (getInteractionState() != InteractionState.Adding && getInteractionState() != InteractionState.Editing)
		{
			creationDateL = new Label(tr("form.datasetForm.editView.creationDate")) ;
			creationDateDF = new DateField(InteractionState.Viewing, this);
			addLineToTable(mainGroupTable_, creationDateL, creationDateDF);
			closedDateL = new Label(tr("form.datasetForm.editView.closedDate"));
			closedDateDF = new DateField(InteractionState.Viewing, this);
			addLineToTable(mainGroupTable_, closedDateL, closedDateDF);
			revisionL = new Label(tr("form.datasetForm.editView.revision"));
			revisionTF = new TextField(InteractionState.Viewing, this);
			addLineToTable(mainGroupTable_, revisionL, revisionTF);
		}
		
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
         
        dataset_.setDescription(descriptionTF.text());
        dataset_.setRevision(1);
        dataset_.setCreationDate(new Date(System.currentTimeMillis()));
        update(dataset_, t);
        t.commit();
        
        RegaDBMain.getApp().getTree().getTreeContent().datasetSelected.setSelectedItem(dataset_);
        RegaDBMain.getApp().getTree().getTreeContent().datasetSelected.expand();
        RegaDBMain.getApp().getTree().getTreeContent().datasetSelected.refreshAllChildren();
        RegaDBMain.getApp().getTree().getTreeContent().datasetView.selectNode();
	}

	@Override
	public void cancel() 
	{
		
	}

	@Override
	public void deleteObject() 
	{
		
	}

	@Override
	public void redirectAfterDelete() 
	{
		
	}
}
