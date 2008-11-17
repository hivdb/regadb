package net.sf.regadb.ui.form.singlePatient;


import net.sf.regadb.db.Genome;
import net.sf.regadb.db.TestType;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.io.util.StandardObjects;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.widgets.SimpleTable;
import net.sf.witty.wt.SignalListener;
import net.sf.witty.wt.WCheckBox;
import net.sf.witty.wt.WContainerWidget;
import net.sf.witty.wt.WEmptyEvent;
import net.sf.witty.wt.WGroupBox;
import net.sf.witty.wt.WMouseEvent;
import net.sf.witty.wt.WPushButton;
import net.sf.witty.wt.WTable;
import net.sf.witty.wt.WTimer;

public class ViralIsolateResistanceForm extends WContainerWidget
{
    private ViralIsolateForm viralIsolateForm_;
    
    private WGroupBox resistanceGroup_;
    private ViralIsolateResistanceTable resistanceTable_;
    private WPushButton refreshButton_;
    private WCheckBox showMutations_;
    
    public ViralIsolateResistanceForm(ViralIsolateForm viralIsolateForm)
    {
        super();
        viralIsolateForm_ = viralIsolateForm;

        init();
    }

    public void init()
    {
        resistanceGroup_ = new WGroupBox(tr("form.viralIsolate.editView.group.resistance"), this);
        
        WTable wrapper = new SimpleTable(resistanceGroup_);
        wrapper.elementAt(0, 0).setStyleClass("navigation");
        wrapper.elementAt(1, 0).setStyleClass("tablewrapper");
        
        resistanceTable_ = new ViralIsolateResistanceTable(wrapper.elementAt(1, 0));
        
        refreshButton_ = new WPushButton(tr("form.viralIsolate.editView.resistance.refreshButton"), wrapper.elementAt(0, 0));
        refreshButton_.clicked.addListener(new SignalListener<WMouseEvent>()
                {
                    public void notify(WMouseEvent a) 
                    {
                        refreshTable();
                    }
                });
        
        showMutations_ = new WCheckBox(tr("form.viralIsolate.editView.resistance.showMutationsCB"), wrapper.elementAt(0, 0));
        showMutations_.clicked.addListener(new SignalListener<WMouseEvent>()
                {
                    public void notify(WMouseEvent a)
                    {
                        refreshTable();
                    }
                });
        
        // delay table loading so IE doesn't get confused by the
        // massive amount of changes
        WTimer.singleShot(200, new SignalListener<WEmptyEvent>() {
            public void notify(WEmptyEvent a) {
                refreshTable();
            }
        });
    }
    
    private void refreshTable() {
        Transaction t = RegaDBMain.getApp().createTransaction();
        t.refresh(viralIsolateForm_.getViralIsolate());
        
        Genome genome = ViralIsolateFormUtils.getGenome(viralIsolateForm_.getViralIsolate());
        TestType gssTestType = (genome == null ? null : StandardObjects.getTestType(StandardObjects.getGssDescription(),genome));
        resistanceTable_.loadTable(showMutations_.isChecked(), viralIsolateForm_.getViralIsolate().getTestResults(),gssTestType);
        
        t.commit();
    }
}
