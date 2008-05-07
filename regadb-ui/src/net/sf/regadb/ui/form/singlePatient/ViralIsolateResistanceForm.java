package net.sf.regadb.ui.form.singlePatient;

import java.util.HashMap;
import java.util.List;

import net.sf.regadb.db.DrugClass;
import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.io.util.StandardObjects;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.widgets.table.TableHeader;
import net.sf.witty.wt.SignalListener;
import net.sf.witty.wt.WBreak;
import net.sf.witty.wt.WCheckBox;
import net.sf.witty.wt.WContainerWidget;
import net.sf.witty.wt.WGroupBox;
import net.sf.witty.wt.WMouseEvent;
import net.sf.witty.wt.WPushButton;
import net.sf.witty.wt.WTable;
import net.sf.witty.wt.WText;
import net.sf.witty.wt.core.utils.WLength;
import net.sf.witty.wt.core.utils.WLengthUnit;
import net.sf.witty.wt.core.utils.WSide;

public class ViralIsolateResistanceForm extends WContainerWidget
{
    private ViralIsolateForm viralIsolateForm_;
    
    private WGroupBox resistanceGroup_;
    private WTable resistanceTable_;
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
        
        resistanceTable_ = new WTable(wrapper.elementAt(0, 0));
        
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
        
        loadTable();
    }
    
    private void refreshTable() {
        Transaction t = RegaDBMain.getApp().createTransaction();
        t.refresh(viralIsolateForm_.getViralIsolate());
        t.commit();
        
        loadTable();
    }
    
    private void loadTable()
    {
        resistanceTable_.clear();
        
        Transaction t = RegaDBMain.getApp().createTransaction();
        List<DrugClass> sortedDrugClasses_  = t.getDrugClassesSortedOnResistanceRanking();
        
        //drug names - column position
        HashMap<String, Integer> algoColumn = new HashMap<String, Integer>();
        int col = 0;
        resistanceTable_.putElementAt(0, col, new WText());
        col = resistanceTable_.numColumns();
        resistanceTable_.putElementAt(0, col, new WText());
        int maxWidth = 0;
        for(Test test : t.getTests())
        {
            if(StandardObjects.getGssId().equals(test.getTestType().getDescription()) && test.getAnalysis()!=null)
            {
                col = resistanceTable_.numColumns();
                resistanceTable_.putElementAt(0, col, new TableHeader(lt(test.getDescription())));
                algoColumn.put(test.getDescription(), col);
                maxWidth += test.getDescription().length();
            }
        }
        
        //drug names - row position
        HashMap<String, Integer> drugColumn = new HashMap<String, Integer>();
        
        List<DrugGeneric> genericDrugs;
        int row;
        boolean firstGenericDrugInThisClass;
        for(DrugClass dc : sortedDrugClasses_)
        {
            genericDrugs = t.getDrugGenericSortedOnResistanceRanking(dc);
            firstGenericDrugInThisClass = true;
            for(DrugGeneric dg : genericDrugs)
            {
                row = resistanceTable_.numRows();
                if(firstGenericDrugInThisClass)
                {
                    resistanceTable_.putElementAt(row, 0, new TableHeader(lt(dc.getClassId()+ ":")));
                    firstGenericDrugInThisClass = false;
                }
                resistanceTable_.putElementAt(row, 1, new TableHeader(lt(dg.getGenericId())));
                drugColumn.put(dg.getGenericId(), row);
            }
        }
        
        //clear table
        for(int i = 1; i < resistanceTable_.numRows(); i++)
        {
            for(int j = 2; j< resistanceTable_.numColumns(); j++)
            {
                ViralIsolateFormUtils.putResistanceTableResult(null, resistanceTable_.elementAt(i, j), false, showMutations_.isChecked());
            }
        }
        
        Integer colN;
        Integer rowN;
        for(TestResult tr : viralIsolateForm_.getViralIsolate().getTestResults())
        {            
            colN = algoColumn.get(tr.getTest().getDescription());
            rowN = drugColumn.get(ViralIsolateFormUtils.getFixedGenericId(tr));
            if(colN!=null && rowN!=null) {
                ViralIsolateFormUtils.putResistanceTableResult(tr, resistanceTable_.elementAt(rowN, colN), false, showMutations_.isChecked());
            }
            rowN = drugColumn.get(ViralIsolateFormUtils.getFixedGenericId(tr)+"/r");
            if(colN!=null && rowN!=null) {
                ViralIsolateFormUtils.putResistanceTableResult(tr, resistanceTable_.elementAt(rowN, colN), true, showMutations_.isChecked());
            }
        }
        
        resistanceTable_.resize(new WLength(maxWidth+maxWidth/2, WLengthUnit.FontEx), new WLength());
        resistanceTable_.setCellPadding(4);
    }
}
