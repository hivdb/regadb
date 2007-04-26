package net.sf.regadb.workflow.jgraph;

import java.awt.Color;

public class WFOutputPortUserObject implements WFPortUserObject
{
    public String portName_;
    
    public Color getSelectionColor()
    {
        return Color.red;
    }

    public String getName() 
    {
        return portName_;
    }
}
