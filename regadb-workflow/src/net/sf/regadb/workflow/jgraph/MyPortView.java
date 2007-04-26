/*
 * @(#)MyPortView.java 3.3 23-APR-04
 * 
 * Copyright (c) 2001-2005, Gaudenz Alder All rights reserved.
 * 
 * See LICENSE file in distribution for licensing details of this source file
 */
package net.sf.regadb.workflow.jgraph;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.CellViewRenderer;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.PortRenderer;
import org.jgraph.graph.PortView;

public class MyPortView extends PortView 
{
    protected static MyPortRenderer renderer = new MyPortRenderer();

    public MyPortView(Object cell)
    {
        super(cell);
        
        AttributeMap am = new AttributeMap();
        GraphConstants.setAbsoluteX(am, true);
        GraphConstants.setAbsoluteY(am, true);
        attributes.applyMap(am);
    }

    /** 
    * Returns the bounds for the port view. 
    */
    public Rectangle2D getBounds()
    {
        Point2D pt = (Point2D) getLocation().clone();
        int width = 10;
        int height = 10;
        Rectangle2D bounds = new Rectangle2D.Double();
        bounds.setFrame(pt.getX() - width / 2, pt.getY() - height / 2, width, height);
        
        return bounds;
    }

    public CellViewRenderer getRenderer()
    {
        return new MyPortRenderer();
    }

    public static class MyPortRenderer extends PortRenderer
    {
        public void paint(Graphics g)
        {
            Dimension d = getSize();
            if (preview)
            {
                g.setColor(Color.green);
                g.drawRect(3, 3, d.width-4, d.height-4);
            }
            else if (!preview)
            {
                g.setColor(Color.white);
                g.drawRect(3, 3, d.width-4, d.height-4);
            }
        }

    }
}
