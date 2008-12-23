package net.sf.regadb.ui.form.singlePatient;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import net.sf.regadb.db.DrugClass;
import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.TestType;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.meta.Equals;
import net.sf.regadb.io.util.StandardObjects;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.widgets.table.TableHeader;
import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WTable;
import eu.webtoolkit.jwt.WText;

public class ViralIsolateResistanceTable extends WTable {
    public ViralIsolateResistanceTable(WContainerWidget parent) {
        super(parent);
        this.setStyleClass("datatable datatable-resistance");
    }
    
    public void loadTable(boolean showMutations, Set<TestResult> testResults, TestType gssTestType) {
        clear();
        
        Transaction t = RegaDBMain.getApp().createTransaction();
        List<DrugClass> sortedDrugClasses_  = t.getDrugClassesSortedOnResistanceRanking();
        
        //drug names - column position
        HashMap<String, Integer> algoColumn = new HashMap<String, Integer>();
        int col = 0;
        elementAt(0, col).addWidget(new WText(lt("")));
        col = columnCount();
        elementAt(0, col).addWidget(new WText(lt("")));
        int maxWidth = 0;
        for(Test test : t.getTests())
        {
            if(test.getAnalysis()!=null
                    && (( gssTestType != null && Equals.isSameTestType(gssTestType, test.getTestType()) ))
                        || (gssTestType == null && StandardObjects.getGssDescription().equals(test.getTestType().getDescription())))
            {
                col = columnCount();
                elementAt(0, col).addWidget(new TableHeader(lt(test.getDescription())));
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
                row = rowCount();
                if(firstGenericDrugInThisClass)
                {
                	elementAt(row, 0).addWidget(new TableHeader(lt(dc.getClassId()+ ":")));
                    firstGenericDrugInThisClass = false;
                    elementAt(row, 0).setStyleClass("form-label-area");
                }
                elementAt(row, 1).addWidget(new TableHeader(lt(dg.getGenericId())));
                drugColumn.put(dg.getGenericId(), row);
                elementAt(row, 1).setStyleClass("form-label-area");
            }
        }
        
        //clear table
        for(int i = 1; i < rowCount(); i++)
        {
            for(int j = 2; j< columnCount(); j++)
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
