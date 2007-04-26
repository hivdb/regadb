package net.sf.regadb.workflow.jgraph;

import java.awt.Color;
import java.awt.Point;
import java.util.Map;

import javax.swing.BorderFactory;

import org.jgraph.JGraph;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphConstants;

public class AnalysesBox extends DefaultGraphCell
{
    public AnalysesBox(JGraph graph, int posX, int posY, String name1, String name2, String name3, String[] input, String[] output)
    {
        int maxWidth = countCellWidth(name1, name2, name3);
        int maxHeigth = (Math.max(input.length, output.length)*30)+40;
        
        attributes.applyMap(createBounds(new AttributeMap(),posX,posY,new Color(171, 171, 171), maxWidth, maxHeigth));
        
        setPorts(input, output, maxWidth, maxHeigth);
        
        this.setUserObject("<html><center>" + name1 + "<br>" + name2 + "<br>" + name3 + "</center></html>");
    }
    
    public int countCellWidth(String name1, String name2, String name3)
    {
        int width = Math.max((Math.max(name1.length(), name2.length())), name3.length())*6;
        if(width < 100)
        {
            return 100;
        }
        else return width;
    }
    
    public Map createBounds(AttributeMap map, int x, int y, Color c,int maxWidth, int maxHeigth)
    {
        
        GraphConstants.setBorder(map, BorderFactory.createRaisedBevelBorder());
        GraphConstants.setBackground(map, c);
        GraphConstants.setForeground(map, Color.white);
        GraphConstants.setSizeable(map, false);
        GraphConstants.setBounds(map, map.createRect(x, y, maxWidth, maxHeigth));
        GraphConstants.setOpaque(map, true);
        GraphConstants.setVerticalAlignment(map, 1);
        
        return map;
    }
    
    public void setPorts(String[] input, String[] output, int maxWidth, int maxSize)
    {
        int startX = 15;
        int startY = maxSize-15;
        //InputPorts
        for(String s : input)
        {
            addPort(new Point(startX, startY));
            startY-=30;
        }
        
        startX = maxWidth-15;
        startY = maxSize-15;
        //OutputPorts
        for(String s : output)
        {
            addPort(new Point(startX, startY));
            startY-=30;
        }
    }
}
