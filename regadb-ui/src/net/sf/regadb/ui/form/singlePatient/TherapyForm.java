package net.sf.regadb.ui.form.singlePatient;

import java.util.ArrayList;
import java.util.List;

import net.sf.regadb.db.DrugCommercial;
import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Therapy;
import net.sf.regadb.db.TherapyCommercial;
import net.sf.regadb.db.TherapyCommercialId;
import net.sf.regadb.db.TherapyGeneric;
import net.sf.regadb.db.TherapyGenericId;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.FormWidget;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.fields.DateField;
import net.sf.regadb.ui.framework.forms.fields.Label;
import net.sf.regadb.ui.framework.forms.fields.TextField;
import net.sf.regadb.util.pair.Pair;
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
    
    //commercial drugs group
    private DrugSelectionForm commercialDrugs_;
    
	public TherapyForm(InteractionState interactionState, WMessage formName, Therapy therapy)
	{
		super(formName, interactionState);
		therapy_ = therapy;
		
		init();
		
		fillData();
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
        //generic drugs group
        List<DrugGeneric> genericDrugs = t.getGenericDrugs();
        ArrayList<DataComboMessage<DrugGeneric>> genericDrugsList = new ArrayList<DataComboMessage<DrugGeneric>>(); 
        for(DrugGeneric dg: genericDrugs)
        {
        	genericDrugsList.add(new DataComboMessage<DrugGeneric>(dg, dg.getGenericId()));
        }
        genericDrugs_ = new DrugSelectionForm(this, tr("form.therapy.editView.genericDrugs"), genericDrugsList, tr("form.therapy.editView.drugDosage.mg"));
        addWidget(genericDrugs_);
        
        //commercial drugs group
        List<DrugCommercial> commercialDrugs = t.getCommercialDrugs();
        ArrayList<DataComboMessage<DrugCommercial>> commercialDrugList = new ArrayList<DataComboMessage<DrugCommercial>>(); 
        for(DrugCommercial dg: commercialDrugs)
        {
        	commercialDrugList.add(new DataComboMessage<DrugCommercial>(dg, dg.getName()));
        }
        commercialDrugs_ = new DrugSelectionForm(this, tr("form.therapy.editView.commercialDrugs"), commercialDrugList, tr("form.therapy.editView.drugDosage.unit"));
        addWidget(commercialDrugs_);
        t.commit();
        
        addControlButtons();
	}
	
	private void fillData()
	{
		if(getInteractionState()!=InteractionState.Adding)
		{
			Transaction t;
			t = RegaDBMain.getApp().createTransaction();
	        t.update(therapy_);
	        t.commit();
	        
			startDateTF.setDate(therapy_.getStartDate());
			stopDateTF.setDate(therapy_.getStopDate());
			commentTF.setText(therapy_.getComment());
			
			ArrayList<Pair<DataComboMessage<DrugGeneric>,Double>> genericResults = null;
			if(therapy_.getTherapyGenerics().size()>0)
			{
				genericResults = new ArrayList<Pair<DataComboMessage<DrugGeneric>,Double>>();
				for(TherapyGeneric tg : therapy_.getTherapyGenerics())
				{
					genericResults.add(new Pair<DataComboMessage<DrugGeneric>,Double>
					(
						new DataComboMessage<DrugGeneric>(tg.getId().getDrugGeneric(),tg.getId().getDrugGeneric().getGenericId()), 
						tg.getDayDosageMg())
					);
				}
			}
			genericDrugs_.fillData(genericResults);
			
			ArrayList<Pair<DataComboMessage<DrugCommercial>,Double>> commercialResults = null;
			if(therapy_.getTherapyCommercials().size()>0)
			{
				commercialResults = new ArrayList<Pair<DataComboMessage<DrugCommercial>,Double>>();
				for(TherapyCommercial tc : therapy_.getTherapyCommercials())
				{
					commercialResults.add(new Pair<DataComboMessage<DrugCommercial>,Double>
					(
						new DataComboMessage<DrugCommercial>(tc.getId().getDrugCommercial(),tc.getId().getDrugCommercial().getName()), 
						tc.getDayDosageUnits())
					);
				}
			}
			commercialDrugs_.fillData(commercialResults);
		}
	}
	
	@Override
	public void saveData()
	{
		Transaction t = RegaDBMain.getApp().createTransaction();
		
		Patient p = RegaDBMain.getApp().getTree().getTreeContent().patientSelected.getSelectedPatient();
		t.update(p);
				
		if(getInteractionState()==InteractionState.Adding)
		{
			therapy_ = p.createTherapy(startDateTF.getDate());
		}
		else
		{
			therapy_.setStartDate(startDateTF.getDate());
		}
		
		therapy_.setStopDate(stopDateTF.getDate());
		
		if(canStore(commentTF.text()))
		{
			therapy_.setComment(commentTF.text());
		}
		else
		{
			therapy_.setComment(null);
		}
		
		//deleting old drugs
		ArrayList<TherapyGeneric> removeMemGeneric = new ArrayList<TherapyGeneric>(); 
		for(TherapyGeneric tg : therapy_.getTherapyGenerics())
		{
			removeMemGeneric.add(tg);
		}
		for(TherapyGeneric tg : removeMemGeneric)
		{
			therapy_.getTherapyGenerics().remove(tg);
			t.delete(tg);
		}
		//deleting old drugs
		
		//deleting old drugs
		ArrayList<TherapyCommercial> removeMemCommercial = new ArrayList<TherapyCommercial>(); 
		for(TherapyCommercial tc : therapy_.getTherapyCommercials())
		{
			removeMemCommercial.add(tc);
		}
		for(TherapyCommercial tc : removeMemCommercial)
		{
			therapy_.getTherapyCommercials().remove(tc);
			t.delete(tc);
		}
		//deleting old drugs
		
		t.update(therapy_);
		t.commit();
		
		//adding new drugs
		t = RegaDBMain.getApp().createTransaction();
		List<Pair<DrugGeneric, Double>> dataG = genericDrugs_.getData();
		for(Pair<DrugGeneric, Double> dg : dataG)
		{
			therapy_.getTherapyGenerics().add(new TherapyGeneric(new TherapyGenericId(therapy_, dg.getKey()), dg.getValue()));
		}
		
		List<Pair<DrugCommercial, Double>> dataC = commercialDrugs_.getData();
		for(Pair<DrugCommercial, Double> dc : dataC)
		{
			therapy_.getTherapyCommercials().add(new TherapyCommercial(new TherapyCommercialId(therapy_, dc.getKey()), dc.getValue()));
		}
		
		t.update(therapy_);
		t.commit();
		//adding new drugs
		
		RegaDBMain.getApp().getTree().getTreeContent().therapiesSelected.setSelectedTherapy(therapy_);
        RegaDBMain.getApp().getTree().getTreeContent().therapiesSelected.expand();
        RegaDBMain.getApp().getTree().getTreeContent().therapiesSelected.refreshAllChildren();
        RegaDBMain.getApp().getTree().getTreeContent().therapiesView.selectNode();
	}
}
