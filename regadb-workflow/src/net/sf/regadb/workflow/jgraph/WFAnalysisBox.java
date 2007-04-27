package net.sf.regadb.workflow.jgraph;

import java.awt.Color;
import java.awt.Point;

import javax.swing.BorderFactory;

import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphConstants;

public class WFAnalysisBox extends DefaultGraphCell
{
    public WFAnalysisBox(int posX, int posY, String [] labelNames, String[] inputs, String[] outputs)
    {
        setSettings(posX,posY,new Color(171, 171, 171), labelNames, inputs, outputs);
    }
    
    public void setSettings(int x, int y, Color c, String[] labelNames, String [] inputs, String [] outputs)
    {
        AttributeMap map = new AttributeMap();
        
        int maxWidth = -1;
        String label = "<html><center>";
        for(int i = 0; i<labelNames.length; i++)
        {
            maxWidth = Math.max(labelNames[i].length(), maxWidth);
            
            label += labelNames[i];
            if(i+1!=labelNames.length)
                label += "<br>";
        }
        label += "</center></html>";
        maxWidth=maxWidth*7 + 10; //supposed font width
        maxWidth = Math.max(maxWidth, 100);
        
        int maxHeigth = (Math.max(inputs.length, outputs.length)*30)+40;
        
        GraphConstants.setBorder(map, BorderFactory.createRaisedBevelBorder());
        GraphConstants.setBackground(map, c);
        GraphConstants.setForeground(map, Color.white);
        GraphConstants.setSizeable(map, false);
        GraphConstants.setBounds(map, map.createRect(x, y, maxWidth, maxHeigth));
        GraphConstants.setOpaque(map, true);
        GraphConstants.setVerticalAlignment(map, 1);
        
        
        setPorts(inputs, outputs, maxWidth, maxHeigth);
        
        this.setUserObject(label);
        
        attributes.applyMap(map);
    }
    
    private void setPorts(String[] input, String[] output, int maxWidth, int maxSize)
    {
        int startX = 15;
        int startY = maxSize-15;
        
        //InputPorts
        for(String s : input)
        {
            WFInputPortUserObject uo = new WFInputPortUserObject();
            uo.portName_ = s;
            addPort(new Point(startX, startY), uo);
            startY-=30;
        }
        
        startX = maxWidth-15;
        startY = maxSize-15;
        //OutputPorts
        for(String s : output)
        {
            WFOutputPortUserObject uo = new WFOutputPortUserObject();
            uo.portName_ = s;
            addPort(new Point(startX, startY), uo);
            startY-=30;
        }
    }
}
