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
            g.drawRect(3, 3, d.width-4, d.height-4);
        }
        else if (!preview)
        {
            g.setColor(Color.white);
            g.drawRect(3, 3, d.width-4, d.height-4);
        }
    }
}
