package net.sf.regadb.ui.form.singlePatient;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Therapy;
import net.sf.regadb.db.TherapyCommercial;
import net.sf.regadb.db.TherapyCommercialId;
import net.sf.regadb.db.TherapyGeneric;
import net.sf.regadb.db.TherapyGenericId;
import net.sf.regadb.db.TherapyMotivation;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.FormWidget;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.fields.ComboBox;
import net.sf.regadb.ui.framework.forms.fields.DateField;
import net.sf.regadb.ui.framework.forms.fields.GenomeComboBox;
import net.sf.regadb.ui.framework.forms.fields.Label;
import net.sf.regadb.ui.framework.forms.fields.TextField;
import net.sf.regadb.ui.framework.widgets.editableTable.EditableTable;
import net.sf.regadb.ui.framework.widgets.formtable.FormTable;
import net.sf.regadb.ui.framework.widgets.messagebox.MessageBox;
import net.sf.regadb.util.date.DateUtils;
import net.sf.witty.wt.SignalListener;
import net.sf.witty.wt.WEmptyEvent;
import net.sf.witty.wt.WGroupBox;
import net.sf.witty.wt.WWidget;
import net.sf.witty.wt.i8n.WMessage;

public class TherapyForm extends FormWidget
{
	private Therapy therapy_;
	private Therapy previousTherapy_ = null;
	
	//general group
    private WGroupBox generalGroup_;
    private FormTable generalGroupTable_;
    private Label startDateL;
    private DateField startDateDF;
    private Label stopDateL;
    private DateField stopDateDF;
    private Label motivationL;
    private ComboBox<TherapyMotivation> motivationCB;
    private Label commentL;
    private TextField commentTF;
    
    private Label genomeL;
    private GenomeComboBox genomeCB;

    
    //generic drugs group
    private WGroupBox genericGroup_;
    private EditableTable<TherapyGeneric> drugGenericList_;
    private IGenericDrugSelectionEditableTable iGenericDrugSelectionEditableTable_;
    
    //commercial drugs group
    private WGroupBox commercialGroup_;
    private EditableTable<TherapyCommercial> drugCommercialList_;
    private ICommercialDrugSelectionEditableTable iCommercialDrugSelectionEditableTable_;
    
	public TherapyForm(InteractionState interactionState, WMessage formName, Therapy therapy)
	{
		super(formName, interactionState);
		therapy_ = therapy;
		
		if(interactionState==InteractionState.Adding && therapy != null){
		    setPreviousTherapy(therapy);
		}
		
		init();
		
		fillData();
	}

	public void init()
	{
		//general group
        generalGroup_ = new WGroupBox(tr("form.therapy.editView.general"), this);
        generalGroupTable_ = new FormTable(generalGroup_);
        startDateL = new Label(tr("form.therapy.editView.startDate"));
        startDateDF = new DateField(getInteractionState(), this);
        startDateDF.setMandatory(true);
        generalGroupTable_.addLineToTable(startDateL, startDateDF);
        stopDateL = new Label(tr("form.therapy.editView.stopDate"));
        stopDateDF = new DateField(getInteractionState(), this);
        generalGroupTable_.addLineToTable(stopDateL, stopDateDF);
        
        stopDateDF.addChangeListener(new SignalListener<WEmptyEvent>()
        {
            public void notify(WEmptyEvent a)
            {
                fillComboBoxes();
            }
        });
        
        motivationL = new Label(tr("form.therapy.editView.motivation"));
        motivationCB = new ComboBox<TherapyMotivation>(getInteractionState(), this);
        generalGroupTable_.addLineToTable( motivationL, motivationCB);
        commentL = new Label(tr("form.therapy.editView.comment"));
        commentTF = new TextField(getInteractionState(), this);
        generalGroupTable_.addLineToTable(commentL, commentTF);
        
        genomeL = new Label(tr("form.therapy.editView.genome"));
        genomeCB = new GenomeComboBox(getInteractionState(), this);
        generalGroupTable_.addLineToTable(genomeL, genomeCB);
        
        genericGroup_ = new WGroupBox(tr("form.therapy.editableTable.genericDrugs"), this);
        commercialGroup_ = new WGroupBox(tr("form.therapy.editableTable.commercialDrugs"), this);
        
        addControlButtons();
	}
	
	private void fillData()
	{
        Transaction t = RegaDBMain.getApp().createTransaction();
        
		if(getInteractionState()==InteractionState.Adding)
		{
		    Therapy loadTherapy = getPreviousTherapy();
		    
            Patient p = RegaDBMain.getApp().getTree().getTreeContent().patientSelected.getSelectedItem();
            t.attach(p);
            therapy_ = new Therapy();
            therapy_.setStartDate(new Date(System.currentTimeMillis()));
            
            if(loadTherapy != null){   //copy this therapy into the new one
                copyTherapy(loadTherapy, therapy_);
                if(loadTherapy.getStopDate()!=null){
                    therapy_.setStartDate(loadTherapy.getStopDate());
                }
            }
        }
        else
        {
	        t.attach(therapy_);
        }
        
        t.commit();
	        
        startDateDF.setDate(therapy_.getStartDate());
        stopDateDF.setDate(therapy_.getStopDate());
		commentTF.setText(therapy_.getComment());
        
        fillComboBoxes();
        
		//generic drugs group
        t = RegaDBMain.getApp().createTransaction();
        List<TherapyGeneric> tgs = new ArrayList<TherapyGeneric>();
        for(TherapyGeneric tg : therapy_.getTherapyGenerics())
        {
            tgs.add(tg);
        }
        t.commit();
        iGenericDrugSelectionEditableTable_ = new IGenericDrugSelectionEditableTable(this, therapy_);
        drugGenericList_ = new EditableTable<TherapyGeneric>(genericGroup_, iGenericDrugSelectionEditableTable_, tgs);
			
        //commercial drugs group
        t = RegaDBMain.getApp().createTransaction();
        List<TherapyCommercial> tcs = new ArrayList<TherapyCommercial>();
        for(TherapyCommercial tc : therapy_.getTherapyCommercials())
        {
            tcs.add(tc);
        }
        t.commit();
        iCommercialDrugSelectionEditableTable_ = new ICommercialDrugSelectionEditableTable(this, therapy_);
        drugCommercialList_ = new EditableTable<TherapyCommercial>(commercialGroup_, iCommercialDrugSelectionEditableTable_, tcs);
	}

   public Therapy getPreviousTherapy(){
        return previousTherapy_;
    }
    
    public void setPreviousTherapy(Therapy t){
        previousTherapy_ = t;
    }

	private void copyTherapy(Therapy from, Therapy to){
	    for(TherapyCommercial tc : from.getTherapyCommercials()){
            TherapyCommercial newtc = new TherapyCommercial(
                    new TherapyCommercialId(to,tc.getId().getDrugCommercial()),
                    tc.getDayDosageUnits(),
                    tc.isPlacebo(),
                    tc.isBlind(),
                    tc.getFrequency());
            to.getTherapyCommercials().add(newtc);
        }
        for(TherapyGeneric tg : from.getTherapyGenerics()){
            TherapyGeneric newtg = new TherapyGeneric(
                    new TherapyGenericId(to,tg.getId().getDrugGeneric()),
                    tg.getDayDosageMg(),
                    tg.isPlacebo(),
                    tg.isBlind(),
                    tg.getFrequency());
            to.getTherapyGenerics().add(newtg);
        }
	}
    
    private void fillComboBoxes()
    {
        Transaction t = RegaDBMain.getApp().createTransaction();
        
        if(stopDateDF.getDate()!=null)
        {
            motivationCB.clearItems();
                
            for(TherapyMotivation therapyMotivation : t.getTherapyMotivations())
            {
                motivationCB.addItem(new DataComboMessage<TherapyMotivation>(therapyMotivation, therapyMotivation.getValue()));
            }
            motivationCB.sort();
            
            if(therapy_.getTherapyMotivation()!=null)
            {
                motivationCB.selectItem(therapy_.getTherapyMotivation().getValue());
            }
        }
        motivationCB.setEnabled(stopDateDF.getDate()!=null);
        
        genomeCB.fill(t);
        if(therapy_.getGenome() != null)
            genomeCB.selectItem(therapy_.getGenome().getOrganismName());
        else
            genomeCB.selectIndex(0);
        
        
        t.commit();
    }
	
	@Override
	public void saveData()
	{
        List<String> genericDrugs = new ArrayList<String>();
        ArrayList<WWidget> genericwidgets= drugGenericList_.getAllWidgets(0);
        for(WWidget widget : genericwidgets)
        {
            if(!genericDrugs.contains(((ComboBox)widget).currentItem().value()))
            {
                genericDrugs.add(((ComboBox)widget).currentItem().value());
            }
        }
        
        List<String> commercialDrugs = new ArrayList<String>();
        ArrayList<WWidget> commercialwidgets= drugCommercialList_.getAllWidgets(0);
        for(WWidget widget : commercialwidgets)
        {
            if(!commercialDrugs.contains(((ComboBox)widget).currentItem().value()))
            {
                commercialDrugs.add(((ComboBox)widget).currentItem().value());
            }
        }
        
        Transaction t = RegaDBMain.getApp().createTransaction();
        
        Patient p = RegaDBMain.getApp().getTree().getTreeContent().patientSelected.getSelectedItem();
        t.attach(p);
        t.commit();
        
        Set<Therapy> therapies = p.getTherapies();
        boolean startDateExists = false;
        for(Therapy therapy : therapies)
        {
            if(DateUtils.compareDates(therapy.getStartDate(), startDateDF.getDate())==0 && getInteractionState()==InteractionState.Adding)
            {
                startDateExists = true;
                break;
            }
        }

        if(genericwidgets.size() != genericDrugs.size() || commercialwidgets.size() != commercialDrugs.size())
        {
            MessageBox.showWarningMessage(tr("form.therapy.edit.warning"));
        }
        else if(!drugCommercialList_.validate() || !drugGenericList_.validate())
        {
            //invalid input
        }
        else if(startDateExists)
        {
            MessageBox.showWarningMessage(tr("form.therapy.add.warning"));
        }
        else if(stopDateDF.getDate() != null && DateUtils.compareDates(startDateDF.getDate(), stopDateDF.getDate())>0)
        {
            MessageBox.showWarningMessage(tr("form.therapy.date.warning"));
        }
        else
        {
            t = RegaDBMain.getApp().createTransaction();
            
            if(getInteractionState()==InteractionState.Adding)
            {
                p.addTherapy(therapy_);
            }
            
            therapy_.setStartDate(startDateDF.getDate());
            
            therapy_.setStopDate(stopDateDF.getDate());
            
            if(therapy_.getStopDate()!=null)
            {
                therapy_.setTherapyMotivation(motivationCB.currentValue());
            }
            else
            {
                therapy_.setTherapyMotivation(null);
            }
            
            therapy_.setGenome(genomeCB.currentValue());
            
            therapy_.setComment(getNulled(commentTF.text()));
            
            iCommercialDrugSelectionEditableTable_.setTransaction(t);
            drugCommercialList_.saveData();
            
            iGenericDrugSelectionEditableTable_.setTransaction(t);
            drugGenericList_.saveData();
            
            update(therapy_, t);
            
            Therapy prevTherapy = getPreviousTherapy(); 
            if(prevTherapy != null){
                prevTherapy.setStopDate(therapy_.getStartDate());
                t.attach(prevTherapy);
                t.update(prevTherapy);
            }
            
            t.commit();
            
            RegaDBMain.getApp().getTree().getTreeContent().therapiesSelected.setSelectedItem(therapy_);
            redirectToView(RegaDBMain.getApp().getTree().getTreeContent().therapiesSelected, RegaDBMain.getApp().getTree().getTreeContent().therapiesView);
        }
    }
    
    @Override
    public void cancel()
    {
        if(getInteractionState()==InteractionState.Adding)
        {
            redirectToSelect(RegaDBMain.getApp().getTree().getTreeContent().therapies, RegaDBMain.getApp().getTree().getTreeContent().therapiesSelect);
        }
        else
        {
            redirectToView(RegaDBMain.getApp().getTree().getTreeContent().therapiesSelected, RegaDBMain.getApp().getTree().getTreeContent().therapiesView);
        } 
    }
    
    @Override
    public WMessage deleteObject()
    {
        Transaction t = RegaDBMain.getApp().createTransaction();
        
        Patient p = RegaDBMain.getApp().getTree().getTreeContent().patientSelected.getSelectedItem();
        p.getTherapies().remove(therapy_);
        
        t.delete(therapy_);
        
        t.commit();
        
        return null;
    }

    @Override
    public void redirectAfterDelete() 
    {
        RegaDBMain.getApp().getTree().getTreeContent().therapiesSelect.selectNode();
        RegaDBMain.getApp().getTree().getTreeContent().therapiesSelected.setSelectedItem(null);
    }
}
