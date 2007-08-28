package net.sf.regadb.workflow.jgraph;

import java.awt.Color;

import net.sf.regadb.workflow.analysis.AnalysisInput;

public class WFInputPortUserObject implements WFPortUserObject
{
    public String portName_;
    public AnalysisInput input;
    
    public Color getSelectionColor()
    {
        return Color.green;
    }

    public String getName() 
    {
        return portName_;
    }
}
