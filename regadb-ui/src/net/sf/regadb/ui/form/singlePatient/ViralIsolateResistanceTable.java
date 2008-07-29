package net.sf.regadb.ui.form.singlePatient;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import net.sf.regadb.db.DrugClass;
import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.io.util.StandardObjects;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.widgets.table.TableHeader;
import net.sf.witty.wt.WContainerWidget;
import net.sf.witty.wt.WTable;
import net.sf.witty.wt.WText;

public class ViralIsolateResistanceTable extends WTable {
    public ViralIsolateResistanceTable(WContainerWidget parent) {
        super(parent);
        this.setStyleClass("datatable datatable-resistance");
    }
    
    public void loadTable(boolean showMutations, Set<TestResult> testResults) {
        clear();
        
        Transaction t = RegaDBMain.getApp().createTransaction();
        List<DrugClass> sortedDrugClasses_  = t.getDrugClassesSortedOnResistanceRanking();
        
        //drug names - column position
        HashMap<String, Integer> algoColumn = new HashMap<String, Integer>();
        int col = 0;
        putElementAt(0, col, new WText(lt("")));
        col = numColumns();
        putElementAt(0, col, new WText(lt("")));
        int maxWidth = 0;
        for(Test test : t.getTests())
        {
            if(StandardObjects.getGssId().equals(test.getTestType().getDescription()) && test.getAnalysis()!=null)
            {
                col = numColumns();
                putElementAt(0, col, new TableHeader(lt(test.getDescription())));
                elementAt(0, col).setStyleClass("column-title");
                
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
                row = numRows();
                if(firstGenericDrugInThisClass)
                {
                    putElementAt(row, 0, new TableHeader(lt(dc.getClassId()+ ":")));
                    firstGenericDrugInThisClass = false;
                    elementAt(row, 0).setStyleClass("form-label-area");
                }
                putElementAt(row, 1, new TableHeader(lt(dg.getGenericId())));
                drugColumn.put(dg.getGenericId(), row);
                elementAt(row, 1).setStyleClass("form-label-area");
            }
        }
        
        //clear table
        for(int i = 1; i < numRows(); i++)
        {
            for(int j = 2; j< numColumns(); j++)
            {
                ViralIsolateFormUtils.putResistanceTableResult(null, elementAt(i, j), false, showMutations);
            }
        }
        
        Integer colN;
        Integer rowN;
        for(TestResult tr : testResults)
        {            
            colN = algoColumn.get(tr.getTest().getDescription());
            rowN = drugColumn.get(ViralIsolateFormUtils.getFixedGenericId(tr));
            if(colN!=null && rowN!=null) {
                ViralIsolateFormUtils.putResistanceTableResult(tr, elementAt(rowN, colN), false, showMutations);
            }
            rowN = drugColumn.get(ViralIsolateFormUtils.getFixedGenericId(tr)+"/r");
            if(colN!=null && rowN!=null) {
                ViralIsolateFormUtils.putResistanceTableResult(tr, elementAt(rowN, colN), true, showMutations);
            }
        }
        
    }
}
