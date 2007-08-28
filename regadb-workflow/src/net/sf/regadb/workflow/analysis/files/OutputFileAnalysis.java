package net.sf.regadb.workflow.analysis.files;

import net.sf.regadb.workflow.analysis.Analysis;
import net.sf.regadb.workflow.analysis.AnalysisInput;
import net.sf.regadb.workflow.analysis.ui.IAnalysisUI;

public class OutputFileAnalysis extends Analysis {

    public OutputFileAnalysis() {
        this.setSpecType("OutputFile");
        this.setDescription("Output file");
        this.getInputs().add(new AnalysisInput("External output file", this));
    }
    
    @Override
    public boolean execute() {
        return false;
    }

    @Override
    public String getType() {
        return "File";
    }

    @Override
    public IAnalysisUI getUI() {
        return new OutputFileAnalysisForm(this);
    }
}