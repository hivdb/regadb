package net.sf.regadb.ui.form.singlePatient;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Therapy;
import net.sf.regadb.db.TherapyCommercial;
import net.sf.regadb.db.TherapyCommercialId;
import net.sf.regadb.db.TherapyGeneric;
import net.sf.regadb.db.TherapyGenericId;
import net.sf.regadb.db.TherapyMotivation;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.ObjectForm;
import net.sf.regadb.ui.framework.forms.fields.ComboBox;
import net.sf.regadb.ui.framework.forms.fields.DateField;
import net.sf.regadb.ui.framework.forms.fields.Label;
import net.sf.regadb.ui.framework.forms.fields.TextField;
import net.sf.regadb.ui.framework.widgets.UIUtils;
import net.sf.regadb.ui.framework.widgets.editableTable.EditableTable;
import net.sf.regadb.ui.framework.widgets.formtable.FormTable;
import net.sf.regadb.ui.tree.ObjectTreeNode;
import net.sf.regadb.util.date.DateUtils;
import net.sf.regadb.util.settings.RegaDBSettings;
import eu.webtoolkit.jwt.Signal;
import eu.webtoolkit.jwt.WGroupBox;
import eu.webtoolkit.jwt.WString;
import eu.webtoolkit.jwt.WWidget;

public class TherapyForm extends ObjectForm<Therapy>
{
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

    
    //generic drugs group
    private WGroupBox genericGroup_;
    private EditableTable<TherapyGeneric> drugGenericList_;
    private IGenericDrugSelectionEditableTable iGenericDrugSelectionEditableTable_;
    
    //commercial drugs group
    private WGroupBox commercialGroup_;
    private EditableTable<TherapyCommercial> drugCommercialList_;
    private ICommercialDrugSelectionEditableTable iCommercialDrugSelectionEditableTable_;
    
	public TherapyForm(WString formName, InteractionState interactionState, ObjectTreeNode<Therapy> node, Therapy therapy)
	{
		super(formName,interactionState,node,therapy);
		
		if(RegaDBMain.getApp().isPatientInteractionAllowed(interactionState)){
			if(interactionState==InteractionState.Adding && therapy != null){
			    setPreviousTherapy(therapy);
			}
			
			init();
			
			fillData();
		}
	}

	public void init()
	{
		//general group
        generalGroup_ = new WGroupBox(tr("form.therapy.general"), this);
        generalGroupTable_ = new FormTable(generalGroup_);
        startDateL = new Label(tr("form.therapy.startDate"));
        startDateDF = new DateField(getInteractionState(), this, RegaDBSettings.getInstance().getDateFormat());
        startDateDF.setMandatory(true);
        generalGroupTable_.addLineToTable(startDateL, startDateDF);
        stopDateL = new Label(tr("form.therapy.stopDate"));
        stopDateDF = new DateField(getInteractionState(), this, RegaDBSettings.getInstance().getDateFormat());
        generalGroupTable_.addLineToTable(stopDateL, stopDateDF);
        
        stopDateDF.addChangeListener(new Signal.Listener()
        {
            public void trigger()
            {
                fillComboBoxes();
            }
        });
        
        motivationL = new Label(tr("form.therapy.motivation"));
        motivationCB = new ComboBox<TherapyMotivation>(getInteractionState(), this);
        generalGroupTable_.addLineToTable( motivationL, motivationCB);
        commentL = new Label(tr("form.therapy.comment"));
        commentTF = new TextField(getInteractionState(), this);
        generalGroupTable_.addLineToTable(commentL, commentTF);
        
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
		    
            Patient p = RegaDBMain.getApp().getTree().getTreeContent().patientTreeNode.getSelectedItem();
            t.attach(p);
            setObject(new Therapy());
            getObject().setStartDate(new Date(System.currentTimeMillis()));
            
            if(loadTherapy != null){   //copy this therapy into the new one
                copyTherapy(loadTherapy, getObject());
                if(loadTherapy.getStopDate()!=null){
                	getObject().setStartDate(loadTherapy.getStopDate());
                }
            }
        }
        else
        {
	        t.attach(getObject());
        }
        
        t.commit();
	        
        startDateDF.setDate(getObject().getStartDate());
        stopDateDF.setDate(getObject().getStopDate());
		commentTF.setText(getObject().getComment());
        
        fillComboBoxes();
        
		//generic drugs group
        t = RegaDBMain.getApp().createTransaction();
        TreeSet<TherapyGeneric> tgs = new TreeSet<TherapyGeneric>(new Comparator<TherapyGeneric>(){
			@Override
			public int compare(TherapyGeneric o1, TherapyGeneric o2) {
				return o1.getId().getDrugGeneric().getGenericName().compareTo(
						o2.getId().getDrugGeneric().getGenericName());
			}
        });
        tgs.addAll(getObject().getTherapyGenerics());
        t.commit();
        iGenericDrugSelectionEditableTable_ = new IGenericDrugSelectionEditableTable(this, getObject());
        drugGenericList_ = new EditableTable<TherapyGeneric>(genericGroup_, iGenericDrugSelectionEditableTable_, tgs);
			
        //commercial drugs group
        t = RegaDBMain.getApp().createTransaction();
        TreeSet<TherapyCommercial> tcs = new TreeSet<TherapyCommercial>(new Comparator<TherapyCommercial>() {
			@Override
			public int compare(TherapyCommercial arg0, TherapyCommercial arg1) {
				return arg0.getId().getDrugCommercial().getName().compareTo(
						arg1.getId().getDrugCommercial().getName());
			}
		});
        tcs.addAll(getObject().getTherapyCommercials());
        t.commit();
        iCommercialDrugSelectionEditableTable_ = new ICommercialDrugSelectionEditableTable(this, getObject());
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
            motivationCB.addNoSelectionItem();
            
            if(getObject().getTherapyMotivation()!=null)
            {
                motivationCB.selectItem(getObject().getTherapyMotivation().getValue());
            }
        }
        motivationCB.setEnabled(stopDateDF.getDate()!=null);
        
        t.commit();
    }
	
	@Override
	public void saveData()
	{
        Set<String> genericDrugs = new HashSet<String>();
        ArrayList<WWidget> genericwidgets= drugGenericList_.getAllWidgets(0);
        for(WWidget widget : genericwidgets)
        {
            if(!genericDrugs.contains(((ComboBox)widget).currentItem().getDataValue()))
            {
                genericDrugs.add(((ComboBox)widget).currentItem().getDataValue().toString());
            }
        }
        
        Set<String> commercialDrugs = new HashSet<String>();
        ArrayList<WWidget> commercialwidgets= drugCommercialList_.getAllWidgets(0);
        for(WWidget widget : commercialwidgets)
        {
            if(!commercialDrugs.contains(((ComboBox)widget).currentItem().getDataValue()))
            {
                commercialDrugs.add(((ComboBox)widget).currentItem().getDataValue().toString());
            }
        }
        
        Transaction t = RegaDBMain.getApp().createTransaction();
        
        Patient p = RegaDBMain.getApp().getTree().getTreeContent().patientTreeNode.getSelectedItem();
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
        	UIUtils.showWarningMessageBox(this, tr("form.therapy.edit.warning"));
        }
        else if(!drugCommercialList_.validate() || !drugGenericList_.validate())
        {
            //invalid input
        }
        else if(startDateExists)
        {
        	UIUtils.showWarningMessageBox(this, tr("form.therapy.add.warning"));
        }
        else if(stopDateDF.getDate() != null && DateUtils.compareDates(startDateDF.getDate(), stopDateDF.getDate())>0)
        {
        	UIUtils.showWarningMessageBox(this, tr("form.therapy.date.warning"));
        }
        else
        {
            t = RegaDBMain.getApp().createTransaction();
            
            if(getInteractionState()==InteractionState.Adding)
            {
                p.addTherapy(getObject());
            }
            
            getObject().setStartDate(startDateDF.getDate());
            
            getObject().setStopDate(stopDateDF.getDate());
            
            if(getObject().getStopDate()!=null)
            {
            	getObject().setTherapyMotivation(motivationCB.currentValue());
            }
            else
            {
            	getObject().setTherapyMotivation(null);
            }
            
            getObject().setComment(getNulled(commentTF.text()));
            
            iCommercialDrugSelectionEditableTable_.setTransaction(t);
            drugCommercialList_.saveData();
            
            iGenericDrugSelectionEditableTable_.setTransaction(t);
            drugGenericList_.saveData();
            
            update(getObject(), t);
            
            Therapy prevTherapy = getPreviousTherapy(); 
            if(prevTherapy != null){
                prevTherapy.setStopDate(getObject().getStartDate());
                t.attach(prevTherapy);
                t.update(prevTherapy);
            }
            
            t.commit();
        }
    }
    
    @Override
    public void cancel()
    {
    }
    
    @Override
    public WString deleteObject()
    {
        Transaction t = RegaDBMain.getApp().createTransaction();
        
        Patient p = RegaDBMain.getApp().getTree().getTreeContent().patientTreeNode.getSelectedItem();
        p.getTherapies().remove(getObject());
        
        t.delete(getObject());
        
        t.commit();
        
        return null;
    }
}
