package net.sf.regadb.workflow.jgraph;

import java.awt.Color;

public class WFInputPortUserObject implements WFPortUserObject
{
    public String portName_;
    
    public Color getSelectionColor()
    {
        return Color.green;
    }

    public String getName() 
    {
        return portName_;
    }
}
