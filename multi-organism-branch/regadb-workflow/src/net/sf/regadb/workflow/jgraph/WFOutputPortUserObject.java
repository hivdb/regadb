package net.sf.regadb.workflow.jgraph;

import java.awt.Color;

import net.sf.regadb.workflow.analysis.AnalysisOutput;

public class WFOutputPortUserObject implements WFPortUserObject
{
    public String portName_;
    public AnalysisOutput output;
    
    public Color getSelectionColor()
    {
        return Color.red;
    }

    public String getName() 
    {
        return portName_;
    }
}
