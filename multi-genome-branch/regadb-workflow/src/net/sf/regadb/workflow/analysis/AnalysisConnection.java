package net.sf.regadb.workflow.analysis;

import org.jgraph.graph.DefaultEdge;

public class AnalysisConnection extends DefaultEdge {
    private AnalysisInput input;
    private AnalysisOutput output;
    public AnalysisInput getInput() {
        return input;
    }
    public void setInput(AnalysisInput input) {
        this.input = input;
    }
    public AnalysisOutput getOutput() {
        return output;
    }
    public void setOutput(AnalysisOutput output) {
        this.output = output;
    }
}
