package net.sf.regadb.ui.form.singlePatient;

import java.util.ArrayList;
import java.util.List;

import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.Therapy;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.FormWidget;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.fields.DateField;
import net.sf.regadb.ui.framework.forms.fields.Label;
import net.sf.regadb.ui.framework.forms.fields.TextField;
import net.sf.witty.wt.i8n.WMessage;
import net.sf.witty.wt.widgets.WGroupBox;
import net.sf.witty.wt.widgets.WTable;

public class TherapyForm extends FormWidget
{
	private Therapy therapy_;
	
	//general group
    private WGroupBox generalGroup_;
    private WTable generalGroupTable_;
    private Label startDateL;
    private DateField startDateTF;
    private Label stopDateL;
    private DateField stopDateTF;
    private Label commentL;
    private TextField commentTF;
    
    //generic drugs group
    private DrugSelectionForm genericDrugs_;
    
	public TherapyForm(InteractionState interactionState, WMessage formName, Therapy therapy)
	{
		super(formName, interactionState);
		therapy_ = therapy;
		
		init();
	}

	public void init()
	{
		//general group
        generalGroup_ = new WGroupBox(tr("form.therapy.editView.general"), this);
        generalGroupTable_ = new WTable(generalGroup_);
        startDateL = new Label(tr("form.therapy.editView.startDate"));
        startDateTF = new DateField(getInteractionState(), this);
        startDateTF.setMandatory(true);
        addLineToTable(generalGroupTable_, startDateL, startDateTF);
        stopDateL = new Label(tr("form.therapy.editView.stopDate"));
        stopDateTF = new DateField(getInteractionState(), this);
        addLineToTable(generalGroupTable_, stopDateL, stopDateTF);
        commentL = new Label(tr("form.therapy.editView.comment"));
        commentTF = new TextField(getInteractionState(), this);
        addLineToTable(generalGroupTable_, commentL, commentTF);
        
        Transaction t = RegaDBMain.getApp().createTransaction();
        //ArrayList<DataComboMessage<DrugType>> comboItems
        //generic drugs group
        List<DrugGeneric> genericDrugs = t.getGenericDrugs();
        ArrayList<DataComboMessage<DrugGeneric>> genericDrugsList = new ArrayList<DataComboMessage<DrugGeneric>>(); 
        for(DrugGeneric dg: genericDrugs)
        {
        	genericDrugsList.add(new DataComboMessage<DrugGeneric>(dg, dg.getGenericId()));
        }
        genericDrugs_ = new DrugSelectionForm(this, tr("form.therapy.editView.genericDrugs"), genericDrugsList, tr("form.therapy.editView.drugDosage.mg"));
        generalGroup_.addWidget(genericDrugs_);
	}
	
	@Override
	public void saveData()
	{
	
	}
}
