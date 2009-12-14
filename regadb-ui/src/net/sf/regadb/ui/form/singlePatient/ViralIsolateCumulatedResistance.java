package net.sf.regadb.ui.form.singlePatient;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.regadb.db.Genome;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.TestType;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.io.util.StandardObjects;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.FormWidget;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.widgets.SimpleTable;
import eu.webtoolkit.jwt.Signal1;
import eu.webtoolkit.jwt.WCheckBox;
import eu.webtoolkit.jwt.WMouseEvent;
import eu.webtoolkit.jwt.WString;
import eu.webtoolkit.jwt.WTable;

public class ViralIsolateCumulatedResistance extends FormWidget
{
    private ViralIsolateResistanceTable resistanceTable_;
    private WCheckBox showMutations_;
    
    private Patient patient_;
    
    public ViralIsolateCumulatedResistance(WString formName, Patient patient) {
        super(formName, InteractionState.Viewing);
        patient_ = patient;
        
        init();
    }

    public void init()
    {
        WTable wrapper = new SimpleTable(this);
        wrapper.getElementAt(0, 0).setStyleClass("navigation");
        wrapper.getElementAt(1, 0).setStyleClass("tablewrapper");
        resistanceTable_ = new ViralIsolateResistanceTable(wrapper.getElementAt(1, 0));
        showMutations_ = new WCheckBox(tr("form.viralIsolate.cumulatedResistance.showMutationsCB"), wrapper.getElementAt(0, 0));
        showMutations_.clicked().addListener(this, new Signal1.Listener<WMouseEvent>()
                {
                    public void trigger(WMouseEvent a)
                    {
                        refreshTable();
                    }
                });
        
        refreshTable();
    }
    
    @SuppressWarnings("unchecked")
	private void refreshTable() {
        Transaction t =  RegaDBMain.getApp().createTransaction();
        
        List<String> proteins = t.createQuery("select distinct(p.abbreviation)" +
        		" from AaSequence aas join aas.protein p join aas.ntSequence nt" +
        		" where nt.viralIsolate.patient.id="+ patient_.getPatientIi()).list();
        Collection<String> drugClasses = ViralIsolateFormUtils.getRelevantDrugClassIds(proteins);
        
        Set<TestResult> cumulatedTestResults = new HashSet<TestResult>();
        
        Set<ViralIsolate> vis = patient_.getViralIsolates();
        
        for(Test test : t.getTests()) {
            if(StandardObjects.getGssDescription().equals(test.getTestType().getDescription())) {
                Map<String, TestResult> cumulatedTestResultsForOneAlgorithm = new HashMap<String, TestResult>();
                for(ViralIsolate vi : vis) {
                    for(TestResult tr : vi.getTestResults()) {
                        if(tr.getTest().getDescription().equals(test.getDescription())) {
                            TestResult tr1 = cumulatedTestResultsForOneAlgorithm.get(tr.getDrugGeneric().getGenericId());
                            if(tr1==null || Double.parseDouble(tr.getValue())<Double.parseDouble(tr1.getValue())) {
                                cumulatedTestResultsForOneAlgorithm.put(tr.getDrugGeneric().getGenericId(), tr);
                            }
                        }
                    }
                }
                for(Map.Entry<String, TestResult> e : cumulatedTestResultsForOneAlgorithm.entrySet()) {
                    cumulatedTestResults.add(e.getValue());
                }
            }
        }
        
        Genome genome = ViralIsolateFormUtils.getGenome((ViralIsolate)patient_.getViralIsolates().toArray()[0]);
        TestType gssTestType = (genome == null ? null : StandardObjects.getTestType(StandardObjects.getGssDescription(),genome));
        
        resistanceTable_.loadTable(drugClasses, showMutations_.isChecked(), cumulatedTestResults, gssTestType);
        
        t.commit();
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
}

