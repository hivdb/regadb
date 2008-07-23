package net.sf.regadb.ui.form.singlePatient;


import net.sf.regadb.db.Transaction;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.witty.wt.SignalListener;
import net.sf.witty.wt.WBreak;
import net.sf.witty.wt.WCheckBox;
import net.sf.witty.wt.WContainerWidget;
import net.sf.witty.wt.WGroupBox;
import net.sf.witty.wt.WMouseEvent;
import net.sf.witty.wt.WPushButton;
import net.sf.witty.wt.WTable;
import net.sf.witty.wt.core.utils.WLength;
import net.sf.witty.wt.core.utils.WSide;

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
        
        WTable wrapper = new WTable(resistanceGroup_);
        
        resistanceTable_ = new ViralIsolateResistanceTable(wrapper.elementAt(0, 0));
        
        refreshButton_ = new WPushButton(tr("form.viralIsolate.editView.resistance.refreshButton"), wrapper.elementAt(0, 1));
        refreshButton_.setMargin(new WLength(15), WSide.Left);
        refreshButton_.clicked.addListener(new SignalListener<WMouseEvent>()
                {
                    public void notify(WMouseEvent a) 
                    {
                        refreshTable();
                    }
                });
        
        wrapper.elementAt(0, 1).addWidget(new WBreak());
        showMutations_ = new WCheckBox(tr("form.viralIsolate.editView.resistance.showMutationsCB"), wrapper.elementAt(0, 1));
        showMutations_.clicked.addListener(new SignalListener<WMouseEvent>()
                {
                    public void notify(WMouseEvent a)
                    {
                        refreshTable();
                    }
                });
        resistanceTable_.loadTable(showMutations_.isChecked(), viralIsolateForm_.getViralIsolate().getTestResults());
    }
    
    private void refreshTable() {
        Transaction t = RegaDBMain.getApp().createTransaction();
        t.refresh(viralIsolateForm_.getViralIsolate());
        t.commit();
        
        resistanceTable_.loadTable(showMutations_.isChecked(), viralIsolateForm_.getViralIsolate().getTestResults());
    }
}
