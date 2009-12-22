package net.sf.regadb.ui.form.singlePatient;

import java.util.Collection;
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
    
    public void loadTable(Collection<String> drugClasses, boolean showMutations, Set<TestResult> testResults, TestType gssTestType) {
        clear();
        
        if(gssTestType == null){
            return;
        }
        
        Transaction t = RegaDBMain.getApp().createTransaction();
        List<DrugClass> sortedDrugClasses_  = t.getDrugClassesSortedOnResistanceRanking();
        
        //drug names - column position
        HashMap<String, Integer> algoColumn = new HashMap<String, Integer>();
        int col = 0;
        getElementAt(0, col).addWidget(new WText(""));
        col = getColumnCount();
        getElementAt(0, col).addWidget(new WText(""));
        int maxWidth = 0;
        for(Test test : t.getTests())
        {
            if(test.getAnalysis()!=null
                    && Equals.isSameTestType(gssTestType, test.getTestType()) )
            {
                col = getColumnCount();
                getElementAt(0, col).addWidget(new TableHeader(test.getDescription()));
                getElementAt(0, col).setStyleClass("column-title");
                
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
        	if(!drugClasses.contains(dc.getClassId()))
        		continue;
        	
            genericDrugs = t.getDrugGenericSortedOnResistanceRanking(dc);
            firstGenericDrugInThisClass = true;
            for(DrugGeneric dg : genericDrugs)
            {
                row = getRowCount();
                if(firstGenericDrugInThisClass)
                {
                	getElementAt(row, 0).addWidget(new TableHeader(dc.getClassId()+ ":"));
                    firstGenericDrugInThisClass = false;
                    getElementAt(row, 0).setStyleClass("form-label-area");
                }
                getElementAt(row, 1).addWidget(new TableHeader(dg.getGenericId()));
                drugColumn.put(dg.getGenericId(), row);
                getElementAt(row, 1).setStyleClass("form-label-area");
            }
        }
        
        //clear table
        for(int i = 1; i < getRowCount(); i++)
        {
            for(int j = 2; j< getColumnCount(); j++)
            {
                ViralIsolateFormUtils.putResistanceTableResult(null, getElementAt(i, j), false, showMutations);
            }
        }
        
        Integer colN;
        Integer rowN;
        for(TestResult tr : testResults)
        {
        	if(!drugClasses.contains(tr.getDrugGeneric().getDrugClass().getClassId()))
        		continue;
        	
            colN = algoColumn.get(tr.getTest().getDescription());
            rowN = drugColumn.get(ViralIsolateFormUtils.getFixedGenericId(tr));
            if(colN!=null && rowN!=null) {
                ViralIsolateFormUtils.putResistanceTableResult(tr, getElementAt(rowN, colN), false, showMutations);
            }
            rowN = drugColumn.get(ViralIsolateFormUtils.getFixedGenericId(tr)+"/r");
            if(colN!=null && rowN!=null) {
                ViralIsolateFormUtils.putResistanceTableResult(tr, getElementAt(rowN, colN), true, showMutations);
            }
        }
        
    }
}
