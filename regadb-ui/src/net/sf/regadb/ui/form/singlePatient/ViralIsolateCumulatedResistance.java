package net.sf.regadb.ui.form.singlePatient;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import net.sf.regadb.db.Genome;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.TestType;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.io.util.StandardObjects;
import net.sf.regadb.sequencedb.SequenceUtils;
import net.sf.regadb.service.wts.ViralIsolateAnalysisHelper;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.FormWidget;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.fields.DateField;
import net.sf.regadb.ui.framework.widgets.SimpleTable;
import net.sf.regadb.util.settings.RegaDBSettings;

import org.xml.sax.SAXException;

import eu.webtoolkit.jwt.Signal;
import eu.webtoolkit.jwt.Signal1;
import eu.webtoolkit.jwt.WCheckBox;
import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WLabel;
import eu.webtoolkit.jwt.WMouseEvent;
import eu.webtoolkit.jwt.WPushButton;
import eu.webtoolkit.jwt.WString;
import eu.webtoolkit.jwt.WTable;

public class ViralIsolateCumulatedResistance extends FormWidget
{
    private ViralIsolateResistanceTable resistanceTable_;
    private WCheckBox showMutations_;
    private WCheckBox showAllAlgorithms_;
    
    private DateField maxDateField;
    private Date maxDate = null;
    
    private Collection<String> drugClasses = null;
    private TestType gssTestType = null;
    private ViralIsolate combinedIsolate = null;
    private Set<TestResult> cumulatedTestResults = new HashSet<TestResult>();
    private Set<Integer> loadedTestIis = new TreeSet<Integer>();
    
    private Patient patient_;
    
    public ViralIsolateCumulatedResistance(WString formName, Patient patient) {
        super(formName, InteractionState.Viewing);
        patient_ = patient;
        
        init();
    }

    public void init()
    {
    	WContainerWidget filterContainer = new WContainerWidget(this);
    	new WLabel(tr("form.viralIsolate.cumulatedResistance.maxDate"), filterContainer).setInline(true);
    	maxDateField = new DateField(InteractionState.Editing, this);
    	maxDateField.setInline(true);
    	filterContainer.addWidget(maxDateField);
    	WPushButton applyMaxDate = new WPushButton(tr("form.viralIsolate.cumulatedResistance.apply"), filterContainer);
    	applyMaxDate.setInline(true);
    	
    	Signal.Listener maxDateListener = new Signal.Listener() {
			@Override
			public void trigger() {
				applyMaxDate();
			}
		};
		
    	applyMaxDate.clicked().addListener(this, maxDateListener);
    	maxDateField.enterPressed().addListener(this, maxDateListener);
    	
    	
        WTable wrapper = new SimpleTable(this);
        wrapper.getElementAt(0, 0).setStyleClass("navigation");
        wrapper.getElementAt(1, 0).setStyleClass("tablewrapper");
        resistanceTable_ = new ViralIsolateResistanceTable(wrapper.getElementAt(1, 0));
        new ViralIsolateResistanceLegend(wrapper.getElementAt(2, 0));
        showMutations_ = new WCheckBox(tr("form.viralIsolate.cumulatedResistance.showMutationsCB"), wrapper.getElementAt(0, 0));
        showMutations_.clicked().addListener(this, new Signal1.Listener<WMouseEvent>()
                {
                    public void trigger(WMouseEvent a)
                    {
                        refreshTable();
                    }
                });
        
        showAllAlgorithms_ = new WCheckBox(tr("form.viralIsolate.cumulatedResistance.showAllAlgorithmsCB"), wrapper.getElementAt(0, 0));
        showAllAlgorithms_.clicked().addListener(this, new Signal1.Listener<WMouseEvent>()
                {
                    public void trigger(WMouseEvent a)
                    {
                        refreshTable();
                    }
                });
        showAllAlgorithms_.setHidden(RegaDBSettings.getInstance().getInstituteConfig().getViralIsolateFormConfig().getAlgorithms() == null);
        
        refreshTable();
    }
    
    private void applyMaxDate(){
    	Date newMaxDate = maxDateField.getDate();
    	
    	if(newMaxDate == maxDate 
    			|| (newMaxDate != null && newMaxDate.equals(maxDate)))
    		return;
    	
    	maxDate = newMaxDate;
    	
    	combinedIsolate = null;
    	loadedTestIis.clear();
    	cumulatedTestResults.clear();
    	
    	refreshTable();
    }
    
    @SuppressWarnings("unchecked")
	private void loadTestResults(Date maxDate) {
        Transaction t =  RegaDBMain.getApp().createTransaction();
        
        if(drugClasses == null){
	        List<String> proteins = t.createQuery("select distinct(p.abbreviation)" +
	        		" from AaSequence aas join aas.protein p join aas.ntSequence nt" +
	        		" where nt.viralIsolate.patient.id="+ patient_.getPatientIi()).list();
	        drugClasses = ViralIsolateFormUtils.getRelevantDrugClassIds(proteins);
        }
        
        if(combinedIsolate == null){
	        Set<ViralIsolate> vis;
	        
	        if(maxDate == null)
	        	vis = patient_.getViralIsolates();
	        else{
	        	vis = new HashSet<ViralIsolate>();
	        	for(ViralIsolate vi : patient_.getViralIsolates())
	        		if(!vi.getSampleDate().after(maxDate))
	        			vis.add(vi);
	        }
	        
	        combinedIsolate = SequenceUtils.combineViralIsolates(vis);
        }
        if(combinedIsolate == null)
        	return;
        
        if(gssTestType == null){
            Genome genome = ((ViralIsolate)(patient_.getViralIsolates().toArray()[0])).getGenome();
    		gssTestType = (genome == null ? null : StandardObjects.getTestType(StandardObjects.getGssDescription(),genome));
        }
        
        if(gssTestType != null){
	        for(Test test : resistanceTable_.getAlgorithms(t, gssTestType, showAllAlgorithms_.isChecked())) {
	        	if(loadedTestIis.add(test.getTestIi())){
		            try {
						cumulatedTestResults.addAll(
								ViralIsolateAnalysisHelper.runViralIsolateResistanceTest(t, combinedIsolate, test));
					} catch (SAXException e1) {
						e1.printStackTrace();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
	        	}
	        }
        }

        t.commit();
    }
        
    private void refreshTable(){
    	loadTestResults(maxDate);
        resistanceTable_.loadTable(
        		drugClasses,
        		showMutations_.isChecked(),
        		showAllAlgorithms_.isChecked(),
        		cumulatedTestResults,
        		gssTestType);
    }

    @Override
    public void cancel() {
        
    }

    @Override
    public WString deleteObject() {
        return null;
    }

    @Override
    public void redirectAfterDelete() {
        
    }

    @Override
    public void saveData() {
        
    }

	@Override
	public void redirectAfterSave() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void redirectAfterCancel() {
		// TODO Auto-generated method stub
		
	}
}

