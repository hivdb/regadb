package net.sf.regadb.workflow.jgraph;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import org.jgraph.graph.PortRenderer;

public class WFPortRenderer extends PortRenderer
{
    Color selectionColor_;
    
    public WFPortRenderer(Color selectionColor)
    {
        selectionColor_ = selectionColor;
    }
    
    public void paint(Graphics g)
    {
        Dimension d = getSize();
        if (preview)
        {
            g.setColor(selectionColor_);
            g.drawRect(0, 0, d.width-1, d.height-1);
        }
        else if (!preview)
        {
            g.setColor(Color.white);
            g.drawRect(0, 0, d.width-1, d.height-1);
        }
    }
}
