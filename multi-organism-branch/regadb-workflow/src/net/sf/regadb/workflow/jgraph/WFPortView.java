/*
 * @(#)MyPortView.java 3.3 23-APR-04
 * 
 * Copyright (c) 2001-2005, Gaudenz Alder All rights reserved.
 * 
 * See LICENSE file in distribution for licensing details of this source file
 */
package net.sf.regadb.workflow.jgraph;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.CellViewRenderer;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.PortView;

public class WFPortView extends PortView 
{
    protected WFPortRenderer renderer_;

    public WFPortView(Object cell)
    {
        super(cell);
        
        renderer_ = new WFPortRenderer(((WFPortUserObject)((DefaultPort)cell).getUserObject()).getSelectionColor());
        
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
        return renderer_;
    }
}
