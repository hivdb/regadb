package net.sf.regadb.ui.form.singlePatient;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import net.sf.regadb.ui.framework.widgets.table.TableHeader;
import net.sf.regadb.util.date.DateUtils;
import eu.webtoolkit.jwt.Signal1;
import eu.webtoolkit.jwt.WCheckBox;
import eu.webtoolkit.jwt.WLabel;
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
    	WTable wrapper = new SimpleTable(this);
    	
        asiCombo_ = new MyComboBox();
        loadCombo();
        asiCombo_.clicked().addListener(this, new Signal1.Listener<WMouseEvent>() {
            public void trigger(WMouseEvent a) {
                loadTable();
            }
        });
        Label asiL = new Label(tr("form.viralIsolate.editView.report.algorithm"));
        
        wrapper.elementAt(0, 0).addWidget(asiL);
        wrapper.elementAt(0, 0).addWidget(new WLabel(lt("   ")));
        wrapper.elementAt(0, 0).addWidget(asiCombo_);
    	
        
        wrapper.elementAt(1, 0).setStyleClass("navigation");
        wrapper.elementAt(2, 0).setStyleClass("tablewrapper");

    	
        
        resistanceTable_ = new WTable(wrapper.elementAt(2, 0));
        resistanceTable_.setStyleClass("datatable datatable-resistance");
        
        
        showMutations_ = new WCheckBox(tr("form.viralIsolate.evolution.resistance.showMutationsCB"), wrapper.elementAt(1, 0));
        showMutations_.clicked().addListener(this, new Signal1.Listener<WMouseEvent>()
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
        
        Set<String> tests = new HashSet<String>();
        for(TestResult tr : patient_.getTestResults()) {
        	Test test = tr.getTest();
            if(StandardObjects.getGssDescription().equals(test.getTestType().getDescription())){
            	if(tests.add(test.getDescription()))
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
                String viId = vi.getSampleId() + "<br/>" + DateUtils.format(vi.getSampleDate());
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
            String viId = vi.getSampleId() + "<br/>" + DateUtils.format(vi.getSampleDate());
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
