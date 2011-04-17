package net.sf.regadb.ui.form.singlePatient;


import java.io.IOException;
import java.util.Collection;
import java.util.List;

import net.sf.regadb.db.Genome;
import net.sf.regadb.db.TestType;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.io.util.StandardObjects;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.widgets.SimpleTable;
import net.sf.regadb.ui.framework.widgets.UIUtils;
import eu.webtoolkit.jwt.Signal;
import eu.webtoolkit.jwt.Signal1;
import eu.webtoolkit.jwt.WAnchor;
import eu.webtoolkit.jwt.WCheckBox;
import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WMouseEvent;
import eu.webtoolkit.jwt.WPushButton;
import eu.webtoolkit.jwt.WResource;
import eu.webtoolkit.jwt.WTable;
import eu.webtoolkit.jwt.servlet.WebRequest;
import eu.webtoolkit.jwt.servlet.WebResponse;

public class ViralIsolateResistanceForm extends WContainerWidget
{
    private ViralIsolateForm viralIsolateForm_;
    
    private ViralIsolateResistanceTable resistanceTable_;
    private WPushButton refreshButton_;
    private WCheckBox showMutations_;
    private WCheckBox showAllAlgorithms_;
    private WAnchor downloadAsCsv_;
    
    public ViralIsolateResistanceForm(ViralIsolateForm viralIsolateForm)
    {
        super();
        viralIsolateForm_ = viralIsolateForm;

        init();
    }

    public void init()
    {
        WTable wrapper = new SimpleTable(this);
        wrapper.getElementAt(0, 0).setStyleClass("navigation");
        wrapper.getElementAt(1, 0).setStyleClass("tablewrapper");
        
        resistanceTable_ = new ViralIsolateResistanceTable(wrapper.getElementAt(1, 0));
        new ViralIsolateResistanceLegend(this);
        
        refreshButton_ = new WPushButton(tr("form.viralIsolate.editView.resistance.refreshButton"), wrapper.getElementAt(0, 0));
        refreshButton_.clicked().addListener(this, new Signal1.Listener<WMouseEvent>()
                {
                    public void trigger(WMouseEvent a) 
                    {
                        refreshTable();
                    }
                });
        
        downloadAsCsv_ = new WAnchor(wrapper.getElementAt(0, 0));
        downloadAsCsv_.setText(tr("form.viralIsolate.editView.resistance.downloadAsCsv"));
        downloadAsCsv_.setResource(new WResource() {
			protected void handleRequest(WebRequest request, WebResponse response) throws IOException {
				resistanceTable_.writeTableToCsv(response.out());
			}
        });
        downloadAsCsv_.getResource().suggestFileName("resistance-table.csv");
        
        showMutations_ = new WCheckBox(tr("form.viralIsolate.editView.resistance.showMutationsCB"), wrapper.getElementAt(0, 0));
        showMutations_.clicked().addListener(this, new Signal1.Listener<WMouseEvent>()
                {
                    public void trigger(WMouseEvent a)
                    {
                        refreshTable();
                    }
                });
        
        showAllAlgorithms_ = new WCheckBox(tr("form.viralIsolate.editView.resistance.showAllAlgorithmsCB"), wrapper.getElementAt(0, 0));
        showAllAlgorithms_.clicked().addListener(this, new Signal1.Listener<WMouseEvent>()
                {
                    public void trigger(WMouseEvent a)
                    {
                        refreshTable();
                    }
                });
        
        
        //TODO
        //is this still required?????
        
        // delay table loading so IE doesn't get confused by the
        // massive amount of changes
        UIUtils.singleShot(this, 200, new Signal.Listener() {
            public void trigger() {
                refreshTable();
            }
        });
    }
    
	private void refreshTable() {
        Transaction t = RegaDBMain.getApp().createTransaction();
        t.refresh(viralIsolateForm_.getViralIsolate());
        
        Genome genome = viralIsolateForm_.getViralIsolate().getGenome();
        Collection<String> drugClasses = getRelevantDrugClassIds(t, viralIsolateForm_.getViralIsolate().getViralIsolateIi());
        
        TestType gssTestType = (genome == null ? null : StandardObjects.getTestType(StandardObjects.getGssDescription(),genome));
        resistanceTable_.loadTable(drugClasses, showMutations_.isChecked(), showAllAlgorithms_.isChecked(), viralIsolateForm_.getViralIsolate().getTestResults(),gssTestType);
        
        t.commit();
    }
    
    @SuppressWarnings("unchecked")
	static Collection<String> getRelevantDrugClassIds(Transaction t, int viralIsolateIi){
        List<String> proteins = t.createQuery("select distinct(p.abbreviation)" +
        		" from AaSequence aas join aas.protein p join aas.ntSequence nt" +
        		" where nt.viralIsolate.id="+ viralIsolateIi).list();
        return ViralIsolateFormUtils.getRelevantDrugClassIds(proteins);
    }
}
