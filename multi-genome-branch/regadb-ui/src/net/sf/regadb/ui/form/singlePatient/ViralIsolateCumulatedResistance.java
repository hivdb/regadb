package net.sf.regadb.ui.form.singlePatient;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.io.util.StandardObjects;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.FormWidget;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.witty.wt.SignalListener;
import net.sf.witty.wt.WBreak;
import net.sf.witty.wt.WCheckBox;
import net.sf.witty.wt.WMouseEvent;
import net.sf.witty.wt.WTable;
import net.sf.witty.wt.i8n.WMessage;

public class ViralIsolateCumulatedResistance extends FormWidget
{
    private ViralIsolateResistanceTable resistanceTable_;
    private WCheckBox showMutations_;
    
    private Patient patient_;
    
    public ViralIsolateCumulatedResistance(WMessage formName, Patient patient) {
        super(formName, InteractionState.Viewing);
        patient_ = patient;
        
        init();
    }

    public void init()
    {
        WTable wrapper = new WTable(this);
        
        resistanceTable_ = new ViralIsolateResistanceTable(wrapper.elementAt(0, 0));
        
        wrapper.elementAt(0, 1).addWidget(new WBreak());
        showMutations_ = new WCheckBox(tr("form.viralIsolate.cumulatedResistance.showMutationsCB"), wrapper.elementAt(0, 1));
        showMutations_.clicked.addListener(new SignalListener<WMouseEvent>()
                {
                    public void notify(WMouseEvent a)
                    {
                        refreshTable();
                    }
                });
        
        refreshTable();
    }
    
    private void refreshTable() {
        Transaction t =  RegaDBMain.getApp().createTransaction();
        
        Set<TestResult> cumulatedTestResults = new HashSet<TestResult>();
        
        Set<ViralIsolate> vis = patient_.getViralIsolates();
        
        for(Test test : t.getTests()) {
            if(StandardObjects.getGssId().equals(test.getTestType().getDescription())) {
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
        
        resistanceTable_.loadTable(showMutations_.isChecked(), cumulatedTestResults);
        
        t.commit();
    }

    @Override
    public void cancel() {
        
    }

    @Override
    public WMessage deleteObject() {
        return null;
    }

    @Override
    public void redirectAfterDelete() {
        
    }

    @Override
    public void saveData() {
        
    }
}

