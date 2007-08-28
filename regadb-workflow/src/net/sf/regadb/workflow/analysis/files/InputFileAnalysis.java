package net.sf.regadb.workflow.analysis.files;

import net.sf.regadb.workflow.analysis.Analysis;
import net.sf.regadb.workflow.analysis.AnalysisOutput;
import net.sf.regadb.workflow.analysis.ui.IAnalysisUI;

public class InputFileAnalysis extends Analysis {

    public InputFileAnalysis() {
        this.setSpecType("InputFile");
        this.setDescription("Input file");
        this.getOutputs().add(new AnalysisOutput("External input file", this));
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
        return new InputFileAnalysisForm(this);
    }
}
