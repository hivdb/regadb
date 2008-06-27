package net.sf.regadb.ui.form.singlePatient;

import java.util.HashMap;
import java.util.List;

import net.sf.regadb.db.DrugClass;
import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.UserAttribute;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.io.util.StandardObjects;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.FormWidget;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.widgets.table.TableHeader;
import net.sf.regadb.util.date.DateUtils;
import net.sf.witty.wt.SignalListener;
import net.sf.witty.wt.WBreak;
import net.sf.witty.wt.WCheckBox;
import net.sf.witty.wt.WComboBox;
import net.sf.witty.wt.WMouseEvent;
import net.sf.witty.wt.WTable;
import net.sf.witty.wt.WText;
import net.sf.witty.wt.core.utils.WLength;
import net.sf.witty.wt.core.utils.WLengthUnit;
import net.sf.witty.wt.i8n.WMessage;

public class ViralIsolateResistanceEvolutionForm extends FormWidget
{
    private Patient patient_;
    
    private WComboBox asiCombo_;
    private WTable resistanceTable_;
    private WCheckBox showMutations_;
    
    public ViralIsolateResistanceEvolutionForm(WMessage formName, Patient patient) {
        super(formName, InteractionState.Viewing);
        patient_ = patient;

        init();
    }

    public void init()
    {
        WTable wrapper = new WTable(this);
        
        asiCombo_ = new WComboBox(wrapper.elementAt(0, 0));
        loadCombo();
        asiCombo_.clicked.addListener(new SignalListener<WMouseEvent>() {
            public void notify(WMouseEvent a) {
                loadTable();
            }
        });
        
        resistanceTable_ = new WTable(wrapper.elementAt(1, 0));
        
        wrapper.elementAt(1, 1).addWidget(new WBreak());
        showMutations_ = new WCheckBox(tr("form.viralIsolate.evolution.resistance.showMutationsCB"), wrapper.elementAt(1, 1));
        showMutations_.clicked.addListener(new SignalListener<WMouseEvent>()
                {
                    public void notify(WMouseEvent a)
                    {
                        loadTable();
                    }
                });
        
        loadTable();
    }
    
    private void loadCombo() {
        Transaction t = RegaDBMain.getApp().createTransaction();
        
        for(Test test : t.getTests()) {
            if(StandardObjects.getGssId().equals(test.getTestType().getDescription())) {
                asiCombo_.addItem(new DataComboMessage<Test>(test, test.getDescription()));
            }
        }
        asiCombo_.sort();
        
        asiCombo_.setCurrentIndex(0);
        
        UserAttribute ua = t.getUserAttribute(t.getSettingsUser(), "chart.mutation");
        if(ua!=null)
            asiCombo_.setCurrentItem(lt(ua.getValue()));
        
        t.commit();
    }

    private void loadTable()
    {
        resistanceTable_.clear();
        
        Transaction t = RegaDBMain.getApp().createTransaction();
        List<DrugClass> sortedDrugClasses_  = t.getDrugClassesSortedOnResistanceRanking();
        
        List<ViralIsolate> sortedViralIsolates = t.getViralIsolatesSortedOnDate(patient_);
        
        //drug names - column position
        HashMap<String, Integer> viralIsolateColumn = new HashMap<String, Integer>();
        int col = 0;
        resistanceTable_.putElementAt(0, col, new WText());
        col = resistanceTable_.numColumns();
        resistanceTable_.putElementAt(0, col, new WText());
        int maxWidth = 0;
        for(ViralIsolate vi : sortedViralIsolates) {
                col = resistanceTable_.numColumns();
                String viId = vi.getSampleId() + "<br>" + DateUtils.getEuropeanFormat(vi.getSampleDate());
                resistanceTable_.putElementAt(0, col, new TableHeader(lt(viId)));
                viralIsolateColumn.put(viId, col);
                maxWidth += viId.length();
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
        String selectAsi = asiCombo_.currentText().value();
        for(ViralIsolate vi : sortedViralIsolates) {
            String viId = vi.getSampleId() + "<br>" + DateUtils.getEuropeanFormat(vi.getSampleDate());
            for(TestResult tr : vi.getTestResults()) {
                if(tr.getTest().getDescription().equals(selectAsi)) {
                    colN = viralIsolateColumn.get(viId);
                    rowN = drugColumn.get(ViralIsolateFormUtils.getFixedGenericId(tr));
                    if(colN!=null && rowN!=null) {
                        ViralIsolateFormUtils.putResistanceTableResult(tr, resistanceTable_.elementAt(rowN, colN), false, showMutations_.isChecked());
                    }
                    rowN = drugColumn.get(ViralIsolateFormUtils.getFixedGenericId(tr)+"/r");
                    if(colN!=null && rowN!=null) {
                        ViralIsolateFormUtils.putResistanceTableResult(tr, resistanceTable_.elementAt(rowN, colN), true, showMutations_.isChecked());
                    }
                }
            }
        }
        
        resistanceTable_.resize(new WLength(maxWidth+maxWidth/2, WLengthUnit.FontEx), new WLength());
        resistanceTable_.setCellPadding(4);
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
