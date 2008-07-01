package net.sf.regadb.workflow.analysis.ui;

import javax.swing.JPanel;

import net.sf.regadb.workflow.analysis.Analysis;

public interface IAnalysisUI {    
    public boolean saveData() ;
    
    public JPanel specificUI(Analysis analysis);

    public boolean saveSpecificUI(Analysis analysis);

    public Analysis getAnalysis();
    
    public JPanel getPanel();
}
