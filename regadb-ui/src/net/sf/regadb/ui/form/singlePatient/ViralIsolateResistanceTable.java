package net.sf.regadb.ui.form.singlePatient;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
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
import net.sf.regadb.util.settings.RegaDBSettings;
import net.sf.regadb.util.settings.ViralIsolateFormConfig;
import net.sf.regadb.util.settings.ViralIsolateFormConfig.Algorithm;
import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WTable;
import eu.webtoolkit.jwt.WTableCell;
import eu.webtoolkit.jwt.WText;
import eu.webtoolkit.jwt.WWidget;

public class ViralIsolateResistanceTable extends WTable {
    public ViralIsolateResistanceTable(WContainerWidget parent) {
        super(parent);
        this.setStyleClass("datatable datatable-resistance");
    }
    
    public void loadTable(Collection<String> drugClasses, boolean showMutations, boolean showAllAlgorithms, Set<TestResult> testResults, TestType gssTestType) {
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
        for(Test test : getAlgorithms(t, gssTestType, showAllAlgorithms)) {
        	col = getColumnCount();
            getElementAt(0, col).addWidget(new TableHeader(test.getDescription()));
            getElementAt(0, col).setStyleClass("column-title");
                
            algoColumn.put(test.getDescription(), col);
            maxWidth += test.getDescription().length();
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
        
        ViralIsolateFormConfig config = 
        	RegaDBSettings.getInstance().getInstituteConfig().getViralIsolateFormConfig();
        
        //clear table
        for(int i = 1; i < getRowCount(); i++)
        {
            for(int j = 2; j< getColumnCount(); j++)
            {
                ViralIsolateFormUtils.putResistanceTableResult(null, getElementAt(i, j), config, false, showMutations);
            }
        }
        
        Integer colN;
        Integer rowN;
        for(TestResult tr : testResults)
        {   
            if(tr.getTest().getAnalysis()!=null
                    && Equals.isSameTestType(gssTestType, tr.getTest().getTestType()))
            {
            	if(!drugClasses.contains(tr.getDrugGeneric().getDrugClass().getClassId()))
            		continue;
            	
	            colN = algoColumn.get(tr.getTest().getDescription());
	            rowN = drugColumn.get(ViralIsolateFormUtils.getFixedGenericId(tr));
	            if(colN!=null && rowN!=null) {
	                ViralIsolateFormUtils.putResistanceTableResult(tr, getElementAt(rowN, colN), config, false, showMutations);
	            }
            }
        }
    }
    
    private List<Test> getAlgorithms(Transaction t, TestType gssTT, boolean showAllAlgorithms) {
    	ViralIsolateFormConfig config = 
    		RegaDBSettings.getInstance().getInstituteConfig().getViralIsolateFormConfig();
    	
    	if (config.getAlgorithms() == null || showAllAlgorithms) {
    		return sortGSSTests(filterTests(t.getTests(), gssTT));
    	} else {
    		List<Test> tests = new ArrayList<Test>();
    		for (Algorithm a : config.getAlgorithms()) {
    			Test test = t.getTest(a.getName(), gssTT.getDescription(), gssTT.getGenome().getOrganismName());
    			if (test != null && a.getOrganism().equals(gssTT.getGenome().getOrganismName()))
    				tests.add(test);
    		}
    		return filterTests(tests, gssTT);
    	}
    }
    
    private List<Test> sortGSSTests(List<Test> tests) {
    	final boolean nameAscending = true;
    	final boolean versionAscending = false;
    	Collections.sort(tests, new Comparator<Test>(){
			@Override
			public int compare(Test t1, Test t2) {
				String d1 = getDescription(t1);
				String d2 = getDescription(t2);
				Integer v1 = getVersion(t1);
				Integer v2 = getVersion(t2);
				
				if (d1.equals(d2))
					if (versionAscending)
						return v1.compareTo(v2);
					else
						return v2.compareTo(v1);
				else
					if (nameAscending)
						return d1.compareTo(d2);
					else
						return d2.compareTo(d1);
			}
			
			private String getDescription(Test t) {
				return t.getDescription().split(" ")[0];
			}
			
			private int getVersion(Test t) {
				int version = -1;
				String [] parts = t.getDescription().split(" ");
				if (parts.length > 1) {
					try {
						StringBuffer buffer = new StringBuffer();
						for (int i = 0; i < parts[1].length(); i++) {
							if (Character.isDigit(parts[1].charAt(i)))
								buffer.append(parts[1].charAt(i));
						}
						version = Integer.parseInt(buffer.toString());
					} catch (Exception e) {
						
					}
				}
				return version;
			}
    	});
    	
    	return tests;
    }
    
    private List<Test> filterTests(List<Test> tests, TestType gssTT) {
		List<Test> filteredTest = new ArrayList<Test>();
		for (Test test : tests) {
			if(test.getAnalysis()!=null
                    && Equals.isSameTestType(gssTT, test.getTestType()))
				filteredTest.add(test);
		}
		return filteredTest;
    }
    
    public void writeTableToCsv(Writer w) throws IOException {
    	int cols = getColumnCount();
    	for (int i = 0; i < getRowCount(); i++) {
    		for (int j = 0; j < cols; j++) {
    			writeCell(w, getElementAt(i, j), 0);
    			writeCell(w, getElementAt(i, j), 1);
    			
    			if (j != (cols - 1))
    				w.write(",");
    		}
    		w.write("\n");
    	}
    }
    
    private void writeCell(Writer w, WTableCell cell, int index) throws IOException {
    	List<WWidget> children = cell.getChildren();
		if (children.size() > index && children.get(index) instanceof WText) {
			WText t = (WText)children.get(index);
			w.write(t.getText().toString());
		}
    }
}
