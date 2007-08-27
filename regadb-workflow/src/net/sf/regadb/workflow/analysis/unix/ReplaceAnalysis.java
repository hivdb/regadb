package net.sf.regadb.workflow.analysis.unix;

import javax.swing.JPanel;

import net.sf.regadb.workflow.analysis.Analysis;
import net.sf.regadb.workflow.analysis.AnalysisInput;
import net.sf.regadb.workflow.analysis.AnalysisOutput;


public class ReplaceAnalysis extends Analysis {

    public ReplaceAnalysis() {
        this.setSpecType("Replace");
        this.setDescription("Replace all occurences of a match");
        this.getInputs().add(new AnalysisInput("Original file", this));
        this.getOutputs().add(new AnalysisOutput("Replacement file", this));
    }
    
    @Override
    public boolean execute() {
        return false;
    }

    @Override
    public String getType() {
        return "Text";
    }

    @Override
    public JPanel getUI() {
        return new ReplaceAnalysisForm(this);
    }
}
