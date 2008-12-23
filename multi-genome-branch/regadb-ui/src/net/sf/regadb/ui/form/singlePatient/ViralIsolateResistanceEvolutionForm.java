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
import net.sf.regadb.ui.framework.forms.fields.Label;
import net.sf.regadb.ui.framework.widgets.MyComboBox;
import net.sf.regadb.ui.framework.widgets.SimpleTable;
import net.sf.regadb.ui.framework.widgets.formtable.FormTable;
import net.sf.regadb.ui.framework.widgets.table.TableHeader;
import net.sf.regadb.util.date.DateUtils;
import eu.webtoolkit.jwt.Signal1;
import eu.webtoolkit.jwt.WCheckBox;
import eu.webtoolkit.jwt.WGroupBox;
import eu.webtoolkit.jwt.WMouseEvent;
import eu.webtoolkit.jwt.WString;
import eu.webtoolkit.jwt.WTable;
import eu.webtoolkit.jwt.WText;

public class ViralIsolateResistanceEvolutionForm extends FormWidget
{
    private Patient patient_;
    
    private MyComboBox asiCombo_;
    private WTable resistanceTable_;
    private WCheckBox showMutations_;
    
    public ViralIsolateResistanceEvolutionForm(WString formName, Patient patient) {
        super(formName, InteractionState.Viewing);
        patient_ = patient;

        init();
    }

    public void init()
    {
    	WGroupBox algorithm = new WGroupBox(tr("form.viralIsolate.evolution.group.algorithm"), this);
    	FormTable form = new FormTable(algorithm);
        asiCombo_ = new MyComboBox();
        loadCombo();
        asiCombo_.clicked.addListener(this, new Signal1.Listener<WMouseEvent>() {
            public void trigger(WMouseEvent a) {
                loadTable();
            }
        });
        Label asiL = new Label(tr("form.viralIsolate.editView.report.algorithm"));
        form.addLineToTable(asiL, asiCombo_);
        
    	
    	WGroupBox resistanceGroup = new WGroupBox(tr("form.viralIsolate.evolution.group.resistance"), algorithm);
        WTable wrapper = new SimpleTable(resistanceGroup);
        wrapper.elementAt(0, 0).setStyleClass("navigation");
        wrapper.elementAt(1, 0).setStyleClass("tablewrapper");

    	
        
        resistanceTable_ = new WTable(wrapper.elementAt(1, 0));
        resistanceTable_.setStyleClass("datatable datatable-resistance");
        
        
        showMutations_ = new WCheckBox(tr("form.viralIsolate.evolution.resistance.showMutationsCB"), wrapper.elementAt(0, 0));
        showMutations_.clicked.addListener(this, new Signal1.Listener<WMouseEvent>()
                {
                    public void trigger(WMouseEvent a)
                    {
                        loadTable();
                    }
                });
        
        loadTable();
    }
    
    private void loadCombo() {
        Transaction t = RegaDBMain.getApp().createTransaction();
        
        for(Test test : t.getTests()) {
            if(StandardObjects.getGssDescription().equals(test.getTestType().getDescription())){
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
        resistanceTable_.elementAt(0, col).addWidget(new WText());
        col = resistanceTable_.columnCount();
        resistanceTable_.elementAt(0, col).addWidget(new WText());

        int maxWidth = 0;
        for(ViralIsolate vi : sortedViralIsolates) {
                col = resistanceTable_.columnCount();
                String viId = vi.getSampleId() + "<br>" + DateUtils.getEuropeanFormat(vi.getSampleDate());
                resistanceTable_.elementAt(0, col).addWidget(new TableHeader(lt(viId)));
                resistanceTable_.elementAt(0, col).setStyleClass("column-title");
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
                row = resistanceTable_.rowCount();
                if(firstGenericDrugInThisClass)
                {
                    resistanceTable_.elementAt(row, 0).addWidget(new TableHeader(lt(dc.getClassId()+ ":")));
                    resistanceTable_.elementAt(row, 0).setStyleClass("form-label-area");
                    firstGenericDrugInThisClass = false;
                }
                resistanceTable_.elementAt(row, 1).addWidget(new TableHeader(lt(dg.getGenericId())));
                resistanceTable_.elementAt(row, 1).setStyleClass("form-label-area");
                drugColumn.put(dg.getGenericId(), row);
            }
        }
        
        //clear table
        for(int i = 1; i < resistanceTable_.rowCount(); i++)
        {
            for(int j = 2; j< resistanceTable_.columnCount(); j++)
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
